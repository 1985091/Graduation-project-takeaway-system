package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.constant.WeChatConstent;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServicelmpl implements UserService {
    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;
    //微信登录
    @Override
    public User wxlogin(UserLoginDTO userLoginDTO) {
        //调用微信接口获取openid
        String openid = getOpenid(userLoginDTO.getCode());
        //判断openid是否存在
        if(openid == null){
            log.error("微信登录失败，code无效");
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        //判断用户是否是新的用户,如果是新用户则自动完成注册
        User user = userMapper.getByOpenid(openid);
        if(user == null){
            user = User.builder().openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }

        return user;
    }
    //私有封装微信调用获取openid接口
    private String getOpenid(String code){
        //这里将微信登录获取openid的代码抽取成了类的私有方法,并且将微信请求接口相关常量封装到了一个常量类WeChatConstent当中
        Map<String,String> params = new HashMap<>();
        params.put(WeChatConstent.WX_LOGIN_APPID,weChatProperties.getAppid());
        params.put(WeChatConstent.WX_LOGIN_SECRET,weChatProperties.getSecret());
        params.put(WeChatConstent.WX_LOGIN_JSCODE,code);
        params.put(WeChatConstent.WX_LOGIN_GRANT_TYPE,WeChatConstent.WX_LOGIN_GRANT_TYPE_VALUE);
        String openid = JSONObject.parseObject(HttpClientUtil.doGet(WeChatConstent.WX_LOGIN_URL,params)).getString(WeChatConstent.WX_LOGIN_OPENID);
        return openid;
    }
}
