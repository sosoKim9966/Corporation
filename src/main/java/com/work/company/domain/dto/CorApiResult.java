package com.work.company.domain.dto;

import com.fasterxml.jackson.databind.JsonNode;

public record CorApiResult(
        CsvCorResponseDto dto,
        JsonNode corItem,
        String lctnRnAddr
) {}