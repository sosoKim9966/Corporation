package com.work.company.domain;

import com.work.company.dto.CsvCorResponseDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "company")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompanyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 통신판매번호
    private String prmmiMnno;
    // 상호
    private String bzmnNm;
    // 사업자등록번호
    private String brno;
    // 법인등록번호
    private String crno;
    // 행정구역코드
    private String admCd;

    public static CompanyEntity toEntity(CsvCorResponseDto dto,
                                   String crno,
                                   String admCd) {

        CompanyEntity entity = new CompanyEntity();
        entity.prmmiMnno = dto.getPrmmiMnno();
        entity.bzmnNm = dto.getBzmnNm();
        entity.brno = dto.getBrno();
        entity.crno = crno;
        entity.admCd = admCd;
        return entity;
    }
}
