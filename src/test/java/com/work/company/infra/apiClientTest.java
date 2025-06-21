package com.work.company.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.work.aop.LogAspect;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.InputStream;
import java.net.URI;

import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(LogAspect.class)
public class apiTest {

    private static final String SERVICE_KEY1 =
            "rwe7KLx3it1dFXPVmKAVQpY5j9SK0bNTf6s0mB2q41WoAUJRUe%2BmWS0UzPh4kDZhnoFg3k52YVApFOFbuS80iQ%3D%3D";

    private static final String SERVICE_KEY2 =
            "devU01TX0FVVEgyMDI1MDMyNTE1MTgxMTExNTU3NzE=";

    private final HttpClient http = HttpClient.newHttpClient();
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void corApiTest() throws Exception {
        // given
        URI uri = UriComponentsBuilder
                .fromUriString("https://apis.data.go.kr/1130000/MllBsDtl_2Service/getMllBsInfoDetail_2")
                .queryParam("serviceKey", SERVICE_KEY1)
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 1)
                .queryParam("resultType", "json")
                .queryParam("brno", "8199501715")
                .build(true)
                .toUri();

        HttpRequest req = HttpRequest.newBuilder(uri)
                .header("Accept", "application/json")
                .GET()
                .build();

        // when
        HttpResponse<InputStream> res =
                http.send(req, HttpResponse.BodyHandlers.ofInputStream());

        // then
        assertEquals(200, res.statusCode());

        try (InputStream is = res.body()) {
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            assertFalse(body.isBlank());
            assertTrue(body.contains("\"resultCode\""));
            System.out.println(body);
        }
    }

    @Test
    void adrApiTest() throws Exception {
        // given
        String encodedKey = URLEncoder.encode(SERVICE_KEY2, StandardCharsets.UTF_8);
        URI uri = UriComponentsBuilder
                .fromUriString("https://business.juso.go.kr/addrlink/addrLinkApi.do")
                .queryParam("confmKey", encodedKey)
                .queryParam("currentPage", 1)
                .queryParam("countPerPage", 1)
                .queryParam("resultType", "json")
                .queryParam("keyword", URLEncoder.encode("서울특별시 성북구 아리랑로19길", StandardCharsets.UTF_8))
                .build(true)
                .toUri();

        // when
//        HttpResponse<InputStream> res =
//                http.send(req, HttpResponse.BodyHandlers.ofInputStream());
        ResponseEntity<String> res = restTemplate.getForEntity(uri, String.class);
        // then
        System.out.println(res.getBody());
//        assertEquals(200, res.getBody());

//        try (InputStream is = res.get)) {
//            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
//
//            assertFalse(body.isBlank());
////            assertTrue(body.contains("\"resultCode\""));
//            System.out.println(body);
//        }
    }


}
