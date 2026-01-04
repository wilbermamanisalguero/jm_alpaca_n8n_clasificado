package com.pe.jm_alpaca_n8n_clasificado.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultaClasificadoResponseDTO {

    @JsonProperty("clasificado")
    private ClasificadoResponseDTO clasificado;

    @JsonProperty("clasificadoPeso")
    private List<ClasificadoPesoResponseDTO> clasificadoPeso;

    @JsonProperty("clasificadoResumen")
    private List<ClasificadoResumenResponseDTO> clasificadoResumen;

    @JsonProperty("clasificadoDetalle")
    private List<ClasificadoDetalleResponseDTO> clasificadoDetalle;

    @JsonProperty("clasificadoErrorCarga")
    private List<ClasificadoErrorCargaResponseDTO> clasificadoErrorCarga;
}
