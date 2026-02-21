package com.fashionstore.controller;

import com.fashionstore.dto.response.ApiResponse;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final RazorpayClient razorpayClient;

    @Value("${razorpay.key.secret:your_key_secret}")
    private String razorpayKeySecret;

    @Value("${razorpay.key.id:your_key_id}")
    private String razorpayKeyId;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> request) {
        if (razorpayClient == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Payment gateway not configured"));
        }
        try {
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String currency = request.getOrDefault("currency", "INR").toString();

            JSONObject orderRequest = new JSONObject();
            // Razorpay expects amount in paisa (smallest currency unit)
            orderRequest.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue());
            orderRequest.put("currency", currency);
            orderRequest.put("receipt", "order_" + System.currentTimeMillis());

            Order order = razorpayClient.orders.create(orderRequest);

            Map<String, Object> response = new HashMap<>();
            response.put("orderId", order.get("id"));
            response.put("amount", order.get("amount"));
            response.put("currency", order.get("currency"));
            response.put("keyId", razorpayKeyId);
            return ResponseEntity.ok(response);
        } catch (RazorpayException e) {
            log.error("Razorpay order creation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Payment order creation failed: " + e.getMessage()));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse> verifyPayment(@RequestBody Map<String, String> request) {
        try {
            String orderId = request.get("razorpay_order_id");
            String paymentId = request.get("razorpay_payment_id");
            String signature = request.get("razorpay_signature");

            String data = orderId + "|" + paymentId;
            String generatedSignature = hmacSha256(data, razorpayKeySecret);

            if (generatedSignature.equals(signature)) {
                return ResponseEntity.ok(ApiResponse.success("Payment verified successfully"));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("Payment verification failed"));
            }
        } catch (Exception e) {
            log.error("Payment verification error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Payment verification error: " + e.getMessage()));
        }
    }

    private String hmacSha256(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(data.getBytes());
        return HexFormat.of().formatHex(hash);
    }
}
