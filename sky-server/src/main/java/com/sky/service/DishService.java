package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;
import org.springframework.stereotype.Service;

import java.util.List;


public interface DishService {
    //新增菜品和对应的口味数据
    public void saveFlavor(DishDTO dishDTO);
    //菜品分页展示
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);
    //批量删除
    void deleteBatch(List<Long> ids);
    //根据id查询菜品
    DishVO getIdwithFlavor(Long id);
    //修改菜品信息
    void updateFlavor(DishDTO dishDTO);
    // 根据分类id查询菜品
    List<Dish> listCategoryId(Long categoryId);
    //菜品起售和停售
    void startAndStop(Integer status, Long id);
    //条件查询菜品和口味
    List<DishVO> listFlavor(Dish dish);
}
