/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shades.testrest;

import ch.qos.logback.classic.Logger;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

/**
 *
 * @author shantanu
 */
@Component
public class CompressingClientHttpRequestInterceptor implements ClientHttpRequestInterceptor{

    private static Logger logger = (Logger)LoggerFactory.getLogger(CompressingClientHttpRequestInterceptor.class);
    
    @Value("${server.compression.min-response-size:51200}")
    private long compressionThreshold;
    
    @Override
    public ClientHttpResponse intercept(HttpRequest req, byte[] body, ClientHttpRequestExecution exec) throws IOException {
        if(body.length >= compressionThreshold) {
            logger.info("payload size {}kb...greater than min-response-size: [{}], enabling compression", (body.length/1024), compressionThreshold);
            HttpHeaders httpHeaders = req.getHeaders();
              httpHeaders.add(HttpHeaders.CONTENT_ENCODING, "gzip");
              httpHeaders.add(HttpHeaders.ACCEPT_ENCODING, "gzip");
            body = compress(body);
        }
          return exec.execute(req, body);
    }
    
    public byte[] compress(byte[] body) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(baos)) {
            gzipOutputStream.write(body);
        }
        return baos.toByteArray();
    }
}
