-- V13__funciones_cursores_sgroas.sql
-- Elementos programables SGROAS alineados a la estructura exigida por la
-- asignatura ABD ("Elementos programables en DBMS"):
--   PARTE I  -> Funciones definidas por el usuario (UDF):
--               fn_total_programaciones (escalar, cuenta por estado) y
--               fn_nivel_atencion_incidente (clasificacion IF/ELSE).
--   PARTE II -> Procedimiento con validacion y DML: sp_registrar_incidente.
--   PARTE III-> Recorrido de filas con CURSOR explicito
--               (DECLARE CURSOR / OPEN / FETCH / WHILE / CLOSE):
--               fn_resumen_programaciones_por_unidad.
--   PARTE IV -> Los disparadores ya estan cubiertos en V12 (auditoria) y V1
--               (actualizar_fecha_modificacion); este archivo agrega tambien
--               el indice que da soporte a la funcion escalar por estado.
-- Contenido sincronizado con db/procs/*.sql.
-- Idempotente: CREATE OR REPLACE / CREATE INDEX IF NOT EXISTS.

-- Indice de soporte para fn_total_programaciones(p_estado) (unido a la
-- medicion de EXPLAIN ANALYZE de docs/basedatos/INDICES-ABD.md).
CREATE INDEX IF NOT EXISTS idx_prog_estado ON programacion (estado);
ANALYZE programacion;

-- =============================================================================
-- fn_total_programaciones
-- Descripcion: Funcion escalar que retorna la cantidad de programaciones,
--              total o filtrada por estado. Equivalente SGROAS de la funcion
--              escalar "count" (PARTE I del enunciado de ABD).
-- Uso:
--   SELECT fn_total_programaciones('Programado');
--   SELECT fn_total_programaciones();
-- El filtro por estado aprovecha el indice idx_prog_estado.
-- =============================================================================

CREATE OR REPLACE FUNCTION fn_total_programaciones(
    p_estado VARCHAR DEFAULT NULL
)
    RETURNS BIGINT
    LANGUAGE plpgsql
AS $$
DECLARE
    v_total BIGINT;
BEGIN
    IF p_estado IS NULL THEN
        SELECT COUNT(*) INTO v_total FROM programacion;
    ELSE
        SELECT COUNT(*) INTO v_total FROM programacion WHERE estado = p_estado;
    END IF;

    RETURN v_total;
END;
$$;

-- =============================================================================
-- fn_nivel_atencion_incidente
-- Descripcion: Funcion escalar que clasifica el nivel de atencion que requiere
--              un incidente segun la cantidad de alertas asociadas
--              (estructura IF/ELSE de la PARTE I del enunciado):
--                  3+ alertas -> CRITICO
--                  2  alertas -> MEDIO
--                  1  alerta  -> BAJO
--                  0  alertas -> SIN_ALERTA
-- Uso:
--   SELECT i.id_incidente, fn_nivel_atencion_incidente(i.id_incidente)
--   FROM incidente i;
-- Tabla relacionada: alerta (id_incidente).
-- =============================================================================

CREATE OR REPLACE FUNCTION fn_nivel_atencion_incidente(
    p_id_incidente INTEGER
)
    RETURNS VARCHAR
    LANGUAGE plpgsql
AS $$
DECLARE
    v_total_alertas INTEGER;
    v_nivel VARCHAR(20);
BEGIN
    SELECT COUNT(*) INTO v_total_alertas
    FROM alerta
    WHERE id_incidente = p_id_incidente;

    IF v_total_alertas >= 3 THEN
        v_nivel := 'CRITICO';
    ELSIF v_total_alertas = 2 THEN
        v_nivel := 'MEDIO';
    ELSIF v_total_alertas = 1 THEN
        v_nivel := 'BAJO';
    ELSE
        v_nivel := 'SIN_ALERTA';
    END IF;

    RETURN v_nivel;
END;
$$;

-- =============================================================================
-- fn_resumen_programaciones_por_unidad
-- Descripcion: Funcion que recorre todas las unidades con un CURSOR explicito
--              (DECLARE CURSOR / OPEN / FETCH NEXT / EXIT WHEN NOT FOUND /
--              CLOSE, equivalente de la PARTE III del enunciado) y agrega por
--              cada una el total de programaciones, realizadas y canceladas.
-- Uso:
--   SELECT * FROM fn_resumen_programaciones_por_unidad();
-- El conteo por unidad aprovecha idx_prog_unidad_estado (id_unidad, estado).
-- =============================================================================

CREATE OR REPLACE FUNCTION fn_resumen_programaciones_por_unidad()
    RETURNS TABLE (
        placa VARCHAR,
        modelo VARCHAR,
        capacidad INTEGER,
        total_programaciones BIGINT,
        programaciones_realizadas BIGINT,
        programaciones_canceladas BIGINT
    )
    LANGUAGE plpgsql
AS $$
DECLARE
    v_id_unidad INTEGER;
    v_placa VARCHAR(15);
    v_modelo VARCHAR(50);
    v_capacidad INTEGER;
    v_total BIGINT;
    v_realizadas BIGINT;
    v_canceladas BIGINT;

    cr_unidades CURSOR FOR
        SELECT u.id_unidad, u.placa, u.modelo, u.capacidad
        FROM unidad u
        ORDER BY u.placa;
BEGIN
    OPEN cr_unidades;

    LOOP
        FETCH NEXT FROM cr_unidades
            INTO v_id_unidad, v_placa, v_modelo, v_capacidad;
        EXIT WHEN NOT FOUND;

        SELECT COUNT(*),
               COUNT(*) FILTER (WHERE estado = 'Realizado'),
               COUNT(*) FILTER (WHERE estado = 'Cancelado')
        INTO v_total, v_realizadas, v_canceladas
        FROM programacion
        WHERE id_unidad = v_id_unidad;

        placa                    := v_placa;
        modelo                   := v_modelo;
        capacidad                := v_capacidad;
        total_programaciones     := v_total;
        programaciones_realizadas := v_realizadas;
        programaciones_canceladas := v_canceladas;
        RETURN NEXT;
    END LOOP;

    CLOSE cr_unidades;
    RETURN;
END;
$$;

-- =============================================================================
-- sp_registrar_incidente
-- Descripcion: Procedimiento que registra un incidente validando previamente
--              que la unidad y el usuario existan y que el nivel sugerido sea
--              valido (misma estructura IF EXISTS / validacion de la PARTE II
--              del enunciado). Tras la validacion inserta la fila y entrega el
--              id generado vía parametro OUT.
-- Uso (caso valido, capturando el id):
--   DO $$
--   DECLARE nuevo_id INTEGER;
--   BEGIN
--       CALL sp_registrar_incidente('AVERIA_MECANICA', 'Fallo de frenos',
--                                    'ALTO', 1, 1, nuevo_id);
--       RAISE NOTICE 'Incidente registrado con id=%', nuevo_id;
--   END $$;
-- Uso (caso invalido, debe lanzar EXCEPTION):
--   CALL sp_registrar_incidente('AVERIA_MECANICA', 'x', 'ALTO', 999, NULL, NULL);
-- La insercion dispara el trigger de auditoria (V12) y el trigger
-- actualizar_fecha_modificacion no aplica (incidente no tiene esa columna).
-- =============================================================================

CREATE OR REPLACE PROCEDURE sp_registrar_incidente(
    p_tipo VARCHAR,
    p_descripcion TEXT,
    p_nivel_sugerido VARCHAR,
    p_id_unidad INTEGER,
    p_id_usuario INTEGER,
    OUT p_id_incidente INTEGER
)
    LANGUAGE plpgsql
AS $$
DECLARE
    v_existe INTEGER;
BEGIN
    -- Validacion 1: la unidad debe existir.
    SELECT COUNT(*) INTO v_existe FROM unidad WHERE id_unidad = p_id_unidad;
    IF v_existe = 0 THEN
        RAISE EXCEPTION 'sp_registrar_incidente: la unidad % no existe.', p_id_unidad;
    END IF;

    -- Validacion 2: el nivel sugerido debe ser permitido.
    IF p_nivel_sugerido IS NULL OR p_nivel_sugerido NOT IN ('ALTO', 'MEDIO', 'BAJO') THEN
        RAISE EXCEPTION 'sp_registrar_incidente: nivel sugerido invalido (%). Permitidos: ALTO, MEDIO, BAJO.', p_nivel_sugerido;
    END IF;

    -- Validacion 3: si se indica el usuario, debe existir (lo registra la auditoria).
    IF p_id_usuario IS NOT NULL THEN
        SELECT COUNT(*) INTO v_existe FROM usuario WHERE id_usuario = p_id_usuario;
        IF v_existe = 0 THEN
            RAISE EXCEPTION 'sp_registrar_incidente: el usuario % no existe.', p_id_usuario;
        END IF;
        PERFORM set_config('app.audit_usuario_id', p_id_usuario::TEXT, false);
    END IF;

    INSERT INTO incidente (tipo, descripcion, nivel_sugerido, fecha_incidente, estado, id_unidad)
    VALUES (p_tipo, p_descripcion, p_nivel_sugerido, CURRENT_TIMESTAMP, 'Reportado', p_id_unidad)
    RETURNING id_incidente INTO p_id_incidente;
END;
$$;