package com.pe.jm_alpaca_n8n_clasificado.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClasificadoActualizarRequestDTO {

    @JsonProperty("clasificado")
    @NotNull(message = "El clasificado es requerido")
    @Valid
    private ClasificadoActualizarDTO clasificado;

    @JsonProperty("clasificadoPeso")
    private List<ClasificadoPesoActualizarDTO> clasificadoPeso;

    @JsonProperty("clasificadoResumen")
    private List<ClasificadoResumenActualizarDTO> clasificadoResumen;

    @JsonProperty("clasificadoDetalle")
    private List<ClasificadoDetalleActualizarDTO> clasificadoDetalle;
}
