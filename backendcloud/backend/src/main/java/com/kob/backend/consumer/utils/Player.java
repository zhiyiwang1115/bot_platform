package com.kob.backend.consumer.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

//class to maintain player information at backend (location)

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    private Integer id;
    //start point
    private Integer sx;
    private Integer sy;
    private List<Integer> steps;

    private boolean check_tail_increasing(int steps){
        if(steps<=10)return true;
        if(steps%3==1)return true;
        return false;
    }

    public List<Cell> getCells(){
        List<Cell> res = new LinkedList<>();
        int[] dx = {-1,0,1,0}, dy = {0,1,0,-1};
        int x = sx, y = sy;
        res.add(new Cell(x,y));
        for(int i = 0;i<steps.size();i++){
            int d = steps.get(i);
            x += dx[d];
            y += dy[d];
            res.add(0,new Cell(x,y));
            if(!check_tail_increasing(i)){
                res.remove(res.size()-1);
            }
        }
        return res;
    }

    public String getStepsString(){
        StringBuilder res = new StringBuilder();
        for(int d: steps){
            res.append(d);
        }
        return res.toString();
    }
}
