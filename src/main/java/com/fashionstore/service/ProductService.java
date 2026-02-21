package com.fashionstore.service;

import com.fashionstore.dto.request.ProductRequest;
import com.fashionstore.dto.response.ApiResponse;
import com.fashionstore.dto.response.PageResponse;
import com.fashionstore.dto.response.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public interface ProductService {
    PageResponse<ProductResponse> getAllProducts(int pageNo, int pageSize, String sortBy, String sortDir,
                                                 Long categoryId, BigDecimal minPrice, BigDecimal maxPrice,
                                                 String keyword);
    ProductResponse getProductById(Long id);
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(Long id, ProductRequest request);
    ApiResponse deleteProduct(Long id);
    ProductResponse uploadProductImage(Long id, MultipartFile file);
    PageResponse<ProductResponse> searchProducts(String keyword, int pageNo, int pageSize);
}
