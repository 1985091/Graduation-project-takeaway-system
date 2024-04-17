package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
* 自定义注解，用于标识需要进行公共字段的自动填充方法*/
@Target(ElementType.METHOD)//指定注解只能加载在方法上
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    //指定数据库操作类型：Update、Insist
    OperationType value();

}
