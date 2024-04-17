package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MemberSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/*
* 自定义切面类，实现公共字段自动填充*/
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    //指定切入点
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")//拦截需要拦截的公共字段，在autofill中的注释
    public void autoFillPointCut(){}
    //设置前置通知，在通知中进行公共字段的赋值
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("开始进行公共字段的自动填充...");
        //获取到当前被拦截方法上的数据库操作类型,它是update or insert
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//方法签名对象
//        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);//获取方法上的注解对象
//        OperationType operationType = autoFill.value();//获取数据库操作类型
        OperationType operationType = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(AutoFill.class).value();
        //获得到当前被拦截方法的参数。。实体对象
        Object[] args = joinPoint.getArgs();
        //判断是否参数是否有
        if(args == null || args.length ==0){
            log.error("参数为空，无法自动填充");
            return;
        }
        //实体对象
        Object entity = args[0];
        //准备赋值的数据
        Long currentId = BaseContext.getCurrentId();
        LocalDateTime currentTime = LocalDateTime.now();
        //当前不同的操作类型，为对应的属性通过反射来赋值
        if(operationType ==OperationType.INSERT){
            try{
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                //通过反射为对象属性赋值
                setCreateTime.invoke(entity,currentTime);
                setUpdateTime.invoke(entity,currentTime);
                setCreateUser.invoke(entity,currentId);
                setUpdateUser.invoke(entity,currentId);
            } catch (Exception e) {
                log.error("反射设置公共字段失败:{}",e.getMessage());
            }
        }else if(operationType == OperationType.UPDATE){
            try{
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                //通过反射设置公共字段
                setUpdateTime.invoke(entity,currentTime);
                setUpdateUser.invoke(entity,currentId);
            } catch (Exception e) {
                log.error("反射设置公共字段失败{}",e.getMessage());
            }
        }
    }
}
