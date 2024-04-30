package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShopCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
@Api(tags = "客户端购物车接口")
public class ShopCartController {
    @Autowired
    private ShopCartService shopCartService;
    //添加购物车
    @PostMapping("/add")
    @ApiOperation("添加购物车")

    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("添加购物车:{}",shoppingCartDTO);
        shopCartService.addShopCart(shoppingCartDTO);
        return Result.success();
    }
    //查看购物车
    @GetMapping("/list")
    @ApiOperation("查看购物车")

    public Result<List<ShoppingCart>> listCart(){
        List<ShoppingCart> list = shopCartService.showShopCart();
        return Result.success(list);
    }
    //清空购物车
    @DeleteMapping("/clean")
    @ApiOperation("清空")
    public Result cleanCart(){
        shopCartService.cleanCart();
        return Result.success();
    }
    //删除已选商品
    @PostMapping("/sub")
    @ApiOperation("删除已选")
    public Result sub(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("删除已选:{}",shoppingCartDTO);
        shopCartService.deleteGood(shoppingCartDTO);
        return Result.success();
    }
}
