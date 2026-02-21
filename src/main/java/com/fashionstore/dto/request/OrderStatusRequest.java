package com.fashionstore.dto.request;

import com.fashionstore.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderStatusRequest {

    @NotNull(message = "Status is required")
    private OrderStatus status;
}
