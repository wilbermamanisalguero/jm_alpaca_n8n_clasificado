package com.pe.jm_alpaca_n8n_clasificado.controller;

import com.pe.jm_alpaca_n8n_clasificado.dto.ClasificadoActualizarRequestDTO;
import com.pe.jm_alpaca_n8n_clasificado.dto.ClasificadoRequestDTO;
import com.pe.jm_alpaca_n8n_clasificado.dto.ConsultaClasificadoRequestDTO;
import com.pe.jm_alpaca_n8n_clasificado.service.ClasificadoService;
import io.vertx.core.Future;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/clasificado")
public class ClasificadoController {

    private final ClasificadoService clasificadoService;

    public ClasificadoController(ClasificadoService clasificadoService) {
        this.clasificadoService = clasificadoService;
    }

    @PostMapping("/registrar")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> registrarClasificado(
            @Valid @RequestBody ClasificadoRequestDTO request) {

        CompletableFuture<ResponseEntity<Map<String, Object>>> completableFuture = new CompletableFuture<>();

        Future<Map<String, Object>> future = clasificadoService.registrarClasificado(request);

        future.onComplete(ar -> {
            if (ar.succeeded()) {
                Map<String, Object> result = ar.result();
                Boolean success = (Boolean) result.get("success");

                if (Boolean.TRUE.equals(success)) {
                    completableFuture.complete(ResponseEntity.ok(result));
                } else {
                    completableFuture.complete(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result));
                }
            } else {
                Map<String, Object> errorResponse = Map.of(
                        "success", false,
                        "message", "Error al procesar el clasificado",
                        "error", ar.cause().getMessage()
                );
                completableFuture.complete(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
            }
        });

        return completableFuture;
    }

    @GetMapping("/consultar")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> consultarClasificado(
            @Valid @RequestBody ConsultaClasificadoRequestDTO request) {

        CompletableFuture<ResponseEntity<Map<String, Object>>> completableFuture = new CompletableFuture<>();

        Future<Map<String, Object>> future = clasificadoService.consultarClasificado(request.getIdClasificado());

        future.onComplete(ar -> {
            if (ar.succeeded()) {
                Map<String, Object> result = ar.result();
                Boolean success = (Boolean) result.get("success");

                if (Boolean.TRUE.equals(success)) {
                    completableFuture.complete(ResponseEntity.ok(result));
                } else {
                    completableFuture.complete(ResponseEntity.status(HttpStatus.NOT_FOUND).body(result));
                }
            } else {
                Map<String, Object> errorResponse = Map.of(
                        "success", false,
                        "message", "Error al consultar el clasificado",
                        "error", ar.cause().getMessage()
                );
                completableFuture.complete(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
            }
        });

        return completableFuture;
    }

    @PostMapping("/actualizar")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> actualizarClasificado(
            @Valid @RequestBody ClasificadoActualizarRequestDTO request) {

        CompletableFuture<ResponseEntity<Map<String, Object>>> completableFuture = new CompletableFuture<>();

        Future<Map<String, Object>> future = clasificadoService.actualizarClasificado(request);

        future.onComplete(ar -> {
            if (ar.succeeded()) {
                Map<String, Object> result = ar.result();
                Boolean success = (Boolean) result.get("success");

                if (Boolean.TRUE.equals(success)) {
                    completableFuture.complete(ResponseEntity.ok(result));
                } else {
                    completableFuture.complete(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result));
                }
            } else {
                Map<String, Object> errorResponse = Map.of(
                        "success", false,
                        "message", "Error al actualizar el clasificado",
                        "error", ar.cause().getMessage()
                );
                completableFuture.complete(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
            }
        });

        return completableFuture;
    }
}
