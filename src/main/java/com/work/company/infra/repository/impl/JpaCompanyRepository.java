package com.work.company.infra.repository.impl;

import com.work.company.domain.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCompanyRepository extends JpaRepository<CompanyEntity, Long> {
}
