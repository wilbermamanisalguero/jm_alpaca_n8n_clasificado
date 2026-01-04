package com.pe.jm_alpaca_n8n_clasificado.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClasificadoPesoResponseDTO {

    @JsonProperty("idPeso")
    private Long idPeso;

    @JsonProperty("idCalidad")
    private String idCalidad;

    @JsonProperty("pesoKg")
    private BigDecimal pesoKg;
}
