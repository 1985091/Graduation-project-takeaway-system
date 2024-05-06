package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderDetailMapper {
    //批量插入订单明细数据
    void insertBatch(List<OrderDetail> orderDetailList);
    //根据id查询订单
    @Select("Select * from order_detail where order_id = #{orderId}" )
    List<OrderDetail> getByOrderId(Long orderId);
}