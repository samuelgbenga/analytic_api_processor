package com.moniepoint.analytic_api.repository;

import com.moniepoint.analytic_api.entity.LoadedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoadedFileRepository extends JpaRepository<LoadedFile, String> {
}