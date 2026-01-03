package com.pe.jm_alpaca_n8n_clasificado.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClasificadoCalidadDTO {

    @JsonProperty("ROYAL")
    private CalidadDataDTO royal;

    @JsonProperty("BL-B")
    private CalidadDataDTO blB;

    @JsonProperty("BL-X")
    private CalidadDataDTO blX;

    @JsonProperty("FS-B")
    private CalidadDataDTO fsB;

    @JsonProperty("FS-X")
    private CalidadDataDTO fsX;

    @JsonProperty("HZ-B")
    private CalidadDataDTO hzB;

    @JsonProperty("HZ-X")
    private CalidadDataDTO hzX;

    @JsonProperty("AG")
    private CalidadDataDTO ag;

    @JsonProperty("STD")
    private CalidadDataDTO std;

    @JsonProperty("SURI-BL")
    private CalidadDataDTO suriBl;

    @JsonProperty("SURI-FS")
    private CalidadDataDTO suriFs;

    @JsonProperty("SURI-HZ")
    private CalidadDataDTO suriHz;
}
