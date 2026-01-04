package com.pe.jm_alpaca_n8n_clasificado.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaClasificadoRequestDTO {

    @JsonProperty("idClasificado")
    @NotBlank(message = "El idClasificado es requerido")
    private String idClasificado;
}
