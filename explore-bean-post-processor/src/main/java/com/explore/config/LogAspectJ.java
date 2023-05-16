package com.explore.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author: YuHaiQing
 * @time: 2023/5/14 18:26
 */
@Slf4j
@Aspect
@Component
public class LogAspectJ {

    @Pointcut("execution(* com.explore.controller.*.*(..))")
    public void pointCut(){
    }

    @Before("pointCut()")
    public void beforeNotice(JoinPoint joinPoint){
        log.info("-------前置通知-------");
    }


    @After("pointCut()")
    public void afterNotice(){
        log.info("-------前置通知-------");
    }

}
