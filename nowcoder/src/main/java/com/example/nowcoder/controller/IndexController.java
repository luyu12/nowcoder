package com.example.nowcoder.controller;

import com.example.nowcoder.aspect.LogAspect;
import com.example.nowcoder.model.User;
import com.example.nowcoder.service.ToutiaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class IndexController {
    private static final Logger logger= LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private ToutiaoService heService;


//    @RequestMapping(path={"/","/index"},method = {RequestMethod.GET,RequestMethod.POST})
//    @ResponseBody
//    public String index(HttpSession session) {
//        //heService.say();
//        logger.info("Visit Index");
//
//        return "hello nowcoder"+session.getAttribute("msg")+"<br> say:"+heService.say();
//
//    }

    @RequestMapping(path={"/profile/{groupId}/{userId}"})
    @ResponseBody
    public String profile(@PathVariable("groupId") String groupId,
                          @PathVariable("userId") int userId,
                          @RequestParam(value="type",defaultValue ="1" ) int type,
                          @RequestParam(value="key", defaultValue = "nowcoder") String key

    ){
        return String.format("{%s},{%d},{%d},{%s}",groupId,userId,type,key);
    }

    @RequestMapping(value={"/testFtl"})
    public String news(ModelMap map)
    {

        List<String> colors= Arrays.asList(new String[]{"red","blue","green"});
        map.put("colors",colors);
        List<String> keys=new ArrayList<String>();
        for(int i=0;i<4;i++)
        {
            keys.add(String.valueOf(i*i));
        }
        map.put("keys",keys);
        map.put("user",new User("luyu"));

        return "/testFtl";
    }

    @RequestMapping("/request")
    @ResponseBody
    public String request(HttpServletRequest request,
                          HttpServletResponse response,
                          HttpSession session){
        StringBuilder sb=new StringBuilder();
        Enumeration<String> headerNames=request.getHeaderNames();
        while(headerNames.hasMoreElements()){
            String name=headerNames.nextElement();
            sb.append(name+":"+request.getHeader(name)+"<br>");
        }

        for(Cookie cookie:request.getCookies())
        {
            sb.append("cookie:");
            sb.append(cookie.getName());
            sb.append(":");
            sb.append(cookie.getValue());
            sb.append("<br>");//<br>换行
        }

        sb.append("getMethod:"+request.getMethod()+"<br>");//get or post
        sb.append("getPathInfo:"+request.getPathInfo()+"<br>");
        sb.append("getQeueryString:"+request.getQueryString()+"<br>");
        sb.append("getRequestURI:"+request.getRequestURI()+"<br>");

        return sb.toString();
    }

    @RequestMapping(value={"/response"})
    @ResponseBody
    public String response(@CookieValue(value="nowcoderid",defaultValue="a") String nowcoderId,
                           @RequestParam(value="key",defaultValue = "key") String key,
                           @RequestParam(value="value",defaultValue = "value")String value,
                           HttpServletResponse response){
        response.addCookie(new Cookie(key,value));
        response.addHeader(key,value);
        return "NowColderIf From Cookie:"+nowcoderId;
    }


    //重定向
    @RequestMapping("/redirect/{code}")
    public String redirect(@PathVariable("code")int code,HttpSession session) {
        /*
        RedirectView red = new RedirectView("/", true);
        if (code == 301){
            red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
    }
        return red;*/
        session.setAttribute("msg","jump from redirect");
        return "redirect:/";
    }

    @RequestMapping("/admin")
    @ResponseBody
    public String admin(@RequestParam(value="key",required=false) String key){
        if("admin".equals(key)){
            return "hello admin";
        }
        throw new IllegalArgumentException("key 错误");
    }

    @ExceptionHandler
    @ResponseBody
    public String error(Exception e){
        return "error:"+e.getMessage();
    }


}
