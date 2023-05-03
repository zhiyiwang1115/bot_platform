package com.kob.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kob.backend.pojo.Bot;
import org.apache.ibatis.annotations.Mapper;

//pay attention to this mapper annotation, must add!
@Mapper
public interface BotMapper extends BaseMapper<Bot> {
}
