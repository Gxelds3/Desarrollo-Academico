-- ============================================================================
--  schema-test.sql
--
--  Esquema minimo de la base de datos "Desarrollo Academico", reconstruido a
--  partir de las sentencias SQL que ejecutan los DAO del proyecto.
--  Se carga automaticamente dentro del contenedor Oracle levantado por
--  Testcontainers al iniciar las pruebas (ver AbstractDaoContainerTest).
--
--  IMPORTANTE: este archivo NO sustituye al esquema real de produccion; es una
--  reconstruccion para poder ejecutar las pruebas de forma aislada. Si el
--  esquema real tiene columnas o restricciones adicionales, conviene
--  reemplazar este archivo por el DDL oficial exportado de Oracle.
-- ============================================================================

-- ------------------------------------------------------------------ DIVISION
CREATE TABLE division (
    id_division   NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre        VARCHAR2(150) NOT NULL
);

-- ------------------------------------------------------------------- USUARIO
CREATE TABLE usuario (
    id_usuario            NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre                VARCHAR2(100) NOT NULL,
    apellido_paterno      VARCHAR2(100),
    apellido_materno      VARCHAR2(100),
    rol                   VARCHAR2(50)  NOT NULL,
    id_division           NUMBER,
    numero_empleado       VARCHAR2(50),
    telefono              VARCHAR2(20),
    correo_institucional  VARCHAR2(150),
    contrasena            VARCHAR2(255),
    fecha_registro        TIMESTAMP DEFAULT SYSTIMESTAMP,
    activo                NUMBER(1) DEFAULT 1,
    creado_por            NUMBER,
    CONSTRAINT fk_usuario_division FOREIGN KEY (id_division) REFERENCES division (id_division)
);

-- -------------------------------------------------------------------- EVENTO
CREATE TABLE evento (
    id_evento       NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre          VARCHAR2(200) NOT NULL,
    lugar           VARCHAR2(200),
    institucion     VARCHAR2(200),
    tipo_evento     VARCHAR2(100),
    descripcion     VARCHAR2(1000),
    fecha_inicio    DATE,
    fecha_fin       DATE,
    modalidad       VARCHAR2(50),
    id_division     NUMBER,
    creado_por      NUMBER,
    fecha_creacion  TIMESTAMP DEFAULT SYSTIMESTAMP,
    CONSTRAINT fk_evento_division FOREIGN KEY (id_division) REFERENCES division (id_division)
);

-- -------------------------------------------------------- PARTICIPANTE_EVENTO
CREATE TABLE participante_evento (
    id_participante  NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_evento        NUMBER NOT NULL,
    id_usuario       NUMBER NOT NULL,
    registrado_por   NUMBER,
    fecha_registro   TIMESTAMP DEFAULT SYSTIMESTAMP,
    CONSTRAINT fk_part_evento  FOREIGN KEY (id_evento)  REFERENCES evento (id_evento),
    CONSTRAINT fk_part_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
);

-- ---------------------------------------------------------------- CONSTANCIA
-- Nota: el proyecto tiene dos DAO de constancia; uno guarda el archivo como
-- BLOB (contenido_archivo) y el otro guarda solo la ruta (ruta_archivo).
-- La tabla incluye ambas columnas para que las dos versiones funcionen.
CREATE TABLE constancia (
    id_constancia      NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_participante    NUMBER NOT NULL,
    nombre_archivo     VARCHAR2(255),
    ruta_archivo       VARCHAR2(500),
    contenido_archivo  BLOB,
    content_type       VARCHAR2(150),
    tiene_vigencia     NUMBER(1) DEFAULT 0,
    fecha_vencimiento  DATE,
    fecha_subida       TIMESTAMP DEFAULT SYSTIMESTAMP,
    subido_por         NUMBER,
    CONSTRAINT fk_const_participante FOREIGN KEY (id_participante) REFERENCES participante_evento (id_participante)
);

-- -------------------------------------------------------------- PERIODO_CARGA
CREATE TABLE periodo_carga (
    id_periodo    NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_division   NUMBER NOT NULL,
    fecha_inicio  DATE,
    fecha_fin     DATE,
    activo        NUMBER(1) DEFAULT 1,
    creado_por    NUMBER,
    CONSTRAINT fk_periodo_division FOREIGN KEY (id_division) REFERENCES division (id_division)
);

-- --------------------------------------------------------- TOKEN_RECUPERACION
CREATE TABLE token_recuperacion (
    id_token           NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_usuario         NUMBER NOT NULL,
    codigo_token       VARCHAR2(50),
    utilizado          NUMBER(1) DEFAULT 0,
    fecha_expiracion   TIMESTAMP,
    CONSTRAINT fk_token_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
);
