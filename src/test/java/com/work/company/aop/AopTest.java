package com.work.company.aop;

import com.work.aop.LogAspect;
import com.work.company.infra.repository.port.CompanyRepository;
import com.work.company.application.CompanyService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Slf4j
@SpringBootTest
@Import(LogAspect.class)
public class AopTest {

    @Autowired
    CompanyService companyService;

    @Autowired
    CompanyRepository companyRepository;

    @Test
    void success() {
        companyService.testService("itemA");
    }

}
