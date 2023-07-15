package com.aiaa.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableAsync
public class ThreadPoolConfig {
    //不加这个配置类（配置类的名字无所谓）和@EnableScheduling注解,
    // 那么ThreadPoolTaskScheduler就无法注入（@Autowired），即无法初始化，
    // 无法得到一个ThreadPoolTaskScheduler的对象

}
