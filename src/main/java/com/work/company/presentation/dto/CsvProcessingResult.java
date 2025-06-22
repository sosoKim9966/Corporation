package com.work.company.presentation.dto;

public record CsvProcessingResult(
        int total,
        int success,
        int fail
) {}
