package com.kob.backend.service.impl.user.account;

import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.account.LoginService;
import com.kob.backend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {

    //need to inject authentication manager
    @Autowired
    AuthenticationManager authenticationManager;

    @Override
    public Map<String, String> login(String username, String password) {
        //make username and password to authentication token
        //password would be encrypted here by the framework (defined in SecurityConfig) and userDetailImpl
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,password);
        //it will automatically resolve any error
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        //get user from authenticate
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticate.getPrincipal();
        User user = loginUser.getUser();
        //generate jwt token based on the user
        String jwt = JwtUtil.createJWT(user.getId().toString());
        Map<String,String> res = new HashMap<>();
        res.put("error_message","success");
        res.put("token",jwt);
        return res;
    }
}
