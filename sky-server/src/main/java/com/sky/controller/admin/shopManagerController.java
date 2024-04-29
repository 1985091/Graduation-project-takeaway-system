package com.sky.controller.admin;

import com.sky.constant.ShopStatusConstant;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/admin/shop")
@Api(tags = "店铺状态接口")
@Slf4j
public class shopManagerController {
    @Autowired
    private RedisTemplate redisTemplate;
    //设置店铺营业状态
    @PutMapping("/{status}")
    @ApiOperation("设置营业状态")
    public Result changeStatus(@PathVariable Integer status){
        log.info("设置营业状态为:{}",status == ShopStatusConstant.ENABLE ? "营业中" : "打烊中");
        redisTemplate.opsForValue().set(ShopStatusConstant.SHOP_STATUS_KEY,status);
        return Result.success();
    }

    //查询店铺状态
    @GetMapping("/status")
    @ApiOperation("获取商铺营业状态")
    public Result<Integer> getShopStatus(){
        Integer status = (Integer) redisTemplate.opsForValue().get(ShopStatusConstant.SHOP_STATUS_KEY);
        log.info("获取商店运营状态:{}", Objects.equals(status, ShopStatusConstant.ENABLE) ? "营业中" : "休息");
        return Result.success(status);
    }
}
