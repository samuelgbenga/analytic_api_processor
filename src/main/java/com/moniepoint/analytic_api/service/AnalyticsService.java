package com.moniepoint.analytic_api.service;

import com.moniepoint.analytic_api.dto.response.FailureRateResponse;
import com.moniepoint.analytic_api.dto.response.KycFunnelResponse;
import com.moniepoint.analytic_api.dto.response.TopMerchantResponse;

import java.util.List;
import java.util.Map;

public interface AnalyticsService {

    TopMerchantResponse getTopMerchant();

    Map<String, Long> getMonthlyActiveMerchants();

    Map<String, Long> getProductAdoption();

    KycFunnelResponse getKycFunnel();

    List<FailureRateResponse> getFailureRates();
}