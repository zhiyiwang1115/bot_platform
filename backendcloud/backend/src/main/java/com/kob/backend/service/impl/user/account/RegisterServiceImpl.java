package com.kob.backend.service.impl.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.service.user.account.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    UserMapper userMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public Map<String, String> register(String username, String password, String confirmed_password) {
        Map<String,String> res = new HashMap<>();
        if(username==null){
            res.put("error_message","username cannot be empty");
            return res;
        }
        if(password==null){
            res.put("error_message","password cannot be empty");
            return res;
        }
        //delete blank at front and at end
        username = username.trim();
        if(username.length()==0){
            res.put("error_message","username cannot be empty");
            return res;
        }
        if(password.length()==0){
            res.put("error_message","password cannot be empty");
            return res;
        }
        if(username.length()>100){
            res.put("error_message","username cannot be longer than 100");
            return res;
        }
//        dont think we need test length of password because after same length after encrpytion
//        if(password.length()>100){
//            res.put("error_message","password cannot be longer than 100");
//            return res;
//        }
        if(!password.equals(confirmed_password)){
            res.put("error_message","password and confirmed password are not same");
            return res;
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        User user = userMapper.selectOne(queryWrapper);
        if(user!=null){
            res.put("error_message","username exists");
            return res;
        }
        String photo = "https://cdn.acwing.com/media/user/profile/photo/27408_lg_6a1cf10e8e.jpeg";
        user = new User(null,username,passwordEncoder.encode(password),null,photo);
        userMapper.insert(user);
        res.put("error_message","success");
        return res;

    }
}
