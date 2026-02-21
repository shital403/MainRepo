package com.fashionstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {

    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;

    private String paymentId;

    private List<Long> cartItemIds;
}
