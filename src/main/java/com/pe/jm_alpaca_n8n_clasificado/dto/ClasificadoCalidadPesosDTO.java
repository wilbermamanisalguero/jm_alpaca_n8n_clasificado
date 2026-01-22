package com.pe.jm_alpaca_n8n_clasificado.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClasificadoCalidadPesosDTO {

    @JsonProperty("ROYAL")
    private CalidadPesosDTO royal;

    @JsonProperty("BL-B")
    private CalidadPesosDTO blB;

    @JsonProperty("BL-X")
    private CalidadPesosDTO blX;

    @JsonProperty("FS-B")
    private CalidadPesosDTO fsB;

    @JsonProperty("FS-X")
    private CalidadPesosDTO fsX;

    @JsonProperty("HZ-B")
    private CalidadPesosDTO hzB;

    @JsonProperty("HZ-X")
    private CalidadPesosDTO hzX;

    @JsonProperty("AG")
    private CalidadPesosDTO ag;

    @JsonProperty("STD")
    private CalidadPesosDTO std;

    @JsonProperty("SURI-BL")
    private CalidadPesosDTO suriBl;

    @JsonProperty("SURI-FS")
    private CalidadPesosDTO suriFs;

    @JsonProperty("SURI-HZ")
    private CalidadPesosDTO suriHz;

    @JsonProperty("SURI-STD")
    private CalidadPesosDTO suriStd;
}
