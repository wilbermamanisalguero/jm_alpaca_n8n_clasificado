# API Documentation - Clasificado Endpoint

## Endpoint: POST /api/clasificado/registrar

### URL
```
POST http://localhost:8083/api/clasificado/registrar
```

### Headers
```
Content-Type: application/json
```

### Request Body Structure

```json
{
  "clasificado": {
    "nombreArchivo": "CLS-2024-001",
    "clasificador": "ROGER",
    "fecha": "2024-01-15",
    "observaciones": "Primera clasificación del mes",
    "importe_total": 15500.50
  },
  "clasificado_calidad": {
    "ROYAL": { "pesos": [10.5, 20.3, 15.2], "total_kg": 46.0 },
    "BL-B": { "pesos": [12.0, 18.5], "total_kg": 30.5 },
    "BL-X": { "pesos": [8.5, 11.5], "total_kg": 20.0 },
    "FS-B": { "pesos": [15.0], "total_kg": 15.0 },
    "FS-X": { "pesos": [10.0], "total_kg": 10.0 },
    "HZ-B": { "pesos": [5.0], "total_kg": 5.0 },
    "HZ-X": { "pesos": [7.5], "total_kg": 7.5 },
    "AG": { "pesos": [2.5], "total_kg": 2.5 },
    "STD": { "pesos": [20.0], "total_kg": 20.0 },
    "SURI-BL": { "pesos": [12.0], "total_kg": 12.0 },
    "SURI-FS": { "pesos": [8.0], "total_kg": 8.0 },
    "SURI-HZ": { "pesos": [6.0], "total_kg": 6.0 }
  },
  "clasificado_detalle": {
    "ROYAL": {
      "componentes": ["ROYAL"],
      "total_kg": 46.0,
      "precio_kg": 150.00,
      "sub_total_importe": 6900.00
    },
    "BL": {
      "componentes": ["BL-B", "BL-X"],
      "total_kg": 50.5,
      "precio_kg": 120.00,
      "sub_total_importe": 6060.00
    },
    "FS": {
      "componentes": ["FS-B", "FS-X"],
      "total_kg": 25.0,
      "precio_kg": 100.00,
      "sub_total_importe": 2500.00
    },
    "HZ": {
      "componentes": ["HZ-B", "HZ-X", "AG"],
      "total_kg": 15.0,
      "precio_kg": 10.50,
      "sub_total_importe": 157.50
    },
    "STD": {
      "componentes": ["STD"],
      "total_kg": 20.0,
      "precio_kg": 8.00,
      "sub_total_importe": 160.00
    },
    "SURI": {
      "componentes": ["SURI-BL", "SURI-FS"],
      "total_kg": 20.0,
      "precio_kg": 85.00,
      "sub_total_importe": 1700.00
    },
    "SURI-HZ": {
      "componentes": ["SURI-HZ"],
      "total_kg": 6.0,
      "precio_kg": 12.00,
      "sub_total_importe": 72.00
    }
  }
}
```

### Validaciones Automáticas

El endpoint realiza las siguientes validaciones:

1. **ERROR_PESO**: La suma de los pesos de `clasificado_calidad.pesos` debe ser igual a `clasificado_calidad.total_kg`

2. **ERROR_TOTAL_KG**: El `total_kg` de `clasificado_calidad` debe ser igual al `total_kg` en `clasificado_detalle` según los componentes:
   - ROYAL = ROYAL
   - BL = BL-B + BL-X
   - FS = FS-B + FS-X
   - HZ = HZ-B + HZ-X + AG
   - STD = STD
   - SURI = SURI-BL + SURI-FS
   - SURI-HZ = SURI-HZ

3. **ERROR_IMPORTE**: El `total_kg * precio_kg` debe ser igual a `sub_total_importe` en cada agrupación

4. **ERROR_IMPORTE_TOTAL**: La suma de todos los `sub_total_importe` debe ser igual a `clasificado.importe_total`

### Response Structure

#### Success (sin errores de validación)
```json
{
  "success": true,
  "message": "Clasificado registrado exitosamente",
  "idClasificado": "CLS-2024-001"
}
```

#### Success con errores de validación
```json
{
  "success": false,
  "message": "Clasificado registrado con errores de validación",
  "idClasificado": "CLS-2024-001",
  "errors": [
    {
      "codigo": "ERROR_PESO",
      "errorCarga": "{\"ROYAL\":{\"pesos\":[10.5,20.3,15.2],\"total_kg\":46.0}}"
    }
  ]
}
```

#### Error del servidor
```json
{
  "success": false,
  "message": "Error al procesar el clasificado",
  "error": "Descripción del error"
}
```

### Mapeo con Base de Datos

- **CLASIFICADO**: Tabla principal
  - `nombreArchivo` → `ID_CLASIFICADO`
  - `clasificador` → `ID_CLASIFICADOR`
  - `fecha` → `FECHA`
  - `importe_total` → `IMPORTE_TOTAL`

- **CLASIFICADO_PESO**: Pesos individuales por calidad
  - `clasificado_calidad.[CALIDAD].pesos[]` → `PESO_KG` (múltiples filas)

- **CLASIFICADO_RESUMEN**: Totales por calidad
  - `clasificado_calidad.[CALIDAD].total_kg` → `TOTAL_KG`

- **CLASIFICADO_DETALLE**: Totales por agrupación
  - `clasificado_detalle.[AGRUPACION].total_kg` → `TOTAL_KG`
  - `clasificado_detalle.[AGRUPACION].precio_kg` → `PRECIO_KG`
  - `clasificado_detalle.[AGRUPACION].sub_total_importe` → `SUBTOTAL_IMPORTE`

- **CLASIFICADO_ERROR_CARGA**: Errores de validación
  - Se guardan automáticamente cuando hay errores

### Cómo ejecutar

1. Asegúrate de tener MySQL corriendo en `206.81.5.2:3307`
2. Ejecuta el script SQL de creación de tablas
3. Inicia la aplicación Spring Boot:
   ```bash
   mvn spring-boot:run
   ```
4. El servidor estará disponible en `http://localhost:8083`

### Ejemplo con curl

```bash
curl -X POST http://localhost:8083/api/clasificado/registrar \
  -H "Content-Type: application/json" \
  -d @clasificado-request.json
```
