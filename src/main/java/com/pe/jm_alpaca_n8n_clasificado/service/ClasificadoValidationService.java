package com.pe.jm_alpaca_n8n_clasificado.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pe.jm_alpaca_n8n_clasificado.dto.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClasificadoValidationService {

    private final ObjectMapper objectMapper;

    public ClasificadoValidationService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<ValidationError> validate(ClasificadoRequestDTO request) {
        List<ValidationError> errors = new ArrayList<>();

        // Validación 1: La suma de pesos debe ser igual a total_kg por calidad
        errors.addAll(validatePesosVsTotalKg(request.getClasificadoCalidad()));

        // Validación 2: total_kg de calidad debe ser igual a la suma en detalle
        errors.addAll(validateCalidadVsDetalle(request.getClasificadoCalidad(), request.getClasificadoDetalle()));

        // Validación 3: total_kg * precio_kg debe ser igual a sub_total_importe
        errors.addAll(validateSubtotalImporte(request.getClasificadoDetalle()));

        // Validación 4: suma de sub_total_importe debe ser igual a importe_total
        errors.addAll(validateImporteTotal(request.getClasificado(), request.getClasificadoDetalle()));

        return errors;
    }

    // Validación 1: La suma de pesos debe ser igual a total_kg
    private List<ValidationError> validatePesosVsTotalKg(ClasificadoCalidadDTO calidad) {
        List<ValidationError> errors = new ArrayList<>();

        validateCalidadField("ROYAL", calidad.getRoyal(), errors);
        validateCalidadField("BL-B", calidad.getBlB(), errors);
        validateCalidadField("BL-X", calidad.getBlX(), errors);
        validateCalidadField("FS-B", calidad.getFsB(), errors);
        validateCalidadField("FS-X", calidad.getFsX(), errors);
        validateCalidadField("HZ-B", calidad.getHzB(), errors);
        validateCalidadField("HZ-X", calidad.getHzX(), errors);
        validateCalidadField("AG", calidad.getAg(), errors);
        validateCalidadField("STD", calidad.getStd(), errors);
        validateCalidadField("SURI-BL", calidad.getSuriBl(), errors);
        validateCalidadField("SURI-FS", calidad.getSuriFs(), errors);
        validateCalidadField("SURI-HZ", calidad.getSuriHz(), errors);

        return errors;
    }

    private void validateCalidadField(String fieldName, CalidadDataDTO data, List<ValidationError> errors) {
        if (data != null && data.getPesos() != null && data.getTotalKg() != null) {
            BigDecimal sumaPesos = data.getPesos().stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal totalKg = data.getTotalKg().setScale(2, RoundingMode.HALF_UP);

            if (sumaPesos.compareTo(totalKg) != 0) {
                Map<String, Object> errorData = new HashMap<>();
                errorData.put(fieldName, data);

                try {
                    errors.add(ValidationError.builder()
                            .codigo("ERROR_PESO")
                            .errorCarga(objectMapper.writeValueAsString(errorData))
                            .build());
                } catch (JsonProcessingException e) {
                    errors.add(ValidationError.builder()
                            .codigo("ERROR_PESO")
                            .errorCarga("Error al serializar: " + fieldName)
                            .build());
                }
            }
        }
    }

    // Validación 2: total_kg de calidad debe coincidir con total_kg en detalle
    private List<ValidationError> validateCalidadVsDetalle(ClasificadoCalidadDTO calidad, ClasificadoDetalleDTO detalle) {
        List<ValidationError> errors = new ArrayList<>();

        // ROYAL
        validateCalidadDetalleMatch("ROYAL",
                List.of(calidad.getRoyal()),
                detalle.getRoyal(),
                errors);

        // BL = BL-B + BL-X
        validateCalidadDetalleMatch("BL",
                List.of(calidad.getBlB(), calidad.getBlX()),
                detalle.getBl(),
                errors);

        // FS = FS-B + FS-X
        validateCalidadDetalleMatch("FS",
                List.of(calidad.getFsB(), calidad.getFsX()),
                detalle.getFs(),
                errors);

        // HZ = HZ-B + HZ-X + AG
        validateCalidadDetalleMatch("HZ",
                List.of(calidad.getHzB(), calidad.getHzX(), calidad.getAg()),
                detalle.getHz(),
                errors);

        // STD
        validateCalidadDetalleMatch("STD",
                List.of(calidad.getStd()),
                detalle.getStd(),
                errors);

        // SURI = SURI-BL + SURI-FS
        validateCalidadDetalleMatch("SURI",
                List.of(calidad.getSuriBl(), calidad.getSuriFs()),
                detalle.getSuri(),
                errors);

        // SURI-HZ
        validateCalidadDetalleMatch("SURI-HZ",
                List.of(calidad.getSuriHz()),
                detalle.getSuriHz(),
                errors);

        return errors;
    }

    private void validateCalidadDetalleMatch(String agrupacion, List<CalidadDataDTO> calidadList,
                                            DetalleDataDTO detalleData, List<ValidationError> errors) {
        if (detalleData != null && detalleData.getTotalKg() != null) {
            BigDecimal sumaCalidad = calidadList.stream()
                    .filter(c -> c != null && c.getTotalKg() != null)
                    .map(CalidadDataDTO::getTotalKg)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal totalDetalle = detalleData.getTotalKg().setScale(2, RoundingMode.HALF_UP);

            if (sumaCalidad.compareTo(totalDetalle) != 0) {
                Map<String, Object> errorData = new HashMap<>();

                Map<String, BigDecimal> calidadMap = new HashMap<>();
                for (CalidadDataDTO c : calidadList) {
                    if (c != null && c.getTotalKg() != null) {
                        calidadMap.put("total_kg", c.getTotalKg());
                    }
                }
                errorData.put("clasificado_calidad", calidadMap);

                Map<String, Object> detalleMap = new HashMap<>();
                detalleMap.put(agrupacion, Map.of(
                    "componentes", detalleData.getComponentes(),
                    "total_kg", detalleData.getTotalKg()
                ));
                errorData.put("clasificado_detalle", detalleMap);

                try {
                    errors.add(ValidationError.builder()
                            .codigo("ERROR_TOTAL_KG")
                            .errorCarga(objectMapper.writeValueAsString(errorData))
                            .build());
                } catch (JsonProcessingException e) {
                    errors.add(ValidationError.builder()
                            .codigo("ERROR_TOTAL_KG")
                            .errorCarga("Error al serializar: " + agrupacion)
                            .build());
                }
            }
        }
    }

    // Validación 3: total_kg * precio_kg debe ser igual a sub_total_importe
    private List<ValidationError> validateSubtotalImporte(ClasificadoDetalleDTO detalle) {
        List<ValidationError> errors = new ArrayList<>();

        validateDetalleImporte("ROYAL", detalle.getRoyal(), errors);
        validateDetalleImporte("BL", detalle.getBl(), errors);
        validateDetalleImporte("FS", detalle.getFs(), errors);
        validateDetalleImporte("HZ", detalle.getHz(), errors);
        validateDetalleImporte("STD", detalle.getStd(), errors);
        validateDetalleImporte("SURI", detalle.getSuri(), errors);
        validateDetalleImporte("SURI-HZ", detalle.getSuriHz(), errors);

        return errors;
    }

    private void validateDetalleImporte(String agrupacion, DetalleDataDTO data, List<ValidationError> errors) {
        if (data != null && data.getTotalKg() != null && data.getPrecioKg() != null && data.getSubTotalImporte() != null) {
            BigDecimal calculado = data.getTotalKg()
                    .multiply(data.getPrecioKg())
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal subTotal = data.getSubTotalImporte().setScale(2, RoundingMode.HALF_UP);

            if (calculado.compareTo(subTotal) != 0) {
                Map<String, Object> errorData = new HashMap<>();
                Map<String, Object> detalleMap = new HashMap<>();
                detalleMap.put(agrupacion, Map.of(
                    "componentes", data.getComponentes(),
                    "total_kg", data.getTotalKg(),
                    "precio_kg", data.getPrecioKg(),
                    "sub_total_importe", data.getSubTotalImporte()
                ));
                errorData.put("clasificado_detalle", detalleMap);

                try {
                    errors.add(ValidationError.builder()
                            .codigo("ERROR_IMPORTE")
                            .errorCarga(objectMapper.writeValueAsString(errorData))
                            .build());
                } catch (JsonProcessingException e) {
                    errors.add(ValidationError.builder()
                            .codigo("ERROR_IMPORTE")
                            .errorCarga("Error al serializar: " + agrupacion)
                            .build());
                }
            }
        }
    }

    // Validación 4: suma de sub_total_importe debe ser igual a importe_total
    private List<ValidationError> validateImporteTotal(ClasificadoDTO clasificado, ClasificadoDetalleDTO detalle) {
        List<ValidationError> errors = new ArrayList<>();

        if (clasificado.getImporteTotal() != null) {
            BigDecimal sumaSubtotales = BigDecimal.ZERO;

            if (detalle.getRoyal() != null && detalle.getRoyal().getSubTotalImporte() != null) {
                sumaSubtotales = sumaSubtotales.add(detalle.getRoyal().getSubTotalImporte());
            }
            if (detalle.getBl() != null && detalle.getBl().getSubTotalImporte() != null) {
                sumaSubtotales = sumaSubtotales.add(detalle.getBl().getSubTotalImporte());
            }
            if (detalle.getFs() != null && detalle.getFs().getSubTotalImporte() != null) {
                sumaSubtotales = sumaSubtotales.add(detalle.getFs().getSubTotalImporte());
            }
            if (detalle.getHz() != null && detalle.getHz().getSubTotalImporte() != null) {
                sumaSubtotales = sumaSubtotales.add(detalle.getHz().getSubTotalImporte());
            }
            if (detalle.getStd() != null && detalle.getStd().getSubTotalImporte() != null) {
                sumaSubtotales = sumaSubtotales.add(detalle.getStd().getSubTotalImporte());
            }
            if (detalle.getSuri() != null && detalle.getSuri().getSubTotalImporte() != null) {
                sumaSubtotales = sumaSubtotales.add(detalle.getSuri().getSubTotalImporte());
            }
            if (detalle.getSuriHz() != null && detalle.getSuriHz().getSubTotalImporte() != null) {
                sumaSubtotales = sumaSubtotales.add(detalle.getSuriHz().getSubTotalImporte());
            }

            sumaSubtotales = sumaSubtotales.setScale(2, RoundingMode.HALF_UP);
            BigDecimal importeTotal = clasificado.getImporteTotal().setScale(2, RoundingMode.HALF_UP);

            if (sumaSubtotales.compareTo(importeTotal) != 0) {
                Map<String, Object> errorData = new HashMap<>();

                Map<String, Object> detalleMap = new HashMap<>();
                if (detalle.getRoyal() != null) {
                    detalleMap.put("ROYAL", Map.of("sub_total_importe", detalle.getRoyal().getSubTotalImporte()));
                }
                if (detalle.getBl() != null) {
                    detalleMap.put("BL", Map.of("sub_total_importe", detalle.getBl().getSubTotalImporte()));
                }
                if (detalle.getFs() != null) {
                    detalleMap.put("FS", Map.of("sub_total_importe", detalle.getFs().getSubTotalImporte()));
                }
                if (detalle.getHz() != null) {
                    detalleMap.put("HZ", Map.of("sub_total_importe", detalle.getHz().getSubTotalImporte()));
                }
                if (detalle.getStd() != null) {
                    detalleMap.put("STD", Map.of("sub_total_importe", detalle.getStd().getSubTotalImporte()));
                }
                if (detalle.getSuri() != null) {
                    detalleMap.put("SURI", Map.of("sub_total_importe", detalle.getSuri().getSubTotalImporte()));
                }
                if (detalle.getSuriHz() != null) {
                    detalleMap.put("SURI-HZ", Map.of("sub_total_importe", detalle.getSuriHz().getSubTotalImporte()));
                }

                errorData.put("clasificado_detalle", detalleMap);
                errorData.put("clasificado", Map.of(
                    "nombreArchivo", clasificado.getNombreArchivo(),
                    "importe_total", clasificado.getImporteTotal()
                ));

                try {
                    errors.add(ValidationError.builder()
                            .codigo("ERROR_IMPORTE_TOTAL")
                            .errorCarga(objectMapper.writeValueAsString(errorData))
                            .build());
                } catch (JsonProcessingException e) {
                    errors.add(ValidationError.builder()
                            .codigo("ERROR_IMPORTE_TOTAL")
                            .errorCarga("Error al serializar importe total")
                            .build());
                }
            }
        }

        return errors;
    }
}
