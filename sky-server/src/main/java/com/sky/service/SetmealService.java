package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

//新增套餐，保持套餐和菜品之间的联系
public interface SetmealService {

    void saveWithDish(SetmealDTO setmealDTO);
    //套餐分页查询
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);
    //删除套餐
    void deleteBatch(List<Long> ids);
    //根据id查询套餐
    SetmealVO getId(Long id);
    //更新套餐
    void updateDish(SetmealDTO setmealDTO);
    //起售或停售
    void startAndStop(Integer status, Long id);
    //条件查询
     List<Setmeal> list02(Setmeal setmeal);
     //客户端根据id查询菜品选项
     List<DishItemVO> getDishItemById(Long id);
}
