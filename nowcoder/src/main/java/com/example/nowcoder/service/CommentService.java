package com.example.nowcoder.service;

import com.example.nowcoder.dao.CommentDAO;
import com.example.nowcoder.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentDAO commentDAO;

    public List<Comment> getCommentsByEntity(int entityId,int entityType){
        return commentDAO.selectByEntity(entityId,entityType);
    }

    public int addComment(Comment comment){
        return commentDAO.addComment(comment);
    }

    public int getCommentCount(int entityId, int entityType){
        return commentDAO.getCommentCount(entityId,entityType);
    }

    //service层必须要用delete而在底层需要使用update
    public void deleteComment(int entityId, int entityType){
        commentDAO.updateStatus(entityId,entityType,1);
    }
}
