package com.kob.backend.service.impl.user.bot;

import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.bot.AddService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AddServiceImpl implements AddService {

    @Autowired
    BotMapper botMapper;

    @Override
    public Map<String, String> add(Map<String, String> map) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl login_user = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = login_user.getUser();
        String title = map.get("title");
        String description = map.get("description");
        String content = map.get("content");
        Map<String,String> res = new HashMap<>();
        if(title==null || title.length()==0){
            res.put("error_message","title cannot be empty");
            return res;
        }
        if(title.length()>100){
            res.put("error_message","title cannot be larger than 100");
            return res;
        }
        if(description !=null && description.length()>300){
            res.put("error_message","description cannot be larger than 300");
            return res;
        }
        if(content != null && content.length()>10000){
            res.put("error_message","content cannot be larger than 10000");
            return res;
        }
        Date now = new Date() ;
        Bot bot = new Bot(null, user.getId(), title,description,content,now,now);
        botMapper.insert(bot);
        res.put("error_message", "success");
        return res;
    }
}
