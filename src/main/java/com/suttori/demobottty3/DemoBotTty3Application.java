package com.suttori.demobottty3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class DemoBotTty3Application {

    public static void main(String[] args) {
        SpringApplication.run(DemoBotTty3Application.class, args);
    }
}
