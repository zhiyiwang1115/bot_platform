package com.kob.backend.service.impl.user.bot;

import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.bot.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UpdateServiceImpl implements UpdateService {
    @Autowired
    BotMapper botMapper;

    @Override
    public Map<String, String> update(Map<String, String> map) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl login_user = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = login_user.getUser();
        Integer bot_id = Integer.parseInt(map.get("bot_id"));
        Bot bot = botMapper.selectById(bot_id);
        Map<String,String> res = new HashMap<>();
        if(bot==null){
            res.put("error_message","The bot has already been deleted");
            return res;
        }
        if(!bot.getUserId().equals(user.getId())){
            res.put("error_message","You do not have right to delete the bot");
            return res;
        }
        String title = map.get("title");
        String description = map.get("description");
        String content = map.get("content");
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
        Bot new_bot = new Bot(bot_id,bot.getUserId(),title,description,content,bot.getCreateTime(),new Date());
        botMapper.updateById(new_bot);
        res.put("error_message","success");
        return res;
    }
}
