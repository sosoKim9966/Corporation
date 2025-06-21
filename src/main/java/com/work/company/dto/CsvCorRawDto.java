package com.work.company.dto;


import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CsvCorRawDto {

    @CsvBindByName(column = "통신판매번호")
    private String salesRegisNo;

    @CsvBindByName(column = "상호")
    private String bizNm;

    @CsvBindByName(column = "법인여부")
    private String bizType;

    @CsvBindByName(column = "사업자등록번호")
    private String brno;

    // 소재지 도로명 주소
    private String lctnAddr;
    // 소재지 주소
    private String lctnRnAddr;
    // 법인등록번호
    private String crno;
    // 행정구역코드
    private String admCd;
}
