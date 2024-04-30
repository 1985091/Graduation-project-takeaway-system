package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShopCartMapper {
    // 查询购物车列表
    List<ShoppingCart> listSelect(ShoppingCart shoppingCart);
    // 修改数量
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateNumberById(ShoppingCart cart);
    //插入购物车数据
    @Insert("insert into shopping_cart (user_id, dish_id, setmeal_id, name, image, amount, number, create_time) values (#{userId}, #{dishId}, #{setmealId}, #{name}, #{image}, #{amount}, #{number}, #{createTime})")
    void insert(ShoppingCart shopCart);
    // 清理购物车
    @Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteByUerId(Long userId);
    @Delete("delete from shopping_cart where id = #{id}")
    void deleteById(ShoppingCart cart2);
}
