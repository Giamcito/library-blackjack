package com.service.algoritmos.delivery.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PageReplacementRequest {
    @JsonProperty("referencias")
    private String referencias;
    
    @JsonProperty("marcos")
    private int marcos;

    public PageReplacementRequest() {}

    public PageReplacementRequest(String referencias, int marcos) {
        this.referencias = referencias;
        this.marcos = marcos;
    }

    public String getReferencias() {
        return referencias;
    }

    public void setReferencias(String referencias) {
        this.referencias = referencias;
    }

    public int getMarcos() {
        return marcos;
    }

    public void setMarcos(int marcos) {
        this.marcos = marcos;
    }

    @Override
    public String toString() {
        return "PageReplacementRequest{" +
                "referencias='" + referencias + '\'' +
                ", marcos=" + marcos +
                '}';
    }
}
