# Ejemplos de CURL para API de Clasificado

## Endpoint Base
- URL: `http://localhost:8083/api/clasificado/registrar`
- Método: `POST`
- Content-Type: `application/json`

---

## 1. CASO EXITOSO - Todos los datos correctos

```bash
curl -X POST http://localhost:8083/api/clasificado/registrar \
  -H "Content-Type: application/json" \
  -d '{
  "clasificado": {
    "nombreArchivo": "CLASIFICADO_001.xlsx",
    "clasificador": "JUAN_PEREZ",
    "fecha": "2025-01-03",
    "observaciones": "Clasificación regular sin observaciones",
    "importe_total": 15000.00
  },
  "clasificado_calidad": {
    "ROYAL": {
      "pesos": [10.5, 12.3, 8.7],
      "total_kg": 31.5
    },
    "BL-B": {
      "pesos": [15.2, 18.8],
      "total_kg": 34.0
    },
    "BL-X": {
      "pesos": [20.0, 15.0],
      "total_kg": 35.0
    },
    "FS-B": {
      "pesos": [12.5, 10.5],
      "total_kg": 23.0
    },
    "FS-X": {
      "pesos": [8.0, 9.0],
      "total_kg": 17.0
    },
    "HZ-B": {
      "pesos": [5.0, 7.0],
      "total_kg": 12.0
    },
    "HZ-X": {
      "pesos": [6.0, 8.0],
      "total_kg": 14.0
    },
    "AG": {
      "pesos": [4.0],
      "total_kg": 4.0
    },
    "STD": {
      "pesos": [10.0, 15.0],
      "total_kg": 25.0
    },
    "SURI-BL": {
      "pesos": [3.0, 2.0],
      "total_kg": 5.0
    },
    "SURI-FS": {
      "pesos": [2.5, 2.5],
      "total_kg": 5.0
    },
    "SURI-HZ": {
      "pesos": [1.0, 2.0],
      "total_kg": 3.0
    }
  },
  "clasificado_detalle": {
    "ROYAL": {
      "componentes": ["ROYAL"],
      "total_kg": 31.5,
      "precio_kg": 150.00,
      "sub_total_importe": 4725.00
    },
    "BL": {
      "componentes": ["BL-B", "BL-X"],
      "total_kg": 69.0,
      "precio_kg": 80.00,
      "sub_total_importe": 5520.00
    },
    "FS": {
      "componentes": ["FS-B", "FS-X"],
      "total_kg": 40.0,
      "precio_kg": 60.00,
      "sub_total_importe": 2400.00
    },
    "HZ": {
      "componentes": ["HZ-B", "HZ-X", "AG"],
      "total_kg": 30.0,
      "precio_kg": 45.00,
      "sub_total_importe": 1350.00
    },
    "STD": {
      "componentes": ["STD"],
      "total_kg": 25.0,
      "precio_kg": 35.00,
      "sub_total_importe": 875.00
    },
    "SURI": {
      "componentes": ["SURI-BL", "SURI-FS"],
      "total_kg": 10.0,
      "precio_kg": 90.00,
      "sub_total_importe": 900.00
    },
    "SURI-HZ": {
      "componentes": ["SURI-HZ"],
      "total_kg": 3.0,
      "precio_kg": 76.67,
      "sub_total_importe": 230.00
    }
  }
}'
```

**Validaciones que pasan:**
- ✓ Suma de pesos de ROYAL: 10.5 + 12.3 + 8.7 = 31.5 (igual a total_kg)
- ✓ Total de calidad BL (BL-B + BL-X): 34.0 + 35.0 = 69.0 (igual a detalle BL)
- ✓ Importe de ROYAL: 31.5 * 150.00 = 4725.00 (igual a sub_total_importe)
- ✓ Suma de sub_total_importe: 4725 + 5520 + 2400 + 1350 + 875 + 900 + 230 = 15000.00 (igual a importe_total)

---

## 2. ERROR_PESO - Suma de pesos no coincide con total_kg

```bash
curl -X POST http://localhost:8083/api/clasificado/registrar \
  -H "Content-Type: application/json" \
  -d '{
  "clasificado": {
    "nombreArchivo": "CLASIFICADO_ERROR_001.xlsx",
    "clasificador": "MARIA_LOPEZ",
    "fecha": "2025-01-03",
    "observaciones": "Error en peso de ROYAL",
    "importe_total": 5000.00
  },
  "clasificado_calidad": {
    "ROYAL": {
      "pesos": [10.0, 15.0, 8.0],
      "total_kg": 50.0
    },
    "BL-B": {
      "pesos": [20.0],
      "total_kg": 20.0
    },
    "BL-X": {
      "pesos": [15.0],
      "total_kg": 15.0
    },
    "FS-B": {
      "pesos": [],
      "total_kg": 0.0
    },
    "FS-X": {
      "pesos": [],
      "total_kg": 0.0
    },
    "HZ-B": {
      "pesos": [],
      "total_kg": 0.0
    },
    "HZ-X": {
      "pesos": [],
      "total_kg": 0.0
    },
    "AG": {
      "pesos": [],
      "total_kg": 0.0
    },
    "STD": {
      "pesos": [15.0],
      "total_kg": 15.0
    },
    "SURI-BL": {
      "pesos": [],
      "total_kg": 0.0
    },
    "SURI-FS": {
      "pesos": [],
      "total_kg": 0.0
    },
    "SURI-HZ": {
      "pesos": [],
      "total_kg": 0.0
    }
  },
  "clasificado_detalle": {
    "ROYAL": {
      "componentes": ["ROYAL"],
      "total_kg": 50.0,
      "precio_kg": 80.00,
      "sub_total_importe": 4000.00
    },
    "BL": {
      "componentes": ["BL-B", "BL-X"],
      "total_kg": 35.0,
      "precio_kg": 60.00,
      "sub_total_importe": 2100.00
    },
    "FS": {
      "componentes": ["FS-B", "FS-X"],
      "total_kg": 0.0,
      "precio_kg": 0.00,
      "sub_total_importe": 0.00
    },
    "HZ": {
      "componentes": ["HZ-B", "HZ-X", "AG"],
      "total_kg": 0.0,
      "precio_kg": 0.00,
      "sub_total_importe": 0.00
    },
    "STD": {
      "componentes": ["STD"],
      "total_kg": 15.0,
      "precio_kg": 60.00,
      "sub_total_importe": 900.00
    },
    "SURI": {
      "componentes": ["SURI-BL", "SURI-FS"],
      "total_kg": 0.0,
      "precio_kg": 0.00,
      "sub_total_importe": 0.00
    },
    "SURI-HZ": {
      "componentes": ["SURI-HZ"],
      "total_kg": 0.0,
      "precio_kg": 0.00,
      "sub_total_importe": 0.00
    }
  }
}'
```

**Error esperado:** `ERROR_PESO`
- ✗ ROYAL: suma de pesos (10.0 + 15.0 + 8.0 = 33.0) ≠ total_kg (50.0)

---

## 3. ERROR_TOTAL_KG - Total de calidad no coincide con detalle

```bash
curl -X POST http://localhost:8083/api/clasificado/registrar \
  -H "Content-Type: application/json" \
  -d '{
  "clasificado": {
    "nombreArchivo": "CLASIFICADO_ERROR_002.xlsx",
    "clasificador": "CARLOS_RAMIREZ",
    "fecha": "2025-01-03",
    "observaciones": "Error en total_kg de BL",
    "importe_total": 6000.00
  },
  "clasificado_calidad": {
    "ROYAL": {
      "pesos": [20.0],
      "total_kg": 20.0
    },
    "BL-B": {
      "pesos": [10.0, 5.0],
      "total_kg": 15.0
    },
    "BL-X": {
      "pesos": [12.0],
      "total_kg": 12.0
    },
    "FS-B": {
      "pesos": [18.0],
      "total_kg": 18.0
    },
    "FS-X": {
      "pesos": [15.0],
      "total_kg": 15.0
    },
    "HZ-B": {
      "pesos": [],
      "total_kg": 0.0
    },
    "HZ-X": {
      "pesos": [],
      "total_kg": 0.0
    },
    "AG": {
      "pesos": [],
      "total_kg": 0.0
    },
    "STD": {
      "pesos": [20.0],
      "total_kg": 20.0
    },
    "SURI-BL": {
      "pesos": [],
      "total_kg": 0.0
    },
    "SURI-FS": {
      "pesos": [],
      "total_kg": 0.0
    },
    "SURI-HZ": {
      "pesos": [],
      "total_kg": 0.0
    }
  },
  "clasificado_detalle": {
    "ROYAL": {
      "componentes": ["ROYAL"],
      "total_kg": 20.0,
      "precio_kg": 100.00,
      "sub_total_importe": 2000.00
    },
    "BL": {
      "componentes": ["BL-B", "BL-X"],
      "total_kg": 50.0,
      "precio_kg": 50.00,
      "sub_total_importe": 2500.00
    },
    "FS": {
      "componentes": ["FS-B", "FS-X"],
      "total_kg": 33.0,
      "precio_kg": 45.00,
      "sub_total_importe": 1485.00
    },
    "HZ": {
      "componentes": ["HZ-B", "HZ-X", "AG"],
      "total_kg": 0.0,
      "precio_kg": 0.00,
      "sub_total_importe": 0.00
    },
    "STD": {
      "componentes": ["STD"],
      "total_kg": 20.0,
      "precio_kg": 25.00,
      "sub_total_importe": 500.00
    },
    "SURI": {
      "componentes": ["SURI-BL", "SURI-FS"],
      "total_kg": 0.0,
      "precio_kg": 0.00,
      "sub_total_importe": 0.00
    },
    "SURI-HZ": {
      "componentes": ["SURI-HZ"],
      "total_kg": 0.0,
      "precio_kg": 0.00,
      "sub_total_importe": 0.00
    }
  }
}'
```

**Error esperado:** `ERROR_TOTAL_KG`
- ✗ BL: suma de calidades (BL-B: 15.0 + BL-X: 12.0 = 27.0) ≠ total_kg en detalle (50.0)
- ✗ FS: suma de calidades (FS-B: 18.0 + FS-X: 15.0 = 33.0) = total_kg en detalle (33.0) ✓

---

## 4. ERROR_IMPORTE - Cálculo de sub_total_importe incorrecto

```bash
curl -X POST http://localhost:8083/api/clasificado/registrar \
  -H "Content-Type: application/json" \
  -d '{
  "clasificado": {
    "nombreArchivo": "CLASIFICADO_ERROR_003.xlsx",
    "clasificador": "ANA_TORRES",
    "fecha": "2025-01-03",
    "observaciones": "Error en cálculo de importe de ROYAL",
    "importe_total": 8000.00
  },
  "clasificado_calidad": {
    "ROYAL": {
      "pesos": [25.0],
      "total_kg": 25.0
    },
    "BL-B": {
      "pesos": [20.0],
      "total_kg": 20.0
    },
    "BL-X": {
      "pesos": [15.0],
      "total_kg": 15.0
    },
    "FS-B": {
      "pesos": [10.0],
      "total_kg": 10.0
    },
    "FS-X": {
      "pesos": [5.0],
      "total_kg": 5.0
    },
    "HZ-B": {
      "pesos": [8.0],
      "total_kg": 8.0
    },
    "HZ-X": {
      "pesos": [7.0],
      "total_kg": 7.0
    },
    "AG": {
      "pesos": [],
      "total_kg": 0.0
    },
    "STD": {
      "pesos": [10.0],
      "total_kg": 10.0
    },
    "SURI-BL": {
      "pesos": [],
      "total_kg": 0.0
    },
    "SURI-FS": {
      "pesos": [],
      "total_kg": 0.0
    },
    "SURI-HZ": {
      "pesos": [],
      "total_kg": 0.0
    }
  },
  "clasificado_detalle": {
    "ROYAL": {
      "componentes": ["ROYAL"],
      "total_kg": 25.0,
      "precio_kg": 120.00,
      "sub_total_importe": 5000.00
    },
    "BL": {
      "componentes": ["BL-B", "BL-X"],
      "total_kg": 35.0,
      "precio_kg": 70.00,
      "sub_total_importe": 2450.00
    },
    "FS": {
      "componentes": ["FS-B", "FS-X"],
      "total_kg": 15.0,
      "precio_kg": 55.00,
      "sub_total_importe": 825.00
    },
    "HZ": {
      "componentes": ["HZ-B", "HZ-X", "AG"],
      "total_kg": 15.0,
      "precio_kg": 40.00,
      "sub_total_importe": 600.00
    },
    "STD": {
      "componentes": ["STD"],
      "total_kg": 10.0,
      "precio_kg": 30.00,
      "sub_total_importe": 300.00
    },
    "SURI": {
      "componentes": ["SURI-BL", "SURI-FS"],
      "total_kg": 0.0,
      "precio_kg": 0.00,
      "sub_total_importe": 0.00
    },
    "SURI-HZ": {
      "componentes": ["SURI-HZ"],
      "total_kg": 0.0,
      "precio_kg": 0.00,
      "sub_total_importe": 0.00
    }
  }
}'
```

**Error esperado:** `ERROR_IMPORTE`
- ✗ ROYAL: total_kg * precio_kg (25.0 * 120.00 = 3000.00) ≠ sub_total_importe (5000.00)

---

## 5. ERROR_IMPORTE_TOTAL - Suma de sub_total_importe no coincide con importe_total

```bash
curl -X POST http://localhost:8083/api/clasificado/registrar \
  -H "Content-Type: application/json" \
  -d '{
  "clasificado": {
    "nombreArchivo": "CLASIFICADO_ERROR_004.xlsx",
    "clasificador": "PEDRO_SANCHEZ",
    "fecha": "2025-01-03",
    "observaciones": "Error en importe total general",
    "importe_total": 20000.00
  },
  "clasificado_calidad": {
    "ROYAL": {
      "pesos": [30.0],
      "total_kg": 30.0
    },
    "BL-B": {
      "pesos": [25.0],
      "total_kg": 25.0
    },
    "BL-X": {
      "pesos": [20.0],
      "total_kg": 20.0
    },
    "FS-B": {
      "pesos": [15.0],
      "total_kg": 15.0
    },
    "FS-X": {
      "pesos": [10.0],
      "total_kg": 10.0
    },
    "HZ-B": {
      "pesos": [5.0],
      "total_kg": 5.0
    },
    "HZ-X": {
      "pesos": [5.0],
      "total_kg": 5.0
    },
    "AG": {
      "pesos": [],
      "total_kg": 0.0
    },
    "STD": {
      "pesos": [10.0],
      "total_kg": 10.0
    },
    "SURI-BL": {
      "pesos": [],
      "total_kg": 0.0
    },
    "SURI-FS": {
      "pesos": [],
      "total_kg": 0.0
    },
    "SURI-HZ": {
      "pesos": [],
      "total_kg": 0.0
    }
  },
  "clasificado_detalle": {
    "ROYAL": {
      "componentes": ["ROYAL"],
      "total_kg": 30.0,
      "precio_kg": 100.00,
      "sub_total_importe": 3000.00
    },
    "BL": {
      "componentes": ["BL-B", "BL-X"],
      "total_kg": 45.0,
      "precio_kg": 60.00,
      "sub_total_importe": 2700.00
    },
    "FS": {
      "componentes": ["FS-B", "FS-X"],
      "total_kg": 25.0,
      "precio_kg": 50.00,
      "sub_total_importe": 1250.00
    },
    "HZ": {
      "componentes": ["HZ-B", "HZ-X", "AG"],
      "total_kg": 10.0,
      "precio_kg": 40.00,
      "sub_total_importe": 400.00
    },
    "STD": {
      "componentes": ["STD"],
      "total_kg": 10.0,
      "precio_kg": 30.00,
      "sub_total_importe": 300.00
    },
    "SURI": {
      "componentes": ["SURI-BL", "SURI-FS"],
      "total_kg": 0.0,
      "precio_kg": 0.00,
      "sub_total_importe": 0.00
    },
    "SURI-HZ": {
      "componentes": ["SURI-HZ"],
      "total_kg": 0.0,
      "precio_kg": 0.00,
      "sub_total_importe": 0.00
    }
  }
}'
```

**Error esperado:** `ERROR_IMPORTE_TOTAL`
- ✗ Suma de sub_total_importe: 3000 + 2700 + 1250 + 400 + 300 = 7650.00 ≠ importe_total (20000.00)

---

## 6. MÚLTIPLES ERRORES - Varios errores combinados

```bash
curl -X POST http://localhost:8083/api/clasificado/registrar \
  -H "Content-Type: application/json" \
  -d '{
  "clasificado": {
    "nombreArchivo": "CLASIFICADO_ERROR_MULTIPLE.xlsx",
    "clasificador": "LUCIA_MENDEZ",
    "fecha": "2025-01-03",
    "observaciones": "Múltiples errores de validación",
    "importe_total": 10000.00
  },
  "clasificado_calidad": {
    "ROYAL": {
      "pesos": [10.0, 5.0],
      "total_kg": 20.0
    },
    "BL-B": {
      "pesos": [15.0],
      "total_kg": 15.0
    },
    "BL-X": {
      "pesos": [10.0],
      "total_kg": 10.0
    },
    "FS-B": {
      "pesos": [8.0],
      "total_kg": 8.0
    },
    "FS-X": {
      "pesos": [7.0],
      "total_kg": 7.0
    },
    "HZ-B": {
      "pesos": [],
      "total_kg": 0.0
    },
    "HZ-X": {
      "pesos": [],
      "total_kg": 0.0
    },
    "AG": {
      "pesos": [],
      "total_kg": 0.0
    },
    "STD": {
      "pesos": [5.0],
      "total_kg": 5.0
    },
    "SURI-BL": {
      "pesos": [],
      "total_kg": 0.0
    },
    "SURI-FS": {
      "pesos": [],
      "total_kg": 0.0
    },
    "SURI-HZ": {
      "pesos": [],
      "total_kg": 0.0
    }
  },
  "clasificado_detalle": {
    "ROYAL": {
      "componentes": ["ROYAL"],
      "total_kg": 20.0,
      "precio_kg": 80.00,
      "sub_total_importe": 2000.00
    },
    "BL": {
      "componentes": ["BL-B", "BL-X"],
      "total_kg": 30.0,
      "precio_kg": 55.00,
      "sub_total_importe": 1650.00
    },
    "FS": {
      "componentes": ["FS-B", "FS-X"],
      "total_kg": 15.0,
      "precio_kg": 45.00,
      "sub_total_importe": 675.00
    },
    "HZ": {
      "componentes": ["HZ-B", "HZ-X", "AG"],
      "total_kg": 0.0,
      "precio_kg": 0.00,
      "sub_total_importe": 0.00
    },
    "STD": {
      "componentes": ["STD"],
      "total_kg": 5.0,
      "precio_kg": 35.00,
      "sub_total_importe": 175.00
    },
    "SURI": {
      "componentes": ["SURI-BL", "SURI-FS"],
      "total_kg": 0.0,
      "precio_kg": 0.00,
      "sub_total_importe": 0.00
    },
    "SURI-HZ": {
      "componentes": ["SURI-HZ"],
      "total_kg": 0.0,
      "precio_kg": 0.00,
      "sub_total_importe": 0.00
    }
  }
}'
```

**Errores esperados múltiples:**
- ✗ ERROR_PESO: ROYAL suma (10.0 + 5.0 = 15.0) ≠ total_kg (20.0)
- ✗ ERROR_TOTAL_KG: BL suma calidades (15.0 + 10.0 = 25.0) ≠ total_kg detalle (30.0)
- ✗ ERROR_IMPORTE: ROYAL (20.0 * 80.00 = 1600.00) ≠ sub_total_importe (2000.00)
- ✗ ERROR_IMPORTE_TOTAL: Suma sub_totales (2000 + 1650 + 675 + 175 = 4500.00) ≠ importe_total (10000.00)

---

## Notas Importantes

1. **Puerto**: Asegúrate de que el servidor esté corriendo en el puerto **8083**
2. **Headers**: Siempre incluir `Content-Type: application/json`
3. **Formato de fecha**: Usar formato ISO `YYYY-MM-DD`
4. **Decimales**: Usar punto decimal, no coma
5. **Arrays vacíos**: Para calidades sin pesos, usar `"pesos": []` y `"total_kg": 0.0`

## Respuestas Esperadas

### Respuesta Exitosa:
```json
{
  "success": true,
  "message": "Clasificado registrado exitosamente",
  "idClasificado": 123
}
```

### Respuesta con Errores:
```json
{
  "success": false,
  "message": "Se encontraron errores de validación",
  "errors": [
    {
      "codigo": "ERROR_PESO",
      "detalles": {...}
    }
  ]
}
```
