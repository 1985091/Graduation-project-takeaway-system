package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesRecommendReportVO implements Serializable {

    //商品名列表
    private String nameList;

    //销量列表逗号分隔，260,215,200
    private String numberList;

}
