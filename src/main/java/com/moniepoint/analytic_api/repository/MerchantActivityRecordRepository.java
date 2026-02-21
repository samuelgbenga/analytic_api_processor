package com.moniepoint.analytic_api.repository;

import com.moniepoint.analytic_api.dto.response.TopMerchantResponse;
import com.moniepoint.analytic_api.entity.MerchantActivityRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MerchantActivityRecordRepository extends JpaRepository<MerchantActivityRecord, UUID> {

    // 1. Top merchant by total successful transaction volume
//    @Query("""
//            SELECT m.merchantId, SUM(m.amount)
//            FROM MerchantActivityRecord m
//            WHERE m.status = 'SUCCESS'
//            GROUP BY m.merchantId
//            ORDER BY SUM(m.amount) DESC
//            LIMIT 1
//            """)
//    Optional<Object[]> findTopMerchantBySuccessfulVolume();

    // 2. Count of unique merchants with at least one successful event per month
//    @Query("""
//            SELECT FUNCTION('DATE_FORMAT', m.eventTimestamp, '%Y-%m'), COUNT(DISTINCT m.merchantId)
//            FROM MerchantActivityRecord m
//            WHERE m.status = 'SUCCESS'
//            AND m.eventTimestamp IS NOT NULL
//            GROUP BY FUNCTION('DATE_FORMAT', m.eventTimestamp, '%Y-%m')
//            ORDER BY FUNCTION('DATE_FORMAT', m.eventTimestamp, '%Y-%m') ASC
//            """)
//    List<Object[]> findMonthlyActiveMerchants();

    @Query(value = """
        SELECT FORMATDATETIME(event_timestamp, 'yyyy-MM') as event_month,
               COUNT(DISTINCT merchant_id) as merchant_count
        FROM merchant_activity_records
        WHERE status = 'SUCCESS'
        AND event_timestamp IS NOT NULL
        GROUP BY FORMATDATETIME(event_timestamp, 'yyyy-MM')
        ORDER BY event_month ASC
        """, nativeQuery = true)
    List<Object[]> findMonthlyActiveMerchants();

    // 3. Unique merchant count per product sorted by count descending
    @Query("""
            SELECT m.product, COUNT(DISTINCT m.merchantId)
            FROM MerchantActivityRecord m
            GROUP BY m.product
            ORDER BY COUNT(DISTINCT m.merchantId) DESC
            """)
    List<Object[]> findProductAdoption();

    // 4. KYC funnel - distinct merchants per event type (SUCCESS only)
    @Query("""
            SELECT COUNT(DISTINCT m.merchantId)
            FROM MerchantActivityRecord m
            WHERE m.product = 'KYC'
            AND m.status = 'SUCCESS'
            AND m.eventType = :eventType
            """)
    long countKycByEventType(@Param("eventType") String eventType);

    // 5. Failure rates per product (exclude PENDING)
    @Query("""
            SELECT m.product,
                   SUM(CASE WHEN m.status = 'FAILED' THEN 1 ELSE 0 END),
                   COUNT(m)
            FROM MerchantActivityRecord m
            WHERE m.status IN ('SUCCESS', 'FAILED')
            GROUP BY m.product
            """)
    List<Object[]> findFailureRatesPerProduct();


    // In your repository
    @Query("""
        SELECT new com.moniepoint.analytic_api.dto.response.TopMerchantResponse(m.merchantId, SUM(m.amount))
        FROM MerchantActivityRecord m
        WHERE m.status = 'SUCCESS'
        GROUP BY m.merchantId
        ORDER BY SUM(m.amount) DESC
        LIMIT 1
        """)
    Optional<TopMerchantResponse> findTopMerchantBySuccessfulVolume();

}