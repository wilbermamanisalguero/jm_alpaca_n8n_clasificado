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

    public Future<Map<String, Object>> consultarClasificado(String idClasificado) {
        return repository.getClasificadoById(idClasificado)
                .compose(clasificado -> {
                    if (clasificado == null) {
                        return Future.succeededFuture(Map.of(
                                "success", false,
                                "message", "Clasificado no encontrado"
                        ));
                    }

                    return CompositeFuture.all(
                            repository.getPesosByClasificadoId(idClasificado),
                            repository.getResumenByClasificadoId(idClasificado),
                            repository.getDetalleByClasificadoId(idClasificado),
                            repository.getErrorsByClasificadoId(idClasificado)
                    ).map(compositeFuture -> {
                        List<ClasificadoPeso> pesos = compositeFuture.resultAt(0);
                        List<ClasificadoResumen> resumenes = compositeFuture.resultAt(1);
                        List<ClasificadoDetalle> detalles = compositeFuture.resultAt(2);
                        List<ClasificadoErrorCarga> errores = compositeFuture.resultAt(3);

                        ClasificadoResponseDTO clasificadoDTO = ClasificadoResponseDTO.builder()
                                .idClasificado(clasificado.getIdClasificado())
                                .idClasificador(clasificado.getIdClasificador())
                                .fecha(clasificado.getFecha())
                                .importeTotal(clasificado.getImporteTotal())
                                .observaciones(clasificado.getObservaciones())
                                .build();

                        List<ClasificadoPesoResponseDTO> pesosDTO = pesos.stream()
                                .map(p -> ClasificadoPesoResponseDTO.builder()
                                        .idPeso(p.getIdPeso())
                                        .idCalidad(p.getIdCalidad())
                                        .pesoKg(p.getPesoKg())
                                        .build())
                                .toList();

                        List<ClasificadoResumenResponseDTO> resumenesDTO = resumenes.stream()
                                .map(r -> ClasificadoResumenResponseDTO.builder()
                                        .idCalidad(r.getIdCalidad())
                                        .totalKg(r.getTotalKg())
                                        .build())
                                .toList();

                        List<ClasificadoDetalleResponseDTO> detallesDTO = detalles.stream()
                                .map(d -> ClasificadoDetalleResponseDTO.builder()
                                        .idAgrupacion(d.getIdAgrupacion())
                                        .totalKg(d.getTotalKg())
                                        .precioKg(d.getPrecioKg())
                                        .subtotalImporte(d.getSubtotalImporte())
                                        .build())
                                .toList();

                        List<ClasificadoErrorCargaResponseDTO> erroresDTO = errores.stream()
                                .map(e -> ClasificadoErrorCargaResponseDTO.builder()
                                        .idErrorCarga(e.getIdErrorCarga())
                                        .codigo(e.getCodigo())
                                        .errorCarga(e.getErrorCarga())
                                        .build())
                                .toList();

                        ConsultaClasificadoResponseDTO responseDTO = ConsultaClasificadoResponseDTO.builder()
                                .clasificado(clasificadoDTO)
                                .clasificadoPeso(pesosDTO)
                                .clasificadoResumen(resumenesDTO)
                                .clasificadoDetalle(detallesDTO)
                                .clasificadoErrorCarga(erroresDTO)
                                .build();

                        return Map.of(
                                "success", true,
                                "data", responseDTO
                        );
                    });
                });
    }

    public Future<Map<String, Object>> registrarPesos(RegistrarPesosRequestDTO request) {
        String idClasificado = request.getClasificado().getNombreArchivo();

        // Construir entidad Clasificado (solo con datos básicos, sin importe_total ni observaciones)
        Clasificado clasificado = Clasificado.builder()
                .idClasificado(idClasificado)
                .idClasificador(request.getClasificado().getClasificador())
                .fecha(request.getClasificado().getFecha())
                .build();

        // Insertar Clasificado
        return repository.insertClasificado(clasificado)
                .compose(v -> {
                    // Insertar solo los pesos de calidad
                    return savePesosData(idClasificado, request.getClasificadoCalidad());
                })
                .compose(v -> {
                    return Future.succeededFuture(Map.of(
                            "success", true,
                            "message", "Pesos registrados exitosamente",
                            "idClasificado", idClasificado
                    ));
                });
    }

    private Future<Void> savePesosData(String idClasificado, ClasificadoCalidadPesosDTO calidad) {
        List<Future<Void>> futures = new ArrayList<>();

        futures.addAll(savePesos(idClasificado, "ROYAL", calidad.getRoyal()));
        futures.addAll(savePesos(idClasificado, "BL-B", calidad.getBlB()));
        futures.addAll(savePesos(idClasificado, "BL-X", calidad.getBlX()));
        futures.addAll(savePesos(idClasificado, "FS-B", calidad.getFsB()));
        futures.addAll(savePesos(idClasificado, "FS-X", calidad.getFsX()));
        futures.addAll(savePesos(idClasificado, "HZ-B", calidad.getHzB()));
        futures.addAll(savePesos(idClasificado, "HZ-X", calidad.getHzX()));
        futures.addAll(savePesos(idClasificado, "AG", calidad.getAg()));
        futures.addAll(savePesos(idClasificado, "STD", calidad.getStd()));
        futures.addAll(savePesos(idClasificado, "SURI-BL", calidad.getSuriBl()));
        futures.addAll(savePesos(idClasificado, "SURI-FS", calidad.getSuriFs()));
        futures.addAll(savePesos(idClasificado, "SURI-HZ", calidad.getSuriHz()));

        return CompositeFuture.all(new ArrayList<>(futures)).mapEmpty();
    }

    private List<Future<Void>> savePesos(String idClasificado, String idCalidad, CalidadPesosDTO data) {
        List<Future<Void>> futures = new ArrayList<>();

        if (data != null && data.getPesos() != null) {
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

        return futures;
    }

    public Future<Map<String, Object>> actualizarClasificado(ClasificadoActualizarRequestDTO request) {
        // Validar datos
        List<ActualizacionValidationError> validationErrors = validationService.validateActualizacion(request);

        if (!validationErrors.isEmpty()) {
            return Future.succeededFuture(Map.of(
                    "success", false,
                    "message", "Error de validación",
                    "errors", validationErrors
            ));
        }

        String idClasificado = request.getClasificado().getIdClasificado();

        // Actualizar clasificado
        Clasificado clasificado = Clasificado.builder()
                .idClasificado(idClasificado)
                .idClasificador(request.getClasificado().getIdClasificador())
                .fecha(request.getClasificado().getFecha())
                .importeTotal(request.getClasificado().getImporteTotal())
                .observaciones(request.getClasificado().getObservaciones())
                .build();

        return repository.updateClasificado(clasificado)
                .compose(v -> {
                    // Actualizar pesos, resumen y detalle
                    return updateClasificadoData(idClasificado, request);
                })
                .compose(v -> {
                    return Future.succeededFuture(Map.of(
                            "success", true,
                            "message", "Clasificado actualizado exitosamente",
                            "idClasificado", idClasificado
                    ));
                });
    }

    private Future<Void> updateClasificadoData(String idClasificado, ClasificadoActualizarRequestDTO request) {
        // Paso 1: Recolectar los IDs/claves que se deben mantener
        List<Long> idsToKeep = new ArrayList<>();
        List<String> calidadesToKeep = new ArrayList<>();
        List<String> agrupacionesToKeep = new ArrayList<>();

        if (request.getClasificadoPeso() != null) {
            idsToKeep = request.getClasificadoPeso().stream()
                    .map(ClasificadoPesoActualizarDTO::getIdPeso)
                    .toList();
        }

        if (request.getClasificadoResumen() != null) {
            calidadesToKeep = request.getClasificadoResumen().stream()
                    .map(ClasificadoResumenActualizarDTO::getIdCalidad)
                    .toList();
        }

        if (request.getClasificadoDetalle() != null) {
            agrupacionesToKeep = request.getClasificadoDetalle().stream()
                    .map(ClasificadoDetalleActualizarDTO::getIdAgrupacion)
                    .toList();
        }

        // Paso 2: Eliminar registros que ya no están en la actualización
        return CompositeFuture.all(
                repository.deleteClasificadoPesoNotInList(idClasificado, idsToKeep),
                repository.deleteClasificadoResumenNotInList(idClasificado, calidadesToKeep),
                repository.deleteClasificadoDetalleNotInList(idClasificado, agrupacionesToKeep)
        ).compose(v -> {
            // Paso 3: Insertar o actualizar los registros enviados
            List<Future<Void>> futures = new ArrayList<>();

            // Actualizar CLASIFICADO_PESO
            if (request.getClasificadoPeso() != null) {
                for (ClasificadoPesoActualizarDTO pesoDTO : request.getClasificadoPeso()) {
                    ClasificadoPeso peso = ClasificadoPeso.builder()
                            .idPeso(pesoDTO.getIdPeso())
                            .idClasificado(idClasificado)
                            .idCalidad(pesoDTO.getIdCalidad())
                            .pesoKg(pesoDTO.getPesoKg())
                            .build();
                    futures.add(repository.upsertClasificadoPeso(peso));
                }
            }

            // Actualizar CLASIFICADO_RESUMEN
            if (request.getClasificadoResumen() != null) {
                for (ClasificadoResumenActualizarDTO resumenDTO : request.getClasificadoResumen()) {
                    ClasificadoResumen resumen = ClasificadoResumen.builder()
                            .idClasificado(idClasificado)
                            .idCalidad(resumenDTO.getIdCalidad())
                            .totalKg(resumenDTO.getTotalKg())
                            .build();
                    futures.add(repository.upsertClasificadoResumen(resumen));
                }
            }

            // Actualizar CLASIFICADO_DETALLE
            if (request.getClasificadoDetalle() != null) {
                for (ClasificadoDetalleActualizarDTO detalleDTO : request.getClasificadoDetalle()) {
                    ClasificadoDetalle detalle = ClasificadoDetalle.builder()
                            .idClasificado(idClasificado)
                            .idAgrupacion(detalleDTO.getIdAgrupacion())
                            .totalKg(detalleDTO.getTotalKg())
                            .precioKg(detalleDTO.getPrecioKg())
                            .subtotalImporte(detalleDTO.getSubtotalImporte())
                            .build();
                    futures.add(repository.upsertClasificadoDetalle(detalle));
                }
            }

            return CompositeFuture.all(new ArrayList<>(futures)).mapEmpty();
        });
    }
}
