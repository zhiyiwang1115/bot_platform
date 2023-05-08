package com.kob.matchingsystem.service.impl.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

//only component case inject beans
@Component
public class MatchingPool extends Thread{

    static RestTemplate restTemplate;
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){
        MatchingPool.restTemplate = restTemplate;
    }

    private static String startGameUrl = "http://127.0.0.1:3000/pk/start/game/";
    //don't need thread safe since we have our own lock
    private static List<Player> players = new ArrayList<>();
    private ReentrantLock lock = new ReentrantLock();

    public void addPlayer(Integer userId, Integer rating){
        lock.lock();
        try {
            players.add(new Player(userId,rating,0));
        }finally {
            lock.unlock();
        }
    }

    public void removePlayer(Integer userId){
        lock.lock();
        try{
            List<Player> newPlayers = new ArrayList<>();
            for(Player player: players){
                if(!player.getId().equals(userId))newPlayers.add(player);
            }
            players = newPlayers;
        }finally {
            lock.unlock();
        }
    }

    private void increaseWaitingTime(){ //increase waiting time of all players by 1
        for(Player player: players){
            player.setWaitingTime(player.getWaitingTime()+1);
        }
    }

    private boolean checkMatched(Player a, Player b){
        return b.getRating() >= a.getRating() - a.getWaitingTime() * 10 && b.getRating() <= a.getRating() + a.getWaitingTime() * 10;
    }

    private void sendResult(Player a, Player b){
        System.out.println("send result: " + a.toString() + " " + b.toString());
        MultiValueMap<String, String> res = new LinkedMultiValueMap<>();
        res.add("a_id",a.getId().toString());
        res.add("b_id",b.getId().toString());
        restTemplate.postForObject(startGameUrl,res,String.class);
    }
    private void matchPlayers(){
        //match logic is that every 1 second passed,
        //the rating difference threshold increases by 10
        System.out.println("match players: " + players.toString());
        boolean used[] = new boolean[players.size()];
        for(int i = 0;i<players.size();i++){
            if(used[i])continue;
            for(int j = i+1;j<players.size();j++){
                if(used[j])continue;
                Player a = players.get(i);
                Player b = players.get(j);
                if(checkMatched(a,b) && checkMatched(b,a)){
                    used[i] = used[j] = true;
                    sendResult(a,b);
                    break;
                }
            }
        }
        List<Player> newPlayers = new ArrayList<>();
        for(int i = 0;i<players.size();i++){
            if(!used[i])newPlayers.add(players.get(i));
        }
        players = newPlayers;
    }


    @Override
    public void run(){
        //while loop, every 1s check if there can be any matches
        while(true){
            try {
                Thread.sleep(1000);
                lock.lock();
                try{
                    matchPlayers();
                    increaseWaitingTime();
                }finally {
                    lock.unlock();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
