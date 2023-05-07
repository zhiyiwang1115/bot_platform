package com.kob.matchingsystem.service.impl.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class MatchingPool extends Thread{
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
        lock.unlock();
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
            player.setWaitingTime(player.getWaitingTime()+1);.
        }
    }

    private boolean checkMatched(Player a, Player b){

    }

    private void sendResult(Player a, Player b){
        
    }
    private void matchPlayers(){

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
