package com.aiaa.mapper;

import com.aiaa.entity.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface MessageMapper {

    // 查询当前用户的会话列表,针对每个会话只返回一条最新的私信.
    @Select("select * from message where id in " +
            "(select max(id) from message where status != 2 and from_id != 1 " +
            "and (from_id = #{userId} or to_id = #{userId}) " +
            "group by conversation_id) " +
            "order by id desc " +
            "limit #{offset}, #{limit}")
    List<Message> selectConversations(int userId, int offset, int limit);

    // 查询当前用户的会话数量.
    @Select("select count(m.maxid) from " +
            "(select max(id) as maxid from message " +
            "where status != 2 and from_id != 1 " +
            "and (from_id = #{userId} or to_id = #{userId}) " +
            "group by conversation_id) as m")
    int selectConversationCount(int userId);

    // 查询某个会话所包含的私信列表.
    @Select("select * from message " +
            "where status != 2 and from_id != 1 and conversation_id = #{conversationId} " +
            "order by id desc " +
            "limit #{offset}, #{limit}")
    List<Message> selectLetters(String conversationId, int offset, int limit);

    // 查询某个会话所包含的私信数量.
    @Select("select count(id) from message " +
            "where status != 2 and from_id != 1 and conversation_id = #{conversationId}")
    int selectLetterCount(String conversationId);

    // 查询全部未读私信的数量
    @Select("select count(id) from message " +
            "where status = 0 and from_id != 1 and to_id = #{userId}")
    int selectAllLetterUnreadCount(int userId);


    // 查询一个人发送的未读私信的数量
    @Select("select count(id) from message " +
            "where status = 0 and from_id != 1 and to_id = #{userId} and conversation_id = #{conversationId}")
    int selectLetterUnreadCount(int userId, String conversationId);

    // 新增消息
    @Insert("insert into message (from_id,to_id,conversation_id,content,status,create_time) " +
            "values (#{fromId},#{toId},#{conversationId},#{content},#{status},#{createTime})")
    int insertMessage(Message message);

    // 修改消息的状态
//    @Update("update message set status = #{status} " +
//            "where id in (#{ids})")
    @Update({
            "<script>",
            "update message set status = #{status} ",
            "WHERE id in ",
            "<foreach collection=\"ids\" item=\"ids\" open=\"(\" separator=\",\" close=\")\"> ",
            "#{ids} ",
            "</foreach> ",
            "</script>"
    })
    int updateStatus(List<Integer> ids, int status);


    // 查询某个主题下最新的通知
    @Select("select * from message where id in " +
            "(select max(id) from message " +
            "where status != 2 " +
            "and from_id = 1 " +
            "and to_id = #{userId} " +
            "and conversation_id = #{topic})")
    Message selectLatestNotice(int userId, String topic);

    // 查询某个主题所包含的通知数量
    @Select("select count(id) from message " +
            "where status != 2 " +
            "and from_id = 1 " +
            "and to_id = #{userId} " +
            "and conversation_id = #{topic}")
    int selectNoticeCount(int userId, String topic);

    // 查询某种topic未读的通知的数量
    @Select(
            "select count(id) from message "+
            "where status = 0 "+
            "and from_id = 1 "+
            "and to_id = #{userId} "+
            "and conversation_id = #{topic}")
    int selectNoticeUnreadCount(int userId, String topic);

    // 查询所有未读的通知的数量
    @Select(
            "select count(id) from message "+
                    "where status = 0 "+
                    "and from_id = 1 "+
                    "and to_id = #{userId} ")
    int selectAllNoticeUnreadCount(int userId);

    // 查询某个主题所包含的通知列表
    @Select("select * from message " +
            "where status != 2 " +
            "and from_id = 1 " +
            "and to_id = #{userId} " +
            "and conversation_id = #{topic} " +
            "order by create_time desc " +
            "limit #{offset}, #{limit}")
    List<Message> selectNotices(int userId, String topic, int offset, int limit);


}