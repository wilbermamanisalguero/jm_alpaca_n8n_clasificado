package com.pe.jm_alpaca_n8n_clasificado.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClasificadoPeso {
    private Long idPeso;
    private String idClasificado;
    private String idCalidad;
    private BigDecimal pesoKg;
}
