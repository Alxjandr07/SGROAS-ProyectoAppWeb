-- V6: Replicación del esquema sgroas_db (prácticas Administración de Bases de Datos)
-- Autora: Escudero Plaza María del Rosario
-- Integra el modelo relacional desarrollado en pgAdmin4 para conectar ABD + Aplicaciones Web.
-- Las tablas conviven con las del backend (usuarios/conductores/vehiculos/...) sin colisión.

-- ============================================================
-- Jerarquía geográfica
-- ============================================================

CREATE TABLE provincia (
    id_provincia integer NOT NULL,
    nombre character varying(100) NOT NULL,
    CONSTRAINT provincia_pkey PRIMARY KEY (id_provincia)
);

CREATE SEQUENCE provincia_id_provincia_seq
    AS integer START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER SEQUENCE provincia_id_provincia_seq OWNED BY provincia.id_provincia;
ALTER TABLE ONLY provincia ALTER COLUMN id_provincia SET DEFAULT nextval('provincia_id_provincia_seq'::regclass);

CREATE TABLE ciudad (
    id_ciudad integer NOT NULL,
    nombre character varying(100) NOT NULL,
    id_provincia integer NOT NULL,
    CONSTRAINT ciudad_pkey PRIMARY KEY (id_ciudad),
    CONSTRAINT fk_ciudad_provincia FOREIGN KEY (id_provincia)
        REFERENCES provincia(id_provincia) ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE SEQUENCE ciudad_id_ciudad_seq
    AS integer START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER SEQUENCE ciudad_id_ciudad_seq OWNED BY ciudad.id_ciudad;
ALTER TABLE ONLY ciudad ALTER COLUMN id_ciudad SET DEFAULT nextval('ciudad_id_ciudad_seq'::regclass);

CREATE TABLE terminal (
    id_terminal integer NOT NULL,
    nombre character varying(150) NOT NULL,
    id_ciudad integer NOT NULL,
    CONSTRAINT terminal_pkey PRIMARY KEY (id_terminal),
    CONSTRAINT fk_terminal_ciudad FOREIGN KEY (id_ciudad)
        REFERENCES ciudad(id_ciudad) ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE SEQUENCE terminal_id_terminal_seq
    AS integer START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER SEQUENCE terminal_id_terminal_seq OWNED BY terminal.id_terminal;
ALTER TABLE ONLY terminal ALTER COLUMN id_terminal SET DEFAULT nextval('terminal_id_terminal_seq'::regclass);

-- ============================================================
-- Roles (normalización del enum Rol del backend)
-- ============================================================

CREATE TABLE rol (
    id_rol integer NOT NULL,
    nombre character varying(100) NOT NULL,
    descripcion character varying(200),
    CONSTRAINT rol_pkey PRIMARY KEY (id_rol)
);

CREATE SEQUENCE rol_id_rol_seq
    AS integer START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER SEQUENCE rol_id_rol_seq OWNED BY rol.id_rol;
ALTER TABLE ONLY rol ALTER COLUMN id_rol SET DEFAULT nextval('rol_id_rol_seq'::regclass);

-- ============================================================
-- Modelo operativo ABD (nombres singulares; no colisionan con el backend)
-- ============================================================

CREATE TABLE usuario (
    id_usuario integer NOT NULL,
    cedula character varying(10) NOT NULL,
    nombre character varying(100) NOT NULL,
    correo character varying(100) NOT NULL,
    contrasena character varying(255) NOT NULL,
    estado character varying(50) DEFAULT 'Activo'::character varying NOT NULL,
    id_rol integer NOT NULL,
    CONSTRAINT usuario_pkey PRIMARY KEY (id_usuario),
    CONSTRAINT usuario_cedula_key UNIQUE (cedula),
    CONSTRAINT usuario_correo_key UNIQUE (correo),
    CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol)
        REFERENCES rol(id_rol) ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE SEQUENCE usuario_id_usuario_seq
    AS integer START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER SEQUENCE usuario_id_usuario_seq OWNED BY usuario.id_usuario;
ALTER TABLE ONLY usuario ALTER COLUMN id_usuario SET DEFAULT nextval('usuario_id_usuario_seq'::regclass);

CREATE TABLE conductor (
    id_conductor integer NOT NULL,
    cedula character varying(10) NOT NULL,
    nombres character varying(100) NOT NULL,
    licencia character varying(30) NOT NULL,
    fecha_vencimiento date NOT NULL,
    telefono character varying(25) NOT NULL,
    CONSTRAINT conductor_pkey PRIMARY KEY (id_conductor),
    CONSTRAINT conductor_cedula_key UNIQUE (cedula)
);

CREATE SEQUENCE conductor_id_conductor_seq
    AS integer START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER SEQUENCE conductor_id_conductor_seq OWNED BY conductor.id_conductor;
ALTER TABLE ONLY conductor ALTER COLUMN id_conductor SET DEFAULT nextval('conductor_id_conductor_seq'::regclass);

CREATE TABLE unidad (
    id_unidad integer NOT NULL,
    placa character varying(15) NOT NULL,
    numero_disco character varying(10) NOT NULL,
    modelo character varying(50) NOT NULL,
    capacidad integer NOT NULL,
    anio_fabricacion integer,
    estado character varying(50) DEFAULT 'Activo'::character varying NOT NULL,
    CONSTRAINT unidad_pkey PRIMARY KEY (id_unidad),
    CONSTRAINT unidad_placa_key UNIQUE (placa),
    CONSTRAINT unidad_numero_disco_key UNIQUE (numero_disco)
);

CREATE SEQUENCE unidad_id_unidad_seq
    AS integer START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER SEQUENCE unidad_id_unidad_seq OWNED BY unidad.id_unidad;
ALTER TABLE ONLY unidad ALTER COLUMN id_unidad SET DEFAULT nextval('unidad_id_unidad_seq'::regclass);

CREATE TABLE ruta (
    id_ruta integer NOT NULL,
    id_terminal_origen integer NOT NULL,
    id_terminal_destino integer NOT NULL,
    precio_pasaje numeric(10,2) NOT NULL,
    CONSTRAINT ruta_pkey PRIMARY KEY (id_ruta),
    CONSTRAINT chk_rutas_distintas CHECK ((id_terminal_origen <> id_terminal_destino)),
    CONSTRAINT fk_ruta_terminal_origen FOREIGN KEY (id_terminal_origen)
        REFERENCES terminal(id_terminal) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_ruta_terminal_destino FOREIGN KEY (id_terminal_destino)
        REFERENCES terminal(id_terminal) ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE SEQUENCE ruta_id_ruta_seq
    AS integer START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER SEQUENCE ruta_id_ruta_seq OWNED BY ruta.id_ruta;
ALTER TABLE ONLY ruta ALTER COLUMN id_ruta SET DEFAULT nextval('ruta_id_ruta_seq'::regclass);

CREATE TABLE programacion (
    id_programacion integer NOT NULL,
    fecha date NOT NULL,
    hora_salida time without time zone NOT NULL,
    hora_estimada_llegada time without time zone NOT NULL,
    estado character varying(50) DEFAULT 'Programado'::character varying NOT NULL,
    id_ruta integer NOT NULL,
    id_unidad integer NOT NULL,
    id_conductor integer NOT NULL,
    id_usuario integer,
    CONSTRAINT programacion_pkey PRIMARY KEY (id_programacion),
    CONSTRAINT fk_programacion_ruta FOREIGN KEY (id_ruta)
        REFERENCES ruta(id_ruta) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_programacion_unidad FOREIGN KEY (id_unidad)
        REFERENCES unidad(id_unidad) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_programacion_conductor FOREIGN KEY (id_conductor)
        REFERENCES conductor(id_conductor) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_programacion_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario) ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE SEQUENCE programacion_id_programacion_seq
    AS integer START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER SEQUENCE programacion_id_programacion_seq OWNED BY programacion.id_programacion;
ALTER TABLE ONLY programacion ALTER COLUMN id_programacion SET DEFAULT nextval('programacion_id_programacion_seq'::regclass);

CREATE TABLE incidente (
    id_incidente integer NOT NULL,
    tipo character varying(50) NOT NULL,
    descripcion text NOT NULL,
    nivel_sugerido character varying(20) NOT NULL,
    fecha_incidente timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    evidencia character varying(255),
    estado character varying(20) DEFAULT 'Reportado'::character varying NOT NULL,
    id_unidad integer NOT NULL,
    CONSTRAINT incidente_pkey PRIMARY KEY (id_incidente),
    CONSTRAINT fk_incidente_unidad FOREIGN KEY (id_unidad)
        REFERENCES unidad(id_unidad) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE SEQUENCE incidente_id_incidente_seq
    AS integer START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER SEQUENCE incidente_id_incidente_seq OWNED BY incidente.id_incidente;
ALTER TABLE ONLY incidente ALTER COLUMN id_incidente SET DEFAULT nextval('incidente_id_incidente_seq'::regclass);

-- ============================================================
-- Alertas y auditoría
-- ============================================================

CREATE TABLE alerta (
    id_alerta integer NOT NULL,
    nivel_riesgo character varying(20) NOT NULL,
    descripcion text NOT NULL,
    fecha timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    id_incidente integer NOT NULL,
    CONSTRAINT alerta_pkey PRIMARY KEY (id_alerta),
    CONSTRAINT fk_alerta_incidente FOREIGN KEY (id_incidente)
        REFERENCES incidente(id_incidente) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE SEQUENCE alerta_id_alerta_seq
    AS integer START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER SEQUENCE alerta_id_alerta_seq OWNED BY alerta.id_alerta;
ALTER TABLE ONLY alerta ALTER COLUMN id_alerta SET DEFAULT nextval('alerta_id_alerta_seq'::regclass);

CREATE TABLE auditoria (
    id_auditoria integer NOT NULL,
    accion character varying(200) NOT NULL,
    fecha_hora timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    ip character varying(50) NOT NULL,
    id_usuario integer NOT NULL,
    CONSTRAINT auditoria_pkey PRIMARY KEY (id_auditoria),
    CONSTRAINT fk_auditoria_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE SEQUENCE auditoria_id_auditoria_seq
    AS integer START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER SEQUENCE auditoria_id_auditoria_seq OWNED BY auditoria.id_auditoria;
ALTER TABLE ONLY auditoria ALTER COLUMN id_auditoria SET DEFAULT nextval('auditoria_id_auditoria_seq'::regclass);
