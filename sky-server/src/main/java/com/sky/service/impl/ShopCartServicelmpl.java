package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.exception.BaseException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShopCartMapper;
import com.sky.service.ShopCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShopCartServicelmpl implements ShopCartService {
    @Autowired
    private ShopCartMapper shopCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    //添加购物车
    @Override
    public void addShopCart(ShoppingCartDTO shoppingCartDTO) {
        //获取购物车表
        ShoppingCart shopCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shopCart);
        shopCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> shoppingCarts = shopCartMapper.listSelect(shopCart);
        //判断购物车中是否存在该商品
        if(shoppingCarts != null && !shoppingCarts.isEmpty()){
            //如果存在，测数量加一
            ShoppingCart cart = shoppingCarts.get(0);
            cart.setNumber(cart.getNumber() + 1);
            shopCartMapper.updateNumberById(cart);
        }else{
            Long dishId = shoppingCartDTO.getDishId();
            //不存在，判断添加的是菜品还是套餐
            if( dishId != null){
                //添加菜品
                Dish dish = dishMapper.getById(dishId);
                shopCart.setName(dish.getName());
                shopCart.setImage(dish.getImage());
                shopCart.setAmount(dish.getPrice());
            }
            else{
                //添加套餐
                Long setmealId = shoppingCartDTO.getSetmealId();
                Setmeal setmeal = setmealMapper.getId(setmealId);
                shopCart.setName(setmeal.getName());
                shopCart.setImage(setmeal.getImage());
                shopCart.setAmount(setmeal.getPrice());
            }
            shopCart.setNumber(1);
            shopCart.setCreateTime(LocalDateTime.now());
            shopCartMapper.insert(shopCart);
        }
    }
    //查看购物车
    @Override
    public List<ShoppingCart> showShopCart() {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shopCart = ShoppingCart.builder().userId(userId).build();
        shopCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> shoppingCarts = shopCartMapper.listSelect(shopCart);
        return shoppingCarts;
    }
    //清空购物车
    @Override
    public void cleanCart() {
        Long userId = BaseContext.getCurrentId();
        shopCartMapper.deleteByUerId(userId);
    }
    //删除已选
    @Override
    public void deleteGood(ShoppingCartDTO shoppingCartDTO) {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart cart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,cart);
        cart.setUserId(userId);
        //获取指定商品的id和数量
        List<ShoppingCart> list = shopCartMapper.listSelect(cart);
        if(list == null && list.isEmpty()){
            throw new BaseException(("商品不存在，无法删除"));
        }
        ShoppingCart cart2 = list.get(0);
        Integer number = cart2.getNumber();
        if(number > 1){
            //数量大于1，减少1
            cart2.setNumber(number -1);
            shopCartMapper.updateNumberById(cart2);
        }else{
            // 数量为1个
            shopCartMapper.deleteById(cart2);
        }
    }
}
