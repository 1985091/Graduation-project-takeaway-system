package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Api( tags = "菜品管理接口项")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    /*增加菜品*/
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO){
      log.info("增加菜品:{}",dishDTO);
      dishService.saveFlavor(dishDTO);
      return Result.success();
    }
    //菜品分页查询
    @GetMapping("/page")
    @ApiOperation("菜品管理分页查询")
    public Result<PageResult> pagemanager(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询:{}",dishPageQueryDTO);
        PageResult page = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(page);
    }
    //删除菜品

    @DeleteMapping
    @ApiOperation("菜品的批量删除")
    public Result delete(@RequestParam List<Long> ids){
        log.info("菜品的批量删除:{}",ids);
        dishService.deleteBatch(ids);
        return Result.success();
    }

    //根据id查询
    @GetMapping("/{id}")
    @ApiOperation("根据id来查询菜品")
    public Result<DishVO> getId(@PathVariable Long id){
        log.info("根据ID来查询菜品:{}",id);
        DishVO dishVO = dishService.getIdwithFlavor(id);
        return Result.success(dishVO);
    }
    //根据id修改菜品
    @PutMapping
    @ApiOperation("根据id修改菜品")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品:{}",dishDTO);
        dishService.updateFlavor(dishDTO);
        return Result.success();
    }
}
