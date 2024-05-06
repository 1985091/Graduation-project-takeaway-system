package com.sky.service;

import com.sky.vo.TurnoverReportVO;

import java.time.LocalDate;

public interface StatisticReportService {
    //获取销售额统计
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);
}
