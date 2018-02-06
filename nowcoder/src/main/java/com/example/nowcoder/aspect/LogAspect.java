package com.example.nowcoder.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

@Aspect
@Component
public class LogAspect {
    private static final Logger logger= LoggerFactory.getLogger(LogAspect.class);

    @Before("execution(* com.example.nowcoder.controller.IndexController.*(..))")
    public void beforeMethod(JoinPoint joinPoint){
        StringBuilder sb=new StringBuilder();
        for(Object arg:joinPoint.getArgs())
        {
            sb.append("args:"+arg.toString()+"|");

        }
        logger.info("before:time",new Date());
        logger.info("before method:");
    }
    @After("execution(* com.example.nowcoder.controller.IndexController.*(..))")
    public void afterMethod(){
        logger.info("after: time",new Date());
        logger.info("after method:");
    }

}
