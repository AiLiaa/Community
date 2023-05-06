package com.aiaa.mapper;

import com.aiaa.entity.DiscussPost;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class DiscussPostMapperTest {

    @Autowired
    DiscussPostMapper discussPostMapper;
    @Test
    public void selectDiscussPostsTest(){

        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(0, 0, 10);
        System.out.println(discussPosts.toString());
    }

    @Test
    public void selectDiscussPostRowsTest(){

        int i = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(i);
    }
}
