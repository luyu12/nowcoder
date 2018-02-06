package com.example.nowcoder.model;

import org.springframework.stereotype.Component;

@Component
public class HostHolder {
    //该类代表当前用户是谁，每一个用户对应一个线程
    private static ThreadLocal<User> users=new ThreadLocal<User>();

    public User getUser(){
        return users.get();
    }

    public void setUser(User user){
        users.set(user);
    }

    public void clear(){
        users.remove();
    }

}
