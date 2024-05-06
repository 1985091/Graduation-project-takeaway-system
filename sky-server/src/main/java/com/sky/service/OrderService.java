package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    //用户下单
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);
    //订单支付
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;
    //支付完，修改订单
    void paySuccessChange(String outTradeNo);
    //订单历史
    PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);
    //订单详情
    OrderVO getDetails(Long id);
    //取消订单
    void userEndById(Long id);
    //再来一单
    void again(Long id);
    // 客户端和服务端分层处理
    PageResult pageQuery2(OrdersPageQueryDTO ordersPageQueryDTO, Integer roleType);
    //订单统计
    OrderStatisticsVO statistics();
    //接单，通过更改status
    void adminConfirm(OrdersConfirmDTO ordersConfirmDTO);
    //拒单
    void adminRejectOrder(OrdersRejectionDTO ordersRejectionDTO);
    //取消订单
    void adminCancelOrder(OrdersCancelDTO ordersCancelDTO);
    //派送订单
    void isDelivery(Long id);
    //完成订单
    void completeOrder(Long id);
}
