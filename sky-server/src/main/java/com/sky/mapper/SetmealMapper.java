package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);
    //添加套餐信息
    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);
    //分页套餐查询
    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);
    //id获取套餐
    @Select("select * from setmeal where id = #{id}")
    Setmeal getId(Long id);
    //根据id获取基本信息和对应类别名
    SetmealVO getByIdWithCategoryName(Long id);
    //更新套餐基础信息
    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);
}
