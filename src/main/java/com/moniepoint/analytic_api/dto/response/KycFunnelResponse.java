package com.moniepoint.analytic_api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KycFunnelResponse {

    @JsonProperty("documents_submitted")
    private long documentsSubmitted;

    @JsonProperty("verifications_completed")
    private long verificationsCompleted;

    @JsonProperty("tier_upgrades")
    private long tierUpgrades;

    public KycFunnelResponse() {}

    public KycFunnelResponse(long documentsSubmitted, long verificationsCompleted, long tierUpgrades) {
        this.documentsSubmitted = documentsSubmitted;
        this.verificationsCompleted = verificationsCompleted;
        this.tierUpgrades = tierUpgrades;
    }

    public long getDocumentsSubmitted() { return documentsSubmitted; }
    public long getVerificationsCompleted() { return verificationsCompleted; }
    public long getTierUpgrades() { return tierUpgrades; }
    public void setDocumentsSubmitted(long documentsSubmitted) { this.documentsSubmitted = documentsSubmitted; }
    public void setVerificationsCompleted(long verificationsCompleted) { this.verificationsCompleted = verificationsCompleted; }
    public void setTierUpgrades(long tierUpgrades) { this.tierUpgrades = tierUpgrades; }
}