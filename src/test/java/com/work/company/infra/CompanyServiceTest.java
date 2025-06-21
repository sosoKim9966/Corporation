package com.work.company.infra;

import com.work.company.application.CompanyService;
import com.work.company.domain.CompanyEntity;
import com.work.company.dto.CsvCorResponseDto;
import com.work.company.infra.repository.port.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CompanyServiceTest {

    @MockitoBean
    CompanyRepository repository;

    @Autowired
    CompanyService service;

    @Test
    public void apiRequestTest() throws Exception {
        // given
        CsvCorResponseDto dto = CsvCorResponseDto.builder()
                .bzmnNm("(주)베토벤보청기")
                .corpYnNm("법인")
                .prmmiMnno("2015-서울은평-0538")
                .brno("1118124728")
                .build();

        // when
        CompletableFuture<CompanyEntity> test = service.apiRequest(dto);
        System.out.println(test);

        // then
        assertThat(test)
                .as("null 아니고")
                .isNotNull();

        System.out.println("test : " + test.toString());
    }
}
