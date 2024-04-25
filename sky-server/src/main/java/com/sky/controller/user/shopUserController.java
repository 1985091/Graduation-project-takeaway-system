package com.sky.controller.user;

import com.sky.constant.ShopStatusConstant;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/shop")
@Api(tags = "客户端店铺状态接口")
@Slf4j
public class shopUserController {
    @Autowired
    private RedisTemplate redisTemplate;
    //查询店铺状态
    @GetMapping("/status")
    @ApiOperation("获取商铺营业状态")
    public Result<Integer> getShopStatus(){
        Integer status = (Integer) redisTemplate.opsForValue().get(ShopStatusConstant.SHOP_STATUS_KEY);
        log.info("获取商店运营状态:{}",status == ShopStatusConstant.ENABLE);
        return Result.success(status);
    }
}