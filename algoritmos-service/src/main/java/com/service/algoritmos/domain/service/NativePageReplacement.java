package com.service.algoritmos.domain.service;

import com.sun.jna.Function;
import com.sun.jna.NativeLibrary;

/**
 * Helper que invoca las funciones nativas exportadas por la librería
 * mediante JNA. Espera que las funciones `fifo`, `lru`, `optimo`
 * reciban `(int referencias[], int n, int marcos)` y devuelvan un
 * arreglo plano de enteros: [fallos, pasos, tabla...]
 */
public final class NativePageReplacement {
    private NativePageReplacement() {}

    private static NativeLibrary lib() {
        NativeLibrary nl = PageRepJNABinding.getNativeLibrary();
        if (nl == null) throw new IllegalStateException("Native library not available");
        return nl;
    }

    private static int[] invoke(String fn, int[] referencias, int marcos) {
        try {
            Function f = resolveFunction(lib(), fn, referencias.length);
            // Intentamos pedir JNA que devuelva un int[] directamente.
            Object ret = f.invoke(int[].class, new Object[]{referencias, referencias.length, marcos});
            if (ret instanceof int[]) return (int[]) ret;
            // Si JNA no pudo convertir directamente, intentar otras rutas
            throw new UnsupportedOperationException("La función nativa no devolvió un int[] (resultado: " +
                    (ret == null ? "null" : ret.getClass().getName()) + ")");
        } catch (Throwable t) {
            throw new RuntimeException("Error invocando función nativa '" + fn + "'", t);
        }
    }

    public static int[] fifo(int[] referencias, int marcos) { return invoke("fifo", referencias, marcos); }
    public static int[] lru(int[] referencias, int marcos) { return invoke("lru", referencias, marcos); }
    public static int[] optimo(int[] referencias, int marcos) { return invoke("optimo", referencias, marcos); }

    // Intenta resolver la función nativa probando variantes comunes de nombres
    private static Function resolveFunction(NativeLibrary lib, String baseName, int numArrayLen) {
        String[] candidates = new String[] {
            baseName,
            "_" + baseName,
            baseName + "@12",    // stdcall con 3 ints -> 12 bytes
            "_" + baseName + "@12",
            baseName + "@16",
            "_" + baseName + "@16"
        };

        for (String cand : candidates) {
            try {
                Function f = lib.getFunction(cand);
                if (f != null) return f;
            } catch (UnsatisfiedLinkError ignored) {
                // probar siguiente candidato
            }
        }

        // si no se encuentra, lanzar con información útil
        String msg = "No se encontró la función nativa '" + baseName + "' (probados: " + String.join(",", candidates) + ")";
        throw new UnsatisfiedLinkError(msg + ". Revise los símbolos exportados en la DLL/SO.");
    }
}
