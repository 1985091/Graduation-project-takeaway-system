package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.List;
@Mapper
public interface SetmealDishMapper {
    //根据菜品ID来查对应的套餐ID
    List<Long> getSetmealIdsDishIds(List<Long> dishIds);
    //批量插入套餐菜品关系
    void insertBatch(List<SetmealDish> setmealDish);
    //根据id删除套餐
    void deleteIds(List<Long> ids);
    //根据套餐id删除套餐与菜品关系数据
    void deleteSetmealIds(List<Long> setmealIds);
    //根据id查询菜品id
    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> getDishesSetmealId(Long id);
}
