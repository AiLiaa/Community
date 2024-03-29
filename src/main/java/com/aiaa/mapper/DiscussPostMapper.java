package com.aiaa.mapper;

import com.aiaa.entity.DiscussPost;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DiscussPostMapper extends BaseMapper<DiscussPost> {

    @Select("select * from discuss_post where if (#{userId} != 0, status != 2 and user_id = #{userId}, status != 2) " +
            "order by type desc,create_time desc " +
            "limit #{offset},#{limit}")
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    @Select("select * from discuss_post where if (#{userId} != 0, status != 2 and user_id = #{userId}, status != 2) " +
            "order by type desc, score desc, create_time desc " +
            "limit #{offset},#{limit}")
    List<DiscussPost> selectDiscussPostsWithScore(int userId, int offset, int limit);

    @Select("select * from discuss_post where if (#{userId} != 0, status != 2 and user_id = #{userId}, status != 2)")
    List<DiscussPost> selectDiscussPostsALL(int userId);

    @Select("select count(id) from discuss_post where if (#{userId} != 0, status != 2 and user_id = #{userId}, status != 2)")
    int selectDiscussPostRows(int userId);

    @Update("update discuss_post set comment_count = #{commentCount} where id = #{id}")
    int updateCommentCount(int id, int commentCount);

    @Insert("insert into discuss_post (user_id,title,content,create_time) values (#{userId},#{title},#{content},#{createTime})")
    int insertDiscussPost(DiscussPost discussPost);

    @Select("select * from discuss_post where id = #{id}")
    DiscussPost selectDiscussPostById(int id);

    @Update("update discuss_post set type = #{type} where id = #{id}")
    int updateType(int id, int type);

    @Update("update discuss_post set status = #{status} where id = #{id}")
    int updateStatus(int id, int status);

    @Update("update discuss_post set score = #{score} where id = #{id}")
    int updateScore(int id, double score);

    @Select("select * from discuss_post where id in " +
            "(select entity_id from comment where user_id = #{userId}) " +
            "order by type desc,create_time desc " +
            "limit #{offset},#{limit}")
    List<DiscussPost> selectDiscussPostListByCommentEntityId(int userId, int offset, int limit);

    @Select("select * from discuss_post where id = #{entityId}")
    DiscussPost selectDiscussPostByCommentEntityId(int entityId);
}
