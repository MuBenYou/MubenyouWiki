package com.mby.wiki.config;



import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

@ComponentScan("com.mby") //指定路径 @ComponentScan({"com.mby","com.test"}) 就可以扫描多个包
@SpringBootApplication
@MapperScan("com.mby.wiki.mapper")//使SpringBoot知道持久层（sql）是哪个
@EnableScheduling//定时器
public class WikiApplication {
    //LOG作用LoggerFactory.getLogger可以在IDE控制台打印日志,便于开发,一般加在代码最上面
    private static final Logger LOG= LoggerFactory.getLogger(WikiApplication.class);
    public static void main(String[] args) {
        SpringApplication app=new SpringApplication(WikiApplication.class);
        Environment env=app.run(args).getEnvironment();//Environment 这个接口代表应用运行时的环境。它代表了两方面、一个是 profile 一个是 properties。
        LOG.info("启动成功！");
        LOG.info("地址：\thttp://127.0.0.1:{}",env.getProperty("server.port"));
    }

}
