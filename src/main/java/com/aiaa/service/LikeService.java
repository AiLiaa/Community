package com.aiaa.service;

import com.aiaa.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;

//    @Autowired
//    private RedisOperations operations;

    //点赞
    public void like(int userId, int entityType, int entityId, int entityUserId) {

        //使用 RedisTemplate 直接调用 opsFor** 来操作 Redis 数据库，
        // 每执行一条命令是要重新拿一个连接，因此很耗资源。
        // 如果让一个连接直接执行多条语句的方法就是使用 SessionCallback，
        // 还可以使用 RedisCallback（它太复杂，不常用）。
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                SetOperations setOperations = operations.opsForSet();
                ValueOperations valueOperations = operations.opsForValue();
                //查询是否存在
                Boolean isMember = setOperations.isMember(entityLikeKey, userId);

                //事务开启(实际上事务中正确的命令得到执行，不正确的命令没有执行，谁出错谁负责。)
                operations.multi();
                if (isMember) {
                    setOperations.remove(entityLikeKey,userId);
                    valueOperations.decrement(userLikeKey);
                } else {
                    setOperations.add(entityLikeKey,userId);
                    valueOperations.increment(userLikeKey);
                }

                return operations.exec();
            }
        });

//        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
//        String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
//
//        boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);
//
//        operations.multi();
//
//        if (isMember) {
//            operations.opsForSet().remove(entityLikeKey, userId);
//            operations.opsForValue().decrement(userLikeKey);
//        } else {
//            operations.opsForSet().add(entityLikeKey, userId);
//            operations.opsForValue().increment(userLikeKey);
//        }
    }

    // 查询某实体点赞的数量
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    // 查询某人对某实体的点赞状态
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }

    // 查询某个用户获得的赞
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count.intValue();
    }

}