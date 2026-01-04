package com.pe.jm_alpaca_n8n_clasificado.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class ClasificadoActualizarDTO {

    @JsonProperty("idClasificado")
    @NotNull(message = "El idClasificado es requerido")
    private String idClasificado;

    @JsonProperty("idClasificador")
    @NotNull(message = "El clasificador es requerido")
    private String idClasificador;

    @JsonProperty("fecha")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate fecha;

    @JsonProperty("importeTotal")
    private BigDecimal importeTotal;

    @JsonProperty("observaciones")
    private String observaciones;
}
