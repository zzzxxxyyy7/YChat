package com.ychat.common;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author catch
 * @date 2024/02/27
 */
@SpringBootApplication(scanBasePackages = {"com.ychat"})
@MapperScan("com.ychat.common.**.Mapper")
public class YChatCustomApplication {

    public static void main(String[] args) {
        SpringApplication.run(YChatCustomApplication.class,args);
    }

}