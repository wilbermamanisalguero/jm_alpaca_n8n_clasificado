package com.pe.jm_alpaca_n8n_clasificado.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalidadDataDTO {

    @JsonProperty("pesos")
    private List<BigDecimal> pesos;

    @JsonProperty("total_kg")
    private BigDecimal totalKg;
}
