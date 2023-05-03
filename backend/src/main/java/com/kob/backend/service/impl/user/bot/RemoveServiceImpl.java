package com.kob.backend.service.impl.user.bot;

import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.bot.RemoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RemoveServiceImpl implements RemoveService {
    @Autowired
    BotMapper botMapper;

    @Override
    public Map<String, String> remove(Map<String, String> map) {
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
        botMapper.deleteById(bot_id);
        res.put("error_message", "success");
        return res;
    }
}
