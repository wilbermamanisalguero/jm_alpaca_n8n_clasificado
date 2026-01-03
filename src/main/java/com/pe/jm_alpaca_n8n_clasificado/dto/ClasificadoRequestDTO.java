package com.pe.jm_alpaca_n8n_clasificado.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClasificadoRequestDTO {

    @JsonProperty("clasificado")
    @NotNull(message = "Los datos del clasificado son requeridos")
    @Valid
    private ClasificadoDTO clasificado;

    @JsonProperty("clasificado_calidad")
    @NotNull(message = "Los datos de calidad son requeridos")
    @Valid
    private ClasificadoCalidadDTO clasificadoCalidad;

    @JsonProperty("clasificado_detalle")
    @NotNull(message = "Los datos de detalle son requeridos")
    @Valid
    private ClasificadoDetalleDTO clasificadoDetalle;
}
