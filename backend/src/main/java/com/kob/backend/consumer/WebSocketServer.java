package com.kob.backend.consumer;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.consumer.utils.JwtAuthentication;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

// In essential, every time there is a websocket connection
// a websocket server object is constructed, which includes all information in the connection
// the object is maintained for maintaining the connection
@Component
@ServerEndpoint("/websocket/{token}")  // !!! Note: do not end with / !!!
public class WebSocketServer {
    //websocket is not a standard spring component
    //it is not a skeleton instance
    //because everytime a connection is build, a new object would be constructed
    //it needs to inject something with a static variable
    private static UserMapper userMapper;
    @Autowired
    public void setUserMapper(UserMapper userMapper){
        WebSocketServer.userMapper = userMapper;
    }

    //thread-safe matching pool
    private static CopyOnWriteArraySet<User> matchPool = new CopyOnWriteArraySet<>();

//    session is used for backend to send message to frontend
    private Session session = null;
    private User user;

    private Game game = null;

    //need to store and maintain all connections using static variable
    //need to be thread safe as many threads would visit it at the same time
    public static ConcurrentHashMap<Integer,WebSocketServer> users = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        // when connection is constructed
        this.session = session;
        System.out.println("connected");
        Integer userId = JwtAuthentication.getUserId(token);
        this.user = userMapper.selectById(userId);
        if(this.user!=null){
            users.put(userId,this);
        }else{
            //remember to close session even it's invalid
            this.session.close();
        }

    }

    @OnClose
    public void onClose() {
        // when closing the connection
        System.out.println("disconnected");
        if(this.user!=null){
            users.remove(this.user.getId());
            matchPool.remove(this.user);
        }
    }

    private void startMatching(){
        System.out.println("start matching");
        //currently just for debugging purpose,
        // would be substituted with micro-service in the future
        matchPool.add(this.user);

        while(matchPool.size()>=2){
            Iterator<User> it = matchPool.iterator();
            User a = it.next(), b = it.next();
            matchPool.remove(a);
            matchPool.remove(b);

            //create map after successful match
            //would need to store game between socket a and socket b in the future
            game = new Game(13,14,20,a.getId(),b.getId());
            game.createMap();
            //it will do run() in game as the second thread
            game.start();

            users.get(a.getId()).game = game;
            users.get(b.getId()).game = game;

            JSONObject respGame = new JSONObject();
            respGame.put("a_id", game.getPlayerA().getId());
            respGame.put("a_sx", game.getPlayerA().getSx());
            respGame.put("a_sy", game.getPlayerA().getSy());

            respGame.put("b_id", game.getPlayerB().getId());
            respGame.put("b_sx", game.getPlayerB().getSx());
            respGame.put("b_sy", game.getPlayerB().getSy());

            respGame.put("map",game.getG());

            JSONObject respA = new JSONObject();
            respA.put("event","match-success");
            respA.put("opponent_username",b.getUsername());
            respA.put("opponent_photo",b.getPhoto());
            respA.put("game",respGame);
            users.get(a.getId()).sendMessage(respA.toJSONString());

            JSONObject respB = new JSONObject();
            respB.put("event","match-success");
            respB.put("opponent_username",a.getUsername());
            respB.put("opponent_photo",a.getPhoto());
            respB.put("game",respGame);
            users.get(b.getId()).sendMessage(respB.toJSONString());
        }
    }

    private void stopMatching(){
        System.out.println("stop matching");
        matchPool.remove(this.user);
    }

    private void move(int direction){
        if(user.getId().equals(game.getPlayerA().getId())){
            game.setNextStepA(direction);
        }else if (user.getId().equals(game.getPlayerB().getId())){
            game.setNextStepB(direction);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // when receiving message from client
        System.out.println("receive message");
        // change string to json object
        JSONObject data = JSONObject.parseObject(message);
        System.out.println(data.toJSONString());
        System.out.println(user.getId());
        String event = data.getString("event");
        if("start-matching".equals(event)){
            startMatching();
        }else if("stop-matching".equals(event)){
            stopMatching();
        }else if("move".equals(event)){
            move(data.getInteger("direction"));
        }

    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void sendMessage(String message){
        //since this is an asychronized process, we lock the session
        synchronized (this.session){
            try {
                //used to send message via session
                this.session.getBasicRemote().sendText(message);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
