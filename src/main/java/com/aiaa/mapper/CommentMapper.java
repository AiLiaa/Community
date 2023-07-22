package com.aiaa.mapper;

import com.aiaa.entity.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    @Select("select * from comment where status = 0 and entity_type = #{entityType} and " +
            "entity_id = #{entityId} " +
            "order by create_time asc " +
            "limit #{offset}, #{limit}")
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    @Select("select count(id) from comment where status = 0 and entity_type = #{entityType} and entity_id = #{entityId}")
    int selectCountByEntity(int entityType, int entityId);

    @Insert("insert into comment (user_id,entity_type,entity_id,target_id,content,status,create_time) " +
            "values(#{userId},#{entityType},#{entityId},#{targetId},#{content},#{status},#{createTime})")
    int insertComment(Comment comment);

    @Select("select * from comment where id = #{id}")
    Comment selectCommentById(int id);

    @Select("select count(entity_id) from comment where user_id = #{userId} and entity_type = 1")
    int selectCountByUserId(int userId);

    @Select("select * from comment where status = 0 and entity_type = #{entityType} and " +
            "user_id = #{userId} " +
            "order by create_time asc " +
            "limit #{offset}, #{limit}")
    List<Comment> selectCommentsByUserId(int entityType,int userId, int offset, int limit);

}
