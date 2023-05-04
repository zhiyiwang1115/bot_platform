package com.kob.backend.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bot {
    @TableId(type = IdType.AUTO)
    private Integer id;
    //user_id in database maps to userId in mybatis-plus
    //same things applies to create date and modify date
    private Integer userId;
    private String title;
    private String description;
    private String content;
    //define the format of date at Australian time
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Australia/Sydney")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Australia/Sydney")
    private Date modifyTime;
}
