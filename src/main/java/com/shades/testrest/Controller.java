/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shades.testrest;

import ch.qos.logback.classic.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Send gzip compressed request
 * @author shantanu
 */
@RestController
public class Controller {
    
    private static Logger logger = (Logger)LoggerFactory.getLogger(Controller.class);
    
    @Autowired
    RestTemplate restTemplate;
    
    @Autowired
    @Qualifier("compressionEnabledRestTemplate")
    RestTemplate gzipRestTemplate;
            
    @RequestMapping(method = RequestMethod.GET, value = "sendCompressed")
    public void sendCompressed() throws IOException, URISyntaxException {
        String payload = getPayloadFromResources("/testData/payload.json");
        
        RequestEntity requestEntity = RequestEntity.post(new URI("http://localhost:8081/handleCompressed")).body(payload);
        long startTs = System.nanoTime();
        restTemplate.exchange(requestEntity, Void.class);
        logger.info("Time to send uncompressed payload: {} ms", ((System.nanoTime()-startTs)/1000/1000));
        
        payload = getPayloadFromResources("/testData/smaller-payload.json");
        requestEntity = RequestEntity.post(new URI("http://localhost:8081/handleCompressed")).body(payload);
        startTs = System.nanoTime();
        gzipRestTemplate.exchange(requestEntity, Void.class);
        logger.info("Time to send compressed payload: {} ms", ((System.nanoTime()-startTs)/1000/1000));
    }
    
    private String getPayloadFromResources(String fileName) {
        InputStream is = this.getClass().getResourceAsStream(fileName);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException ex) {
            logger.error("{}",ex);
            return "";
        }
    }
    
    
    
    
}
