package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShopCartService {
    //添加购物车方法
    void addShopCart(ShoppingCartDTO shoppingCartDTO);
    //查看购物车
    List<ShoppingCart> showShopCart();
    //清空购物车
    void cleanCart();
    //删除已选
    void deleteGood(ShoppingCartDTO shoppingCartDTO);
}
