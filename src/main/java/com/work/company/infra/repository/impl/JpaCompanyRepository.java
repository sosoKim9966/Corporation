package com.work.company.infra.repository.impl;

import com.work.company.domain.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepositoryCustom extends JpaRepository<CompanyEntity, Long>, CompanyRepositoryCustom{
    String testRepository(String test);
}
