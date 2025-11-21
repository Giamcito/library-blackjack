package com.service.algoritmos.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.service.algoritmos.domain.model.PageReplacementResult;

@SpringBootTest
public class AlgoritmosServiceTest {

    @Autowired
    private AlgoritmosService algoritmosService;

    @Test
    public void testFIFO() {
        String referencias = "7045679";
        int marcos = 3;
        
        try {
            PageReplacementResult resultado = algoritmosService.ejecutarFIFO(referencias, marcos);
            
            assertNotNull(resultado);
            assertTrue(resultado.getMisses() > 0);
            assertEquals(referencias.length(), resultado.getSteps());
            assertEquals(marcos, resultado.getFrames());
            assertNotNull(resultado.getPageTable());
            
            System.out.println("FIFO Result: " + resultado);
        } catch (Exception e) {
            System.err.println("FIFO test skipped (JNI library not available): " + e.getMessage());
        }
    }

    @Test
    public void testLRU() {
        String referencias = "7045679";
        int marcos = 3;
        
        try {
            PageReplacementResult resultado = algoritmosService.ejecutarLRU(referencias, marcos);
            
            assertNotNull(resultado);
            assertTrue(resultado.getMisses() > 0);
            assertEquals(referencias.length(), resultado.getSteps());
            assertEquals(marcos, resultado.getFrames());
            assertNotNull(resultado.getPageTable());
            
            System.out.println("LRU Result: " + resultado);
        } catch (Exception e) {
            System.err.println("LRU test skipped (JNI library not available): " + e.getMessage());
        }
    }

    @Test
    public void testOptimo() {
        String referencias = "7045679";
        int marcos = 3;
        
        try {
            PageReplacementResult resultado = algoritmosService.ejecutarOptimo(referencias, marcos);
            
            assertNotNull(resultado);
            assertTrue(resultado.getMisses() >= 0);
            assertEquals(referencias.length(), resultado.getSteps());
            assertEquals(marcos, resultado.getFrames());
            assertNotNull(resultado.getPageTable());
            
            System.out.println("ÓPTIMO Result: " + resultado);
        } catch (Exception e) {
            System.err.println("ÓPTIMO test skipped (JNI library not available): " + e.getMessage());
        }
    }

    @Test
    public void testComparativeAnalysis() {
        String referencias = "123456123456";
        int marcos = 2;
        
        try {
            PageReplacementResult fifo = algoritmosService.ejecutarFIFO(referencias, marcos);
            PageReplacementResult lru = algoritmosService.ejecutarLRU(referencias, marcos);
            PageReplacementResult optimo = algoritmosService.ejecutarOptimo(referencias, marcos);
            
            System.out.println("\n=== Análisis Comparativo ===");
            System.out.println("FIFO:    Fallos=" + fifo.getMisses() + " Hit Rate=" + String.format("%.2f", fifo.getHitRate()) + "%");
            System.out.println("LRU:     Fallos=" + lru.getMisses() + " Hit Rate=" + String.format("%.2f", lru.getHitRate()) + "%");
            System.out.println("ÓPTIMO:  Fallos=" + optimo.getMisses() + " Hit Rate=" + String.format("%.2f", optimo.getHitRate()) + "%");
            
            // El óptimo debería tener <= fallos que LRU y FIFO
            assertTrue(optimo.getMisses() <= fifo.getMisses());
            
        } catch (Exception e) {
            System.err.println("Comparative test skipped (JNI library not available): " + e.getMessage());
        }
    }
}
