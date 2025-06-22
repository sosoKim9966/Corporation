package com.work.company.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.work.company.domain.dto.AdrApiResult;
import com.work.company.domain.model.CompanyEntity;
import com.work.company.domain.dto.CorApiResult;
import com.work.company.domain.dto.CsvCorResponseDto;
import com.work.company.infra.api.ApiClient;
import com.work.company.presentation.dto.CsvProcessingResult;
import com.work.company.infra.csv.CsvDownloader;
import com.work.company.domain.repository.CompanyRepository;
import com.work.company.common.exception.CustomException;
import com.work.company.common.exception.ErrorCode;
import com.work.company.common.parser.CsvCorParser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final CsvDownloader downloader;
    private final CsvCorParser parser;
    private final ApiClient client;

    @Qualifier("corporationExecutor")
    private final Executor corporationExecutor;

    @Transactional
    public CsvProcessingResult processCsvAndSaveCompanies(String city, String district) throws Exception {
        // csv 파일 다운로드
        Path path = downloader.download(city, district);

        List<CsvCorResponseDto> list = parser.getCorList(path);

        return fetchAndSaveCompanyDetailsAsync(list);
    }

    public static final Map<String, String> ROAD_NAME_MAP = Map.ofEntries(
            Map.entry("인촌로", "고려대로")
    );

    public static String changeAdrMap(String lctnRnAddr) {
        for (var e : ROAD_NAME_MAP.entrySet()){
            if(lctnRnAddr.contains(e.getKey())){
                return lctnRnAddr.replace(e.getKey(), e.getValue());
            }
        }
        return lctnRnAddr;
    }

    public CompletableFuture<Optional<CompanyEntity>> fetchCompanyDetailAsync(CsvCorResponseDto dto) {
        return validate(dto)
                .thenCompose(this::callCorporationApi)
                .thenCompose(this::callAddressApi)
                .thenApply(this::toCompanyEntity)
                .exceptionally(ex -> {
                    log.error("[apiRequest] 실패 brno={}", dto != null ? dto.getBrno() : "null", ex);
                    return Optional.empty();
                });
    }

    private CompletableFuture<CsvCorResponseDto> validate(CsvCorResponseDto dto) {
        if (dto == null || !StringUtils.hasText(dto.getBrno())) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Invalid input DTO"));
        }

        return CompletableFuture.completedFuture(dto);
    }

    private CompletableFuture<CorApiResult> callCorporationApi(CsvCorResponseDto dto) {
        return CompletableFuture.supplyAsync(() -> client.callCorApi(dto.getBrno()), corporationExecutor)
                .thenApply(cor -> {
                    JsonNode items = cor.path("items");
                    if (!items.isArray() || items.isEmpty()) {
                        throw new CustomException(ErrorCode.COR_API_NO_RESPONSE, "법인 API 응답 없음");
                    }

                    JsonNode corItem = items.get(0);
                    String rnAddr = changeAdrMap(corItem.path("lctnRnAddr").asText());

                    if (!StringUtils.hasText(rnAddr) || "N/A".equalsIgnoreCase(rnAddr)) {
                        throw new CustomException(ErrorCode.ADDRESS_NOT_FOUND, "주소 없음");
                    }

                    return new CorApiResult(dto, corItem, rnAddr);
                });
    }

    private CompletableFuture<AdrApiResult> callAddressApi(CorApiResult input) {
        return CompletableFuture.supplyAsync(() -> client.callAdrApi(input.rnAddr()), corporationExecutor)
                .thenApply(adr -> {
                    JsonNode juso = adr.path("results").path("juso");
                    if (!juso.isArray() || juso.isEmpty()) {
                        throw new CustomException(ErrorCode.ADR_API_NO_RESULT, "주소 API 결과 없음");
                    }

                    String admCd = juso.get(0).path("admCd").asText();
                    if (!StringUtils.hasText(admCd) || "N/A".equalsIgnoreCase(admCd)) {
                        throw new CustomException(ErrorCode.ADMCD_NOT_FOUND, "행정코드 없음");
                    }

                    return new AdrApiResult(input.dto(), input.corItem(), admCd);
                });
    }

    public CsvProcessingResult fetchAndSaveCompanyDetailsAsync(List<CsvCorResponseDto> list) {
        List<CompletableFuture<Optional<CompanyEntity>>> futures = list.stream()
                .map(this::fetchCompanyDetailAsync)
                .toList();

        List<Optional<CompanyEntity>> optionalResults = CompletableFuture
                .allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .toList())
                .join();

        List<CompanyEntity> entities = optionalResults.stream()
                .flatMap(Optional::stream)
                .toList();

        if (!entities.isEmpty()) {
            companyRepository.saveAll(entities);
        }

        return new CsvProcessingResult(
                list.size(),
                entities.size(),
                list.size() - entities.size()
        );
    }

    private Optional<CompanyEntity> toCompanyEntity(AdrApiResult input) {
        CompanyEntity entity = CompanyEntity.toEntity(
                input.dto(),
                input.admCd(),
                input.corItem().path("crno").asText()
        );

        return Optional.ofNullable(entity);
    }
}