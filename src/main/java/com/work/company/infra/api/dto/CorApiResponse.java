package com.work.company.infra.api.dto;

/**
 * 공정거래위원회 _통신판매사업자 등록상세 제공 api dto
 * https://apis.data.go.kr/1130000/MllBsDtl_2Service/getMllBsInfoDetail_2
 */
public record CorApiResponse(
        // 법인여부명, 법인명, 법인등록번호, 사업자등록번호, 소재지도로명주소, 소재지주소
        String corpYnNm,
        String bzmnNm,
        String crno,
        String brno,
        String lctnRnAddr
) {
}
