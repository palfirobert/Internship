package com.example.java.configurations;

import com.azure.communication.email.EmailClient;
import com.azure.communication.email.EmailClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.example.java.domain.networking.Response;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ArrayBlockingQueue;

@Configuration
public class RestConfiguration {

    /**
     * Mail service connection string.
     */
    @Value("${azure.mail-service.connection-string}")
    private String connectionString;
    /**
     * Proxy config for mail service.
     */
    @Autowired
    private com.azure.core.util.Configuration proxyEmailConfiguration;

    /**
     * Integer  that represents the capacity of the response queue.
     */
    static final int CAPACITY = 690;

    /**
     * @return bean that is used in UserDetailServiceDB
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * @return create a bean for rest template to be injected in service class
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Function used to instantiate the response queue for the requestProcessingService.
     *
     * @return ArrayBlockingQueue<Response>
     */
    @Bean
    public ArrayBlockingQueue<Response> responseQueueInstance() {
        return new ArrayBlockingQueue<>(CAPACITY);
    }

    /**
     * Client for email sending.
     * @return - client.
     */
    @Bean
    public EmailClient emailClient() {
        return new EmailClientBuilder()
                .connectionString(this.connectionString)
                .configuration(proxyEmailConfiguration)
                .buildClient();
    }
}
