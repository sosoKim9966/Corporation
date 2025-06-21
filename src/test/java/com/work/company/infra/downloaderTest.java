package com.work.corporation.company.infra;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.work.corporation.company.infra.csv.CsvDownloader;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
public class downloaderTest {

    @Autowired
    private CsvDownloader downloader;

    @Test
    void downloadTest() {
        // given
        String city = "서울특별시";
        String district = "성북구";

        // when
        Path file = downloader.download(city, district);

        // then
        assertThat(file)
                .as("파일이 null이 아니고")
                .isNotNull();

        assertThat(Files.exists(file))
                .as("다운로드된 CSV 파일이 존재해야 함: " + file)
                .isTrue();

        assertThat(file.toString()).endsWith(".csv");
    }
}
