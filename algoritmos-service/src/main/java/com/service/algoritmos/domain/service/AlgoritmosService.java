package com.service.algoritmos.domain.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.service.algoritmos.domain.algorithm.PageReplacementAlgorithms;
import com.service.algoritmos.domain.model.PageReplacementResult;

/**
 * Servicio de algoritmos de reemplazo de páginas
 * Implementaciones puras en Java (FIFO, LRU, ÓPTIMO)
 */
@Service
public class AlgoritmosService {

    private static final Logger logger = LoggerFactory.getLogger(AlgoritmosService.class);

    private final boolean useNative;

    public AlgoritmosService() {
        this.useNative = PageRepJNABinding.isLoaded();
        if (useNative) {
            logger.info("✓ Librería nativa cargada correctamente; usando implementación nativa");
        } else {
            String err = PageRepJNABinding.getLoadError();
            logger.warn("La librería nativa no está disponible (se usará fallback Java): {}", err);
        }
    }

    public PageReplacementResult ejecutarFIFO(String referencias, int marcos) {
        validarParametros(referencias, marcos);
        int[] refs = parseReferences(referencias);
        if (useNative) {
            try {
                int[] res = NativePageReplacement.fifo(refs, marcos);
                return toResult(res, marcos);
            } catch (Throwable t) {
                logger.warn("Error en implementación nativa FIFO, haciendo fallback a Java: {}", t.getMessage());
            }
        }
        return PageReplacementAlgorithms.fifo(referencias, marcos);
    }

    public PageReplacementResult ejecutarLRU(String referencias, int marcos) {
        validarParametros(referencias, marcos);
        int[] refs = parseReferences(referencias);
        if (useNative) {
            try {
                int[] res = NativePageReplacement.lru(refs, marcos);
                return toResult(res, marcos);
            } catch (Throwable t) {
                logger.warn("Error en implementación nativa LRU, haciendo fallback a Java: {}", t.getMessage());
            }
        }
        return PageReplacementAlgorithms.lru(referencias, marcos);
    }

    public PageReplacementResult ejecutarOptimo(String referencias, int marcos) {
        validarParametros(referencias, marcos);
        int[] refs = parseReferences(referencias);
        if (useNative) {
            try {
                int[] res = NativePageReplacement.optimo(refs, marcos);
                return toResult(res, marcos);
            } catch (Throwable t) {
                logger.warn("Error en implementación nativa ÓPTIMO, haciendo fallback a Java: {}", t.getMessage());
            }
        }
        return PageReplacementAlgorithms.optimo(referencias, marcos);
    }

    private int[] parseReferences(String referencias) {
        String clean = referencias.replaceAll("\\s+", "");
        if (!clean.matches("\\d+")) {
            throw new IllegalArgumentException("Las referencias deben contener sólo dígitos (0-9)");
        }
        int n = clean.length();
        int[] refs = new int[n];
        for (int i = 0; i < n; i++) {
            refs[i] = clean.charAt(i) - '0';
        }
        return refs;
    }

    private PageReplacementResult toResult(int[] flat, int marcos) {
        if (flat == null || flat.length < 2) throw new IllegalStateException("Resultado nativo inválido");
        int fallos = flat[0];
        int pasos = flat[1];
        int[][] tabla = new int[pasos][marcos];
        int idx = 2;
        for (int i = 0; i < pasos; i++) {
            for (int j = 0; j < marcos; j++) {
                if (idx < flat.length) tabla[i][j] = flat[idx++];
                else tabla[i][j] = -1;
            }
        }
        return new PageReplacementResult(fallos, pasos, tabla, marcos);
    }

    private void validarParametros(String referencias, int marcos) {
        if (referencias == null || referencias.isEmpty()) {
            throw new IllegalArgumentException("Referencias no puede ser nula o vacía");
        }
        if (!referencias.replaceAll("\\s+", "").matches("\\d+")) {
            throw new IllegalArgumentException("Referencias debe contener sólo dígitos (0-9)");
        }
        if (marcos <= 0) {
            throw new IllegalArgumentException("Marcos debe ser mayor que 0");
        }
        if (marcos > 10) {
            throw new IllegalArgumentException("Marcos no puede ser mayor que 10");
        }
    }
}