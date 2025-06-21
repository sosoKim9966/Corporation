package com.work.corporation.company.infra.csv;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
@Component
public class CsvDownloader {

    private static final String BASE_URL = "https://www.ftc.go.kr/www/downloadBizComm.do";
    private static final String REFERER = "https://www.ftc.go.kr/www/selectBizCommOpenList.do?key=255";

    public Path download(String city, String district) {
        try {
            // 매주 일요일 갱신 -> 파일 다운로드 데이터가 너무 방대 사용자가 다운로드를 요청할때마다 다운로드 할 순 없음.
            //
            String fileName = "통신판매사업자_" + city + "_" + district + ".csv";
            String encodedFileName = URLEncoder.encode(fileName, "UTF-8");
            String url = BASE_URL + "?atchFileUrl=dataopen&atchFileNm=" + encodedFileName;

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Referer", REFERER);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Path downloadPath = Paths.get(System.getProperty("user.home"), "Downloads", fileName);

                try (InputStream in = conn.getInputStream()) {
                    Files.copy(in, downloadPath, StandardCopyOption.REPLACE_EXISTING);
                    log.info("다운로드 성공: {}", downloadPath);
                    return downloadPath;
                }
            } else {
                throw new RuntimeException("다운로드 실패: HTTP " + responseCode);
            }
        } catch (Exception e) {
            throw new RuntimeException("파일 다운로드 중 오류 발생", e);
        }
    }
}
