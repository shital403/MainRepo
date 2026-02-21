package com.fashionstore.service.impl;

import com.fashionstore.dto.request.OrderRequest;
import com.fashionstore.dto.request.OrderStatusRequest;
import com.fashionstore.dto.response.ApiResponse;
import com.fashionstore.dto.response.OrderItemResponse;
import com.fashionstore.dto.response.OrderResponse;
import com.fashionstore.entity.*;
import com.fashionstore.exception.BadRequestException;
import com.fashionstore.exception.ResourceNotFoundException;
import com.fashionstore.repository.CartItemRepository;
import com.fashionstore.repository.CartRepository;
import com.fashionstore.repository.OrderRepository;
import com.fashionstore.repository.UserRepository;
import com.fashionstore.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    @Transactional
    public OrderResponse placeOrder(String email, OrderRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new BadRequestException("Cart is empty"));

        List<CartItem> cartItems;
        if (request.getCartItemIds() != null && !request.getCartItemIds().isEmpty()) {
            cartItems = cartItemRepository.findAllById(request.getCartItemIds());
        } else {
            cartItems = new ArrayList<>(cart.getCartItems());
        }

        if (cartItems.isEmpty()) {
            throw new BadRequestException("No items to order");
        }

        Order order = Order.builder()
                .user(user)
                .shippingAddress(request.getShippingAddress())
                .paymentId(request.getPaymentId())
                .paymentStatus(request.getPaymentId() != null ? "PAID" : "PENDING")
                .totalAmount(BigDecimal.ZERO)
                .build();

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new BadRequestException("Insufficient stock for product: " + product.getName());
            }
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .price(product.getPrice())
                    .size(cartItem.getSize())
                    .color(cartItem.getColor())
                    .build();
            orderItems.add(orderItem);
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(total);
        Order savedOrder = orderRepository.save(order);

        // Clear ordered items from cart
        cart.getCartItems().removeAll(cartItems);
        cartRepository.save(cart);

        log.info("Order placed: {} for user: {}", savedOrder.getId(), email);
        return toResponse(savedOrder);
    }

    @Override
    public List<OrderResponse> getOrdersByUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public OrderResponse getOrderById(Long id, String email) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        // Admin can access any order; customers can only access their own
        if (email != null && !order.getUser().getEmail().equals(email)) {
            User requester = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
            boolean isAdmin = requester.getRoles().stream()
                    .anyMatch(r -> r.getName() == ERole.ROLE_ADMIN);
            if (!isAdmin) {
                throw new BadRequestException("Access denied");
            }
        }
        return toResponse(order);
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long id, OrderStatusRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        order.setStatus(request.getStatus());
        return toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional
    public ApiResponse cancelOrder(Long id, String email) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        if (!order.getUser().getEmail().equals(email)) {
            throw new BadRequestException("You can only cancel your own orders");
        }
        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.SHIPPED) {
            throw new BadRequestException("Cannot cancel order in status: " + order.getStatus());
        }

        // Restore stock
        for (OrderItem item : order.getOrderItems()) {
            item.getProduct().setStockQuantity(
                    item.getProduct().getStockQuantity() + item.getQuantity());
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        return ApiResponse.success("Order cancelled successfully");
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getOrderItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .size(item.getSize())
                        .color(item.getColor())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .items(items)
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .shippingAddress(order.getShippingAddress())
                .paymentId(order.getPaymentId())
                .paymentStatus(order.getPaymentStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
