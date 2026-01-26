package com.pe.jm_alpaca_n8n_clasificado.service;

import com.pe.jm_alpaca_n8n_clasificado.dto.*;
import com.pe.jm_alpaca_n8n_clasificado.entity.*;
import com.pe.jm_alpaca_n8n_clasificado.repository.ClasificadoRepository;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                    // Insertar dados de calidad, resumen y detalle
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

    // Orden fijo para calidades (clasificadoResumen y clasificado_pesos)
    private static final List<String> ORDEN_CALIDADES = Arrays.asList(
            "ROYAL", "BL-B", "BL-X", "FS-B", "FS-X", "HZ-B", "HZ-X", "AG", "STD",
            "SURI-BL", "SURI-FS", "SURI-HZ", "SURI-STD"
    );

    // Orden fijo para agrupaciones (clasificadoDetalle)
    private static final List<String> ORDEN_AGRUPACIONES = Arrays.asList(
            "ROYAL", "BL", "FS", "HZ", "STD", "SURI", "SURI-HZ", "SURI-STD"
    );

    public Future<Map<String, Object>> registrarPesos(RegistrarPesosRequestDTO request) {
        String idClasificado = request.getClasificado().getNombreArchivo();

        // Construir entidad Clasificado (solo con datos básicos, sin importe_total ni observaciones)
        Clasificado clasificado = Clasificado.builder()
                .idClasificado(idClasificado)
                .idClasificador(request.getClasificado().getClasificador())
                .fecha(request.getClasificado().getFecha())
                .validado(request.getClasificado().getValidado())
                .build();

        // Primero eliminar registros existentes, luego insertar nuevos
        return repository.deleteClasificadoCompleto(idClasificado)
                .compose(v -> {
                    // Insertar Clasificado
                    return repository.insertClasificado(clasificado);
                })
                .compose(v -> {
                    // Insertar solo los pesos de calidad
                    return savePesosData(idClasificado, request.getClasificadoCalidad());
                })
                .compose(v -> {
                    // Llamar al procedimiento almacenado para procesar el detalle
                    return repository.callProcesarClasificadoDetalle(idClasificado);
                })
                .compose(spResult -> {
                    // Consultar todos los datos procesados de la BD
                    return CompositeFuture.all(
                            repository.getClasificadoById(idClasificado),
                            repository.getPesosByClasificadoId(idClasificado),
                            repository.getResumenByClasificadoId(idClasificado),
                            repository.getDetalleByClasificadoId(idClasificado)
                    ).map(compositeFuture -> {
                        Clasificado clasificadoDB = compositeFuture.resultAt(0);
                        List<ClasificadoPeso> pesos = compositeFuture.resultAt(1);
                        List<ClasificadoResumen> resumenes = compositeFuture.resultAt(2);
                        List<ClasificadoDetalle> detalles = compositeFuture.resultAt(3);

                        // Construir respuesta
                        return buildRegistrarPesosResponse(
                                idClasificado, spResult, clasificadoDB, pesos, resumenes, detalles, request
                        );
                    });
                });
    }

    private Map<String, Object> buildRegistrarPesosResponse(
            String idClasificado,
            SpResultDTO spResult,
            Clasificado clasificadoDB,
            List<ClasificadoPeso> pesos,
            List<ClasificadoResumen> resumenes,
            List<ClasificadoDetalle> detalles,
            RegistrarPesosRequestDTO request) {

        // 1. Construir clasificado DTO
        ClasificadoResponseDTO clasificadoDTO = ClasificadoResponseDTO.builder()
                .idClasificado(clasificadoDB.getIdClasificado())
                .idClasificador(clasificadoDB.getIdClasificador())
                .fecha(clasificadoDB.getFecha())
                .importeTotal(clasificadoDB.getImporteTotal())
                .validado(clasificadoDB.getValidado())
                .build();

        // 2. Construir clasificadoResumen como Map<String, BigDecimal> ordenado y filtrado (solo totalKg > 0)
        Map<String, BigDecimal> resumenMapOriginal = resumenes.stream()
                .collect(Collectors.toMap(
                        ClasificadoResumen::getIdCalidad,
                        ClasificadoResumen::getTotalKg
                ));

        Map<String, BigDecimal> clasificadoResumenMap = new LinkedHashMap<>();
        for (String calidad : ORDEN_CALIDADES) {
            if (resumenMapOriginal.containsKey(calidad) &&
                    resumenMapOriginal.get(calidad) != null &&
                    resumenMapOriginal.get(calidad).compareTo(BigDecimal.ZERO) > 0) {
                clasificadoResumenMap.put(calidad, resumenMapOriginal.get(calidad));
            }
        }

        // 3. Construir clasificadoDetalle como List<List<Object>> ordenado y filtrado (solo subtotalImporte > 0)
        Map<String, ClasificadoDetalle> detalleMap = detalles.stream()
                .collect(Collectors.toMap(
                        ClasificadoDetalle::getIdAgrupacion,
                        d -> d
                ));

        List<List<Object>> clasificadoDetalleList = new ArrayList<>();
        for (String agrupacion : ORDEN_AGRUPACIONES) {
            if (detalleMap.containsKey(agrupacion) &&
                    detalleMap.get(agrupacion).getSubtotalImporte() != null &&
                    detalleMap.get(agrupacion).getSubtotalImporte().compareTo(BigDecimal.ZERO) > 0) {
                ClasificadoDetalle d = detalleMap.get(agrupacion);
                // Formato: [idAgrupacion, totalKg, precioKg, subtotalImporte]
                clasificadoDetalleList.add(Arrays.asList(
                        agrupacion,
                        d.getTotalKg(),
                        d.getPrecioKg(),
                        d.getSubtotalImporte()
                ));
            }
        }

        // 4. Construir clasificado_pesos como Map<String, List<BigDecimal>> ordenado y filtrado
        Map<String, List<BigDecimal>> pesosAgrupados = pesos.stream()
                .collect(Collectors.groupingBy(
                        ClasificadoPeso::getIdCalidad,
                        Collectors.mapping(ClasificadoPeso::getPesoKg, Collectors.toList())
                ));

        Map<String, List<BigDecimal>> clasificadoPesosMap = new LinkedHashMap<>();
        for (String calidad : ORDEN_CALIDADES) {
            List<BigDecimal> pesosList = pesosAgrupados.getOrDefault(calidad, new ArrayList<>());
            // Solo incluir si tiene pesos (lista no vacía)
            if (!pesosList.isEmpty()) {
                clasificadoPesosMap.put(calidad, pesosList);
            }
        }

        // 5. Construir objeto data
        RegistrarPesosResponseDTO data = RegistrarPesosResponseDTO.builder()
                .clasificado(clasificadoDTO)
                .clasificadoResumen(clasificadoResumenMap)
                .clasificadoDetalle(clasificadoDetalleList)
                .clasificadoPesos(clasificadoPesosMap)
                .build();

        // 6. Construir sección parametro con datos del request (filtrado)
        Map<String, Object> parametro = buildParametroSection(request);

        // 7. Construir respuesta final
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idClasificado", idClasificado);
        response.put("spDescripcion", spResult.getDescripcion());
        response.put("spCodigo", spResult.getCodigo());
        response.put("message", "Pesos registrados exitosamente");
        response.put("success", true);
        response.put("data", data);
        response.put("parametro", parametro);

        return response;
    }

    private Map<String, Object> buildParametroSection(RegistrarPesosRequestDTO request) {
        Map<String, Object> parametro = new LinkedHashMap<>();

        // Construir sección clasificado
        Map<String, Object> clasificadoInfo = new LinkedHashMap<>();
        clasificadoInfo.put("nombreArchivo", request.getClasificado().getNombreArchivo());
        clasificadoInfo.put("clasificador", request.getClasificado().getClasificador());
        if (request.getClasificado().getFecha() != null) {
            clasificadoInfo.put("fecha", request.getClasificado().getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        } else {
            clasificadoInfo.put("fecha", null);
        }
        // Si validado viene null o no viene, devolver 1 por defecto
        Integer validadoValue = request.getClasificado().getValidado();
        clasificadoInfo.put("validado", validadoValue != null ? validadoValue : 1);
        parametro.put("clasificado", clasificadoInfo);

        // Construir sección clasificado_calidad (solo calidades con pesos > 0)
        Map<String, Object> clasificadoCalidad = new LinkedHashMap<>();
        ClasificadoCalidadPesosDTO calidad = request.getClasificadoCalidad();

        addCalidadIfHasPesos(clasificadoCalidad, "ROYAL", calidad.getRoyal());
        addCalidadIfHasPesos(clasificadoCalidad, "BL-B", calidad.getBlB());
        addCalidadIfHasPesos(clasificadoCalidad, "BL-X", calidad.getBlX());
        addCalidadIfHasPesos(clasificadoCalidad, "FS-B", calidad.getFsB());
        addCalidadIfHasPesos(clasificadoCalidad, "FS-X", calidad.getFsX());
        addCalidadIfHasPesos(clasificadoCalidad, "HZ-B", calidad.getHzB());
        addCalidadIfHasPesos(clasificadoCalidad, "HZ-X", calidad.getHzX());
        addCalidadIfHasPesos(clasificadoCalidad, "AG", calidad.getAg());
        addCalidadIfHasPesos(clasificadoCalidad, "STD", calidad.getStd());
        addCalidadIfHasPesos(clasificadoCalidad, "SURI-BL", calidad.getSuriBl());
        addCalidadIfHasPesos(clasificadoCalidad, "SURI-FS", calidad.getSuriFs());
        addCalidadIfHasPesos(clasificadoCalidad, "SURI-HZ", calidad.getSuriHz());
        addCalidadIfHasPesos(clasificadoCalidad, "SURI-STD", calidad.getSuriStd());

        parametro.put("clasificado_calidad", clasificadoCalidad);

        return parametro;
    }

    private void addCalidadIfHasPesos(Map<String, Object> map, String calidad, CalidadPesosDTO data) {
        if (data != null && data.getPesos() != null && !data.getPesos().isEmpty()) {
            Map<String, Object> calidadData = new LinkedHashMap<>();
            calidadData.put("pesos", data.getPesos());
            map.put(calidad, calidadData);
        }
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
        futures.addAll(savePesos(idClasificado, "SURI-STD", calidad.getSuriStd()));

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
