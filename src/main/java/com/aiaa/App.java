package com.aiaa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class App {

    @PostConstruct
    public void init() {
        // 解决netty启动冲突问题(Elasticsearch和redis底层都是基于netty)
        // see Netty4Utils.setAvailableProcessors()
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
        System.out.println("/////////////// 项目启动 ///////////////");
    }

}
