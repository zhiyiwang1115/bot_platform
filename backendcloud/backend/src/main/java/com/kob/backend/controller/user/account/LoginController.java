package com.kob.backend.controller.user.account;

import com.kob.backend.service.user.account.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class LoginController {
    @Autowired
    LoginService loginService;

    //RequestParam is used to extract parameters from post
    @PostMapping("/user/account/token/")
    public Map<String,String> login(@RequestParam Map<String,String> map){
        String username = map.get("username");
        String password = map.get("password");
        return loginService.login(username,password);
    }
}
