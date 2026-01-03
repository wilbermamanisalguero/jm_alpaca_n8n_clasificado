package com.pe.jm_alpaca_n8n_clasificado.repository;

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
}
