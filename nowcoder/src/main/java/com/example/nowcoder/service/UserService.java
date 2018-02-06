package com.example.nowcoder.service;


import com.example.nowcoder.dao.LoginTicketDAO;
import com.example.nowcoder.dao.UserDAO;
import com.example.nowcoder.model.LoginTicket;
import com.example.nowcoder.model.User;

import com.example.nowcoder.util.ToutiaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;


@Service
public class UserService {
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    public User getUser(int id){
        return userDAO.selectById(id);
    }
    public Map<String,Object> register(String username,String password) {
        Map<String,Object> map=new HashMap<String,Object>();
        //后端验证要需要完成，防止代码攻击
        if(StringUtils.isEmpty(username)){
            map.put("msgname","用户名不能为空");
            return map;
        }

        if(StringUtils.isEmpty(username)){
            map.put("msgpwd","密码不能为空");
            return map;
        }

        User user=userDAO.selectByName(username);
        if(user!=null){//返回user不为空的话，代表user已经被注册过了
            map.put("msgname","用户名已经被注册");
            return map;
        }

        //密码强度：


        user=new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        //不能直接把明文密码存到数据库里面（直接加md5也是不行的），需要先加salt再存储到数据库
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setPassword(ToutiaoUtil.MD5(password+user.getSalt()));
        userDAO.addUser(user);

        //login
        String ticket=addLoginTicket(user.getId());
        map.put("ticket",ticket);

        return map;
    }

    public Map<String,Object> login(String username,String password) {
        Map<String,Object> map=new HashMap<String,Object>();
        //后端验证要需要完成，防止代码攻击
        if(StringUtils.isEmpty(username)){
            map.put("msgname","用户名不能为空");
            return map;
        }

        if(StringUtils.isEmpty(username)){
            map.put("msgpwd","密码不能为空");
            return map;
        }

        User user=userDAO.selectByName(username);
        if(user==null){//返回user不为空的话，代表user已经被注册过了
            map.put("msgname","用户名不存在");
            return map;
        }

        if(!ToutiaoUtil.MD5(password+user.getSalt()).equals(user.getPassword())){
            map.put("msgpwd","密码不正确");
            return map;
        }

        map.put("userId", user.getId());

        //ticket
        String ticket=addLoginTicket(user.getId());
        map.put("ticket",ticket);

        return map;
    }

    private String addLoginTicket(int userId){
        LoginTicket ticket=new LoginTicket();
        ticket.setUserId(userId);
        Date date=new Date();
        //有效期为24小时长的expired time
        date.setTime(date.getTime()+1000*3600*24);
        ticket.setExpired(date);
        ticket.setStatus(0);
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));
        loginTicketDAO.addTicket(ticket);
        return ticket.getTicket();
    }

    public void logout(String ticket){
        loginTicketDAO.updateStatus(ticket,1);
    }

}
