package com.pe.jm_alpaca_n8n_clasificado.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpResultDTO {
    private String codigo;
    private String descripcion;
}
