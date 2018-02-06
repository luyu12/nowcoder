package com.example.nowcoder.controller;

import com.example.nowcoder.aspect.LogAspect;
import com.example.nowcoder.model.EntityType;
import com.example.nowcoder.model.HostHolder;
import com.example.nowcoder.model.News;
import com.example.nowcoder.model.ViewObject;
import com.example.nowcoder.service.LikeService;
import com.example.nowcoder.service.NewsService;
import com.example.nowcoder.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    private static final Logger logger= LoggerFactory.getLogger(HomeController.class);

    @Autowired
    NewsService newsService;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    LikeService likeService;

    private  List<ViewObject> getNews(int userId, int offset,int limit){
        List<News> newsList=newsService.getLatestNews(userId,offset,limit);
        List<ViewObject> vos=new ArrayList<>();
        for(News news:newsList)
        {
            //vo里面可以放跟一条新闻相关的任何对象信息
           // System.out.println("newsId:"+news.getId());
            int localUserId=hostHolder.getUser()!=null?hostHolder.getUser().getId():0;
            ViewObject vo=new ViewObject();
            vo.set("news",news);
            vo.set("user",userService.getUser(news.getUserId()));
            //vos.add(vo);

            //当前登录用户不为空
            if(localUserId!=0){
                vo.set("like",likeService.getLikeStatus(localUserId, EntityType.ENTITY_NEWS,news.getId()));
            }else{
                vo.set("like",0);
            }
            vos.add(vo);
        }
        return vos;
    }

    @RequestMapping(path={"/","/index"},method={RequestMethod.GET,RequestMethod.POST})
        public String index(Model model){
        model.addAttribute("vos",getNews(0,0,10));
        return "home";
    }

    @RequestMapping(path={"/user/{userId}"},method={RequestMethod.GET,RequestMethod.POST})
    public String userIndex(Model model, @PathVariable ("userId") int userId,
                            @RequestParam(value="pop",defaultValue ="0")int pop){
        model.addAttribute("vos",getNews(userId,0,10));
        model.addAttribute("pop",pop);
        return "home";
    }
}
