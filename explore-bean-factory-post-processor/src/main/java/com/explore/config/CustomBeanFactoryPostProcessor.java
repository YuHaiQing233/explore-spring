package com.explore.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

/**
 * 自定义 Bean工厂后置处理器
 * @Author HaiQing.Yu
 * @Date 2023/4/26 14:16
 */
@Slf4j
@Component
public class CustomBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        log.info("开始执行 BeanFactoryPostProcessor#postProcessBeanFactory");
    }
}
