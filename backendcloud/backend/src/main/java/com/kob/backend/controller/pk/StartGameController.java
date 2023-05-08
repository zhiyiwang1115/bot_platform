package com.kob.backend.controller.pk;

import com.kob.backend.service.pk.StartGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StartGameController {
    @Autowired
    StartGameService startGameService;

    @PostMapping("/pk/start/game/")
    public String startGame(@RequestParam MultiValueMap<String,String> map){
        Integer aId = Integer.parseInt(map.getFirst("a_id"));
        Integer bId = Integer.parseInt(map.getFirst("b_id"));
        return startGameService.startGame(aId,bId);
    }
}
