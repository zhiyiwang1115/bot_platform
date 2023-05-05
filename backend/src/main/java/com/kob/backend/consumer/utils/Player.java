package com.kob.backend.consumer.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
