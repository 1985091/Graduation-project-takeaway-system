package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.RoleConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserOrderServicelmpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShopCartMapper shoppingCartMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;

    //用户下单
    @Transactional
    @Override
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        //处理业务异常(地址簿空，购物车空等)
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        //获取当前用户的购物车数据
        ShoppingCart shoppingCart = new ShoppingCart();
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.listSelect(shoppingCart);
        if (shoppingCartList == null || shoppingCartList.size() == 0) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        //向订单表插入一条订单数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        //设置订单中在DTO中没有的属性数据
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        // TODO: 2024/4/29 用时间戳生成订单号是否可靠
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setConsignee(addressBook.getConsignee());
        orders.setUserId(userId);
        orders.setAddress(addressBook.getDetail());
        orders.setPhone(addressBook.getPhone());
        //插入数据进入订单表
        orderMapper.insert(orders);
        //向订单详情表插入多条订单详情数据
        List<OrderDetail> orderDetailList = new ArrayList<>();
        //遍历购物车数据，封装成OrderDetail对象
        for (ShoppingCart cart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            //拷贝后由于cart对象中缺少orderId
            orderDetail.setOrderId(orders.getId());//设置订单id
            orderDetailList.add(orderDetail);//用list批量封装
        }
        //批量插入到订单详情表
        orderDetailMapper.insertBatch(orderDetailList);
        //清空用户购物车
        shoppingCartMapper.deleteByUerId(userId);
        //封装返回VO结果
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime()).build();//构建出返回的VO对象

        return orderSubmitVO;
    }
    //支付订单
    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //调用支付接口生成交易单(个人无法获得商户认证)
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );
        // TODO: 2024/4/30 跳过微信支付接口，直接生成交易单,并执行支付成功操作
        JSONObject jsonObject = new JSONObject();//由于个人微信支付接口未开通，所以直接跳过微信支付接口
        paySuccessChange(ordersPaymentDTO.getOrderNumber());
        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }
    // 支付完修改订单
    @Override
    public void paySuccessChange(String outTradeNo) {
        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
        // TODO: 2024/4/30 暂时跳过微信支付接口，直接修改订单状态的代码
//        //通过webSocket向客户端推送消息
//        Map map = new HashMap(){{
//            put("type", "order");
//            put("orderId", ordersDB.getId());
//            put("content", "您有新的订单,请计时处理,"+"订单号: "+outTradeNo);
//            }};
//        String json = JSON.toJSONString(map);
//        webSocketServer.sendToAllClient(json);
    }
    //查询订单历史
    @Override
    public PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        //设置分页
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        //分页条件查询
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);
        //封装返回结果
        List<OrderVO> orderVOList = new ArrayList<>();
        if(page != null && page.size() > 0){
            for (Orders orders : page) {
                Long orderId = orders.getId();
                //查询订单详情
                List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orderId);
                //封装订单详情
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(orderDetailList);
                orderVOList.add(orderVO);
            }
        }
        return new PageResult(page.getTotal(), orderVOList);
    }
    //订单详情
    @Override
    public OrderVO getDetails(Long id) {
        //根据id查询订单
        Orders orders = orderMapper.getById(id);
        //根据订单id查询订单详情
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
        //封装返回结果
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetailList);
        return orderVO;
    }
    //取消订单
    @Override
    public void userEndById(Long id) {
//根据id查询订单
        Orders orders = orderMapper.getById(id);
        //判断订单是否存在
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        //如果订单状态到达商家接单以后的阶段，不能直接取消订单
        if (orders.getStatus() >= Orders.CONFIRMED) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        // TODO: 2024/5/1 =跳过微信退款接口=
        //修改订单状态为已取消,并设置取消原因和取消时间
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason(MessageConstant.ORDER_CANCELLED_BY_USER);
        orders.setCancelTime(LocalDateTime.now());
        //更新订单
        orderMapper.update(orders);
    }
    //再来一单
    @Override
    public void again(Long id) {
        //根据id查询订单详情
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
        //查询用户id
        Long userId = BaseContext.getCurrentId();
        //根据订单详情生成购物车数据
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map(orderDetail -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            //将除了id,createTime以外的属性拷贝到shoppingCart对象中
            BeanUtils.copyProperties(orderDetail, shoppingCart, "id", "createTime");
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());
            return shoppingCart;
        }).collect(Collectors.toList());
        //批量插入购物车数据
        shoppingCartMapper.insertCart(shoppingCartList);
    }
    //客户端和服务端分层处理查询
    @Override
    public PageResult pageQuery2(OrdersPageQueryDTO ordersPageQueryDTO, Integer roleType) {
        //设置分页
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        if(roleType.equals(RoleConstant.USER)){
            //如果是用户查询订单，需要根据用户id查询
            ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        }
        //分页条件查询
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);
        //封装返回结果
        List<OrderVO> orderVOList = new ArrayList<>();
        Long total = 0L;
        if(page != null && page.size() > 0){
            //如果是管理员查询订单，需要补充otherDishes字段信息,否则补充orderDetailList字段信息
            if (roleType.equals(RoleConstant.ADMIN)) {
                addOtherDishes(page, orderVOList);
            } else {
                addOrderDetails(page, orderVOList);
            }
            total = page.getTotal();
        }
        return new PageResult(total, orderVOList);
    }
    //服务订单统计
    @Override
    public OrderStatisticsVO statistics() {
        //分别查询待接单,待派送,派送中的订单数量
        Integer toBeConfirmed = orderMapper.countByStatus(Orders.TO_BE_CONFIRMED);
        Integer confirmed = orderMapper.countByStatus(Orders.CONFIRMED);
        Integer deliveryInProgress = orderMapper.countByStatus(Orders.DELIVERY_IN_PROGRESS);
        //封装返回结果
        return OrderStatisticsVO.builder()
                .toBeConfirmed(toBeConfirmed)
                .confirmed(confirmed)
                .deliveryInProgress(deliveryInProgress).build();
    }
    //服务端接单
    @Override
    public void adminConfirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = Orders.builder()
                .id(ordersConfirmDTO.getId())
                .status(Orders.CONFIRMED)
                .build();
        orderMapper.update(orders);
    }
    //服务端拒单
    @Override
    public void adminRejectOrder(OrdersRejectionDTO ordersRejectionDTO) {
    //判断有没有填写拒单原因
        if(ordersRejectionDTO.getRejectionReason().isEmpty()){
            throw new OrderBusinessException(MessageConstant.ORDER_REJECTION_REASON_IS_NULL);
        }
        Orders ordersDB = orderMapper.getById(ordersRejectionDTO.getId());
        //只有订单为待接单才能执行拒单操作
        if(!ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        // TODO: 2024/5/1 暂时跳过微信退款接口，直接修改订单状态的代码
        //修改订单状态为已取消,并设置取消原因和取消时间
        Orders orders = Orders.builder()
                .id(ordersRejectionDTO.getId())
                .status(Orders.CANCELLED)
                .cancelReason(ordersRejectionDTO.getRejectionReason())
                .cancelTime(LocalDateTime.now())
                .build();
        orderMapper.update(orders);
    }
    //服务端取消订单
    @Override
    public void adminCancelOrder(OrdersCancelDTO ordersCancelDTO) {
        //根据id查询订单
        Orders ordersDB = orderMapper.getById(ordersCancelDTO.getId());
        // TODO: 2024/5/1 跳过微信退款接口，直接修改订单状态的代码
//        weChatRefundVerify(ordersDB);
        //修改订单状态为已取消,并设置取消原因和取消时间
        Orders orders = Orders.builder()
                .id(ordersCancelDTO.getId())
                .status(Orders.CANCELLED)
                .cancelReason(ordersCancelDTO.getCancelReason())
                .cancelTime(LocalDateTime.now())
                .payStatus(ordersDB.getPayStatus())
                .build();
        orderMapper.update(orders);
    }
    //派送订单，将待派送订单更改为已派送

    @Override
    public void isDelivery(Long id) {
        //根据id查询订单
        Orders ordersDB = orderMapper.getById(id);
        //只有订单为待派送才能执行派送操作
        if(ordersDB == null || !ordersDB.getStatus().equals(Orders.CONFIRMED)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders orders = Orders.builder()
                .id(id)
                .status(Orders.DELIVERY_IN_PROGRESS)
                .build();
        orderMapper.update(orders);
    }
    //完成订单
    @Override
    public void completeOrder(Long id) {
    // 根据id查询订单
        Orders ordersDB = orderMapper.getById(id);

        // 校验订单是否存在，并且状态为4
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders orders = Orders.builder()
                .id(id)
                .status(Orders.COMPLETED)
                .build();
        orderMapper.update(orders);
    }

    //封装一个对退款处理的方法
    private void weChatRefundVerify(Orders ordersDB) {
        //如果订单支付状态为已支付，需要执行退款操作
        if(ordersDB.getPayStatus().equals(Orders.PAID)){
            try {
                String refund = weChatPayUtil.refund(
                        ordersDB.getNumber(),//商户订单号
                        ordersDB.getNumber(),//商户退款单号
                        ordersDB.getAmount(),//订单金额
                        ordersDB.getAmount()//退款金额
                );
            } catch (Exception e) {
                throw new OrderBusinessException(MessageConstant.ORDER_REFUND_ERROR);
            }
            //修改订单支付状态为退款
            ordersDB.setPayStatus(Orders.REFUND);
        }
    }
    //补充订单字段信息
    private void addOrderDetails(Page<Orders> page, List<OrderVO> orderVOList) {
        for (Orders orders : page) {
            Long orderId = orders.getId();
            //查询订单详情
            List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orderId);
            //封装订单详情
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orders, orderVO);
            orderVO.setOrderDetailList(orderDetailList);
            orderVOList.add(orderVO);
        }
    }
    // 补充订单otherDishes字段信息
    private void addOtherDishes(Page<Orders> page, List<OrderVO> orderVOList) {
        for (Orders orders : page) {
            //封装订单基础信息
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orders, orderVO);
            //获取订单详情信息
            List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());
            //根据订单详情生成otherDishes字符串,并封装到orderVO中
            String otherDishes = getOtherDishes(orderDetailList);
            orderVO.setOrderDishes(otherDishes);
            orderVOList.add(orderVO);
        }
    }
    //根据订单详情生成otherDishes字符串
    private String getOtherDishes(List<OrderDetail> orderDetailList) {
        //根据订单详情生成otherDishes字符串
        List<String> otherDishesList = orderDetailList.stream().map(orderDetail -> orderDetail.getName() + "*" + orderDetail.getNumber()).collect(Collectors.toList());
        return String.join(";", otherDishesList);
    }
}
