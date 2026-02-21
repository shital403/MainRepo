package com.fashionstore.controller;

import com.fashionstore.dto.request.CartItemRequest;
import com.fashionstore.dto.response.ApiResponse;
import com.fashionstore.dto.response.CartResponse;
import com.fashionstore.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.getCartByUser(userDetails.getUsername()));
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(@AuthenticationPrincipal UserDetails userDetails,
                                                   @Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.addToCart(userDetails.getUsername(), request));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> updateCartItem(@AuthenticationPrincipal UserDetails userDetails,
                                                        @PathVariable Long itemId,
                                                        @RequestParam int quantity) {
        return ResponseEntity.ok(cartService.updateCartItem(userDetails.getUsername(), itemId, quantity));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse> removeFromCart(@AuthenticationPrincipal UserDetails userDetails,
                                                       @PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeFromCart(userDetails.getUsername(), itemId));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse> clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.clearCart(userDetails.getUsername()));
    }
}
