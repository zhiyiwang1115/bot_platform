package com.kob.matchingsystem.controller;

import com.kob.matchingsystem.service.MatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MatchingController {
    @Autowired
    MatchingService matchingService;

    //here must be MultiValueMap, a key maps to multiple values
    // can't use map, thinks it because rest template in spring boot
    @PostMapping("/player/add/")
    public String addPlayer(@RequestParam MultiValueMap<String,String> map){
        Integer userId = Integer.parseInt(map.getFirst("user_id"));
        Integer rating = Integer.parseInt(map.getFirst("rating"));
        return matchingService.addPlayer(userId,rating);
    }

    @PostMapping("/player/remove/")
    public String removePlayer(@RequestParam MultiValueMap<String,String> map){
        Integer userId = Integer.parseInt(map.getFirst("user_id"));
        return matchingService.removePlayer(userId);
    }
}
