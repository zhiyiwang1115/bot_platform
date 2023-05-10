package com.kob.backend.service.ranklist;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface GetRanklistService {
    JSONObject getList(Integer page);
}
