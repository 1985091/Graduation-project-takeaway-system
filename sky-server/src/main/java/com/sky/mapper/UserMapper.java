package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    //根据openid查用户
    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);
    //add用户
    void insert(User user);
    // 根据Id获取用户
    @Select("select * from user where id = #{id}")
    User getById(Long userId);
}
