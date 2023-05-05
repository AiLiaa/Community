package com.aiaa.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DiscussPost {

    private int id;
    private int user_id;
    private String title;
    private String content;
    private int type;
    private int status;
    private Date create_time;
    private int comment_Count;
    private double score;

}
