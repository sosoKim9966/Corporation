package com.work.company.infra.csv;

import com.work.exception.CustomException;
import com.work.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
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

    private final String baseUrl;
    private final String referer;

    public CsvDownloader(
            @Value("${corporation.csv.base-url}") String baseUrl,
            @Value("${corporation.csv.referer}")  String referer) {
        this.baseUrl = baseUrl;
        this.referer = referer;
    }

    public Path download(String city, String district) {
        try {
            String fileName = "통신판매사업자_" + city + "_" + district + ".csv";
            String encodedFileName = URLEncoder.encode(fileName, "UTF-8");
            HttpURLConnection conn = getHttpURLConnection(encodedFileName);

            Path downloadPath = Paths.get(System.getProperty("user.home"), "Downloads", fileName);

            try (InputStream in = conn.getInputStream()) {
                Files.copy(in, downloadPath, StandardCopyOption.REPLACE_EXISTING);
                log.info("[CsvDownloader] 다운로드 성공: downloadPath={}", downloadPath);
                return downloadPath;
            }

        } catch (IOException e) {
            throw new CustomException(ErrorCode.DOWNLOAD_IO_ERROR, e.getMessage());
        } catch (Exception e) {
            throw new CustomException(ErrorCode.DOWNLOAD_FAIL, "[CsvDownloader] 파일 다운로드 중 오류 : " + e.getMessage());
        }
    }

    private HttpURLConnection getHttpURLConnection(String encodedFileName) throws IOException {
        String url = baseUrl + "?atchFileUrl=dataopen&atchFileNm=" + encodedFileName;

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Referer", referer);
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");

        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new CustomException(ErrorCode.DOWNLOAD_FAIL, "responseCode : " + conn.getResponseCode());
        }

        return conn;
    }
}
