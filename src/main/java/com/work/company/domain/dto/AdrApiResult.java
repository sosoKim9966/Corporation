package com.work.company.domain.dto;

import com.fasterxml.jackson.databind.JsonNode;

public record AdrApiResult(
        CsvCorResponseDto dto,
        JsonNode corItem,
        String admCd
) {}
