package com.fashionstore.controller;

import com.fashionstore.dto.request.OrderRequest;
import com.fashionstore.dto.request.OrderStatusRequest;
import com.fashionstore.dto.response.ApiResponse;
import com.fashionstore.dto.response.OrderResponse;
import com.fashionstore.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/api/orders/place")
    public ResponseEntity<OrderResponse> placeOrder(@AuthenticationPrincipal UserDetails userDetails,
                                                     @Valid @RequestBody OrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.placeOrder(userDetails.getUsername(), request));
    }

    @GetMapping("/api/orders/my-orders")
    public ResponseEntity<List<OrderResponse>> getMyOrders(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(orderService.getOrdersByUser(userDetails.getUsername()));
    }

    @GetMapping("/api/orders/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(orderService.getOrderById(id, userDetails.getUsername()));
    }

    @DeleteMapping("/api/orders/{id}/cancel")
    public ResponseEntity<ApiResponse> cancelOrder(@PathVariable Long id,
                                                    @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(orderService.cancelOrder(id, userDetails.getUsername()));
    }

    @GetMapping("/api/admin/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PutMapping("/api/admin/orders/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable Long id,
                                                            @Valid @RequestBody OrderStatusRequest request) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, request));
    }
}
