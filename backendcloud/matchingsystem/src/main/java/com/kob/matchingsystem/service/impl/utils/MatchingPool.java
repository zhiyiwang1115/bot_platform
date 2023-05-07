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


    @Override
    public void run(){

    }
}
