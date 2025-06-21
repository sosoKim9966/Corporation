package com.work.corporation.company.infra.repository.port;

import com.work.corporation.company.domain.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<CompanyEntity, Long>, CompanyRepositoryCustom {
}
