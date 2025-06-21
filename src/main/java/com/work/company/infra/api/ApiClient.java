package com.work.company.infra.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.work.exception.CustomException;
import com.work.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiClient {

    @Value("${corporation.cor.api.url}")
    private String corUrl;
    @Value("${corporation.cor.api.key}")
    private String corKey;
    @Value("${corporation.adr.api.url}")
    private String adrUrl;
    @Value("${corporation.adr.api.key}")
    private String adrKey;

    public Object request(String type, JSONObject form) throws Exception, CustomException {
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(form.toString(), headers);

        String url = type.equals("cor") ? corUrl : adrUrl;

        log.info(" API 호출 [url: " + url + "]" + form.toString());

        ResponseEntity<String> response = rt.exchange(url, HttpMethod.GET, entity, String.class);

        Map<String, Object> body = jsonToDataMap(response.getBody());

        String resultStatus = getToString(body, "KEY_RESULT_STATUS");

        log.info(" API 호출 결과 [result status: " + resultStatus + "]");

        if(resultStatus.equals("ok")) {
            return body.get("resultValue");
        } else {
            String errorCode = getToString(body, "errorCode");
            String errorMessage = getToString(body, "resultMessage");

            log.error("API Error : [" + errorCode + "] " + errorMessage);

            throw new CustomException(ErrorCode.valueOf(errorCode), errorMessage);
        }
    }

    static Map<String, Object> jsonToDataMap(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<Map<String, Object>>(){});
    }

    public String getToString(Map<String, Object> params, String key) {
        return getToString(params, key, null);
    }

    // get string
    public String getToString(Map<String, Object> params, String key, String defaultVal) {
        if (!params.isEmpty() && params.get(key) != null) {
            return params.get(key).toString();
        }
        return defaultVal;
    }

}
