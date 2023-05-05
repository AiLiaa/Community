package com.aiaa.service;

import com.aiaa.entity.User;
import com.aiaa.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User findUserById(int userId) {
        return userMapper.selectById(userId);
    }
}
