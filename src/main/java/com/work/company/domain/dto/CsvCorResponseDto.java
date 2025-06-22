package com.work.company.domain.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class CsvCorResponseDto{
    private final String prmmiMnno;
    private final String bzmnNm;
    private final String corpYnNm;
    private final String brno;
}
