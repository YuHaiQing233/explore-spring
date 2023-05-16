package com.explore.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: YuHaiQing
 * @time: 2023/5/11 17:19
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/sayHello")
    public String sayHello(String name){
        return "Hello " + name;
    }

}
