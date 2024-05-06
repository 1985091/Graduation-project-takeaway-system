package com.sky.controller.user;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/order")
@Slf4j
@Api(tags = "用户端订单管理")
public class UserOrderController {
    @Autowired
    private OrderService orderService;
    //用户下单
    @PostMapping("/submit")
    @ApiOperation("用户下单")
    public Result<OrderSubmitVO> submitVOResult(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        log.info("用户下单:{}",ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submitOrder(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }
    //订单支付
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }
    //订单查询
    //订单状态 1 为待付款 2 待接单 3 已结单 4 派送中 5 已完成 6 已取消
    @GetMapping("/historyOrders")
    @ApiOperation("分页查询历史订单")
    public Result<PageResult> historyOrders(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("分页查询订单：{}", ordersPageQueryDTO);
        PageResult pageResult = orderService.pageQuery(ordersPageQueryDTO);
        return Result.success(pageResult);
    }
     //查询订单的详细情况
     @GetMapping("/orderDetail/{id}")
     @ApiOperation("查询订单详情")
     public Result<OrderVO> details(@PathVariable("id") Long id) {
         OrderVO orderVO = orderService.getDetails(id);
         return Result.success(orderVO);
     }
     //取消订单
     @PutMapping("/cancel/{id}")
     @ApiOperation("取消订单")
     public Result cancel(@PathVariable Long id){
         log.info("取消订单：{}", id);
         orderService.userEndById(id);
         return Result.success();
     }
     //再来一单
     @PostMapping("/repetition/{id}")
     @ApiOperation("再来一单")
     public Result again(@PathVariable Long id){
         log.info("再来一单：{}", id);
         orderService.again(id);
         return Result.success();
     }
}
