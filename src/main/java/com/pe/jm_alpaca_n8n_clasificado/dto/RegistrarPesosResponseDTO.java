package com.pe.jm_alpaca_n8n_clasificado.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrarPesosResponseDTO {

    @JsonProperty("clasificado")
    private ClasificadoResponseDTO clasificado;

    // Formato: { "ROYAL": 625.00, "BL-B": 625.00, ... }
    @JsonProperty("clasificadoResumen")
    private Map<String, BigDecimal> clasificadoResumen;

    // Formato: [ ["ROYAL", 703.00, 0.80, 562.40], ["BL", 703.00, 0.80, 562.40], ... ]
    @JsonProperty("clasificadoDetalle")
    private List<List<Object>> clasificadoDetalle;

    // Formato: { "BL-B": [23.00, 36.00, ...], "BL-X": [31.00], ... }
    @JsonProperty("clasificado_pesos")
    private Map<String, List<BigDecimal>> clasificadoPesos;
}
