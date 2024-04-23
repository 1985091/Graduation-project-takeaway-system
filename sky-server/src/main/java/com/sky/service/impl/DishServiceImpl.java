package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    public DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Transactional //保证方法是原子性的，要么全成功或全失败
    @Override
    public void saveFlavor(DishDTO dishDTO) {
        //向菜品表插入一条数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.insert(dish);
        //获取insert之后生成的主键值
        Long dishID = dish.getId();
        // 获取口味数据
        List<DishFlavor> flavor = dishDTO.getFlavors();
        if(flavor != null && flavor.size() > 0){
            flavor.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishID);
            });
            dishFlavorMapper.insertBatch(flavor);
        }

    }
//菜品分页查询
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());//开始分页
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }
//批量删除
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Transactional
    @Override
    public void deleteBatch(List<Long> ids) {
        //判断菜品是否存在起售的，起售的不能删除
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //判断菜品是否关联套餐，关联不能删
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsDishIds(ids);
        if (setmealIds == null && setmealIds.size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //删除菜品数据
        //删除口味数据
        for (Long id : ids){
            dishMapper.deleteBbyId(id);
            dishFlavorMapper.deleteByDishId(id);
        }
    }
}
