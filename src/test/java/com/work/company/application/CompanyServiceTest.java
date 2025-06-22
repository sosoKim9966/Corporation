package com.work.company.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.work.company.application.CompanyService;
import com.work.company.common.parser.CsvCorParser;
import com.work.company.domain.dto.CsvCorResponseDto;
import com.work.company.domain.model.CompanyEntity;
import com.work.company.domain.repository.CompanyRepository;
import com.work.company.infra.api.ApiClient;
import com.work.company.infra.csv.CsvDownloader;
import com.work.company.presentation.dto.CsvProcessingResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CsvDownloader downloader;

    @Mock
    private CsvCorParser parser;

    @Mock
    private ApiClient client;

    private Executor executor;

    @Spy
    @InjectMocks
    private CompanyService companyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void fetchCompanyDetailAsync_fail_brnoIsNull() {
        // given
        CsvCorResponseDto dto = new CsvCorResponseDto("2024-서울강남-0012", "테스트상사", "법인", null);

        // when
        Optional<CompanyEntity> result = companyService.fetchCompanyDetailAsync(dto).join();

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void fetchCompanyDetailAsync_success_dtoIsValid() {
        // given
        CsvCorResponseDto dto = new CsvCorResponseDto("2024-서울강남-0012", "테스트상사", "법인", "1234567890");

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode corItem = mapper.createObjectNode();
        corItem.put("lctnRnAddr", "인촌로 12");
        corItem.put("crno", "COR123");

        ObjectNode corResponse = mapper.createObjectNode();
        corResponse.putArray("items").add(corItem);

        ObjectNode adrNode = mapper.createObjectNode();
        adrNode.put("admCd", "1168010300");

        ObjectNode adrResponse = mapper.createObjectNode();
        adrResponse.putObject("results").putArray("juso").add(adrNode);

        when(client.callCorApi(dto.getBrno())).thenReturn(corResponse);
        when(client.callAdrApi("고려대로 12")).thenReturn(adrResponse);

        // when
        Optional<CompanyEntity> result = companyService.fetchCompanyDetailAsync(dto).join();

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getBrno()).isEqualTo("1234567890");
    }

    @Test
    void fetchCompanyDetailAsync_toEntity_success() {
        // given
        CsvCorResponseDto dto = new CsvCorResponseDto("2024-서울강남-0012", "테스트상사", "법인", "1234567890");

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode corItem = mapper.createObjectNode();
        corItem.put("lctnRnAddr", "인촌로 12");  // 주소 매핑 대상
        corItem.put("crno", "CRN123456");

        ObjectNode corResponse = mapper.createObjectNode();
        corResponse.putArray("items").add(corItem);

        ObjectNode adrNode = mapper.createObjectNode();
        adrNode.put("admCd", "1168010300");
        ObjectNode adrResponse = mapper.createObjectNode();
        adrResponse.putObject("results").putArray("juso").add(adrNode);

        when(client.callCorApi(dto.getBrno())).thenReturn(corResponse);
        when(client.callAdrApi("고려대로 12")).thenReturn(adrResponse);

        // when
        Optional<CompanyEntity> result = companyService.fetchCompanyDetailAsync(dto).join();

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getBrno()).isEqualTo("1234567890");
        assertThat(result.get().getAdmCd()).isEqualTo("1168010300");
        assertThat(result.get().getCrno()).isEqualTo("CRN123456");
        assertThat(result.get().getBzmnNm()).isEqualTo("테스트상사");
    }

    @Test
    void changeAdrMap_success_test() {
        // given
        String original = "서울 인촌로 123";

        // when
        String changed = CompanyService.changeAdrMap(original);

        // then
        assertThat(changed).isEqualTo("서울 고려대로 123");
    }

    @Test
    void changeAdrMap_fail_test() {
        // given
        String original = "서울 종로대로";

        // when
        String changed = CompanyService.changeAdrMap(original);

        // then
        assertThat(changed).isEqualTo(original);
    }

    @Test
    void fetchCompanyDetailAsync_success_test() {
        // given
        CsvCorResponseDto dto = new CsvCorResponseDto("2024-서울강남-0012", "테스트상사", "법인", "1234567890");
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode corItem = mapper.createObjectNode();
        corItem.put("lctnRnAddr", "인촌로 12");
        corItem.put("crno", "COR123");
        ObjectNode corResponse = mapper.createObjectNode();
        corResponse.putArray("items").add(corItem);

        ObjectNode adrNode = mapper.createObjectNode();
        adrNode.put("admCd", "1168010300");
        ObjectNode adrResponse = mapper.createObjectNode();
        adrResponse.putObject("results").putArray("juso").add(adrNode);

        when(client.callAdrApi(dto.getBrno())).thenReturn(corResponse);
        when(client.callAdrApi("고려대로 12")).thenReturn(adrResponse);

        // when
        Optional<CompanyEntity> result = companyService.fetchCompanyDetailAsync(dto).join();

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getBrno()).isEqualTo("1234567890");
    }

    @Test
    void fetchCompanyDetailAsync_corApiFail_test() {
        // given
        CsvCorResponseDto dto = new CsvCorResponseDto("2024-서울강남-0012", "테스트상사", "법인", "1234567890");
        when(client.callCorApi(dto.getBrno())).thenThrow(new RuntimeException("COR API 실패"));

        // when
        Optional<CompanyEntity> result = companyService.fetchCompanyDetailAsync(dto).join();

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void fetchCompanyDetailAsync_adrApiFail_test() {
        // given
        CsvCorResponseDto dto = new CsvCorResponseDto("2024-서울강남-0012", "테스트상사", "법인", "1234567890");
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode corItem = mapper.createObjectNode();
        corItem.put("lctnRnAddr", "인촌로 12");
        corItem.put("crno", "COR123");
        ObjectNode corResponse = mapper.createObjectNode();
        corResponse.putArray("items").add(corItem);

        when(client.callCorApi(dto.getBrno())).thenReturn(corResponse);
        when(client.callAdrApi("고려대로 12")).thenThrow(new RuntimeException("ADR API 실패"));

        // when
        Optional<CompanyEntity> result = companyService.fetchCompanyDetailAsync(dto).join();

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void fetchAndSaveCompanyDetailsAsync_test() {
        // given
        CsvCorResponseDto dto = new CsvCorResponseDto("2024-서울강남-0012", "테스트상사", "법인", "1234567890");
        CompanyEntity entity = CompanyEntity.toEntity(dto, "1168010300", "COR123");
        CompletableFuture<Optional<CompanyEntity>> future = CompletableFuture.completedFuture(Optional.of(entity));

        CompanyService spyService = Mockito.spy(companyService);
        doReturn(future).when(spyService).fetchCompanyDetailAsync(any());

        // when
        CsvProcessingResult result = spyService.fetchAndSaveCompanyDetailsAsync(List.of(dto));

        // then
        assertThat(result.total()).isEqualTo(1);
        assertThat(result.success()).isEqualTo(1);
        assertThat(result.fail()).isEqualTo(0);
        verify(companyRepository).saveAll(any());
    }

    CsvCorResponseDto mockDto(int i) {
        return new CsvCorResponseDto("2024-서울강남-001" + i, "상호" + i, "법인","123456789" + i);
    }

    @Test
    void fetchAndSaveCompanyDetailsAsync_concurrencyTest() throws Exception {
        // given
        List<CsvCorResponseDto> dtoList = IntStream.range(0, 100)
                .mapToObj(this::mockDto)
                .toList();

        CountDownLatch latch = new CountDownLatch(dtoList.size());

        // when
        doAnswer(invocation -> {
            CsvCorResponseDto dto = invocation.getArgument(0);
            return CompletableFuture.supplyAsync(() -> {
                System.out.println("처리 중: " + dto.getBrno() + " - " + Thread.currentThread().getName());
                latch.countDown();
                return Optional.empty();
            }, Executors.newFixedThreadPool(10)); // 또는 실제 CompanyService에서 사용하는 executor
        }).when(companyService).fetchCompanyDetailAsync(any());

        // when
        companyService.fetchAndSaveCompanyDetailsAsync(dtoList);
        latch.await();
    }
}
