package com.work.company.infra.repository;

import com.work.company.domain.model.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCompanyRepository extends JpaRepository<CompanyEntity, Long> {
}
