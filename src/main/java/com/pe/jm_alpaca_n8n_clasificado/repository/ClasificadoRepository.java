package com.pe.jm_alpaca_n8n_clasificado.repository;

import com.pe.jm_alpaca_n8n_clasificado.dto.SpResultDTO;
import com.pe.jm_alpaca_n8n_clasificado.entity.*;
import io.vertx.core.Future;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ClasificadoRepository {

    private final MySQLPool client;

    public ClasificadoRepository(MySQLPool client) {
        this.client = client;
    }

    public Future<Void> insertClasificado(Clasificado clasificado) {
        String query = "INSERT INTO CLASIFICADO (ID_CLASIFICADO, ID_CLASIFICADOR, FECHA, IMPORTE_TOTAL, OBSERVACIONES) " +
                       "VALUES (?, ?, ?, ?, ?)";

        Tuple params = Tuple.of(
            clasificado.getIdClasificado(),
            clasificado.getIdClasificador(),
            clasificado.getFecha(),
            clasificado.getImporteTotal(),
            clasificado.getObservaciones()
        );

        return client.preparedQuery(query)
                .execute(params)
                .mapEmpty();
    }

    public Future<Void> insertClasificadoPeso(ClasificadoPeso peso) {
        String query = "INSERT INTO CLASIFICADO_PESO (ID_CLASIFICADO, ID_CALIDAD, PESO_KG) " +
                       "VALUES (?, ?, ?)";

        Tuple params = Tuple.of(
            peso.getIdClasificado(),
            peso.getIdCalidad(),
            peso.getPesoKg()
        );

        return client.preparedQuery(query)
                .execute(params)
                .mapEmpty();
    }

    public Future<Void> insertClasificadoResumen(ClasificadoResumen resumen) {
        String query = "INSERT INTO CLASIFICADO_RESUMEN (ID_CLASIFICADO, ID_CALIDAD, TOTAL_KG) " +
                       "VALUES (?, ?, ?)";

        Tuple params = Tuple.of(
            resumen.getIdClasificado(),
            resumen.getIdCalidad(),
            resumen.getTotalKg()
        );

        return client.preparedQuery(query)
                .execute(params)
                .mapEmpty();
    }

    public Future<Void> insertClasificadoDetalle(ClasificadoDetalle detalle) {
        String query = "INSERT INTO CLASIFICADO_DETALLE (ID_CLASIFICADO, ID_AGRUPACION, TOTAL_KG, PRECIO_KG, SUBTOTAL_IMPORTE) " +
                       "VALUES (?, ?, ?, ?, ?)";

        Tuple params = Tuple.of(
            detalle.getIdClasificado(),
            detalle.getIdAgrupacion(),
            detalle.getTotalKg(),
            detalle.getPrecioKg(),
            detalle.getSubtotalImporte()
        );

        return client.preparedQuery(query)
                .execute(params)
                .mapEmpty();
    }

    public Future<Void> insertClasificadoError(ClasificadoErrorCarga error) {
        String query = "INSERT INTO CLASIFICADO_ERROR_CARGA (ID_CLASIFICADO, CODIGO, ERROR_CARGA) " +
                       "VALUES (?, ?, ?)";

        Tuple params = Tuple.of(
            error.getIdClasificado(),
            error.getCodigo(),
            error.getErrorCarga()
        );

        return client.preparedQuery(query)
                .execute(params)
                .mapEmpty();
    }

    public Future<List<ClasificadoErrorCarga>> getErrorsByClasificadoId(String idClasificado) {
        String query = "SELECT ID_ERROR_CARGA, ID_CLASIFICADO, CODIGO, ERROR_CARGA " +
                       "FROM CLASIFICADO_ERROR_CARGA WHERE ID_CLASIFICADO = ?";

        return client.preparedQuery(query)
                .execute(Tuple.of(idClasificado))
                .map(this::mapToErrorList);
    }

    private List<ClasificadoErrorCarga> mapToErrorList(RowSet<Row> rows) {
        List<ClasificadoErrorCarga> errors = new ArrayList<>();
        for (Row row : rows) {
            errors.add(ClasificadoErrorCarga.builder()
                    .idErrorCarga(row.getLong("ID_ERROR_CARGA"))
                    .idClasificado(row.getString("ID_CLASIFICADO"))
                    .codigo(row.getString("CODIGO"))
                    .errorCarga(row.getString("ERROR_CARGA"))
                    .build());
        }
        return errors;
    }

    public Future<Clasificado> getClasificadoById(String idClasificado) {
        String query = "SELECT ID_CLASIFICADO, ID_CLASIFICADOR, FECHA, IMPORTE_TOTAL, OBSERVACIONES " +
                       "FROM CLASIFICADO WHERE ID_CLASIFICADO = ?";

        return client.preparedQuery(query)
                .execute(Tuple.of(idClasificado))
                .map(rows -> {
                    if (rows.size() == 0) {
                        return null;
                    }
                    Row row = rows.iterator().next();
                    return Clasificado.builder()
                            .idClasificado(row.getString("ID_CLASIFICADO"))
                            .idClasificador(row.getString("ID_CLASIFICADOR"))
                            .fecha(row.getLocalDate("FECHA"))
                            .importeTotal(row.getBigDecimal("IMPORTE_TOTAL"))
                            .observaciones(row.getString("OBSERVACIONES"))
                            .build();
                });
    }

    public Future<List<ClasificadoPeso>> getPesosByClasificadoId(String idClasificado) {
        String query = "SELECT ID_PESO, ID_CLASIFICADO, ID_CALIDAD, PESO_KG " +
                       "FROM CLASIFICADO_PESO WHERE ID_CLASIFICADO = ?";

        return client.preparedQuery(query)
                .execute(Tuple.of(idClasificado))
                .map(rows -> {
                    List<ClasificadoPeso> pesos = new ArrayList<>();
                    for (Row row : rows) {
                        pesos.add(ClasificadoPeso.builder()
                                .idPeso(row.getLong("ID_PESO"))
                                .idClasificado(row.getString("ID_CLASIFICADO"))
                                .idCalidad(row.getString("ID_CALIDAD"))
                                .pesoKg(row.getBigDecimal("PESO_KG"))
                                .build());
                    }
                    return pesos;
                });
    }

    public Future<List<ClasificadoResumen>> getResumenByClasificadoId(String idClasificado) {
        String query = "SELECT ID_CLASIFICADO, ID_CALIDAD, TOTAL_KG " +
                       "FROM CLASIFICADO_RESUMEN WHERE ID_CLASIFICADO = ?";

        return client.preparedQuery(query)
                .execute(Tuple.of(idClasificado))
                .map(rows -> {
                    List<ClasificadoResumen> resumenes = new ArrayList<>();
                    for (Row row : rows) {
                        resumenes.add(ClasificadoResumen.builder()
                                .idClasificado(row.getString("ID_CLASIFICADO"))
                                .idCalidad(row.getString("ID_CALIDAD"))
                                .totalKg(row.getBigDecimal("TOTAL_KG"))
                                .build());
                    }
                    return resumenes;
                });
    }

    public Future<List<ClasificadoDetalle>> getDetalleByClasificadoId(String idClasificado) {
        String query = "SELECT ID_CLASIFICADO, ID_AGRUPACION, TOTAL_KG, PRECIO_KG, SUBTOTAL_IMPORTE " +
                       "FROM CLASIFICADO_DETALLE WHERE ID_CLASIFICADO = ?";

        return client.preparedQuery(query)
                .execute(Tuple.of(idClasificado))
                .map(rows -> {
                    List<ClasificadoDetalle> detalles = new ArrayList<>();
                    for (Row row : rows) {
                        detalles.add(ClasificadoDetalle.builder()
                                .idClasificado(row.getString("ID_CLASIFICADO"))
                                .idAgrupacion(row.getString("ID_AGRUPACION"))
                                .totalKg(row.getBigDecimal("TOTAL_KG"))
                                .precioKg(row.getBigDecimal("PRECIO_KG"))
                                .subtotalImporte(row.getBigDecimal("SUBTOTAL_IMPORTE"))
                                .build());
                    }
                    return detalles;
                });
    }

    public Future<Void> updateClasificado(Clasificado clasificado) {
        String query = "UPDATE CLASIFICADO SET ID_CLASIFICADOR = ?, FECHA = ?, IMPORTE_TOTAL = ?, OBSERVACIONES = ? " +
                       "WHERE ID_CLASIFICADO = ?";

        Tuple params = Tuple.of(
            clasificado.getIdClasificador(),
            clasificado.getFecha(),
            clasificado.getImporteTotal(),
            clasificado.getObservaciones(),
            clasificado.getIdClasificado()
        );

        return client.preparedQuery(query)
                .execute(params)
                .mapEmpty();
    }

    public Future<Void> upsertClasificadoPeso(ClasificadoPeso peso) {
        if (peso.getIdPeso() == null) {
            return insertClasificadoPeso(peso);
        } else {
            String query = "UPDATE CLASIFICADO_PESO SET ID_CALIDAD = ?, PESO_KG = ? " +
                           "WHERE ID_PESO = ? AND ID_CLASIFICADO = ?";

            Tuple params = Tuple.of(
                peso.getIdCalidad(),
                peso.getPesoKg(),
                peso.getIdPeso(),
                peso.getIdClasificado()
            );

            return client.preparedQuery(query)
                    .execute(params)
                    .mapEmpty();
        }
    }

    public Future<Void> upsertClasificadoResumen(ClasificadoResumen resumen) {
        String checkQuery = "SELECT COUNT(*) as count FROM CLASIFICADO_RESUMEN " +
                            "WHERE ID_CLASIFICADO = ? AND ID_CALIDAD = ?";

        return client.preparedQuery(checkQuery)
                .execute(Tuple.of(resumen.getIdClasificado(), resumen.getIdCalidad()))
                .compose(rows -> {
                    Row row = rows.iterator().next();
                    int count = row.getInteger("count");

                    if (count > 0) {
                        String updateQuery = "UPDATE CLASIFICADO_RESUMEN SET TOTAL_KG = ? " +
                                             "WHERE ID_CLASIFICADO = ? AND ID_CALIDAD = ?";
                        Tuple updateParams = Tuple.of(
                            resumen.getTotalKg(),
                            resumen.getIdClasificado(),
                            resumen.getIdCalidad()
                        );
                        return client.preparedQuery(updateQuery).execute(updateParams).mapEmpty();
                    } else {
                        return insertClasificadoResumen(resumen);
                    }
                });
    }

    public Future<Void> upsertClasificadoDetalle(ClasificadoDetalle detalle) {
        String checkQuery = "SELECT COUNT(*) as count FROM CLASIFICADO_DETALLE " +
                            "WHERE ID_CLASIFICADO = ? AND ID_AGRUPACION = ?";

        return client.preparedQuery(checkQuery)
                .execute(Tuple.of(detalle.getIdClasificado(), detalle.getIdAgrupacion()))
                .compose(rows -> {
                    Row row = rows.iterator().next();
                    int count = row.getInteger("count");

                    if (count > 0) {
                        String updateQuery = "UPDATE CLASIFICADO_DETALLE SET TOTAL_KG = ?, PRECIO_KG = ?, SUBTOTAL_IMPORTE = ? " +
                                             "WHERE ID_CLASIFICADO = ? AND ID_AGRUPACION = ?";
                        Tuple updateParams = Tuple.of(
                            detalle.getTotalKg(),
                            detalle.getPrecioKg(),
                            detalle.getSubtotalImporte(),
                            detalle.getIdClasificado(),
                            detalle.getIdAgrupacion()
                        );
                        return client.preparedQuery(updateQuery).execute(updateParams).mapEmpty();
                    } else {
                        return insertClasificadoDetalle(detalle);
                    }
                });
    }

    public Future<Void> deleteClasificadoPesoNotInList(String idClasificado, List<Long> idsToKeep) {
        if (idsToKeep == null || idsToKeep.isEmpty()) {
            // Si no hay IDs para mantener, eliminar todos los registros de ese clasificado
            String query = "DELETE FROM CLASIFICADO_PESO WHERE ID_CLASIFICADO = ?";
            return client.preparedQuery(query)
                    .execute(Tuple.of(idClasificado))
                    .mapEmpty();
        }

        // Construir placeholders para la lista de IDs
        String placeholders = String.join(",", idsToKeep.stream().map(id -> "?").toList());
        String query = "DELETE FROM CLASIFICADO_PESO WHERE ID_CLASIFICADO = ? AND ID_PESO NOT IN (" + placeholders + ")";

        // Construir Tuple con todos los parámetros
        Tuple params = Tuple.of(idClasificado);
        for (Long id : idsToKeep) {
            params.addLong(id);
        }

        return client.preparedQuery(query)
                .execute(params)
                .mapEmpty();
    }

    public Future<Void> deleteClasificadoResumenNotInList(String idClasificado, List<String> calidadesToKeep) {
        if (calidadesToKeep == null || calidadesToKeep.isEmpty()) {
            // Si no hay calidades para mantener, eliminar todos los registros de ese clasificado
            String query = "DELETE FROM CLASIFICADO_RESUMEN WHERE ID_CLASIFICADO = ?";
            return client.preparedQuery(query)
                    .execute(Tuple.of(idClasificado))
                    .mapEmpty();
        }

        // Construir placeholders para la lista de calidades
        String placeholders = String.join(",", calidadesToKeep.stream().map(c -> "?").toList());
        String query = "DELETE FROM CLASIFICADO_RESUMEN WHERE ID_CLASIFICADO = ? AND ID_CALIDAD NOT IN (" + placeholders + ")";

        // Construir Tuple con todos los parámetros
        Tuple params = Tuple.of(idClasificado);
        for (String calidad : calidadesToKeep) {
            params.addString(calidad);
        }

        return client.preparedQuery(query)
                .execute(params)
                .mapEmpty();
    }

    public Future<Void> deleteClasificadoDetalleNotInList(String idClasificado, List<String> agrupacionesToKeep) {
        if (agrupacionesToKeep == null || agrupacionesToKeep.isEmpty()) {
            // Si no hay agrupaciones para mantener, eliminar todos los registros de ese clasificado
            String query = "DELETE FROM CLASIFICADO_DETALLE WHERE ID_CLASIFICADO = ?";
            return client.preparedQuery(query)
                    .execute(Tuple.of(idClasificado))
                    .mapEmpty();
        }

        // Construir placeholders para la lista de agrupaciones
        String placeholders = String.join(",", agrupacionesToKeep.stream().map(a -> "?").toList());
        String query = "DELETE FROM CLASIFICADO_DETALLE WHERE ID_CLASIFICADO = ? AND ID_AGRUPACION NOT IN (" + placeholders + ")";

        // Construir Tuple con todos los parámetros
        Tuple params = Tuple.of(idClasificado);
        for (String agrupacion : agrupacionesToKeep) {
            params.addString(agrupacion);
        }

        return client.preparedQuery(query)
                .execute(params)
                .mapEmpty();
    }

    /**
     * Elimina todos los registros relacionados con un clasificado en el orden correcto
     * (primero las tablas hijas, luego la tabla padre)
     */
    public Future<Void> deleteClasificadoCompleto(String idClasificado) {
        // Eliminar en orden: primero tablas dependientes, luego la principal
        return client.preparedQuery("DELETE FROM CLASIFICADO_ERROR_CARGA WHERE ID_CLASIFICADO = ?")
                .execute(Tuple.of(idClasificado))
                .compose(v -> client.preparedQuery("DELETE FROM CLASIFICADO_DETALLE WHERE ID_CLASIFICADO = ?")
                        .execute(Tuple.of(idClasificado)))
                .compose(v -> client.preparedQuery("DELETE FROM CLASIFICADO_PESO WHERE ID_CLASIFICADO = ?")
                        .execute(Tuple.of(idClasificado)))
                .compose(v -> client.preparedQuery("DELETE FROM CLASIFICADO_RESUMEN WHERE ID_CLASIFICADO = ?")
                        .execute(Tuple.of(idClasificado)))
                .compose(v -> client.preparedQuery("DELETE FROM CLASIFICADO WHERE ID_CLASIFICADO = ?")
                        .execute(Tuple.of(idClasificado)))
                .mapEmpty();
    }

    public Future<SpResultDTO> callProcesarClasificadoDetalle(String idClasificado) {
        String query = "CALL SP_PROCESAR_CLASIFICADO_DETALLE(?, @p_codigo, @p_descripcion)";

        return client.preparedQuery(query)
                .execute(Tuple.of(idClasificado))
                .compose(v -> client.query("SELECT @p_codigo AS codigo, @p_descripcion AS descripcion").execute())
                .map(rows -> {
                    Row row = rows.iterator().next();
                    return SpResultDTO.builder()
                            .codigo(row.getString("codigo"))
                            .descripcion(row.getString("descripcion"))
                            .build();
                });
    }
}
