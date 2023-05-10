package com.kob.backend.service.record.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.Record;
import com.kob.backend.pojo.User;
import com.kob.backend.service.record.GetRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GetRecordServiceImpl implements GetRecordService {
    @Autowired
    RecordMapper recordMapper;
    @Autowired
    UserMapper userMapper;

    @Override
    public JSONObject getList(Integer page) {
        //ipage is the api in mybatis for pagination purpose
        //10 means 10 records per page
        IPage<Record> recordIPage = new Page<>(page, 10);
        //order records by create time (id)
        QueryWrapper<Record> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        List<Record> records = recordMapper.selectPage(recordIPage,queryWrapper).getRecords();
        JSONObject resp = new JSONObject();
        List<JSONObject> items = new ArrayList<>();
        for(Record record: records){
            User userA = userMapper.selectById(record.getAId());
            User userB = userMapper.selectById(record.getBId());
            JSONObject item = new JSONObject();
            item.put("a_username", userA.getUsername());
            item.put("a_photo",userA.getPhoto());
            item.put("b_username", userB.getUsername());
            item.put("b_photo",userB.getPhoto());
            item.put("record", record);
            items.add(item);
        }
        resp.put("records",items);
        //pass how many records in total for frontend
        resp.put("records_count",records.size());
        return resp;
    }
}
