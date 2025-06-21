package com.work.company.infra;

import com.work.company.infra.csv.CsvDownloader;
import com.work.util.CsvCorParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
public class downloaderTest {

    @Autowired
    private CsvDownloader downloader;

    @Autowired
    private CsvCorParser parser;

    @Test
    void downloadTest() {
        // given
        String city = "서울특별시";
        String district = "강남구";

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

    @Test
    void findFileEncoding() throws IOException {
        String encoding = parser.findFileEncoding("/Users/sojinkim/Downloads/통신판매사업자_서울특별시_성북구.csv");
        System.out.println(encoding);
        assertNotEquals("UTF-8", encoding);
    }

    @Test
    void readLineTest() throws IOException {
        parser.readLines(downloader.download("서울특별시", "강남구"));
    }

    @Test
    void readWholeFile_ms949() throws Exception {
        Path csv = downloader.download("서울특별시", "성북구");

        long lineCount;
        try (Stream<String> lines =
                     Files.lines(csv, Charset.forName("MS949"))) {
            lineCount = lines.count();
        }

        assertThat(lineCount).isGreaterThan(0);
        System.out.printf("✅ MS949로 %d줄 읽힘%n", lineCount);
    }
}
