package com.example.nowcoder.interceptor;

import com.example.nowcoder.dao.LoginTicketDAO;
import com.example.nowcoder.dao.UserDAO;
import com.example.nowcoder.model.HostHolder;
import com.example.nowcoder.model.LoginTicket;
import com.example.nowcoder.model.User;
import com.example.nowcoder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class PassportInterceptor implements HandlerInterceptor {

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String ticket=null ;
        if(httpServletRequest.getCookies()!=null){
            for(Cookie cookie:httpServletRequest.getCookies()){
                if(cookie.getName().equals("ticket")){
                    ticket=cookie.getValue();
                    break;
                }
            }
        }

        if(ticket!=null){//说明用户登录了，但是！也不一定可能是伪造的ticket所以要先去
            //查一下
            LoginTicket loginTicket=loginTicketDAO.selectByTicket(ticket);
            if(loginTicket==null||loginTicket.getExpired().before(new Date())||loginTicket.getStatus()!=0){
                //用户没登录或者票过期
                return true;
            }

            //这时候已经知道具体哪个用户
            //需要把用户记下来

            User user=userDAO.selectById(loginTicket.getUserId());
            hostHolder.setUser(user);
        }
           return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        //在渲染之前
        if(modelAndView!=null&&hostHolder.getUser()!=null){

            modelAndView.addObject("user",hostHolder.getUser());

        }
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        hostHolder.clear();
    }
}
