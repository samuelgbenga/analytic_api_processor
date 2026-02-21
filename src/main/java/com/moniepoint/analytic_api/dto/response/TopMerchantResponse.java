package com.moniepoint.analytic_api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class TopMerchantResponse {

    @JsonProperty("merchant_id")
    private String merchantId;

    @JsonProperty("total_volume")
    private BigDecimal totalVolume;

    public TopMerchantResponse() {}

    public TopMerchantResponse(String merchantId, BigDecimal totalVolume) {
        this.merchantId = merchantId;
        this.totalVolume = totalVolume;
    }

    public String getMerchantId() { return merchantId; }
    public BigDecimal getTotalVolume() { return totalVolume; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }
    public void setTotalVolume(BigDecimal totalVolume) { this.totalVolume = totalVolume; }
}