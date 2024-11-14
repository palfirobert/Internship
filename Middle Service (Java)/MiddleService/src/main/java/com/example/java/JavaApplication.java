package com.example.java;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
@SpringBootApplication
public class JavaApplication {

    @SuppressWarnings({"checkstyle:FinalParameters", "checkstyle:MissingJavadocMethod"})
    public static void main(String[] args) {
        SpringApplication.run(JavaApplication.class, args);
    }

}
