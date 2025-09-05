package com.ub19.quality;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QualityRunnerApplication {
    public static void main(String[] args) {
        int code = SpringApplication.exit(SpringApplication.run(QualityRunnerApplication.class, args));
        System.exit(code);
    }
}
