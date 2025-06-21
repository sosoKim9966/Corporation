package com.work.corporation.company.application;

import com.work.corporation.company.infra.repository.port.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;

    public void testService(String test) {
        log.info("[companyService] 실행");
        companyRepository.testRepository(test);
    }
}
