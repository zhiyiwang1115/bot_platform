package com.kob.backend.consumer;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.consumer.utils.JwtAuthentication;
import com.kob.backend.mapper.BotMapper;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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
    public static UserMapper userMapper;
    @Autowired
    public void setUserMapper(UserMapper userMapper){
        WebSocketServer.userMapper = userMapper;
    }

    private static BotMapper botMapper;
    @Autowired
    public void setBotMapper(BotMapper botMapper){
        WebSocketServer.botMapper = botMapper;
    }
    //not sure if it can be stored in player.java
    public static RecordMapper recordMapper;
    @Autowired
    public void setRecordMapperMapper(RecordMapper recordMapper){
        WebSocketServer.recordMapper = recordMapper;
    }

    public static RestTemplate restTemplate;
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {WebSocketServer.restTemplate = restTemplate;}

//    session is used for backend to send message to frontend
    private Session session = null;
    private User user;

    public Game game = null;

    //need to store and maintain all connections using static variable
    //need to be thread safe as many threads would visit it at the same time
    public static ConcurrentHashMap<Integer,WebSocketServer> users = new ConcurrentHashMap<>();

    private Integer botId;

    private static String addPlayerUrl = "http://127.0.0.1:3001/player/add/";
    private static String removePlayerUrl = "http://127.0.0.1:3001/player/remove/";

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
            stopMatching();
        }
    }

    public static void StartGame(Integer aId, Integer bId){
        User a = userMapper.selectById(aId);
        Integer aBotId = users.get(aId).botId;
        User b = userMapper.selectById(bId);
        Integer bBotId = users.get(bId).botId;

        Bot botA = botMapper.selectById(aBotId);
        Bot botB = botMapper.selectById(bBotId);

        //create map after successful match
        //would need to store game between socket a and socket b in the future
        Game game = new Game(
                13,
                14,
                20,
                a.getId(),
                botA,
                b.getId(),
                botB);
        game.createMap();
        //it will do run() in game as the second thread
        game.start();

        //when a client close the browser after matching
        //then users.get(a.getId()) can be null, so exception
        //therefore, have to first verify if it is null
        //same applies to tje others as well
        if(users.get(a.getId())!=null)
            users.get(a.getId()).game = game;
        if(users.get(b.getId())!=null)
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

        if(users.get(a.getId())!=null)
            users.get(a.getId()).sendMessage(respA.toJSONString());

        JSONObject respB = new JSONObject();
        respB.put("event","match-success");
        respB.put("opponent_username",a.getUsername());
        respB.put("opponent_photo",a.getPhoto());
        respB.put("game",respGame);
        if(users.get(b.getId())!=null)
            users.get(b.getId()).sendMessage(respB.toJSONString());
    }

    private void startMatching(){
        System.out.println("start matching");
        MultiValueMap<String,String> res = new LinkedMultiValueMap<>();
        res.add("user_id", user.getId().toString());
        res.add("rating",user.getRating().toString());
        //send as object
        restTemplate.postForObject(addPlayerUrl, res, String.class);
    }

    private void stopMatching(){
        System.out.println("stop matching");
        MultiValueMap<String,String> res = new LinkedMultiValueMap<>();
        res.add("user_id", user.getId().toString());
        restTemplate.postForObject(removePlayerUrl, res, String.class);
    }

    private void move(int direction){
        if(user.getId().equals(game.getPlayerA().getId())){
            //if player is using bot, then ignore operations by player
            if(game.getPlayerA().getBotId().equals(-1))
                game.setNextStepA(direction);
        }else if (user.getId().equals(game.getPlayerB().getId())){
            if(game.getPlayerB().getBotId().equals(-1))
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
            botId = data.getInteger("bot_id");
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
