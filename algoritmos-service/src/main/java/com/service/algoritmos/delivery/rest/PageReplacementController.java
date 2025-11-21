package com.service.algoritmos.delivery.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.service.algoritmos.delivery.rest.dto.PageReplacementRequest;
import com.service.algoritmos.domain.model.PageReplacementResult;
import com.service.algoritmos.domain.service.AlgoritmosService;

/**
 * Controlador REST para los algoritmos de reemplazo de páginas
 * Proporciona endpoints para ejecutar FIFO, LRU y ÓPTIMO
 */
@RestController
@RequestMapping("/api/algoritmos/pagereplacement")
public class PageReplacementController {

    @Autowired
    private AlgoritmosService algoritmosService;

    /**
     * Endpoint para ejecutar el algoritmo FIFO
     * POST /api/algoritmos/pagereplacement/fifo
     * Body: {"referencias": "7045679", "marcos": 3}
     */
    @PostMapping("/fifo")
    public ResponseEntity<?> fifo(@RequestBody PageReplacementRequest request) {
        try {
            if (request.getReferencias() == null || request.getReferencias().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Referencias no puede ser nula o vacía"));
            }
            if (request.getMarcos() <= 0 || request.getMarcos() > 10) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Marcos debe estar entre 1 y 10"));
            }

            PageReplacementResult resultado = algoritmosService.ejecutarFIFO(
                    request.getReferencias(),
                    request.getMarcos()
            );
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error al ejecutar FIFO: " + e.getMessage()));
        }
    }

    /**
     * Endpoint para ejecutar el algoritmo LRU
     * POST /api/algoritmos/pagereplacement/lru
     * Body: {"referencias": "7045679", "marcos": 3}
     */
    @PostMapping("/lru")
    public ResponseEntity<?> lru(@RequestBody PageReplacementRequest request) {
        try {
            if (request.getReferencias() == null || request.getReferencias().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Referencias no puede ser nula o vacía"));
            }
            if (request.getMarcos() <= 0 || request.getMarcos() > 10) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Marcos debe estar entre 1 y 10"));
            }

            PageReplacementResult resultado = algoritmosService.ejecutarLRU(
                    request.getReferencias(),
                    request.getMarcos()
            );
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error al ejecutar LRU: " + e.getMessage()));
        }
    }

    /**
     * Endpoint para ejecutar el algoritmo ÓPTIMO
     * POST /api/algoritmos/pagereplacement/optimo
     * Body: {"referencias": "7045679", "marcos": 3}
     */
    @PostMapping("/optimo")
    public ResponseEntity<?> optimo(@RequestBody PageReplacementRequest request) {
        try {
            if (request.getReferencias() == null || request.getReferencias().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Referencias no puede ser nula o vacía"));
            }
            if (request.getMarcos() <= 0 || request.getMarcos() > 10) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Marcos debe estar entre 1 y 10"));
            }

            PageReplacementResult resultado = algoritmosService.ejecutarOptimo(
                    request.getReferencias(),
                    request.getMarcos()
            );
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error al ejecutar ÓPTIMO: " + e.getMessage()));
        }
    }

    /**
     * Endpoint de salud para verificar que el servicio está activo
     * GET /api/algoritmos/pagereplacement/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Page Replacement Service is running");
    }

    /**
     * Endpoint para obtener información sobre los algoritmos disponibles
     * GET /api/algoritmos/pagereplacement/info
     */
    @GetMapping("/info")
    public ResponseEntity<String> info() {
        return ResponseEntity.ok("Available algorithms: FIFO, LRU, ÓPTIMO");
    }

    /**
     * Clase interna para respuestas de error
     */
    public static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
