package com.aiaa.service;

import com.aiaa.entity.DiscussPost;
import com.aiaa.mapper.DiscussPostMapper;
import com.aiaa.util.SensitiveFilter;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DiscussPostService {

    private static final Logger logger = LoggerFactory.getLogger(DiscussPostService.class);

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Value("${caffeine.posts.max-size}")
    private int maxSize;

    @Value("${caffeine.posts.expire-seconds}")
    private int expireSeconds;

    // Caffeine核心接口: Cache, LoadingCache, AsyncLoadingCache

    // 帖子列表缓存
    private LoadingCache<String, List<DiscussPost>> postListCache;

    // 帖子总数缓存
    private LoadingCache<Integer, Integer> postRowsCache;

    @PostConstruct
    public void init() {
        // 初始化帖子列表缓存
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Nullable
                    @Override
                    public List<DiscussPost> load(@NonNull String key) throws Exception {
                        if (key == null || key.length() == 0) {
                            throw new IllegalArgumentException("参数错误!");
                        }

                        String[] params = key.split(":");
                        if (params == null || params.length != 2) {
                            throw new IllegalArgumentException("参数错误!");
                        }

                        int offset = Integer.valueOf(params[0]);
                        int limit = Integer.valueOf(params[1]);

                        // 二级缓存: Redis -> mysql

                        logger.info("load post list from DB.");
                        return discussPostMapper.selectDiscussPosts(0, offset, limit);
                    }
                });
        // 初始化帖子总数缓存
        postRowsCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Nullable
                    @Override
                    public Integer load(@NonNull Integer key) throws Exception {
                        logger.info("load post rows from DB.");
                        return discussPostMapper.selectDiscussPostRows(key);
                    }
                });
    }


    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit){
        if (userId == 0) {
            logger.info("load post list from caffeine.");
            return postListCache.get(offset + ":" + limit);
        }
        logger.info("load post list from DB.");

        // 以下不再执行，如果caffeine缓存中不存在该数据，init函数中返回discussPostMapper.selectDiscussPosts(userId,offset,limit)并且存入缓存
        return discussPostMapper.selectDiscussPosts(userId,offset,limit);
    }

    public List<DiscussPost> findDiscussPostsWithScore(int userId, int offset, int limit){
        if (userId == 0) {
            logger.info("load post list from caffeine.");
            return postListCache.get(offset + ":" + limit);
        }
        logger.info("load post list from DB.");

        return discussPostMapper.selectDiscussPostsWithScore(userId,offset,limit);
    }

    public int findDiscussPostRows(int userId){
        if (userId == 0) {
            logger.info("load post rows from caffeine.");
            return postRowsCache.get(userId);
        }

        logger.debug("load post rows from DB.");
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    public int addDiscussPost(DiscussPost discussPost) {
        if (discussPost == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }

        // 转义HTML标记
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));
        // 过滤敏感词
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));

        return discussPostMapper.insertDiscussPost(discussPost);
    }

    public DiscussPost findDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    public int updateCommentCount(int id, int commentCount) {
        return discussPostMapper.updateCommentCount(id, commentCount);
    }

    public int updateType(int id, int type) {
        return discussPostMapper.updateType(id, type);
    }

    public int updateStatus(int id, int status) {
        return discussPostMapper.updateStatus(id, status);
    }

    public int updateScore(int id, double score) {
        return discussPostMapper.updateScore(id, score);
    }

    public List<DiscussPost> selectDiscussPostByCommentEntityId(int userId, int offset, int limit){
        return discussPostMapper.selectDiscussPostListByCommentEntityId(userId,offset,limit);
    }

    public DiscussPost findDiscussPostByCommentEntityId(int entityId){
        return discussPostMapper.selectDiscussPostByCommentEntityId(entityId);
    }
}
