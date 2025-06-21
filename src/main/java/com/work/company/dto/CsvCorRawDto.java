package com.work.company.dto;


import com.opencsv.bean.CsvBindByName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CsvCorResponseDto {

    public CsvCorResponseDto() {
    }

    @CsvBindByName(column = "통신판매번호")
    private String salesRegisNo;

    @CsvBindByName(column = "상호")
    private String bizNm;

    @CsvBindByName(column = "법인여부")
    private String bizType;

    @CsvBindByName(column = "사업자등록번호")
    private String brno;

}
