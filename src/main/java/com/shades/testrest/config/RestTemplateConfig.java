/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shades.testrest.config;

import com.shades.testrest.CompressingClientHttpRequestInterceptor;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    
    @Autowired
    CompressingClientHttpRequestInterceptor compressingClientHttpRequestInterceptor;
    
    /**
     * A compression applying interceptor enabled Rest Template
     * @param builder
     * @return 
     */
    @Bean("compressionEnabledRestTemplate")
    public RestTemplate compressionEnabledRestTemplate(RestTemplateBuilder builder) {
        System.out.println("Configuring Rest Template");
        return builder
                .setConnectTimeout(Duration.ofMillis(10000))
                .setReadTimeout(Duration.ofMillis(10000))
                .interceptors(compressingClientHttpRequestInterceptor)
                .build();
        
    }
    
    /**
     * Generic Rest Template
     * @param builder
     * @return 
     */
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        System.out.println("Configuring Rest Template");
        return builder.build();
        
    }
}
