package com.moniepoint.analytic_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "merchant_activity_records",
        indexes = {
                @Index(name = "idx_status_merchant", columnList = "status, merchant_id"),
                @Index(name = "idx_status_timestamp", columnList = "status, event_timestamp"),
                @Index(name = "idx_product_status", columnList = "product, status"),
                @Index(name = "idx_product_status_merchant", columnList = "product, status, merchant_id")
        })
public class MerchantActivityRecord {

    @Id
    @Column(name = "event_id", nullable = false, updatable = false)
    private UUID eventId;

    @Column(name = "merchant_id", nullable = false, length = 20)
    private String merchantId;

    @Column(name = "event_timestamp")
    private LocalDateTime eventTimestamp;

    @Column(name = "product", nullable = false, length = 50)
    private String product;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Digits(integer = 17, fraction = 2)
    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "channel", nullable = false, length = 20)
    private String channel;

    @Column(name = "region", nullable = false, length = 50)
    private String region;

    @Column(name = "merchant_tier", nullable = false, length = 20)
    private String merchantTier;

    // --- Constructors ---

    public MerchantActivityRecord() {}

    public MerchantActivityRecord(UUID eventId, String merchantId, LocalDateTime eventTimestamp,
                                  String product, String eventType, BigDecimal amount,
                                  String status, String channel, String region, String merchantTier) {
        this.eventId = eventId;
        this.merchantId = merchantId;
        this.eventTimestamp = eventTimestamp;
        this.product = product;
        this.eventType = eventType;
        this.amount = amount;
        this.status = status;
        this.channel = channel;
        this.region = region;
        this.merchantTier = merchantTier;
    }

    // --- Getters ---

    public UUID getEventId() { return eventId; }
    public String getMerchantId() { return merchantId; }
    public LocalDateTime getEventTimestamp() { return eventTimestamp; }
    public String getProduct() { return product; }
    public String getEventType() { return eventType; }
    public BigDecimal getAmount() { return amount; }
    public String getStatus() { return status; }
    public String getChannel() { return channel; }
    public String getRegion() { return region; }
    public String getMerchantTier() { return merchantTier; }

    // --- Setters ---

    public void setEventId(UUID eventId) { this.eventId = eventId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }
    public void setEventTimestamp(LocalDateTime eventTimestamp) { this.eventTimestamp = eventTimestamp; }
    public void setProduct(String product) { this.product = product; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setStatus(String status) { this.status = status; }
    public void setChannel(String channel) { this.channel = channel; }
    public void setRegion(String region) { this.region = region; }
    public void setMerchantTier(String merchantTier) { this.merchantTier = merchantTier; }

    // --- Builder ---

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UUID eventId;
        private String merchantId;
        private LocalDateTime eventTimestamp;
        private String product;
        private String eventType;
        private BigDecimal amount;
        private String status;
        private String channel;
        private String region;
        private String merchantTier;

        public Builder eventId(UUID eventId) { this.eventId = eventId; return this; }
        public Builder merchantId(String merchantId) { this.merchantId = merchantId; return this; }
        public Builder eventTimestamp(LocalDateTime eventTimestamp) { this.eventTimestamp = eventTimestamp; return this; }
        public Builder product(String product) { this.product = product; return this; }
        public Builder eventType(String eventType) { this.eventType = eventType; return this; }
        public Builder amount(BigDecimal amount) { this.amount = amount; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder channel(String channel) { this.channel = channel; return this; }
        public Builder region(String region) { this.region = region; return this; }
        public Builder merchantTier(String merchantTier) { this.merchantTier = merchantTier; return this; }

        public MerchantActivityRecord build() {
            return new MerchantActivityRecord(eventId, merchantId, eventTimestamp, product,
                    eventType, amount, status, channel, region, merchantTier);
        }
    }
}