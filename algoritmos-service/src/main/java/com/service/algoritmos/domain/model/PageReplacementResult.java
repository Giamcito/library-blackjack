package com.service.algoritmos.domain.model;

public class PageReplacementResult {
    private int misses;
    private int steps;
    private int[][] pageTable;
    private int frames;

    public PageReplacementResult(int misses, int steps, int[][] pageTable, int frames) {
        this.misses = misses;
        this.steps = steps;
        this.pageTable = pageTable;
        this.frames = frames;
    }

    public int getMisses() {
        return misses;
    }

    public void setMisses(int misses) {
        this.misses = misses;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int[][] getPageTable() {
        return pageTable;
    }

    public void setPageTable(int[][] pageTable) {
        this.pageTable = pageTable;
    }

    public int getFrames() {
        return frames;
    }

    public void setFrames(int frames) {
        this.frames = frames;
    }

    public double getHitRate() {
        return steps > 0 ? ((steps - misses) / (double) steps) * 100 : 0;
    }

    @Override
    public String toString() {
        return "PageReplacementResult{" +
                "misses=" + misses +
                ", steps=" + steps +
                ", frames=" + frames +
                ", hitRate=" + String.format("%.2f", getHitRate()) + "%" +
                '}';
    }
}
