package com.pe.jm_alpaca_n8n_clasificado.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClasificadoPesosInfoDTO {

    @JsonProperty("nombreArchivo")
    private String nombreArchivo;

    @JsonProperty("clasificador")
    private String clasificador;

    @JsonProperty("fecha")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate fecha;

    @JsonProperty("validado")
    private Integer validado;
}
