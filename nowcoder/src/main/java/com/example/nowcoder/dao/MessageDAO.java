package com.example.nowcoder.dao;

import com.example.nowcoder.model.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MessageDAO {
    String TABLE_NAME = "message";
    String INSERT_FIELD = "from_id,to_id,content,created_date,has_read,conversation_id";
    String SELECT_FIELD = "id," + INSERT_FIELD;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELD,
            ") values (#{fromId},#{toId},#{content},#{createdDate},#{hasRead},#{conversationId})"})
    int addMessage(Message message);

    @Select({"select ",SELECT_FIELD," from ",TABLE_NAME," WHERE conversation_id=#{conversationId} order by id desc limit #{offset}, #{limit}"})
    List<Message> getConversationDetail(@Param("conversationId") String conversationId,
                                        @Param("offset") int offset,
                                        @Param("limit") int limit);


    @Select({"select ",INSERT_FIELD," ,count(id) as cnt from (select * from ",TABLE_NAME,
            " where from_id=#{userId} or to_id=#{userId} order by id desc) as tt group by id,conversation_id order by id desc" +
                    " limit #{offset},#{limit}"})
    List<Message> getConversationList(@Param("userId") int userId,
                                      @Param("offset")int offset,@Param("limit")int limit);

    @Select({"select count(id) from ",TABLE_NAME," WHERE has_read=0 and to_id=#{userId} and conversation_id=#{conversationId}"})
    int getConversationUnreadCount(@Param("userId")int userId,@Param("conversationId")String conversationId);

}
