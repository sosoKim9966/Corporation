package com.work.company.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CsvCorResponseDto{
    private final String prmmiMnno;
    private final String bzmnNm;
    private final String corpYnNm;
    private final String brno;

}
