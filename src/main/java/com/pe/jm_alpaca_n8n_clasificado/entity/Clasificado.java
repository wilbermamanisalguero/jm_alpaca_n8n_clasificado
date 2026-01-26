package com.pe.jm_alpaca_n8n_clasificado.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Clasificado {
    private String idClasificado;
    private String idClasificador;
    private LocalDate fecha;
    private BigDecimal importeTotal;
    private String observaciones;
    private Integer validado;
}
