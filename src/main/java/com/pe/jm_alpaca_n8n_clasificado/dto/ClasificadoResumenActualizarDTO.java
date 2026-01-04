package com.pe.jm_alpaca_n8n_clasificado.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClasificadoResumenActualizarDTO {

    @JsonProperty("idCalidad")
    private String idCalidad;

    @JsonProperty("totalKg")
    private BigDecimal totalKg;
}
