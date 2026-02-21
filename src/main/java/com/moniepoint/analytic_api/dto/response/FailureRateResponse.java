package com.moniepoint.analytic_api.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;

public class FailureRateResponse {

    @JsonProperty("product")
    private String product;

    @JsonProperty("failure_rate")
    private double failureRate;

    public FailureRateResponse() {}

    public FailureRateResponse(String product, double failureRate) {
        this.product = product;
        this.failureRate = failureRate;
    }

    public String getProduct() { return product; }
    public double getFailureRate() { return failureRate; }
    public void setProduct(String product) { this.product = product; }
    public void setFailureRate(double failureRate) { this.failureRate = failureRate; }
}