package com.service.algoritmos.domain.algorithm;

import com.service.algoritmos.domain.model.PageReplacementResult;

public class PageReplacementAlgorithms {

    public static PageReplacementResult fifo(String referencias, int marcos) {
        int pasos = referencias.length();
        int[] refs = parseReferences(referencias);
        int[][] tabla = createTable(pasos, marcos);
        int[] frames = createFrames(marcos);

        int misses = 0;
        int fifoPtr = 0;

        for (int i = 0; i < pasos; i++) {
            int r = refs[i];
            boolean hit = contains(frames, r);
            if (!hit) {
                misses++;
                frames[fifoPtr] = r;
                fifoPtr = (fifoPtr + 1) % marcos;
            }
            fillRow(tabla, i, frames);
        }

        return new PageReplacementResult(misses, pasos, tabla, marcos);
    }

    public static PageReplacementResult lru(String referencias, int marcos) {
        int pasos = referencias.length();
        int[] refs = parseReferences(referencias);
        int[][] tabla = createTable(pasos, marcos);
        int[] frames = createFrames(marcos);
        int[] lastUsed = new int[marcos];
        for (int i = 0; i < marcos; i++) lastUsed[i] = -1;

        int misses = 0;

        for (int i = 0; i < pasos; i++) {
            int r = refs[i];
            int idx = indexOf(frames, r);
            if (idx != -1) {
                lastUsed[idx] = i;
            } else {
                misses++;
                int empty = indexOf(frames, -1);
                if (empty != -1) {
                    frames[empty] = r;
                    lastUsed[empty] = i;
                } else {
                    int lruIdx = 0;
                    int min = Integer.MAX_VALUE;
                    for (int j = 0; j < marcos; j++) {
                        if (lastUsed[j] < min) {
                            min = lastUsed[j];
                            lruIdx = j;
                        }
                    }
                    frames[lruIdx] = r;
                    lastUsed[lruIdx] = i;
                }
            }
            fillRow(tabla, i, frames);
        }

        return new PageReplacementResult(misses, pasos, tabla, marcos);
    }

    public static PageReplacementResult optimo(String referencias, int marcos) {
        int pasos = referencias.length();
        int[] refs = parseReferences(referencias);
        int[][] tabla = createTable(pasos, marcos);
        int[] frames = createFrames(marcos);

        int misses = 0;

        for (int i = 0; i < pasos; i++) {
            int r = refs[i];
            int idx = indexOf(frames, r);
            if (idx != -1) {
                // hit
            } else {
                misses++;
                int empty = indexOf(frames, -1);
                if (empty != -1) {
                    frames[empty] = r;
                } else {
                    int replaceIdx = 0;
                    int farthest = -1;
                    for (int j = 0; j < marcos; j++) {
                        int next = nextUseIndex(refs, i + 1, frames[j]);
                        if (next == -1) {
                            replaceIdx = j;
                            farthest = Integer.MAX_VALUE;
                            break;
                        }
                        if (next > farthest) {
                            farthest = next;
                            replaceIdx = j;
                        }
                    }
                    frames[replaceIdx] = r;
                }
            }
            fillRow(tabla, i, frames);
        }

        return new PageReplacementResult(misses, pasos, tabla, marcos);
    }

    private static int[] parseReferences(String referencias) {
        int n = referencias.length();
        int[] refs = new int[n];
        for (int i = 0; i < n; i++) {
            char c = referencias.charAt(i);
            if (Character.isDigit(c)) refs[i] = c - '0';
            else refs[i] = c;
        }
        return refs;
    }

    private static int[][] createTable(int pasos, int marcos) {
        int[][] tabla = new int[pasos][marcos];
        for (int i = 0; i < pasos; i++) {
            for (int j = 0; j < marcos; j++) tabla[i][j] = -1;
        }
        return tabla;
    }

    private static int[] createFrames(int marcos) {
        int[] frames = new int[marcos];
        for (int i = 0; i < marcos; i++) frames[i] = -1;
        return frames;
    }

    private static boolean contains(int[] arr, int v) {
        for (int x : arr) if (x == v) return true;
        return false;
    }

    private static int indexOf(int[] arr, int v) {
        for (int i = 0; i < arr.length; i++) if (arr[i] == v) return i;
        return -1;
    }

    private static void fillRow(int[][] tabla, int row, int[] frames) {
        for (int j = 0; j < frames.length; j++) tabla[row][j] = frames[j];
    }

    private static int nextUseIndex(int[] refs, int start, int value) {
        for (int i = start; i < refs.length; i++) if (refs[i] == value) return i;
        return -1;
    }
}
