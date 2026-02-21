package com.fashionstore.service;

import com.fashionstore.dto.request.OrderRequest;
import com.fashionstore.dto.request.OrderStatusRequest;
import com.fashionstore.dto.response.ApiResponse;
import com.fashionstore.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse placeOrder(String email, OrderRequest request);
    List<OrderResponse> getOrdersByUser(String email);
    OrderResponse getOrderById(Long id, String email);
    List<OrderResponse> getAllOrders();
    OrderResponse updateOrderStatus(Long id, OrderStatusRequest request);
    ApiResponse cancelOrder(Long id, String email);
}
