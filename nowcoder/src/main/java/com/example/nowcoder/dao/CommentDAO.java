package com.example.nowcoder.dao;

import com.example.nowcoder.model.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

//CommentDAO是用来读取数据的
@Mapper
public interface CommentDAO {
    String TABLE_NAME="comment";
    String INSERT_FIELD="user_id,content,created_date,entity_id,entity_type,status";
    String SELECT_FIELD="id,"+INSERT_FIELD;

    @Insert({"insert into ",TABLE_NAME, "(",INSERT_FIELD,") VALUES " +
            "(#{userId},#{content},#{createdDate},#{entityId},#{entityType},#{status})"})
    int addComment(Comment comment);//返回int类型，如果成功返回一个值，否则为0

    //功能：删除该条评论，但是实质上是不会删除所以是update
    @Update({"update ", TABLE_NAME," SET status=#{status} where entity_id=#{entityId} and entity_type=#{entityType}"})
    void updateStatus(@Param("entityId") int entityId,@Param("entityType") int entityType,@Param("status") int status);

    @Select({"select ",SELECT_FIELD," FROM ",TABLE_NAME," where entity_id=#{entityId} and entity_type=#{entityType} order by id desc"})
    List<Comment> selectByEntity(@Param("entityId") int entityId,
                                 @Param("entityType") int entityType);

    @Select({"select count(id) from ",TABLE_NAME," where entity_id=#{entityId} and entity_type=#{entityType}"})
    int getCommentCount(@Param("entityId")int entityId,@Param("entityType")int entityType);

}
