package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import org.springframework.stereotype.Service;

import java.util.List;


public interface DishService {
    //新增菜品和对应的口味数据
    public void saveFlavor(DishDTO dishDTO);
    //菜品分页展示
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);
    //批量删除
    void deleteBatch(List<Long> ids);
}
