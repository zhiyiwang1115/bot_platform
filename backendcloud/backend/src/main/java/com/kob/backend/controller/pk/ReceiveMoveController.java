package com.kob.backend.controller.pk;

import com.kob.backend.service.pk.ReceiveMoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReceiveMoveController {
    @Autowired
    ReceiveMoveService receiveMoveService;

    @PostMapping("/pk/receive/move/")
    String receiveBotMove(@RequestParam MultiValueMap<String,String> map){
        Integer userId = Integer.parseInt(map.getFirst("user_id"));
        Integer direction = Integer.parseInt(map.getFirst("direction"));
        return receiveMoveService.receiveBotMove(userId, direction);
    }
}
