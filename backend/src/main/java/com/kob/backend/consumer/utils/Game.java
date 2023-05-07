package com.kob.backend.consumer.utils;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.pojo.Record;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

//make game class support multi-thread, or as a second thread
//after creating a game, a thread will wait some seconds for next operations of two players
//if a single thread is doing that, then it will block the whole progress
//therefore, we need two threads here, one is for read client operations (the websocketserver)
//the other is checking if next step should be done and judged (the game itself)
public class Game extends Thread{
    private Integer rows;
    private Integer cols;
    private Integer inner_walls_cnt;
    private int[][] g;

    private Player playerA, playerB;
    private Integer nextStepA = null;
    private Integer nextStepB = null;
    //here in this thread, it will check if nextstepA and nextstepB is null, which is a read operation
    //while in the main thread, it will have write operation into these two variables
    //for write-read, write-write operations, there may be race condition
    //therefore, should add a lock when reading and writing
    public ReentrantLock lock = new ReentrantLock();
    private String status = "playing"; // playing -> finished
    private String loser = ""; // all, A, B


    private static int[] dx = {-1,0,1,0};
    private static int[] dy = {0,1,0,-1};

    public Game(Integer rows, Integer cols, Integer inner_walls_cnt, Integer idA, Integer idB){
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_cnt = inner_walls_cnt;
        this.g = new int[rows][cols];
        playerA = new Player(idA,rows-2,1,new ArrayList<>());
        playerB = new Player(idB,1,cols-2,new ArrayList<>());
    }

    public Player getPlayerA() {
        return playerA;
    }

    public Player getPlayerB() {
        return playerB;
    }

    public void setNextStepA(Integer nextStepA) {
        lock.lock();;
        try{
            this.nextStepA = nextStepA;
        }finally {
            //unlock the lock anyway
            lock.unlock();
        }
    }

    public void setNextStepB(Integer nextStepB) {
        lock.lock();;
        try{
            this.nextStepB = nextStepB;
        }finally {
            //unlock the lock anyway
            lock.unlock();
        }
    }

    public int[][] getG(){
        return g;
    }

    private boolean check_connectivity(int sx, int sy, int tx, int ty){
        if(sx==tx && sy==ty)return true;
        g[sx][sy] = 1;

        for(int i = 0;i<4;i++){
            int x = sx+dx[i], y = sy+dy[i];
            if(g[x][y]==0 && check_connectivity(x,y,tx,ty)){
                g[sx][sy] = 0;
                return true;
            }
        }

        g[sx][sy] = 0;
        return false;
    }

    private boolean draw(){
        for(int i = 0;i<rows;i++){
            for(int j = 0;j<cols;j++){
                g[i][j] = 0;
            }
        }

        for(int r = 0;r<rows;r++){
            g[r][0] = g[r][cols-1] = 1;
        }

        for(int c = 0;c<cols;c++){
            g[0][c] = g[rows-1][c] = 1;
        }

        Random random = new Random();
        for(int i = 0;i<inner_walls_cnt/2;i++){
            for(int j = 0;j<1000;j++){
                int r = random.nextInt(rows);
                int c = random.nextInt(cols);

                if(g[r][c]==1)continue;
                if((r==rows-2&&c==1)||(r==1&&c==cols-2))continue;
                g[r][c] = g[rows-1-r][cols-1-c] = 1;
                break;
            }
        }
        return check_connectivity(rows-2,1,1,cols-2);
    }

    public void createMap(){
        for(int i = 0;i<1000;i++){
            if(draw()){
                break;
            }
        }
    }

    private boolean nextStep(){ //wait next steps of two players
        try {
            //the reason of sleeping here is that the thread will keep getting next step
            //it is possible that the operations are done so fast by bots
            // that frontend is not able to render at the same rate
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try{
            for(int i = 0;i<50;i++){
                Thread.sleep(100);
                lock.lock();
                try{
                    if(nextStepA!=null && nextStepB!=null){
                        playerA.getSteps().add(nextStepA);
                        playerB.getSteps().add(nextStepB);
                        return true;
                    }
                }finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void sendAllMessage(String message){
        WebSocketServer.users.get(playerA.getId()).sendMessage(message);
        WebSocketServer.users.get(playerB.getId()).sendMessage(message);
    }
    private void sendMove(){//send moves to two clients
        lock.lock();
        try {
            JSONObject resp = new JSONObject();
            resp.put("event","move");
            //here has potential race condition as well, should use the last element at playerA.getSteps()
            resp.put("a_direction",nextStepA);
            resp.put("b_direction",nextStepB);
            sendAllMessage(resp.toJSONString());
            //empty the direction
            nextStepA = nextStepB = null;
        }finally {
            lock.unlock();
        }

    }

    private boolean check_valid(List<Cell> cellsA, List<Cell> cellsB){
        Cell cell = cellsA.get(0);
        if(g[cell.x][cell.y]==1)return false;
        //can be updated to iterator so that the efficiency would be better
        for(int i = 1;i<cellsA.size();i++){
            Cell c = cellsA.get(i);
            if(cell.x==c.x && cell.y==c.y)return false;
        }
        for(int i = 0;i<cellsB.size();i++){
            Cell c = cellsB.get(i);
            if(cell.x==c.x && cell.y==c.y)return false;
        }
        return true;
    }

    private void judge(){ //check if operations from two players are valid
        List<Cell> cellsA = playerA.getCells();
        List<Cell> cellsB = playerB.getCells();
        boolean validA = check_valid(cellsA,cellsB);
        boolean validB = check_valid(cellsB, cellsA);
        if(!validA && !validB){
            status = "finished";
            loser = "all";
        }
        else if(!validA){
            status = "finished";
            loser = "A";
        }
        else if(!validB){
            status = "finished";
            loser = "B";
        }
    }

    private String getMapString(){
        StringBuilder res = new StringBuilder();
        for(int i = 0;i<rows;i++){
            for(int j = 0;j<cols;j++){
                res.append(g[i][j]);
            }
        }
        return res.toString();
    }
    private void saveToDatabase(){
        Record record = new Record(
                null,
                playerA.getId(),
                playerA.getSx(),
                playerA.getSy(),
                playerB.getId(),
                playerB.getSx(),
                playerB.getSy(),
                playerA.getStepsString(),
                playerB.getStepsString(),
                getMapString(),
                new Date(),
                loser
                );
        WebSocketServer.recordMapper.insert(record);
    }

    private void sendResult(){ //send results to two clients
        JSONObject resp = new JSONObject();
        resp.put("event","result");
        resp.put("loser",loser);
        saveToDatabase();
        sendAllMessage(resp.toJSONString());
    }

    //entrance function of running a thread
    //here it would wait 5 seconds, checking if there are moves from two players
    @Override
    public void run(){
        //the game must end within 1000 steps
        for(int i = 0;i<1000;i++){
            if(nextStep()){
                judge();
                if(status.equals("playing")){
                    sendMove();
                }else {
                    sendResult();
                    break;
                }
            }else{
                //if some player does not have next input
                //game is finished
                status = "finished";
                //here there may still have race condition
                //can let nextstep return losers directly in the future
                lock.lock();
                try{
                    if(nextStepA==null && nextStepB==null){
                        loser = "all";
                    }else if (nextStepA==null){
                        loser = "A";
                    }else{
                        loser = "B";
                    }
                }finally {
                    lock.unlock();
                }
                sendResult();
                break;
            }
        }
    }
}
