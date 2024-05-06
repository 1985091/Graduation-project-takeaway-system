package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface OrderMapper {
    // 插入数据
    void insert(Orders orders);
    //根据订单号查询订单
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);
    //修改订单信息
    void update(Orders orders);
    //分页条件查询并按下单时间倒序排列
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);
    //订单详情
    @Select("select * from order_detail where order_id = #{orderId}")
    Orders getByOrderId(Long id);
    //根据id查询订单
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);
    //订单统计
    @Select("select count(id) from orders where status = #{status}")
    Integer countByStatus(Integer toBeConfirmed);
    // 计算当天营业额的合计
    Double sumByMap(Map map);
}
