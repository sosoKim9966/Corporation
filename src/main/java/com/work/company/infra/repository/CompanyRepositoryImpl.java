package com.work.company.infra.repository;

import com.work.company.domain.model.CompanyEntity;
import com.work.company.domain.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CompanyRepositoryImpl implements CompanyRepository {

    private final JpaCompanyRepository repository;

    @Override
    public void saveAll(List<CompanyEntity> companies) {
        log.info("[orderRepository] 실행");
        repository.saveAll(companies);
    }

}
