package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class SetmealServicelmpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    //起售和停售
    @Override
    public void startAndStop(Integer status, Long id) {
        if(Objects.equals(status, StatusConstant.ENABLE)){
            //查看套餐对应菜品是否处于启售状态
            List<SetmealDish> setmealDishes = setmealDishMapper.getDishesSetmealId(id);
            // TODO: 2023/12/23 找机会把循环sql的代码优化了
            // TODO: 2023/12/23 修改套餐内容的时候,如果添加了新的菜品,但是没有启售,那么套餐是否应该也设置为停售状态?还是统一在修改之后均设置成停售状态?
            for(SetmealDish setmealDish : setmealDishes){
                if(dishMapper.getById(setmealDish.getDishId()).getStatus().equals(StatusConstant.DISABLE)){
                    throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            }

        }
        //封装套餐对象,修改状态
        Setmeal setmeal = Setmeal.builder().id(id).status(status).build();
        setmealMapper.update(setmeal);
    }

    @Transactional
    @Override
    public void saveWithDish(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //向套餐表中插入一条数据
        setmealMapper.insert(setmeal);
        //像套餐菜品表中插入多条数据
        List<SetmealDish> setmealDish = setmealDTO.getSetmealDishes();
        //判断是否有数据，填充套餐id
        if (setmealDish != null && setmealDish.size() > 0) {
            setmealDish.forEach(dish -> dish.setSetmealId(setmeal.getId()));
            //批量插入
             setmealDishMapper.insertBatch(setmealDish);
        }
    }
    //套餐分页查询
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> voPage = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(voPage.getTotal(),voPage.getResult());
    }
    //删除套餐
    @Override
    public void deleteBatch(List<Long> ids) {
        //判断当前套餐是否是起售状态，如果是起售中则不能删除
         for(Long id : ids){
             Setmeal setmeal = setmealMapper.getId(id);
             if (Objects.equals(setmeal.getStatus(), StatusConstant.ENABLE)) {
                 throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
             }
         }
         //删除套餐表中的对应数据
         setmealDishMapper.deleteIds(ids);
         //删除套餐菜品关系表中对应数据
         setmealDishMapper.deleteSetmealIds(ids);
    }
    //根据id查询套餐
    @Override
    public SetmealVO getId(Long id) {
        //获取套餐信息
        SetmealVO setmealVO = setmealMapper.getByIdWithCategoryName(id);
        //根据套餐id获取套餐对应的菜品信息
        List<SetmealDish> setmealDishes = setmealDishMapper.getDishesSetmealId(id);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }
    //更新套餐
    @Override
    public void updateDish(SetmealDTO setmealDTO) {
        //修改套餐基础信息
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.update(setmeal);
        //删除套餐菜品关系表中对应的数据
        setmealDishMapper.deleteSetmealIds(Arrays.asList(setmeal.getId()));
         //向套餐菜品关系表中插入多条数据
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        //判断是否有菜品数据，并将套餐id填充
        if (setmealDishes == null && setmealDishes.size() > 0) {
           setmealDishes.forEach(dish -> dish.setSetmealId(setmeal.getId()));
           //批量插入
             setmealDishMapper.insertBatch(setmealDishes);
        }
    }
}
