package com.example.java;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

@Profile("test")
@ActiveProfiles("test")
@SpringBootTest
class JavaApplicationTests {

    @Test
    void contextLoads() {
    }

}
