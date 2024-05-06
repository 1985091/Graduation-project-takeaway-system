package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.StatisticReportService;
import com.sky.vo.TurnoverReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class StatisticReportServicelmpl implements StatisticReportService {

    @Autowired
    private OrderMapper orderMapper;
    //销售额统计
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        //计算从开始到结束日期的dateList
        //创建list集合存放从begin到end的所有销售数据,迭代器一天天相加
        List<LocalDate> dateList = Stream.iterate(begin, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(begin, end.plusDays(1)))
                .collect(Collectors.toList());

        List<Double> turnoverList = new ArrayList<>();
        for(LocalDate date : dateList){
            //获取日期开始结束时分秒具体时间
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            //查询指定时间区间的订单营业额
            Map map = new HashMap(){{
                put("beginTime",beginTime);
                put("endTime",endTime);
                put("status", Orders.COMPLETED);
            }};
            Double turnover = orderMapper.sumByMap(map);
            //判断如果当天营业额为空，设置为0.0
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }
        //通过StringUtils工具类将turnoverList和dateList转成字符串封装进VO
        TurnoverReportVO turnoverReportVO = TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
        return turnoverReportVO;
    }
}
