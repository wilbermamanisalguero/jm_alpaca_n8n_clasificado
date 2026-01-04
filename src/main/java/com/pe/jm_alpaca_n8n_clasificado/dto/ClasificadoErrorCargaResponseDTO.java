package com.pe.jm_alpaca_n8n_clasificado.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClasificadoErrorCargaResponseDTO {

    @JsonProperty("idErrorCarga")
    private Long idErrorCarga;

    @JsonProperty("codigo")
    private String codigo;

    @JsonProperty("errorCarga")
    private String errorCarga;
}
