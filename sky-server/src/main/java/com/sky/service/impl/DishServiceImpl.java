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
   //查询对应id菜品
    @Override
    public DishVO getIdwithFlavor(Long id) {
        //更具id查询菜品数据
        Dish dish = dishMapper.getById(id);
        //根据id查询口味数据
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);
        //整合数据封装到DishVo中
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(flavors);
        return dishVO;
    }
    //修改菜品信息
    @Transactional
    @Override
    public void updateFlavor(DishDTO dishDTO) {
      // 更改菜品数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish);
        //更改菜品口味
      //删除原来的口味数据
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
      //插入新的口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
      //新增口味数据重新设置菜品id
        if(flavors != null && flavors.size() > 0){
            flavors.forEach(flavor -> flavor.setDishId(dishDTO.getId()));
            dishFlavorMapper.insertBatch(flavors);
        }
    }
    //根据分类id查询
    @Override
    public List<Dish> listCategoryId(Long categoryId) {
        return dishMapper.listCategoryId(categoryId);
    }
    //菜品起售和停售
    @Override
    public void startAndStop(Integer status, Long id) {
        Dish dish1 = Dish.builder().status(status).id(id).build();
        dishMapper.update(dish1);
    }
}
