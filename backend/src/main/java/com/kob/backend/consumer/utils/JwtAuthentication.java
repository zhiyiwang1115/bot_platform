package com.kob.backend.consumer.utils;


import com.kob.backend.utils.JwtUtil;
import io.jsonwebtoken.Claims;

//utility class for jwt authentication at websocket connection
//since it would not reach jwt filter with websocket connection
//so we need to do authentication with token in the url
public class JwtAuthentication {
    public static Integer getUserId(String token){
        Integer userId = -1;
        try{
            Claims claims = JwtUtil.parseJWT(token);
            userId = Integer.parseInt(claims.getSubject());
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        //if -1, means does not exist
        return userId;
    }
}
