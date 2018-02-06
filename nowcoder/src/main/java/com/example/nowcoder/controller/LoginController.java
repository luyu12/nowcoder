package com.example.nowcoder.controller;

import com.example.nowcoder.async.EventModel;
import com.example.nowcoder.async.EventProducer;
import com.example.nowcoder.async.EventType;
import com.example.nowcoder.service.UserService;
import com.example.nowcoder.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class LoginController {
    private static final Logger logger= LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path={"/reg/"},method={RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String reg(Model model, @RequestParam("username") String username,
                      @RequestParam("password") String password,
                      @RequestParam(value="rember",defaultValue = "0") int remember,
                      HttpServletResponse repsonse){
        try{
            Map<String,Object> map=userService.register(username,password);
            if(map.containsKey("ticket")){
                Cookie cookie=new Cookie("ticket",map.get("ticket").toString());
                repsonse.addCookie(cookie);

                cookie.setPath("/");
                if(remember>0){
                    //remember me有5天的有效时间
                    cookie.setMaxAge(3600*24*5);
                }
                return ToutiaoUtil.getJSONString(0,"注册成功");
            }
            else{
                return ToutiaoUtil.getJSONString(1,map);
            }

   //数据格式json:{"code":0, "msg":"xxxx"}
        }catch(Exception e){
            logger.error("注册异常"+e.getMessage());
            return ToutiaoUtil.getJSONString(1,"注册异常");
        }

    }

    @RequestMapping(path={"/login/"},method={RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String login(Model model, @RequestParam("username") String username,
                      @RequestParam("password") String password,
                      @RequestParam(value="rember",defaultValue = "0") int remember,
                        HttpServletResponse response){
        try{
            Map<String,Object> map=userService.login(username,password);
            if(map.containsKey("ticket")){
                Cookie cookie=new Cookie("ticket",map.get("ticket").toString());
                cookie.setPath("/");

                if(remember>0){
                    //remember me有5天的有效时间
                    cookie.setMaxAge(3600*24*5);

                }
                response.addCookie(cookie);
                eventProducer.fireEvent(new EventModel(EventType.LOGIN).setActorId((int) map.get("userId")).
                        setExt("username",username).setExt("to","781105626@qq.com"));
                return ToutiaoUtil.getJSONString(0,"登录成功");
            }
            else{
                return ToutiaoUtil.getJSONString(1,map);
            }

            //数据格式json:{"code":0, "msg":"xxxx"}
        }catch(Exception e){
            logger.error("登录异常"+e.getMessage());
            return ToutiaoUtil.getJSONString(1,"登录异常");
        }


    }

    @RequestMapping(path={"/logout/"},method={RequestMethod.GET,RequestMethod.POST})
    public String logout(@CookieValue("ticket") String ticket){
        //当logout就把cookie中的ticket读出来执行logout
        userService.logout(ticket);
        return "redirect:/";//登出之后自动跳到首页

    }



}
