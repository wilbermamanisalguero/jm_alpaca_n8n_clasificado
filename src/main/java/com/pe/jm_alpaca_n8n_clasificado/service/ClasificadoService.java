package com.pe.jm_alpaca_n8n_clasificado.service;

import com.pe.jm_alpaca_n8n_clasificado.dto.*;
import com.pe.jm_alpaca_n8n_clasificado.entity.*;
import com.pe.jm_alpaca_n8n_clasificado.repository.ClasificadoRepository;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ClasificadoService {

    private final ClasificadoRepository repository;
    private final ClasificadoValidationService validationService;

    public ClasificadoService(ClasificadoRepository repository, ClasificadoValidationService validationService) {
        this.repository = repository;
        this.validationService = validationService;
    }

    public Future<Map<String, Object>> registrarClasificado(ClasificadoRequestDTO request) {
        // Validar datos
        List<ValidationError> validationErrors = validationService.validate(request);

        // Construir entidad Clasificado
        String idClasificado = request.getClasificado().getNombreArchivo();

        Clasificado clasificado = Clasificado.builder()
                .idClasificado(idClasificado)
                .idClasificador(request.getClasificado().getClasificador())
                .fecha(request.getClasificado().getFecha())
                .importeTotal(request.getClasificado().getImporteTotal())
                .observaciones(request.getClasificado().getObservaciones())
                .build();

        // Insertar Clasificado
        return repository.insertClasificado(clasificado)
                .compose(v -> {
                    // Si hay errores de validación, guardarlos
                    if (!validationErrors.isEmpty()) {
                        return saveValidationErrors(idClasificado, validationErrors);
                    }
                    return Future.succeededFuture();
                })
                .compose(v -> {
                    // Insertar datos de calidad, resumen y detalle
                    return saveClasificadoData(idClasificado, request);
                })
                .compose(v -> {
                    // Retornar respuesta
                    if (!validationErrors.isEmpty()) {
                        return Future.succeededFuture(Map.of(
                                "success", false,
                                "message", "Clasificado registrado con errores de validación",
                                "idClasificado", idClasificado,
                                "errors", validationErrors
                        ));
                    } else {
                        return Future.succeededFuture(Map.of(
                                "success", true,
                                "message", "Clasificado registrado exitosamente",
                                "idClasificado", idClasificado
                        ));
                    }
                });
    }

    private Future<Void> saveValidationErrors(String idClasificado, List<ValidationError> errors) {
        List<Future<Void>> futures = new ArrayList<>();

        for (ValidationError error : errors) {
            ClasificadoErrorCarga errorCarga = ClasificadoErrorCarga.builder()
                    .idClasificado(idClasificado)
                    .codigo(error.getCodigo())
                    .errorCarga(error.getErrorCarga())
                    .build();
            futures.add(repository.insertClasificadoError(errorCarga));
        }

        return CompositeFuture.all(new ArrayList<>(futures)).mapEmpty();
    }

    private Future<Void> saveClasificadoData(String idClasificado, ClasificadoRequestDTO request) {
        List<Future<Void>> futures = new ArrayList<>();

        // Guardar CLASIFICADO_PESO y CLASIFICADO_RESUMEN
        ClasificadoCalidadDTO calidad = request.getClasificadoCalidad();

        futures.addAll(savePesosYResumen(idClasificado, "ROYAL", calidad.getRoyal()));
        futures.addAll(savePesosYResumen(idClasificado, "BL-B", calidad.getBlB()));
        futures.addAll(savePesosYResumen(idClasificado, "BL-X", calidad.getBlX()));
        futures.addAll(savePesosYResumen(idClasificado, "FS-B", calidad.getFsB()));
        futures.addAll(savePesosYResumen(idClasificado, "FS-X", calidad.getFsX()));
        futures.addAll(savePesosYResumen(idClasificado, "HZ-B", calidad.getHzB()));
        futures.addAll(savePesosYResumen(idClasificado, "HZ-X", calidad.getHzX()));
        futures.addAll(savePesosYResumen(idClasificado, "AG", calidad.getAg()));
        futures.addAll(savePesosYResumen(idClasificado, "STD", calidad.getStd()));
        futures.addAll(savePesosYResumen(idClasificado, "SURI-BL", calidad.getSuriBl()));
        futures.addAll(savePesosYResumen(idClasificado, "SURI-FS", calidad.getSuriFs()));
        futures.addAll(savePesosYResumen(idClasificado, "SURI-HZ", calidad.getSuriHz()));

        // Guardar CLASIFICADO_DETALLE
        ClasificadoDetalleDTO detalle = request.getClasificadoDetalle();

        futures.add(saveDetalle(idClasificado, "ROYAL", detalle.getRoyal()));
        futures.add(saveDetalle(idClasificado, "BL", detalle.getBl()));
        futures.add(saveDetalle(idClasificado, "FS", detalle.getFs()));
        futures.add(saveDetalle(idClasificado, "HZ", detalle.getHz()));
        futures.add(saveDetalle(idClasificado, "STD", detalle.getStd()));
        futures.add(saveDetalle(idClasificado, "SURI", detalle.getSuri()));
        futures.add(saveDetalle(idClasificado, "SURI-HZ", detalle.getSuriHz()));

        return CompositeFuture.all(new ArrayList<>(futures)).mapEmpty();
    }

    private List<Future<Void>> savePesosYResumen(String idClasificado, String idCalidad, CalidadDataDTO data) {
        List<Future<Void>> futures = new ArrayList<>();

        if (data != null) {
            // Guardar pesos
            if (data.getPesos() != null) {
                for (BigDecimal peso : data.getPesos()) {
                    if (peso != null) {
                        ClasificadoPeso pesoEntity = ClasificadoPeso.builder()
                                .idClasificado(idClasificado)
                                .idCalidad(idCalidad)
                                .pesoKg(peso)
                                .build();
                        futures.add(repository.insertClasificadoPeso(pesoEntity));
                    }
                }
            }

            // Guardar resumen
            if (data.getTotalKg() != null) {
                ClasificadoResumen resumen = ClasificadoResumen.builder()
                        .idClasificado(idClasificado)
                        .idCalidad(idCalidad)
                        .totalKg(data.getTotalKg())
                        .build();
                futures.add(repository.insertClasificadoResumen(resumen));
            }
        }

        return futures;
    }

    private Future<Void> saveDetalle(String idClasificado, String idAgrupacion, DetalleDataDTO data) {
        if (data != null && data.getTotalKg() != null && data.getPrecioKg() != null && data.getSubTotalImporte() != null) {
            ClasificadoDetalle detalle = ClasificadoDetalle.builder()
                    .idClasificado(idClasificado)
                    .idAgrupacion(idAgrupacion)
                    .totalKg(data.getTotalKg())
                    .precioKg(data.getPrecioKg())
                    .subtotalImporte(data.getSubTotalImporte())
                    .build();
            return repository.insertClasificadoDetalle(detalle);
        }
        return Future.succeededFuture();
    }
}
