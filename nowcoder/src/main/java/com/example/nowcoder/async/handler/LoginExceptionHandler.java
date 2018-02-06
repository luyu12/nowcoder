package com.example.nowcoder.async.handler;

import com.example.nowcoder.async.EventHandler;
import com.example.nowcoder.async.EventModel;
import com.example.nowcoder.async.EventType;
import com.example.nowcoder.model.Message;
import com.example.nowcoder.service.MessageService;
import com.example.nowcoder.util.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class LoginExceptionHandler implements EventHandler{
    @Autowired
    MessageService messageService;

    @Autowired
    MailSender mailSender;

    @Override
    public void doHandle(EventModel model) {
        //判断是否有异常登录

        Message message=new Message();
        message.setToId(model.getActorId());
        message.setContent("你上次的登录id异常");
        message.setFromId(3);
        message.setCreatedDate(new Date());
        message.setConversationId("3"+"_"+model.getActorId());
        //System.out.println(message.getConversationId());
        messageService.addMessage(message);

        Map<String,Object> map=new HashMap<String,Object>();
        map.put("username",model.getExt("username"));
       //System.out.println("to address:"+model.getExt("email"));
        mailSender.sendWithHTMLTemplate(model.getExt("to"),
                "登录异常","mails/welcome.html",map);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN);
    }
}
