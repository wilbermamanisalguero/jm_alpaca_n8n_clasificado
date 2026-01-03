package com.pe.jm_alpaca_n8n_clasificado.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClasificadoDetalleDTO {

    @JsonProperty("ROYAL")
    private DetalleDataDTO royal;

    @JsonProperty("BL")
    private DetalleDataDTO bl;

    @JsonProperty("FS")
    private DetalleDataDTO fs;

    @JsonProperty("HZ")
    private DetalleDataDTO hz;

    @JsonProperty("STD")
    private DetalleDataDTO std;

    @JsonProperty("SURI")
    private DetalleDataDTO suri;

    @JsonProperty("SURI-HZ")
    private DetalleDataDTO suriHz;
}
