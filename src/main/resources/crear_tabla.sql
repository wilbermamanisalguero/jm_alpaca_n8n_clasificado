USE jm_alpaca_clasificado;

/* ==========================================================
   YOJO LANAS - MODELO CLASIFICADO (MySQL)
   - Sin auditoría
   - SECCION y AGRUPACION como catálogos
   - CALIDAD mapea SECCION + AGRUPACION
   - CLASIFICADO_PESO: pesos[] por calidad (N filas)
   - CLASIFICADO_RESUMEN: total_kg por calidad (1 fila)
   - CLASIFICADO_DETALLE: totales por agrupación (1 fila)
   ========================================================== */

SET NAMES utf8mb4;

-- Si tu MySQL no tiene cargadas las tablas de zona horaria, 'America/Lima' puede fallar.
-- En ese caso usa '-05:00'.
 SET time_zone = 'America/Lima';
-- SET time_zone = '-05:00';

-- ==========================================================
-- LIMPIEZA (eliminar si existen)  -- (incluye CLASIFICADOR)
-- ==========================================================
DROP TABLE IF EXISTS CLASIFICADO_DETALLE;
DROP TABLE IF EXISTS CLASIFICADO_RESUMEN;
DROP TABLE IF EXISTS CLASIFICADO_PESO;
DROP TABLE IF EXISTS CLASIFICADO_ERROR_CARGA;
DROP TABLE IF EXISTS CLASIFICADO;
DROP TABLE IF EXISTS CLASIFICADOR;
DROP TABLE IF EXISTS CALIDAD;
DROP TABLE IF EXISTS AGRUPACION;
DROP TABLE IF EXISTS SECCION;

-- ==========================================================
-- CATALOGO: SECCION
-- ==========================================================
CREATE TABLE SECCION (
  ID_SECCION VARCHAR(20) NOT NULL,
  CONSTRAINT PK_SECCION PRIMARY KEY (ID_SECCION)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_spanish_ci;

INSERT INTO SECCION (ID_SECCION) VALUES
('HUACAYO'),
('SURI');

-- ==========================================================
-- CATALOGO: AGRUPACION (DETALLE)
-- ==========================================================
CREATE TABLE AGRUPACION (
  ID_AGRUPACION VARCHAR(30) NOT NULL,
  CONSTRAINT PK_AGRUPACION PRIMARY KEY (ID_AGRUPACION)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_spanish_ci;

INSERT INTO AGRUPACION (ID_AGRUPACION) VALUES
('ROYAL'),
('BL'),
('FS'),
('HZ'),
('STD'),
('SURI'),
('SURI-HZ'),
('SURI-STD');

-- ==========================================================
-- CATALOGO: CALIDAD (relaciona SECCION + AGRUPACION)
-- ==========================================================
CREATE TABLE CALIDAD (
  ID_CALIDAD    VARCHAR(30) NOT NULL,
  ID_SECCION    VARCHAR(20) NOT NULL,
  ID_AGRUPACION VARCHAR(30) NOT NULL,

  CONSTRAINT PK_CALIDAD PRIMARY KEY (ID_CALIDAD),

  CONSTRAINT FK_CALIDAD_SECCION
    FOREIGN KEY (ID_SECCION) REFERENCES SECCION(ID_SECCION),

  CONSTRAINT FK_CALIDAD_AGRUPACION
    FOREIGN KEY (ID_AGRUPACION) REFERENCES AGRUPACION(ID_AGRUPACION)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_spanish_ci;

INSERT INTO CALIDAD (ID_CALIDAD, ID_SECCION, ID_AGRUPACION) VALUES
-- HUACAYO
('ROYAL', 'HUACAYO', 'ROYAL'),
('BL-B',  'HUACAYO', 'BL'),
('BL-X',  'HUACAYO', 'BL'),
('FS-B',  'HUACAYO', 'FS'),
('FS-X',  'HUACAYO', 'FS'),
('HZ-B',  'HUACAYO', 'HZ'),
('HZ-X',  'HUACAYO', 'HZ'),
('AG',    'HUACAYO', 'HZ'),
('STD',   'HUACAYO', 'STD'),
-- SURI
('SURI-BL',  'SURI', 'SURI'),
('SURI-FS',  'SURI', 'SURI'),
('SURI-HZ',  'SURI', 'SURI-HZ'),
('SURI-STD', 'SURI', 'SURI-STD');

-- ==========================================================
-- TABLA: CLASIFICADOR
-- ==========================================================
CREATE TABLE CLASIFICADOR (
  ID_CLASIFICADOR VARCHAR(60) NOT NULL,
  CONSTRAINT PK_CLASIFICADOR PRIMARY KEY (ID_CLASIFICADOR)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_spanish_ci;

INSERT INTO CLASIFICADOR (ID_CLASIFICADOR) VALUES
('ROGER'),
('YOLA'),
('ISIDORA');

-- ==========================================================
-- TABLA: CLASIFICADO (cabecera)
-- ==========================================================
CREATE TABLE CLASIFICADO (
  ID_CLASIFICADO  VARCHAR(20) NOT NULL,
  ID_CLASIFICADOR VARCHAR(60) NOT NULL,
  FECHA           DATE NULL,
  IMPORTE_TOTAL   DECIMAL(14,2) NULL,
  OBSERVACIONES   VARCHAR(500) NULL,

  CONSTRAINT PK_CLASIFICADO PRIMARY KEY (ID_CLASIFICADO),

  CONSTRAINT FK_CP_ID_CLASIFICADOR
    FOREIGN KEY (ID_CLASIFICADOR)
    REFERENCES CLASIFICADOR(ID_CLASIFICADOR)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_spanish_ci;

-- ==========================================================
-- TABLA: CLASIFICADO_PESO (pesos[] por calidad)
-- ==========================================================
CREATE TABLE CLASIFICADO_PESO (
  ID_PESO        BIGINT NOT NULL AUTO_INCREMENT,
  ID_CLASIFICADO VARCHAR(20) NOT NULL,
  ID_CALIDAD     VARCHAR(30) NOT NULL,
  PESO_KG        DECIMAL(10,2) NOT NULL,

  CONSTRAINT PK_CLASIFICADO_PESO PRIMARY KEY (ID_PESO),

  CONSTRAINT FK_CP_CLASIFICADO
    FOREIGN KEY (ID_CLASIFICADO)
    REFERENCES CLASIFICADO(ID_CLASIFICADO)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,

  CONSTRAINT FK_CP_CALIDAD
    FOREIGN KEY (ID_CALIDAD)
    REFERENCES CALIDAD(ID_CALIDAD)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_spanish_ci;

-- ==========================================================
-- TABLA: CLASIFICADO_RESUMEN (total_kg por calidad)
-- ==========================================================
CREATE TABLE CLASIFICADO_RESUMEN (
  ID_CLASIFICADO VARCHAR(20) NOT NULL,
  ID_CALIDAD     VARCHAR(30) NOT NULL,
  TOTAL_KG       DECIMAL(10,2) NOT NULL,

  CONSTRAINT PK_CLASIFICADO_RESUMEN PRIMARY KEY (ID_CLASIFICADO, ID_CALIDAD),

  CONSTRAINT FK_CR_CLASIFICADO
    FOREIGN KEY (ID_CLASIFICADO)
    REFERENCES CLASIFICADO(ID_CLASIFICADO)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,

  CONSTRAINT FK_CR_CALIDAD
    FOREIGN KEY (ID_CALIDAD)
    REFERENCES CALIDAD(ID_CALIDAD)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_spanish_ci;

-- ==========================================================
-- TABLA: CLASIFICADO_DETALLE (totales por agrupación)
-- ==========================================================
CREATE TABLE CLASIFICADO_DETALLE (
  ID_CLASIFICADO   VARCHAR(20) NOT NULL,
  ID_AGRUPACION    VARCHAR(30) NOT NULL,
  TOTAL_KG         DECIMAL(10,2) NOT NULL,
  PRECIO_KG        DECIMAL(10,2) NOT NULL,
  SUBTOTAL_IMPORTE DECIMAL(12,2) NOT NULL,

  CONSTRAINT PK_CLASIFICADO_DETALLE PRIMARY KEY (ID_CLASIFICADO, ID_AGRUPACION),

  CONSTRAINT FK_CD_CLASIFICADO
    FOREIGN KEY (ID_CLASIFICADO)
    REFERENCES CLASIFICADO(ID_CLASIFICADO)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,

  CONSTRAINT FK_CD_AGRUPACION
    FOREIGN KEY (ID_AGRUPACION)
    REFERENCES AGRUPACION(ID_AGRUPACION)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_spanish_ci;

-- ==========================================================
-- TABLA: CLASIFICADO_ERROR_CARGA
-- ==========================================================
CREATE TABLE CLASIFICADO_ERROR_CARGA (
  ID_ERROR_CARGA  BIGINT NOT NULL AUTO_INCREMENT,
  ID_CLASIFICADO  VARCHAR(20) NOT NULL,
  CODIGO          VARCHAR(20) NOT NULL,
  ERROR_CARGA     VARCHAR(500) NOT NULL,

  CONSTRAINT PK_CLASIFICADO_ERROR_CARGA PRIMARY KEY (ID_ERROR_CARGA),

  CONSTRAINT FK_CEC_CLASIFICADO
    FOREIGN KEY (ID_CLASIFICADO)
    REFERENCES CLASIFICADO(ID_CLASIFICADO)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_spanish_ci;

-- ==========================================================
-- INDICES RECOMENDADOS
-- ==========================================================
CREATE INDEX IX_CALIDAD_SECCION   ON CALIDAD (ID_SECCION);
CREATE INDEX IX_CALIDAD_AGRUP     ON CALIDAD (ID_AGRUPACION);

CREATE INDEX IX_CP_CLASIF_CALIDAD ON CLASIFICADO_PESO (ID_CLASIFICADO, ID_CALIDAD);
CREATE INDEX IX_CR_ID_CALIDAD     ON CLASIFICADO_RESUMEN (ID_CALIDAD);
CREATE INDEX IX_CD_ID_AGRUPACION  ON CLASIFICADO_DETALLE (ID_AGRUPACION);

 

==================================================================================

Analiza la imagen proporcionada y devuelve ÚNICAMENTE un JSON válido.
NO incluyas texto adicional.
NO Markdown.
NO explicaciones.
NO comentarios.

OBJETIVO:
Extraer un CLASIFICADO (cabecera + calidad + detalle) desde una hoja escrita a mano.

==================================================
REGLAS CRÍTICAS (OBLIGATORIAS)
==================================================

1) La respuesta DEBE ser exclusivamente un JSON válido.
2) PROHIBIDO inferir, asumir o completar información no escrita explícitamente en la imagen.
3) Si un dato NO aparece de forma clara y explícita → su valor DEBE ser null.
4) PROHIBIDO generar valores por defecto (0, [], "", fechas, precios, etc.).
5) PROHIBIDO deducir o inventar:
   - clasificador
   - calidad
   - agrupación
   - precios
6) SOLO se permite el mapeo cuando el TEXTO COINCIDE EXACTAMENTE con los nombres definidos.
7) Si un cálculo depende de un dato inexistente → el resultado DEBE ser null.
8) No corrijas errores humanos del manuscrito.

9) SI UNA MISMA CALIDAD APARECE EN MÁS DE UN BLOQUE VISUAL,
   TODOS LOS VALORES VISIBLES DEBEN SER CONSIDERADOS
   COMO PARTE DE LA MISMA LISTA DE PESOS.

==================================================
PASO 1: IDENTIFICAR CABECERA
==================================================

CLASIFICADOR:
Reconocer SOLO si aparece explícitamente:
- ROGER
- YOLA
- ISIDORA

FECHA:
- dd-mm-yy o dd-m-yy → dd/mm/yyyy
- Año de 2 dígitos → 20yy

OBSERVACIONES:
- SOLO si hay texto descriptivo adicional

==================================================
PASO 2: IDENTIFICAR COLUMNAS DE CALIDAD
==================================================

Reconocer SOLO:

HUACAYO:
- ROYAL
- BL-B
- BL-X
- FS-B
- FS-X
- HZ-B
- HZ-X
- AG
- STD

SURI:
- SURI-BL
- SURI-FS
- SURI-HZ

==================================================
PASO 3: EXTRAER PESOS
==================================================

Para cada CALIDAD:

- Extraer TODOS los valores numéricos visibles
- Incluir valores aunque estén en bloques separados
- Mantener orden de arriba hacia abajo
- NO eliminar duplicados
- NO corregir
- NO completar

Ejemplo:
"pesos": [50,45,54,...,46,56,52,...]

==================================================
PASO 4: SUMA POR CALIDAD
==================================================

- total_kg = suma de TODOS los pesos visibles
- Si no hay pesos → total_kg = null

==================================================
PASO 5: AGRUPACIONES
==================================================

- BL → BL-B + BL-X
- FS → FS-B + FS-X
- HZ → HZ-B + HZ-X + AG
(usar SOLO datos existentes)

==================================================
PASO 6: PRECIOS
==================================================

- precio_kg SOLO si está escrito explícitamente
- NO inferir desde multiplicaciones

==================================================
PASO 7: IMPORTE TOTAL
==================================================

- importe_total = suma de subtotales válidos
- Si no existen → null

   

{
  "clasificado": {
    "clasificador": null,
    "fecha": null,
    "observaciones": null,
    "importe_total": null
  },
  "clasificado_calidad": {
    "ROYAL": { "pesos": [], "total_kg": null },
    "BL-B": { "pesos": [], "total_kg": null },
    "BL-X": { "pesos": [], "total_kg": null },
    "FS-B": { "pesos": [], "total_kg": null },
    "FS-X": { "pesos": [], "total_kg": null },
    "HZ-B": { "pesos": [], "total_kg": null },
    "HZ-X": { "pesos": [], "total_kg": null },
    "AG": { "pesos": [], "total_kg": null },
    "STD": { "pesos": [], "total_kg": null },
    "SURI-BL": { "pesos": [], "total_kg": null },
    "SURI-FS": { "pesos": [], "total_kg": null },
    "SURI-HZ": { "pesos": [], "total_kg": null }
  },
  "clasificado_detalle": {
    "ROYAL": {
      "componentes": ["ROYAL"],
      "total_kg": null,
      "precio_kg": null,
      "sub_total_importe": null
    },
    "BL": {
      "componentes": ["BL-B", "BL-X"],
      "total_kg": null,
      "precio_kg": null,
      "sub_total_importe": null
    },
    "FS": {
      "componentes": ["FS-B", "FS-X"],
      "total_kg": null,
      "precio_kg": null,
      "sub_total_importe": null
    },
    "HZ": {
      "componentes": ["HZ-B", "HZ-X", "AG"],
      "total_kg": null,
      "precio_kg": null,
      "sub_total_importe": null
    },
    "STD": {
      "componentes": ["STD"],
      "total_kg": null,
      "precio_kg": null,
      "sub_total_importe": null
    },
    "SURI": {
      "componentes": ["SURI-BL", "SURI-FS"],
      "total_kg": null,
      "precio_kg": null,
      "sub_total_importe": null
    },
    "SURI-HZ": {
      "componentes": ["SURI-HZ"],
      "total_kg": null,
      "precio_kg": null,
      "sub_total_importe": null
    }
  }
}