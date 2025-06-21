package com.work.corporation.company.infra.repository.impl;

import com.work.corporation.company.infra.repository.port.CompanyRepositoryCustom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class CompanyRepositoryImpl implements CompanyRepositoryCustom {

    @Override
    public String testRepository(String test) {
        log.info("[orderRepository] 실행");

        //저장 로직
        if (test.equals("ex")) {
            throw new IllegalStateException("예외 발생!");
        }

        return "ok";
    }
}
