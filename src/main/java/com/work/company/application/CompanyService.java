package com.work.company.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.work.company.domain.CompanyEntity;
import com.work.company.dto.CsvCorResponseDto;
import com.work.company.infra.api.ApiClient;
import com.work.company.infra.csv.CsvDownloader;
import com.work.company.infra.repository.port.CompanyRepository;
import com.work.util.CsvCorParser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    public void csvDownload(String city, String district) throws Exception {
        log.info("[csvDownload] 실행");

        // csv 파일 다운로드
        Path path = downloader.download(city, district);

        // api 호출 실행
        apiFutureProcess(parser.getCorList(path));
    }

    public static final Map<String, String> changeAdrMap = Map.ofEntries(
            Map.entry("인촌로", "고려대로")
    );

    public static String changeAdrMap(String lctnRnAddr) {
        for(Map.Entry<String, String> m : changeAdrMap.entrySet()) {
            if(lctnRnAddr.contains(m.getKey())) {
                return lctnRnAddr.replace(m.getKey(), m.getValue());
            }
        }

        return lctnRnAddr;
    }

    public CompletableFuture<CompanyEntity> apiRequest(CsvCorResponseDto dto) {
        if(dto == null || dto.getBrno().isBlank()) {
            log.info("[apiRequest] 스킵 dto 비었음, dto.getBrno={}, dto.getCorpYnNm={}, " +
                    "dto.getBzmnNm={}, dto.getPrmmiMnno={}, ", dto.getBrno(), dto.getCorpYnNm(), dto.getBzmnNm(), dto.getPrmmiMnno());
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.supplyAsync(() -> client.requestCorApi(dto.getBrno()), corporationExecutor)
                .thenCompose(cor -> {
                    JsonNode items = cor.path("items");

                    if(!items.isArray() || items.size() == 0) {
                        log.warn("[apiRequest] corItem 비었음 brno={}", dto.getBrno());
                        return CompletableFuture.completedFuture(null);
                    }

                    JsonNode corItem = items.get(0);
                    String lctnRnAddr = changeAdrMap(corItem.path("lctnRnAddr").asText());

                    if(lctnRnAddr == null || lctnRnAddr.isBlank() || "N/A".equalsIgnoreCase(lctnRnAddr)) {
                        // 다른 주소로 재시도 로직 보강 필요
                        log.warn("[apiRequest] 스킵 lctnRnAddr 비었음 brno={}, lctnRnAddr={}", dto.getBrno(), lctnRnAddr);
                        return CompletableFuture.completedFuture(null);
                    }

                    return CompletableFuture.supplyAsync(() -> client.requestAdrApi(lctnRnAddr), corporationExecutor)
                            .thenApply(adr -> {
                                JsonNode adrItem = adr.path("results").path("juso");

                                if(!adrItem.isArray() || adrItem.size() == 0) {
                                    log.warn("[apiRequest] adrItem 비었음 brno={}", adrItem);
                                    return null;
                                }

                                String admCd = adrItem.get(0).path("admCd").asText();

                                if(admCd == null || "N/A".equalsIgnoreCase(admCd)) {
                                    log.warn("[apiRequest] 스킵 admCd 비었음 lctnRnAddr={}", lctnRnAddr);
                                    return null;
                                }

                                return CompanyEntity.toEntity(
                                        dto,
                                        admCd,
                                        corItem.path("crno").asText());
                            });
                })
                .exceptionally(ex -> {
                    log.error("[apiRequestV1] 실패 brno={}", dto.getBrno(), ex);
                    return null;
                });
    }

    public void apiFutureProcess(List<CsvCorResponseDto> list) {
        List<CompletableFuture<CompanyEntity>> futures = list.stream()
                .map(this::apiRequest)
                .toList();

        List<CompanyEntity> entities = CompletableFuture
                                        .allOf(futures.toArray(new CompletableFuture[0]))
                                        .thenApply(e -> futures.stream()
                                                .map(f -> f.getNow(null))
                                                .filter(Objects::nonNull))
                                                .join()
                                                .toList();

        companyRepository.saveAll(entities);
    }

    public void testService(String test) {
        log.info("[companyService] 실행 test={}", test);
        companyRepository.testCode();
    }
}
