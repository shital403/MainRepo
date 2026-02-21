package com.fashionstore.controller;

import com.fashionstore.dto.request.ProductRequest;
import com.fashionstore.dto.response.ApiResponse;
import com.fashionstore.dto.response.PageResponse;
import com.fashionstore.dto.response.ProductResponse;
import com.fashionstore.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/api/products")
    public ResponseEntity<PageResponse<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "12") int pageSize,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(productService.getAllProducts(
                pageNo, pageSize, sortBy, sortDir, categoryId, minPrice, maxPrice, keyword));
    }

    @GetMapping("/api/products/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping("/api/admin/products")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request));
    }

    @PutMapping("/api/admin/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id,
                                                          @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/api/admin/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.deleteProduct(id));
    }

    @PostMapping("/api/admin/products/{id}/image")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> uploadProductImage(@PathVariable Long id,
                                                               @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(productService.uploadProductImage(id, file));
    }

    @GetMapping("/api/products/search")
    public ResponseEntity<PageResponse<ProductResponse>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "12") int pageSize) {
        return ResponseEntity.ok(productService.searchProducts(keyword, pageNo, pageSize));
    }
}
