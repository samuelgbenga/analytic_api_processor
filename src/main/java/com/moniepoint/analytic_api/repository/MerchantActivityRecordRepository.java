package com.moniepoint.analytic_api.repository;

import com.moniepoint.analytic_api.entity.MerchantActivityRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MerchantActivityRecordRepository extends JpaRepository<MerchantActivityRecord, UUID> {
}
