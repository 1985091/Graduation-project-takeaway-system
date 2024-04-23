package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

import java.util.List;
@Mapper
public interface SetmealDishMapper {
    //根据菜品ID来查对应的套餐ID
    List<Long> getSetmealIdsDishIds(List<Long> dishIds);

}
