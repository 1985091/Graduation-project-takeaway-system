package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);
//插入菜品
   @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);
//  菜品分页
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    //根据主键查询菜品
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    //根据主键来删除菜品信息
    @Delete("delete from dish where id = #{id}")
    void deleteBbyId(Long id);
    //跟新菜品信息
    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);
    //根据分类id查询菜品
    @Select("select * from dish where category_id = #{categoryId}")
    List<Dish> listCategoryId(Long categoryId);

    //客户端根据id查询菜品（新）
    List<Dish> list(Dish dish);
}
