package com.explore.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * @author: YuHaiQing
 * @time: 2023/5/11 17:20
 */
@Slf4j
@Component
public class CustomBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        log.info("beanName:{}, 初始化之后执行了 postProcessAfterInitialization() 方法", beanName);
        return null;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        log.info("beanName:{}, 初始化之前执行了 postProcessBeforeInitialization() 方法", beanName);
        return null;
    }
}
