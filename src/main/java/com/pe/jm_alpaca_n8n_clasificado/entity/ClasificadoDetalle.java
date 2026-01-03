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
public class ClasificadoDetalle {
    private String idClasificado;
    private String idAgrupacion;
    private BigDecimal totalKg;
    private BigDecimal precioKg;
    private BigDecimal subtotalImporte;
}
