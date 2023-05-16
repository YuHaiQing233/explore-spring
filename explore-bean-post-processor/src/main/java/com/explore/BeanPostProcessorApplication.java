package com.explore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author: YuHaiQing
 * @time: 2023/5/11 17:14
 */
@SpringBootApplication
@EnableAspectJAutoProxy
public class BeanPostProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeanPostProcessorApplication.class);
    }

}
