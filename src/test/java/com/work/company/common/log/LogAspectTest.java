package com.work.company.common.log;

import com.work.company.common.exception.CustomException;
import com.work.company.common.log.LogAspect;
import com.work.company.domain.repository.CompanyRepository;
import com.work.company.application.CompanyService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(LogAspect.class)
class LogAspectTest {

    @Autowired
    DummyService dummyService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public DummyService dummyService() {
            return new DummyService();
        }
    }

    public static class DummyService {
        public String doWork(String input) {
            if ("error".equals(input)) {
                throw new IllegalArgumentException("강제 에러");
            }
            return "success: " + input;
        }
    }

    @Test
    void log_success_test() {
        // given
        String input = "test";

        // when
        String result = dummyService.doWork(input);

        // then
        assertThat(result).isEqualTo("success: test");
    }

    @Test
    void log_fail_test() {
        // given
        String input = "error";

        // when & then
        assertThatThrownBy(() -> dummyService.doWork(input))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("강제 에러");
    }

}