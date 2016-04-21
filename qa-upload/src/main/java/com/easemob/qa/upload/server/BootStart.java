package com.easemob.qa.upload.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.easemob.qa.upload")
public class BootStart {

    public static void main(String[] args) {
        SpringApplication.run(BootStart.class, args);
    }

}
