package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}
