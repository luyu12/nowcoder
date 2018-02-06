package com.example.nowcoder.controller;

import com.example.nowcoder.dao.MessageDAO;
import com.example.nowcoder.model.HostHolder;
import com.example.nowcoder.model.Message;
import com.example.nowcoder.model.User;
import com.example.nowcoder.model.ViewObject;
import com.example.nowcoder.service.MessageService;
import com.example.nowcoder.service.UserService;
import com.example.nowcoder.util.ToutiaoUtil;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {
    private static final Logger logger= LoggerFactory.getLogger(LoginController.class);

    @Autowired
    MessageDAO messageDAO;

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @RequestMapping(path={"/msg/addMessage"},method={RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("fromId")int fromId,
                             @RequestParam("toId")int toId,
                             @RequestParam("content")String content){
        try{
            Message msg=new Message();
            msg.setContent(content);
            msg.setFromId(fromId);
            msg.setToId(toId);
            msg.setCreatedDate(new Date());
            msg.setConversationId(fromId<toId?String.format("%d_%d",fromId,toId):String.format("%d_%d",toId,fromId));
            messageService.addMessage(msg);
            return ToutiaoUtil.getJSONString(msg.getId());
        }catch (Exception e){
            logger.error("增加失败"+e.getMessage());
            return ToutiaoUtil.getJSONString(1,"插入评论失败");
        }
    }

    @RequestMapping(path={"/msg/detail"},method={RequestMethod.GET})
    public String conversationDetail(Model model, @Param("conversationId")String conversationId){
        try {
            List<Message> conversationList = messageService.getConversationDetail(conversationId, 0,10);
            //需要把评论的人头像显示出来，所以需要新建一个viewobject
            List<ViewObject> messages=new ArrayList<>();
            for(Message msg:conversationList){
                ViewObject vo=new ViewObject();
                vo.set("message",msg);
                User user=userService.getUser(msg.getFromId());
                if(user==null){
                    continue;
                }
                vo.set("headUrl",user.getHeadUrl());
                vo.set("userId",user.getId());
                messages.add(vo);
            }
            model.addAttribute("messages",messages);

        }catch(Exception e){
            logger.error("获取详情消息失败"+e.getMessage());
        }
        return "letterDetail";
    }

    @RequestMapping(path={"/msg/list"},method={RequestMethod.GET})
    public String conversationList(Model model) {
        try{

            int localUserId=hostHolder.getUser().getId();
            //System.out.println("active user id: "+localUserId);
            List<ViewObject> conversations=new ArrayList<>();
            List<Message> conversationList=messageService.getConversationList(localUserId,0,10);
            for(Message msg:conversationList){
                ViewObject vo=new ViewObject();
                vo.set("conversation",msg);
                //需要判断当前登录用户是消息发送者还是接受收者
                int targetId= msg.getFromId()==localUserId? msg.getToId():msg.getFromId();
                //注意：这里的user不是当前登录用户，登录用户为localUser
                User user=userService.getUser(targetId);
                //System.out.println("user id:"+user.getId());
                vo.set("user",user);
                vo.set("unread",messageService.getConversationUnreadCount(localUserId,msg.getConversationId()));
                conversations.add(vo);
            }
            model.addAttribute("conversations",conversations);

        }catch (Exception e){
            logger.error("获取站内信列表失败"+e.getMessage());
        }
        return "letter";
    }


}
