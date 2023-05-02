package com.kob.backend.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Wrapper;
import java.util.List;
//deprecated [just for tuning apis]


//4 layers in springboot
//pojo: database table => class
//mapper: curd in class => sql (basic operations)
//service: implement service (more complicated, may involve multiple mappers)
//controller: schedule service according to the data from frontend

//Controller + Response Body
@RestController
public class UserController {

    // when use mapper, must put autowired here
    @Autowired
    UserMapper userMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/user/all/")
    public List<User> getAll(){
        return userMapper.selectList(null);
    }

    //case when parameter in the url
    @GetMapping("/user/{userId}/")
    public User getUser(@PathVariable int userId){
        //grammar for select with condition
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",userId);
        return userMapper.selectOne(queryWrapper);
    }

    @GetMapping("/user/add/{userId}/{username}/{password}/")
    public String addUser(@PathVariable int userId,
                          @PathVariable String username,
                          @PathVariable String password){
        User user = new User(null,username,passwordEncoder.encode(password),null,null);
        userMapper.insert(user);
        return "success";
    }

    @GetMapping("/user/delete/{userId}")
    public String deleteUser(@PathVariable int userId){
        userMapper.deleteById(userId);
        return "success";
    }
}
