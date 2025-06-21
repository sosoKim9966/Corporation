package com.work.company.infra;

import com.work.aop.LogAspect;
import com.work.company.infra.repository.port.CompanyRepository;
import com.work.company.presentation.CompanyController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

//
@EnableAspectJAutoProxy
@Import(LogAspect.class)
@SpringBootTest
@WebMvcTest(CompanyController.class)
public class RepositoryTest {

    @Autowired
    CompanyRepository repository;
}
