package com.aiaa.util;

public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";

    // 某个实体的赞
    // like:entity:entityType:entityId -> set(userId)
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    // 某个用户的赞
    // like:user:userId -> int
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    // 某个用户关注的实体
    // followee:userId:entityType -> zset(entityId,now)
    // userId -> 某个关注别人的关注者id
    // entityType -> 关注的类型，可以是帖子可以是人等待
    // value是zset(entityId,now) 是一个有序集合以时间为score，以实体id为值
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    // 某个实体拥有的粉丝
    // follower:entityType:entityId -> zset(userId,now)
    // entityType -> 实体类型可以是用户可以是帖子等等
    // entityId -> 这个实体的id
    // 有了这两个参数可以唯一的表示某个实体，比如说如果这个实体是人那么entityType就是人entityId就是userId如果这个实体是帖子那么entityType就是帖子entityId就是帖子id
    // value是zset(userId,now)是一个有序集合，集合中值是userId表示是哪个用户关注了这个实体
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityId + SPLIT + entityId;
    }

}
