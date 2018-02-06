package com.example.nowcoder.async.handler;

import com.example.nowcoder.async.EventHandler;
import com.example.nowcoder.async.EventModel;
import com.example.nowcoder.async.EventType;
import com.example.nowcoder.model.Message;
import com.example.nowcoder.model.User;
import com.example.nowcoder.service.MessageService;
import com.example.nowcoder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class LikeHandler implements EventHandler {
    @Autowired
    UserService userService;

    @Autowired
    MessageService messageService;

    @Override
    public void doHandle(EventModel model) {
        System.out.println("Liked");
        Message message=new Message();
        message.setFromId(12);
        //先给自己发一条看看后台数据显示
        message.setToId(model.getActorId());
        User user=userService.getUser(model.getActorId());
        String conversationId="12"+"_"+"12";
        message.setContent("用户"+user.getName()+"赞了你的咨询，http://127.0.0.1:8080/news/"+model.getEntityId());
        message.setCreatedDate(new Date());
        message.setConversationId(conversationId);
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
