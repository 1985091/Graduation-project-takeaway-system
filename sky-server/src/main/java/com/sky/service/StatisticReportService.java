package com.sky.service;

import com.sky.vo.SalesRecommendReportVO;
import com.sky.vo.TurnoverReportVO;

import java.time.LocalDate;

public interface StatisticReportService {

    SalesRecommendReportVO getSalesRecommend(LocalDate begin, LocalDate end);

    //获取销售额统计
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);
}
