package com.example.java.configurations;

import com.azure.core.amqp.ProxyAuthenticationType;
import com.azure.core.http.HttpClient;
import com.azure.core.http.ProxyOptions;
import com.azure.core.http.netty.NettyAsyncHttpClientBuilder;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.net.Proxy;

@Configuration
public class ProxyConfiguration {

    /**
     * Proxy Host.
     */
    @Value("${proxy.host}")
    private String proxyHost;

    /**
     * Proxy status, shows if it is needed or not.
     */
    @Value("${proxy.enabled}")
    private boolean proxyStatus;

    /**
     * Proxy Port.
     */
    @Value("${proxy.port}")
    private int proxyPort;
    /**
     * Proxy Username.
     */
    @Value("${proxy.username}")
    private String proxyUsername;
    /**
     * Proxy Password.
     */
    @Value("${proxy.password}")
    private String proxyPassword;
    /**
     * Storage url.
     */
    @Value("${azure.storage.connection-string}")
    private String azureUrl;
    /**
     * Sas token.
     */
    @Value("${azure.storage.sas-token}")
    private String sasToken;


    /**
     * Function that creates the HttpClient.
     *
     * @return a HttpClient for this proxy.
     */
    @Bean
    public HttpClient createHttpClient() {
        // this is the god class that forces the proxy in the service
        if (proxyHost == null || proxyHost.isEmpty() || proxyPort <= 0 || proxyStatus) {
            return HttpClient.createDefault();
        } else {
            com.azure.core.http.ProxyOptions proxyOptions = new com.azure.core.http.ProxyOptions(ProxyOptions.Type.HTTP,
                    new InetSocketAddress(proxyHost, proxyPort));

            if (proxyUsername != null && !proxyUsername.isEmpty() && proxyPassword != null && !proxyStatus) {
                proxyOptions.setCredentials(proxyUsername, proxyPassword);
            }

            return new NettyAsyncHttpClientBuilder().proxy(proxyOptions).build();
        }
    }

    /**
     * Function that creates a BlobServiceClient.
     *
     * @return a BlobServiceClient.
     */
    @Bean
    public BlobServiceClient blobServiceClient() {
        // Set up BlobServiceClient with custom HttpClient
        return new BlobServiceClientBuilder()
                .connectionString(azureUrl)
                .sasToken(sasToken)
                .httpClient(HttpClient.createDefault())
                .buildClient();
    }

    /**
     * Function that creates proxyOptions from amqp.
     *
     * @return com.azure.core.amqp.ProxyOptions
     */
    @Bean
    public com.azure.core.amqp.ProxyOptions proxyOptions() {
        if (proxyStatus) {
            return new com.azure.core.amqp.ProxyOptions(ProxyAuthenticationType.NONE,
                    new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)),
                    proxyUsername,
                    proxyPassword);
        } else {
            return com.azure.core.amqp.ProxyOptions.SYSTEM_DEFAULTS;
        }
    }

    /**
     * @return The configuration for proxi needed to send mails.
     */
    @SuppressWarnings("checkstyle:DesignForExtension")
    @Bean
    public com.azure.core.util.Configuration proxyEmailConfiguration() {
        if (proxyStatus) {
            return new com.azure.core.util.Configuration()
                    .put("java.net.useSystemProxies", "true")
                    .put("http.proxyHost", proxyHost)
                    .put("http.proxyPort", String.valueOf(proxyPort))
                    .put("http.proxyUser", proxyUsername)
                    .put("http.proxyPassword", proxyPassword);
        } else {
            return com.azure.core.util.Configuration.NONE;
        }
    }
}
