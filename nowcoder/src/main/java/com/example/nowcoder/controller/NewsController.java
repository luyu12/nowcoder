package com.example.nowcoder.controller;

import com.example.nowcoder.model.*;
import com.example.nowcoder.service.*;
import com.example.nowcoder.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class NewsController {
    //上传一张图片
    private static final Logger logger= LoggerFactory.getLogger(NewsController.class);

    @Autowired
    private NewsService newsService;

    @Autowired
    private QiniuService qiniuService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    LikeService likeService;

    @RequestMapping(path={"/news/{newsId}"},method={RequestMethod.GET})
    public String newsDetail(@PathVariable("newsId") int newsId,Model model){
        News news=newsService.getById(newsId);

        if(news!=null){
            int localUserId=hostHolder.getUser()!=null?hostHolder.getUser().getId():0;
            if(localUserId!=0){
                model.addAttribute("like",likeService.getLikeStatus(localUserId, EntityType.ENTITY_NEWS,news.getId()));
            }else{
                model.addAttribute("like",0);
            }


            //评论
            List<Comment> comments=commentService.getCommentsByEntity(news.getId(), EntityType.ENTITY_NEWS);
            //前端页面除了显示comment数据之外还需要显示人物头像，等其他信息.
            List<ViewObject> commentVOs=new ArrayList<>();
            for(Comment comment:comments){
                ViewObject vo=new ViewObject();
                vo.set("comment",comment);
                vo.set("user",userService.getUser(comment.getUserId()));
                commentVOs.add(vo);
            }
            model.addAttribute("comments",commentVOs);
        }

        model.addAttribute("news",news);
        model.addAttribute("owner",userService.getUser(news.getUserId()));

        return "detail";

    }

    //添加评论功能
    @RequestMapping(path={"/addComment"},method={RequestMethod.POST})
    public String addComment(@RequestParam("newsId")int newsId,
                             @RequestParam("content")String content){
        try{
            //过滤敏感词
            Comment comment=new Comment();
            comment.setUserId(hostHolder.getUser().getId());
            comment.setContent(content);
            comment.setEntityId(newsId);
            comment.setEntityType(EntityType.ENTITY_NEWS);
            comment.setCreatedDate(new Date());
            comment.setStatus(0);

            commentService.addComment(comment);
            //更新news里面的评论数量
            int count=commentService.getCommentCount(comment.getEntityId(),comment.getEntityType());
            newsService.updateCommentCount(comment.getEntityId(),count);
            //如何异步化

        }catch (Exception e){
            logger.error("增加评论失败"+e.getMessage());
        }
        return "redirect:/news/"+String.valueOf(newsId);
    }


    //展示图片:需要把图片返回给前端
    @RequestMapping(path={"/image"},method={RequestMethod.GET})
    @ResponseBody
    public void getImage(@RequestParam("name") String imageName,
            HttpServletResponse response){
        try {
            response.setContentType("image/jpeg");
            StreamUtils.copy(new FileInputStream(new
                    File(ToutiaoUtil.IMAGE_DIR + imageName)), response.getOutputStream());
        }catch (Exception e){
            logger.error("读取图片错误"+e.getMessage());
        }
    }

    @RequestMapping(path={"/user/addNews/"},method={RequestMethod.POST})
    @ResponseBody
    public String addNews(@RequestParam("image")String image,
                          @RequestParam("title")String title,
                          @RequestParam("link")String link){
        try{
            News news=new News();
            //查看用户是否为登录状态，如果该用户登录的话则会被存储在hostHolder中
            if(hostHolder.getUser()!=null){
                news.setUserId(hostHolder.getUser().getId());
            }else{
                //匿名用户id可以在自己定义
                news.setUserId(3);
            }
            news.setImage(image);
            news.setCreatedDate(new Date());
            news.setTitle(title);
            news.setLink(link);
            newsService.addNews(news);
            return ToutiaoUtil.getJSONString(0);

        }catch (Exception e){
            logger.error("添加咨询错误"+e.getMessage());
            return ToutiaoUtil.getJSONString(1,"发布失败");
        }

    }



        @RequestMapping(path={"/uploadImage/"},method={RequestMethod.POST})
    @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile file){

        try{
            //本地上传图片
            //String fileUrl=newsService.saveImage(file);
            //通过七牛的服务上传
            String fileUrl=qiniuService.saveImage(file);
            if(fileUrl==null){
                return ToutiaoUtil.getJSONString(1,"上传图片失败");
            }
            System.out.println("/n"+"2"+fileUrl);
            return ToutiaoUtil.getJSONString(0,fileUrl);

        }catch(Exception e){
            logger.error("上传图片失败"+e.getMessage());
            return ToutiaoUtil.getJSONString(1,"上传失败");
        }

    }
}
