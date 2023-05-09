package com.kob.botrunningsystem.service.impl.utils;

import com.kob.botrunningsystem.utils.BotInterface;
import org.joor.Reflect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.function.Supplier;

//every time some piece of code need to be run
//we use a thread to run it since we can kill a thread
//after some seconds, avoid infinite loop
//since there is autowired, but add component
@Component
public class Consumer extends Thread{

    private static RestTemplate restTemplate;
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){
        Consumer.restTemplate = restTemplate;
    }

    private static String receiveMoveUrl = "http://127.0.0.1:3000/pk/receive/move/";

    private Bot bot;

    public void startTimeout(long timeout, Bot bot){
        this.bot = bot;
        this.start();

        try {
            //either finish executing or after timeout
            //better than sleep because it can finish if the thread finishes early
            this.join(timeout);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.interrupt();
    }

    private String addUid(String code, String uid){
        //add uid at the end of Bot class name
        int k = code.indexOf(" implements java.util.function.Supplier<Integer>");
        return code.substring(0,k) + uid + code.substring(k);
    }

    @Override
    public void run(){
        UUID uuid = UUID.randomUUID();
        //since uuid have '-' inside, cannot appear in the class name
        String uid = uuid.toString().substring(0,8);
        //Reflect can dynamically compile a piece of java code
        //first variable is name, second variable is code content
        //for Reflect if name of an object is same, it will only compile code once
        //so using uuid after bot class name
        System.out.println(addUid(bot.getBotCode(), uid));
        Supplier<Integer> botInterface = Reflect.compile(
                "com.kob.botrunningsystem.utils.Bot" + uid,
                addUid(bot.getBotCode(), uid)
        ).create().get();

//        Later, for different languages, file is usually used to pass input
//        also can use a more general interface Supplier<Integer>
        File file = new File("input.txt");
        try (PrintWriter fout = new PrintWriter(file)) {
            fout.println(bot.getInput());
            //empty the cache; otherwise may not read the correct content
            fout.flush();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Integer direction = botInterface.get();
        System.out.println("move: " + bot.getUserId() + " " + direction);
        MultiValueMap<String,String> res = new LinkedMultiValueMap<>();
        res.add("user_id",bot.getUserId().toString());
        res.add("direction", direction.toString());
        restTemplate.postForObject(receiveMoveUrl,res,String.class);
    };
}
