package com.work.corporation.company.presentation;

import com.work.corporation.company.infra.csv.CsvDownloader;
import com.work.corporation.company.application.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CompanyController {
    private final CsvDownloader downloader;
    private final CompanyService companyService;

//    @GetMapping("/test-company")
//    public void test() throws Exception {
//        String test = "test";
//        companyService.testService(test);
//    }

    @PostMapping(value = "/download-csv")
    public void downloadCompanyCsv(@RequestBody Map<String, Object> body) throws Exception {
        // 다운로드 전 날짜 비교 같은 날짜에 다운로드 받은 파일 존재한다면 그냥 리턴
        // fileName.equal(날짜_city_district) -> 로직 x
        String city = body.get("city").toString();
        String district = body.get("district").toString();

        log.info("city = {}", city);
        log.info("district = {}", district);

//        downloader.download(city, district);

        // 사업자번호 000-00-00000 substring 돌리고 api 하나씩 호출? 너무 비효율적인디.. 흠..

    }


}
