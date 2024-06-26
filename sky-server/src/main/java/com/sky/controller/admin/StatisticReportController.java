package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.StatisticReportService;
import com.sky.vo.SalesRecommendReportVO;
import com.sky.vo.TurnoverReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/admin/report")
@Api(tags = "数据统计接口")
@Slf4j
public class StatisticReportController {
    @Autowired
    private StatisticReportService statisticReportService;
    //销售统计
    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额统计")
    public Result<TurnoverReportVO> turnoverStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("营业额报表：{}-{}",begin,end);
        TurnoverReportVO turnoverReportVO = statisticReportService.getTurnoverStatistics(begin,end);
        return Result.success(turnoverReportVO);

    }
    //商品销售推荐
    @GetMapping("/top10")
    @ApiOperation("商品销售推荐")
    public Result<SalesRecommendReportVO> recommend(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("商品销量top10：{}-{}",begin,end);
        SalesRecommendReportVO salesRecommendReportVO = statisticReportService.getSalesRecommend(begin,end);
        return Result.success(salesRecommendReportVO);

    }
}
