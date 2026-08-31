-- V3__nuevas_tablas.sql
-- Migracion: Vehiculos, Rutas, Asignacion_Rutas e Incidentes

-- ============================================================
-- 1. VEHICULOS
-- ============================================================
CREATE TABLE vehiculos (
                           id BIGSERIAL PRIMARY KEY,
                           placa VARCHAR(20) NOT NULL,
                           marca VARCHAR(50) NOT NULL,
                           modelo VARCHAR(50) NOT NULL,
                           anio INTEGER NOT NULL,
                           capacidad_pasajeros INTEGER NOT NULL,
                           numero_motor VARCHAR(50),
                           numero_chasis VARCHAR(50),
                           color VARCHAR(30),
                           estado VARCHAR(25) NOT NULL DEFAULT 'ACTIVO',
                           activo BOOLEAN NOT NULL DEFAULT TRUE,
                           creado_en TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                           actualizado_en TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX idx_vehiculos_placa
    ON vehiculos(placa);

ALTER TABLE vehiculos
    ADD CONSTRAINT chk_vehiculos_estado
        CHECK ( estado IN (
                           'ACTIVO',
                           'EN_MANTENIMIENTO',
                           'FUERA_DE_SERVICIO'
            ));

ALTER TABLE vehiculos
    ADD CONSTRAINT chk_vehiculos_anio
        CHECK ( anio >= 1990 AND anio <= 2030 );

ALTER TABLE vehiculos
    ADD CONSTRAINT chk_vehiculos_capacidad
        CHECK ( capacidad_pasajeros >= 1 );

CREATE TRIGGER trg_vehiculos_actualizado_en
    BEFORE UPDATE ON vehiculos
    FOR EACH ROW
EXECUTE FUNCTION actualizar_fecha_modificacion();


-- ============================================================
-- 2. RUTAS
-- ============================================================
CREATE TABLE rutas (
                       id BIGSERIAL PRIMARY KEY,
                       codigo VARCHAR(20) NOT NULL,
                       nombre VARCHAR(100) NOT NULL,
                       origen VARCHAR(150) NOT NULL,
                       destino VARCHAR(150) NOT NULL,
                       distancia_km DOUBLE PRECISION NOT NULL,
                       duracion_estimada_min INTEGER NOT NULL,
                       estado VARCHAR(10) NOT NULL DEFAULT 'ACTIVA',
                       activo BOOLEAN NOT NULL DEFAULT TRUE,
                       creado_en TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                       actualizado_en TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX idx_rutas_codigo
    ON rutas(codigo);

ALTER TABLE rutas
    ADD CONSTRAINT chk_rutas_estado
        CHECK ( estado IN ('ACTIVA', 'INACTIVA') );

ALTER TABLE rutas
    ADD CONSTRAINT chk_rutas_distancia
        CHECK ( distancia_km >= 0 );

ALTER TABLE rutas
    ADD CONSTRAINT chk_rutas_duracion
        CHECK ( duracion_estimada_min >= 1 );

CREATE TRIGGER trg_rutas_actualizado_en
    BEFORE UPDATE ON rutas
    FOR EACH ROW
EXECUTE FUNCTION actualizar_fecha_modificacion();


-- ============================================================
-- 3. ASIGNACION_RUTAS
-- ============================================================
CREATE TABLE asignacion_rutas (
                                  id BIGSERIAL PRIMARY KEY,
                                  conductor_id BIGINT NOT NULL,
                                  vehiculo_id BIGINT NOT NULL,
                                  ruta_id BIGINT NOT NULL,
                                  fecha_asignacion DATE NOT NULL,
                                  fecha_inicio DATE NOT NULL,
                                  fecha_fin DATE,
                                  estado VARCHAR(15) NOT NULL DEFAULT 'ACTIVA',
                                  activo BOOLEAN NOT NULL DEFAULT TRUE,
                                  creado_en TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                  actualizado_en TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_asignacion_conductor
        FOREIGN KEY (conductor_id)
            REFERENCES conductores(id),

    CONSTRAINT fk_asignacion_vehiculo
        FOREIGN KEY (vehiculo_id)
            REFERENCES vehiculos(id),

    CONSTRAINT fk_asignacion_ruta
        FOREIGN KEY (ruta_id)
            REFERENCES rutas(id)
);

ALTER TABLE asignacion_rutas
    ADD CONSTRAINT chk_asignacion_estado
        CHECK ( estado IN ('ACTIVA', 'COMPLETADA', 'CANCELADA') );

ALTER TABLE asignacion_rutas
    ADD CONSTRAINT chk_asignacion_fechas
        CHECK ( fecha_fin IS NULL OR fecha_fin >= fecha_inicio );

CREATE INDEX idx_asignacion_conductor
    ON asignacion_rutas(conductor_id);

CREATE INDEX idx_asignacion_vehiculo
    ON asignacion_rutas(vehiculo_id);

CREATE INDEX idx_asignacion_ruta
    ON asignacion_rutas(ruta_id);

CREATE TRIGGER trg_asignacion_rutas_actualizado_en
    BEFORE UPDATE ON asignacion_rutas
    FOR EACH ROW
EXECUTE FUNCTION actualizar_fecha_modificacion();


-- ============================================================
-- 4. INCIDENTES
-- ============================================================
CREATE TABLE incidentes (
                            id BIGSERIAL PRIMARY KEY,
                            asignacion_id BIGINT NOT NULL,
                            reportado_por VARCHAR(100) NOT NULL,
                            tipo VARCHAR(25) NOT NULL,
                            descripcion TEXT NOT NULL,
                            fecha_incidente TIMESTAMPTZ NOT NULL,
                            ubicacion VARCHAR(255),
                            gravedad VARCHAR(10) NOT NULL,
                            estado VARCHAR(20) NOT NULL DEFAULT 'REPORTADO',
                            activo BOOLEAN NOT NULL DEFAULT TRUE,
                            creado_en TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                            actualizado_en TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_incidente_asignacion
        FOREIGN KEY (asignacion_id)
            REFERENCES asignacion_rutas(id)
);

ALTER TABLE incidentes
    ADD CONSTRAINT chk_incidentes_tipo
        CHECK ( tipo IN (
                         'ACCIDENTE',
                         'AVERIA_MECANICA',
                         'INFRACCION',
                         'QUEJA',
                         'OTRO'
            ));

ALTER TABLE incidentes
    ADD CONSTRAINT chk_incidentes_gravedad
        CHECK ( gravedad IN ('BAJA', 'MEDIA', 'ALTA', 'CRITICA') );

ALTER TABLE incidentes
    ADD CONSTRAINT chk_incidentes_estado
        CHECK ( estado IN (
                           'REPORTADO',
                           'EN_INVESTIGACION',
                           'RESUELTO',
                           'CERRADO'
            ));

CREATE INDEX idx_incidentes_asignacion
    ON incidentes(asignacion_id);

CREATE INDEX idx_incidentes_tipo
    ON incidentes(tipo);

CREATE INDEX idx_incidentes_gravedad
    ON incidentes(gravedad);

CREATE TRIGGER trg_incidentes_actualizado_en
    BEFORE UPDATE ON incidentes
    FOR EACH ROW
EXECUTE FUNCTION actualizar_fecha_modificacion();
