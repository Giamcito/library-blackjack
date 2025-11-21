// Este archivo fue dejado como marcador: la integración JNA/JNI fue retirada
// porque la implementación nativa causó inestabilidad. El proyecto usa
// ahora `PageReplacementAlgorithms` (implementación Java pura).
// Si necesita restaurar la integración nativa, revierta los cambios en
// el control de versiones o consulte las fuentes originales en el repo.

package com.service.algoritmos.domain.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.sun.jna.NativeLibrary;

/**
 * Carga `libpagerep.so` desde los recursos y expone la instancia de
 * NativeLibrary para su uso mediante JNA. Intenta comprobar que las
 * funciones esperadas existen en la librería.
 */
public final class PageRepJNABinding {
    private PageRepJNABinding() {}

    private static boolean loaded = false;
    private static String loadError = null;
    private static NativeLibrary nativeLib = null;

    public static boolean isLoaded() { return loaded; }

    public static String getLoadError() { return loadError == null ? "" : loadError; }

    public static NativeLibrary getNativeLibrary() {
        return nativeLib;
    }

    static {
        String os = System.getProperty("os.name");
        boolean success = false;
        try {
            boolean isWindows = os != null && os.toLowerCase().contains("win");

            String resourceName = isWindows ? "/libpagerep.dll" : "/libpagerep.so";
            InputStream in = PageRepJNABinding.class.getResourceAsStream(resourceName);

            block: {
                if (in == null) {
                    loadError = "Recurso '" + resourceName + "' no encontrado en classpath. Asegúrese de que el archivo esté en src/main/resources.";
                    break block;
                }

                // Extraer el recurso a archivo temporal con la extensión adecuada
                String ext = isWindows ? ".dll" : ".so";
                File tmp = File.createTempFile("libpagerep", ext);
                tmp.deleteOnExit();
                try (FileOutputStream out = new FileOutputStream(tmp)) {
                    byte[] buf = new byte[8192];
                    int r;
                    while ((r = in.read(buf)) != -1) out.write(buf, 0, r);
                }

                try {
                    // Intentar cargar con System.load
                    System.load(tmp.getAbsolutePath());
                } catch (UnsatisfiedLinkError ule) {
                    loadError = "No se pudo cargar la librería nativa desde '" + tmp.getAbsolutePath() + "': " + ule.getMessage();
                    break block;
                }

                // Abrir con JNA para permitir lookup de funciones
                nativeLib = NativeLibrary.getInstance(tmp.getAbsolutePath());

                // Verificar que las funciones esperadas existan (sanity-check)
                String[] expected = new String[]{"fifo", "lru", "optimo"};
                for (String fn : expected) {
                    try {
                        // getFunction puede lanzar UnsatisfiedLinkError
                        nativeLib.getFunction(fn);
                    } catch (UnsatisfiedLinkError ule) {
                        loadError = "Función nativa no encontrada: " + fn + " -> " + ule.getMessage();
                        nativeLib = null;
                        break block;
                    }
                }

                success = true;
            }

        } catch (Exception e) {
            loadError = e.getClass().getName() + ": " + e.getMessage();
            success = false;
            nativeLib = null;
        }

        loaded = success;
    }
}
