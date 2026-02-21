package com.fashionstore.service.impl;

import com.fashionstore.dto.request.ProductRequest;
import com.fashionstore.dto.response.ApiResponse;
import com.fashionstore.dto.response.PageResponse;
import com.fashionstore.dto.response.ProductResponse;
import com.fashionstore.entity.Category;
import com.fashionstore.entity.Product;
import com.fashionstore.exception.ResourceNotFoundException;
import com.fashionstore.repository.CategoryRepository;
import com.fashionstore.repository.ProductRepository;
import com.fashionstore.service.FileStorageService;
import com.fashionstore.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;

    @Override
    public PageResponse<ProductResponse> getAllProducts(int pageNo, int pageSize, String sortBy, String sortDir,
                                                        Long categoryId, BigDecimal minPrice, BigDecimal maxPrice,
                                                        String keyword) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Product> page = productRepository.findWithFilters(categoryId, minPrice, maxPrice, keyword, pageable);
        return buildPageResponse(page);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return toResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .sizes(request.getSizes())
                .colors(request.getColors())
                .category(category)
                .active(request.isActive())
                .build();

        return toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setSizes(request.getSizes());
        product.setColors(request.getColors());
        product.setCategory(category);
        product.setActive(request.isActive());

        return toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ApiResponse deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        if (product.getImageUrl() != null) {
            fileStorageService.deleteFile(product.getImageUrl());
        }
        productRepository.delete(product);
        return ApiResponse.success("Product deleted successfully");
    }

    @Override
    @Transactional
    public ProductResponse uploadProductImage(Long id, MultipartFile file) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        if (product.getImageUrl() != null) {
            fileStorageService.deleteFile(product.getImageUrl());
        }
        String filename = fileStorageService.storeFile(file);
        product.setImageUrl(filename);
        return toResponse(productRepository.save(product));
    }

    @Override
    public PageResponse<ProductResponse> searchProducts(String keyword, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("createdAt").descending());
        Page<Product> page = productRepository.searchProducts(keyword, pageable);
        return buildPageResponse(page);
    }

    private PageResponse<ProductResponse> buildPageResponse(Page<Product> page) {
        List<ProductResponse> content = page.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return PageResponse.<ProductResponse>builder()
                .content(content)
                .pageNo(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    public ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .imageUrl(product.getImageUrl())
                .sizes(product.getSizes())
                .colors(product.getColors())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .active(product.isActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
