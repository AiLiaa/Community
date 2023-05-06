package com.aiaa.mapper;

import com.aiaa.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    @Select("select * from discuss_post where if (#{userId} != 0, status != 2 and user_id = #{userId}, status != 2) " +
            "order by type desc,create_time desc " +
            "limit #{offset},#{limit}")
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);


    @Select("select count(id) from discuss_post where if (#{userId} != 0, status != 2 and user_id = #{userId}, status != 2)")
    int selectDiscussPostRows(int userId);
}
