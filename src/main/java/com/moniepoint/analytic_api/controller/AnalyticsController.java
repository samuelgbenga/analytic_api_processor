package com.moniepoint.analytic_api.controller;

import com.moniepoint.analytic_api.dto.response.FailureRateResponse;
import com.moniepoint.analytic_api.dto.response.KycFunnelResponse;
import com.moniepoint.analytic_api.dto.response.TopMerchantResponse;
import com.moniepoint.analytic_api.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    // GET /analytics/top-merchant
    @GetMapping("/top-merchant")
    public ResponseEntity<TopMerchantResponse> getTopMerchant() {
        return ResponseEntity.ok(analyticsService.getTopMerchant());
    }

    // GET /analytics/monthly-active-merchants
    @GetMapping("/monthly-active-merchants")
    public ResponseEntity<Map<String, Long>> getMonthlyActiveMerchants() {
        return ResponseEntity.ok(analyticsService.getMonthlyActiveMerchants());
    }

    // GET /analytics/product-adoption
    @GetMapping("/product-adoption")
    public ResponseEntity<Map<String, Long>> getProductAdoption() {
        return ResponseEntity.ok(analyticsService.getProductAdoption());
    }

    // GET /analytics/kyc-funnel
    @GetMapping("/kyc-funnel")
    public ResponseEntity<KycFunnelResponse> getKycFunnel() {
        return ResponseEntity.ok(analyticsService.getKycFunnel());
    }

    // GET /analytics/failure-rates
    @GetMapping("/failure-rates")
    public ResponseEntity<List<FailureRateResponse>> getFailureRates() {
        return ResponseEntity.ok(analyticsService.getFailureRates());
    }
}