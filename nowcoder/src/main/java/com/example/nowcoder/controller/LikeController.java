package com.example.nowcoder.controller;

import com.example.nowcoder.async.EventModel;
import com.example.nowcoder.async.EventProducer;
import com.example.nowcoder.async.EventType;
import com.example.nowcoder.model.EntityType;
import com.example.nowcoder.model.HostHolder;
import com.example.nowcoder.model.News;
import com.example.nowcoder.service.LikeService;
import com.example.nowcoder.service.NewsService;
import com.example.nowcoder.service.UserService;
import com.example.nowcoder.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@Controller
public class LikeController {
    private static final Logger logger= LoggerFactory.getLogger(LikeController.class);

    @Autowired
    HostHolder hostHolder;

    @Autowired
    LikeService likeService;

    @Autowired
    NewsService newsService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path={"/like"},method={RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String like(Model model, @RequestParam("newsId") int newsId) {
            int userId=hostHolder.getUser().getId();
            long likeCount=likeService.like(userId, EntityType.ENTITY_NEWS,newsId);
            News news=newsService.getById(newsId);
            newsService.updateLikeCount(newsId,(int)likeCount);
            eventProducer.fireEvent(new EventModel(EventType.LIKE).setActorId(hostHolder.getUser().getId()).
                    setEntityId(newsId).setEntityType(EntityType.ENTITY_NEWS).setEntityOwnerId(news.getUserId()));

            return ToutiaoUtil.getJSONString(0,String.valueOf(likeCount));
        }

    @RequestMapping(path={"/dislike"},method={RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String dislike(Model model, @RequestParam("newsId") int newsId) {
        int userId=hostHolder.getUser().getId();
        long disLikeCount=likeService.disLike(userId, EntityType.ENTITY_NEWS,newsId);
        newsService.updateLikeCount(newsId,(int)disLikeCount);
        return ToutiaoUtil.getJSONString(0,String.valueOf(disLikeCount));
    }
}
