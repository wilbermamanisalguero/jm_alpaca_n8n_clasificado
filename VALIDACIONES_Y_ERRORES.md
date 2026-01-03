# Validaciones y Manejo de Errores - API Clasificado

## Resumen de Validaciones

La API valida 4 tipos de errores principales que se registran en la tabla `CLASIFICADO_ERROR_CARGA`:

### 1. ERROR_PESO
**Validación:** La suma de los pesos individuales debe ser igual al total_kg de cada calidad.

**Fórmula:**
```
SUM(clasificado_calidad.[CALIDAD].pesos[]) = clasificado_calidad.[CALIDAD].total_kg
```

**Ejemplo de Error:**
```json
{
  "clasificado_calidad": {
    "ROYAL": {
      "pesos": [10.0, 15.0, 8.0],
      "total_kg": 50.0
    }
  }
}
```
- Suma: 10.0 + 15.0 + 8.0 = 33.0
- Total_kg: 50.0
- **ERROR:** 33.0 ≠ 50.0

**Registro en BD:**
```sql
INSERT INTO CLASIFICADO_ERROR_CARGA (ID_CLASIFICADO, CODIGO, ERROR_CARGA)
VALUES (
  [ID_CLASIFICADO],
  'ERROR_PESO',
  '{
    "clasificado_calidad": {
      "ROYAL": {
        "pesos": [10.0, 15.0, 8.0],
        "total_kg": 50.0,
        "suma_pesos": 33.0,
        "diferencia": -17.0
      }
    }
  }'
);
```

---

### 2. ERROR_TOTAL_KG
**Validación:** La suma de los total_kg de las calidades componentes debe ser igual al total_kg del detalle.

**Fórmula:**
```
Para BL: clasificado_calidad.BL-B.total_kg + clasificado_calidad.BL-X.total_kg = clasificado_detalle.BL.total_kg
Para FS: clasificado_calidad.FS-B.total_kg + clasificado_calidad.FS-X.total_kg = clasificado_detalle.FS.total_kg
Para HZ: clasificado_calidad.HZ-B.total_kg + clasificado_calidad.HZ-X.total_kg + clasificado_calidad.AG.total_kg = clasificado_detalle.HZ.total_kg
Para SURI: clasificado_calidad.SURI-BL.total_kg + clasificado_calidad.SURI-FS.total_kg = clasificado_detalle.SURI.total_kg
Para ROYAL: clasificado_calidad.ROYAL.total_kg = clasificado_detalle.ROYAL.total_kg
Para STD: clasificado_calidad.STD.total_kg = clasificado_detalle.STD.total_kg
Para SURI-HZ: clasificado_calidad.SURI-HZ.total_kg = clasificado_detalle.SURI-HZ.total_kg
```

**Ejemplo de Error:**
```json
{
  "clasificado_calidad": {
    "BL-B": { "total_kg": 15.0 },
    "BL-X": { "total_kg": 12.0 }
  },
  "clasificado_detalle": {
    "BL": { "total_kg": 50.0 }
  }
}
```
- Suma calidades: 15.0 + 12.0 = 27.0
- Total detalle: 50.0
- **ERROR:** 27.0 ≠ 50.0

**Registro en BD:**
```sql
INSERT INTO CLASIFICADO_ERROR_CARGA (ID_CLASIFICADO, CODIGO, ERROR_CARGA)
VALUES (
  [ID_CLASIFICADO],
  'ERROR_TOTAL_KG',
  '{
    "agrupacion": "BL",
    "clasificado_calidad": {
      "BL-B": { "total_kg": 15.0 },
      "BL-X": { "total_kg": 12.0 },
      "suma_componentes": 27.0
    },
    "clasificado_detalle": {
      "BL": { "total_kg": 50.0 }
    },
    "diferencia": -23.0
  }'
);
```

---

### 3. ERROR_IMPORTE
**Validación:** El producto de total_kg * precio_kg debe ser igual a sub_total_importe para cada agrupación.

**Fórmula:**
```
clasificado_detalle.[AGRUPACION].total_kg * clasificado_detalle.[AGRUPACION].precio_kg = clasificado_detalle.[AGRUPACION].sub_total_importe
```

**Ejemplo de Error:**
```json
{
  "clasificado_detalle": {
    "ROYAL": {
      "total_kg": 25.0,
      "precio_kg": 120.00,
      "sub_total_importe": 5000.00
    }
  }
}
```
- Cálculo: 25.0 * 120.00 = 3000.00
- Sub_total_importe: 5000.00
- **ERROR:** 3000.00 ≠ 5000.00

**Registro en BD:**
```sql
INSERT INTO CLASIFICADO_ERROR_CARGA (ID_CLASIFICADO, CODIGO, ERROR_CARGA)
VALUES (
  [ID_CLASIFICADO],
  'ERROR_IMPORTE',
  '{
    "clasificado_detalle": {
      "ROYAL": {
        "total_kg": 25.0,
        "precio_kg": 120.00,
        "sub_total_importe": 5000.00,
        "calculo_correcto": 3000.00,
        "diferencia": 2000.00
      }
    }
  }'
);
```

---

### 4. ERROR_IMPORTE_TOTAL
**Validación:** La suma de todos los sub_total_importe debe ser igual al importe_total del clasificado.

**Fórmula:**
```
SUM(clasificado_detalle.[TODAS_AGRUPACIONES].sub_total_importe) = clasificado.importe_total
```

**Ejemplo de Error:**
```json
{
  "clasificado": {
    "importe_total": 20000.00
  },
  "clasificado_detalle": {
    "ROYAL": { "sub_total_importe": 3000.00 },
    "BL": { "sub_total_importe": 2700.00 },
    "FS": { "sub_total_importe": 1250.00 },
    "HZ": { "sub_total_importe": 400.00 },
    "STD": { "sub_total_importe": 300.00 },
    "SURI": { "sub_total_importe": 0.00 },
    "SURI-HZ": { "sub_total_importe": 0.00 }
  }
}
```
- Suma sub_totales: 3000 + 2700 + 1250 + 400 + 300 = 7650.00
- Importe_total: 20000.00
- **ERROR:** 7650.00 ≠ 20000.00

**Registro en BD:**
```sql
INSERT INTO CLASIFICADO_ERROR_CARGA (ID_CLASIFICADO, CODIGO, ERROR_CARGA)
VALUES (
  [ID_CLASIFICADO],
  'ERROR_IMPORTE_TOTAL',
  '{
    "clasificado": {
      "importe_total": 20000.00
    },
    "clasificado_detalle": {
      "ROYAL": { "sub_total_importe": 3000.00 },
      "BL": { "sub_total_importe": 2700.00 },
      "FS": { "sub_total_importe": 1250.00 },
      "HZ": { "sub_total_importe": 400.00 },
      "STD": { "sub_total_importe": 300.00 },
      "SURI": { "sub_total_importe": 0.00 },
      "SURI-HZ": { "sub_total_importe": 0.00 }
    },
    "suma_sub_totales": 7650.00,
    "diferencia": -12350.00
  }'
);
```

---

## Mapeo de Componentes

### Agrupaciones y sus Componentes

| Agrupación (Detalle) | Componentes (Calidad)      |
|----------------------|----------------------------|
| ROYAL                | ROYAL                      |
| BL                   | BL-B, BL-X                 |
| FS                   | FS-B, FS-X                 |
| HZ                   | HZ-B, HZ-X, AG             |
| STD                  | STD                        |
| SURI                 | SURI-BL, SURI-FS           |
| SURI-HZ              | SURI-HZ                    |

---

## Estructura de Respuesta de la API

### Respuesta Exitosa (HTTP 200)

```json
{
  "success": true,
  "message": "Clasificado registrado exitosamente",
  "idClasificado": 123,
  "data": {
    "nombreArchivo": "CLASIFICADO_001.xlsx",
    "clasificador": "JUAN_PEREZ",
    "fecha": "2025-01-03",
    "importe_total": 15000.00
  }
}
```

### Respuesta con Errores de Validación (HTTP 400)

```json
{
  "success": false,
  "message": "Se encontraron errores de validación en el clasificado",
  "idClasificado": 124,
  "errores": [
    {
      "codigo": "ERROR_PESO",
      "mensaje": "La suma de pesos no coincide con el total_kg",
      "detalles": {
        "calidad": "ROYAL",
        "suma_pesos": 33.0,
        "total_kg": 50.0,
        "diferencia": -17.0
      }
    },
    {
      "codigo": "ERROR_TOTAL_KG",
      "mensaje": "El total de calidades no coincide con el total del detalle",
      "detalles": {
        "agrupacion": "BL",
        "suma_calidades": 27.0,
        "total_detalle": 50.0,
        "diferencia": -23.0
      }
    }
  ]
}
```

### Respuesta de Error del Servidor (HTTP 500)

```json
{
  "success": false,
  "message": "Error interno al procesar el clasificado",
  "error": "Mensaje de error técnico",
  "timestamp": "2025-01-03T10:30:00"
}
```

---

## Tolerancia para Comparaciones Decimales

Para las validaciones de importes, se recomienda usar una tolerancia de **0.01** debido a redondeos:

```java
BigDecimal TOLERANCIA = new BigDecimal("0.01");

// Comparación con tolerancia
boolean sonIguales = calculado.subtract(esperado).abs().compareTo(TOLERANCIA) <= 0;
```

---

## Orden de Validaciones

Las validaciones deben ejecutarse en el siguiente orden:

1. **ERROR_PESO**: Validar cada calidad individualmente
2. **ERROR_TOTAL_KG**: Validar agrupaciones (requiere que pesos sean correctos)
3. **ERROR_IMPORTE**: Validar cálculos de importes por agrupación
4. **ERROR_IMPORTE_TOTAL**: Validar importe total general

Si se encuentran múltiples errores, se deben registrar **todos** en `CLASIFICADO_ERROR_CARGA`, no solo el primero.

---

## Tabla: CLASIFICADO_ERROR_CARGA

### Estructura

```sql
CREATE TABLE CLASIFICADO_ERROR_CARGA (
    ID_ERROR_CARGA INT PRIMARY KEY AUTO_INCREMENT,
    ID_CLASIFICADO INT NOT NULL,
    CODIGO VARCHAR(50) NOT NULL,
    ERROR_CARGA TEXT NOT NULL,
    FECHA_REGISTRO TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ID_CLASIFICADO) REFERENCES CLASIFICADO(ID_CLASIFICADO)
);
```

### Valores posibles para CODIGO

- `ERROR_PESO`
- `ERROR_TOTAL_KG`
- `ERROR_IMPORTE`
- `ERROR_IMPORTE_TOTAL`

### Campo ERROR_CARGA

Debe contener un JSON con:
- Los datos que causaron el error
- Los valores calculados
- La diferencia encontrada
- Información adicional para debugging

---

## Ejemplo Completo de Flujo con Error

### Request

```bash
curl -X POST http://localhost:8083/api/clasificado/registrar \
  -H "Content-Type: application/json" \
  -d '{...datos con error...}'
```

### Proceso en el Backend

1. Recibir el request
2. Validar estructura básica (DTO validation)
3. Insertar en `CLASIFICADO` (genera ID_CLASIFICADO)
4. Insertar en `CLASIFICADO_RESUMEN`
5. Insertar en `CLASIFICADO_PESO`
6. Insertar en `CLASIFICADO_DETALLE`
7. **Ejecutar validaciones**
8. Si hay errores:
   - Insertar en `CLASIFICADO_ERROR_CARGA` (uno por cada error)
   - Responder HTTP 400 con lista de errores
9. Si no hay errores:
   - Responder HTTP 200 con éxito

### Response (con errores)

```json
{
  "success": false,
  "message": "Se encontraron 4 errores de validación",
  "idClasificado": 125,
  "errores": [
    {
      "codigo": "ERROR_PESO",
      "mensaje": "Error en suma de pesos",
      "detalles": {...}
    },
    {
      "codigo": "ERROR_TOTAL_KG",
      "mensaje": "Error en total de kilogramos",
      "detalles": {...}
    },
    {
      "codigo": "ERROR_IMPORTE",
      "mensaje": "Error en cálculo de importe",
      "detalles": {...}
    },
    {
      "codigo": "ERROR_IMPORTE_TOTAL",
      "mensaje": "Error en importe total",
      "detalles": {...}
    }
  ]
}
```

### Datos en CLASIFICADO_ERROR_CARGA

```sql
SELECT * FROM CLASIFICADO_ERROR_CARGA WHERE ID_CLASIFICADO = 125;

+----------------+----------------+---------------------+--------------------+---------------------+
| ID_ERROR_CARGA | ID_CLASIFICADO | CODIGO              | ERROR_CARGA        | FECHA_REGISTRO      |
+----------------+----------------+---------------------+--------------------+---------------------+
| 1              | 125            | ERROR_PESO          | {...json...}       | 2025-01-03 10:30:00 |
| 2              | 125            | ERROR_TOTAL_KG      | {...json...}       | 2025-01-03 10:30:00 |
| 3              | 125            | ERROR_IMPORTE       | {...json...}       | 2025-01-03 10:30:00 |
| 4              | 125            | ERROR_IMPORTE_TOTAL | {...json...}       | 2025-01-03 10:30:00 |
+----------------+----------------+---------------------+--------------------+---------------------+
```
