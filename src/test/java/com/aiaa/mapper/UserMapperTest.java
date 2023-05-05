package com.aiaa.mapper;

import com.aiaa.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class UserMapperTest {

    @Autowired
    UserMapper userMapper;

    @Test
    public void selectByIdTest(){
        User user = userMapper.selectById(166);
        System.out.println(user);
    }

    @Test
    public void selectByUsernameTest(){
        User user = userMapper.selectByName("SYSTEM");
        System.out.println(user);
    }

    @Test
    public void selectByEmailTest(){
        User user = userMapper.selectByEmail("nowcoder1@sina.com");
        System.out.println(user);
    }

    @Test
    public void insertUserTest(){
        User user = new User();

        user.setUsername("aiaa");
//        user.setPassword("123456");
//        user.setSalt("abc");
//        user.setEmail("test@qq.com");
//        user.setHeaderUrl("http://www.nowcoder.com/101.png");
//        user.setCreateTime(new Date());

        userMapper.insertUser(user);
    }

    @Test
    public void updateStatusTest(){
        userMapper.updateStatus(166,1);
    }

    @Test
    public void updateHeaderUrlTest(){
        userMapper.updateHeader(166,"https://aiaa.github.io");
    }

    @Test
    public void updatePasswordTest(){
        userMapper.updatePassword(166,"123456");
    }

}
