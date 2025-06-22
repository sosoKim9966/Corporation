package com.work.company.domain.repository;

import com.work.company.domain.model.CompanyEntity;

import java.util.List;

public interface CompanyRepository {
    void saveAll(List<CompanyEntity> companies);
}
