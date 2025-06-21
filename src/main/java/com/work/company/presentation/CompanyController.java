package com.work.company.presentation;

import com.work.company.application.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyService companyService;

    @PostMapping(value = "/download-csv")
    public ResponseEntity<Void> downloadCompanyCsv(@RequestBody Map<String, Object> body) throws Exception {
        companyService.csvDownload(body.get("city").toString(), body.get("district").toString());
        return ResponseEntity.ok().build();
    }

}
