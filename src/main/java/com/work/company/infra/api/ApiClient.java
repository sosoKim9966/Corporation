package com.work.company.infra.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.work.company.common.exception.CustomException;
import com.work.company.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
@Component
public class ApiClient {

    @Value("${corporation.cor.api.url}")
    private String corUrl;
    @Value("${corporation.cor.api.key}")
    private String corKey;
    @Value("${corporation.adr.api.url}")
    private String adrUrl;
    @Value("${corporation.adr.api.key}")
    private String adrKey;

    @Qualifier("pooledRestTemplate")
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    public JsonNode callCorApi(String brno) {
        if (brno == null || brno.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_ARGUMENT, "brno 비었음");
        }

        URI uri = UriComponentsBuilder.fromHttpUrl(corUrl)
                .queryParam("serviceKey", corKey)
                .queryParam("pageNo", 1)
                .queryParam("numOfRows",1)
                .queryParam("resultType","json")
                .queryParam("brno", brno)
                .build(true)
                .toUri();

        return getAndParse(uri);
    }

    public JsonNode callAdrApi(String lctnRnAddr) {
        String encodedKey = URLEncoder.encode(adrKey, StandardCharsets.UTF_8);

        URI uri = UriComponentsBuilder.fromHttpUrl(adrUrl)
                .queryParam("confmKey", encodedKey)
                .queryParam("currentPage", 1)
                .queryParam("countPerPage",1)
                .queryParam("resultType","json")
                .queryParam("keyword", URLEncoder.encode(lctnRnAddr), StandardCharsets.UTF_8)  // 자동 인코딩
                .build(true)
                .toUri();

        return getAndParse(uri);
    }

    private JsonNode getAndParse(URI uri) {
        ResponseEntity<String> res = restTemplate.getForEntity(uri, String.class);

        assert2xx(res);
        return safeParse(res.getBody());
    }

    private static void assert2xx(ResponseEntity<?> res) {
        if (!res.getStatusCode().is2xxSuccessful()) {
            throw new CustomException(ErrorCode.HTTP_STATUS_ERROR, res.getStatusCode().toString());
        }
    }

    public JsonNode safeParse(String body) {
        try {
            if (body == null || body.isBlank() || body.startsWith("<")) {
                throw new CustomException(ErrorCode.NON_JSON_RESPONSE, "JSON 형식 오류");
            }
            return mapper.readTree(body);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.JSON_PARSE_ERROR , e.getMessage());
        }
    }
}
