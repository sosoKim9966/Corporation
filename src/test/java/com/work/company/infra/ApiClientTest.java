package com.work.company.infra;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.work.company.infra.api.ApiClient;
import com.work.company.common.exception.CustomException;
import com.work.company.common.exception.ErrorCode;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApiClientTest {

    @Mock
    RestTemplate restTemplate;

    @Spy
    ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    ApiClient apiClient;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(apiClient, "corUrl", "http://dummy");
        ReflectionTestUtils.setField(apiClient, "corKey", "dummy");
        ReflectionTestUtils.setField(apiClient, "adrUrl", "http://dummy");
        ReflectionTestUtils.setField(apiClient, "adrKey", "dummy");
    }

    @Test
    void jsonParse_fail_test1() {
        //given
        String html = "<html></html>";

        //when
        CustomException ex = assertThrows(CustomException.class,
                () -> apiClient.safeParse(html));

        //then
        assertEquals(ErrorCode.NON_JSON_RESPONSE, ex.getErrorCode());
    }

    @Test
    void jsonParse_fail_test2() {
        //given
        String broken = "{test}";

        //when
        CustomException ex = assertThrows(
                CustomException.class,
                () -> ReflectionTestUtils.invokeMethod(apiClient, "safeParse", broken)
        );

        //then
        assertEquals(ErrorCode.JSON_PARSE_ERROR, ex.getErrorCode());
    }

    @Test
    void safeParse_test () {
        //given
        String ok = "{\"foo\":\"bar\"}";

        //when
        JsonNode node = assertDoesNotThrow(
                () -> ReflectionTestUtils.invokeMethod(apiClient, "safeParse", ok)
        );

        //then
        assertEquals("bar", node.get("foo").asText());
    }

    @Test
    void call_api_test() {
        String json = "{ \"items\":[{\"crno\":\"123\"}] }";
        when(restTemplate.getForEntity(any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok(json));

        JsonNode node = apiClient.callCorApi("1101110999");

        assertEquals("123", node.path("items").get(0).path("crno").asText());
    }


}
