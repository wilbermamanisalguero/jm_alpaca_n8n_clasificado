package com.pe.jm_alpaca_n8n_clasificado.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClasificadoErrorCarga {
    private Long idErrorCarga;
    private String idClasificado;
    private String codigo;
    private String errorCarga;
}
