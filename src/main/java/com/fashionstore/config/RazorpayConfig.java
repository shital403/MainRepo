package com.fashionstore.config;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RazorpayConfig {

    @Value("${razorpay.key.id:your_key_id}")
    private String keyId;

    @Value("${razorpay.key.secret:your_key_secret}")
    private String keySecret;

    @Bean
    public RazorpayClient razorpayClient() {
        try {
            return new RazorpayClient(keyId, keySecret);
        } catch (RazorpayException e) {
            log.warn("Could not initialize Razorpay client: {}. Payment features may not work.", e.getMessage());
            return null;
        }
    }
}
