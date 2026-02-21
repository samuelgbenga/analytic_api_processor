package com.moniepoint.analytic_api.service;

import com.moniepoint.analytic_api.dto.response.FailureRateResponse;
import com.moniepoint.analytic_api.dto.response.KycFunnelResponse;
import com.moniepoint.analytic_api.dto.response.TopMerchantResponse;
import com.moniepoint.analytic_api.repository.MerchantActivityRecordRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final MerchantActivityRecordRepository repository;

    public AnalyticsServiceImpl(MerchantActivityRecordRepository repository) {
        this.repository = repository;
    }

    @Override
    public TopMerchantResponse getTopMerchant() {
        return repository.findTopMerchantBySuccessfulVolume()
                .orElseThrow(() -> new RuntimeException("No merchant data found"));
    }

    @Override
    public Map<String, Long> getMonthlyActiveMerchants() {
        List<Object[]> results = repository.findMonthlyActiveMerchants();

        Map<String, Long> data = new LinkedHashMap<>();
        for (Object[] row : results) {
            if (row[0] == null) continue; // skip null months
            String month = (String) row[0];
            Long count = ((Number) row[1]).longValue(); // safe cast for H2
            data.put(month, count);
        }

        return data;
    }

    @Override
    public Map<String, Long> getProductAdoption() {
        List<Object[]> results = repository.findProductAdoption();

        // LinkedHashMap preserves insertion order (already sorted by repo query)
        Map<String, Long> data = new LinkedHashMap<>();
        for (Object[] row : results) {
            String product = (String) row[0];
            Long count = (Long) row[1];
            data.put(product, count);
        }

        return data;
    }

    @Override
    public KycFunnelResponse getKycFunnel() {
        long documentsSubmitted = repository.countKycByEventType("DOCUMENT_SUBMITTED");
        long verificationsCompleted = repository.countKycByEventType("VERIFICATION_COMPLETED");
        long tierUpgrades = repository.countKycByEventType("TIER_UPGRADE");

        return new KycFunnelResponse(documentsSubmitted, verificationsCompleted, tierUpgrades);
    }

    @Override
    public List<FailureRateResponse> getFailureRates() {
        List<Object[]> results = repository.findFailureRatesPerProduct();

        return results.stream()
                .map(row -> {
                    String product = (String) row[0];
                    long failed = (Long) row[1];
                    long total = (Long) row[2]; // SUCCESS + FAILED

                    double rate = total == 0 ? 0.0 :
                            BigDecimal.valueOf(failed)
                                    .multiply(BigDecimal.valueOf(100))
                                    .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP)
                                    .doubleValue();

                    return new FailureRateResponse(product, rate);
                })
                .sorted(Comparator.comparingDouble(FailureRateResponse::getFailureRate).reversed())
                .collect(Collectors.toList());
    }
}