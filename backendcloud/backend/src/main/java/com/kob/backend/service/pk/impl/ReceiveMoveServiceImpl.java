package com.kob.backend.service.pk.impl;

import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.service.pk.ReceiveMoveService;
import org.springframework.stereotype.Service;

@Service
public class ReceiveMoveServiceImpl implements ReceiveMoveService {
    @Override
    public String receiveBotMove(Integer userId, Integer direction) {
        if(WebSocketServer.users.get(userId)!=null){
            Game game = WebSocketServer.users.get(userId).game;
            if(game!=null){
                if(userId.equals(game.getPlayerA().getId())){
                        game.setNextStepA(direction);
                }else if (userId.equals(game.getPlayerB().getId())){
                        game.setNextStepB(direction);
                }
            }
        }
        return "receive success";
    }
}
