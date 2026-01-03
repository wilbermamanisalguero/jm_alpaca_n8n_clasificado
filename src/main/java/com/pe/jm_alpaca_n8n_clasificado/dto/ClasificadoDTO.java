package com.pe.jm_alpaca_n8n_clasificado.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClasificadoDTO {

    @JsonProperty("nombreArchivo")
    private String nombreArchivo;

    @JsonProperty("clasificador")
    @NotNull(message = "El clasificador es requerido")
    private String clasificador;

    @JsonProperty("fecha")
    private LocalDate fecha;

    @JsonProperty("observaciones")
    private String observaciones;

    @JsonProperty("importe_total")
    private BigDecimal importeTotal;
}
