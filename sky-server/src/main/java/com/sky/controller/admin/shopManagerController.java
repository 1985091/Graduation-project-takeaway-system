package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/shop")
@Api(tags = "店铺状态接口")
@Slf4j
public class shopManagerController {

    public Result changeStatus(Integer status){
        return Result.success();
    }
}
