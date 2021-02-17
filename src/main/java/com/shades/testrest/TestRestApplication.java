package com.shades.testrest;

import ch.qos.logback.classic.Logger;
import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class TestRestApplication implements CommandLineRunner {
    
    private static Logger LOGGER = (Logger)LoggerFactory.getLogger(TestRestApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(TestRestApplication.class, args);
	}

    @Override
    public void run(String... args) throws Exception {
//        if(args.length != 1) {
//            LOGGER.error("Insufficient arguments provided");
//            LOGGER.error("Please provide below arguments:\n\n1. test GET url to hit");
//            return;
//        }
//        String url = args[0];
//        RestTemplate restTemplate = getRestTemplate();
//        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, new HashMap<>());
//        LOGGER.info("Response from secured service: {}", response.getBody());
    }
    
    RestTemplate getRestTemplate() throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException, KeyManagementException {
        URL url = this.getClass().getResource("/dip-keystore.pkcs");
        LOGGER.info("URL for pkcs store found? :{}",url!=null);
        TrustStrategy trustStrategy = new TrustSelfSignedStrategy();

        SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial(url, "password".toCharArray())
                //.loadTrustMaterial(null, trustStrategy)
                .build();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLContext(sslContext)
                .setSSLHostnameVerifier(getNoHostnameVerifier()) //not prod safe
                .build();
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        httpRequestFactory.setHttpClient(httpClient);
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
        
        return restTemplate;
    }
    
    private HostnameVerifier getNoHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String string, SSLSession ssls) {
                return true;
            }
        };
    }

}
