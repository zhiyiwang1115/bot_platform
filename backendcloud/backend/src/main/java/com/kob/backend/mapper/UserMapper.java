package com.kob.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kob.backend.pojo.User;
import org.apache.ibatis.annotations.Mapper;

//mapper is an interface
//mybatis annotation, extends BaseMapper for basic sql functionalities
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
