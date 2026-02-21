package com.fashionstore.service;

import com.fashionstore.dto.request.CartItemRequest;
import com.fashionstore.dto.response.ApiResponse;
import com.fashionstore.dto.response.CartResponse;

public interface CartService {
    CartResponse getCartByUser(String email);
    CartResponse addToCart(String email, CartItemRequest request);
    CartResponse updateCartItem(String email, Long itemId, int quantity);
    ApiResponse removeFromCart(String email, Long itemId);
    ApiResponse clearCart(String email);
}
