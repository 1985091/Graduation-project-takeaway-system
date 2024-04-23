package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.bridge.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;


/*通用接口*/
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;
    // 文件上传
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传:{}",file);
        try{
            //获取原始文件的扩展名
            String extensionName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            //更改文件名为uuid
            String uuidName = UUID.randomUUID().toString() + extensionName;
            // 文件上传
            String Path = aliOssUtil.upload(file.getBytes(), uuidName);
            return  Result.success(Path);
        }catch (IOException e){
            log.error((e.getMessage()));
        }
        return  Result.error((MessageConstant.UPLOAD_FAILED));
    }
}
