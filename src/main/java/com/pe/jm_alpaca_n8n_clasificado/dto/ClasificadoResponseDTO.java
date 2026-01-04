package com.pe.jm_alpaca_n8n_clasificado.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClasificadoResponseDTO {

    @JsonProperty("idClasificado")
    private String idClasificado;

    @JsonProperty("idClasificador")
    private String idClasificador;

    @JsonProperty("fecha")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate fecha;

    @JsonProperty("importeTotal")
    private BigDecimal importeTotal;

    @JsonProperty("observaciones")
    private String observaciones;
}
