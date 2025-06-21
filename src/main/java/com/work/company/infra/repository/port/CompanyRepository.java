package com.work.company.infra.repository.port;

import com.work.company.domain.CompanyEntity;

import java.util.List;

public interface CompanyRepository {
    void saveAll(List<CompanyEntity> companies);

    void testCode();
}
