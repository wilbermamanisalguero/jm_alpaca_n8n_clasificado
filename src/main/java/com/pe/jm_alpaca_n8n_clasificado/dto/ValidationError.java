package com.pe.jm_alpaca_n8n_clasificado.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationError {
    private String codigo;
    private String errorCarga;
}
