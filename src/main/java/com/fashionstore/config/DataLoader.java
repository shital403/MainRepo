package com.fashionstore.config;

import com.fashionstore.entity.*;
import com.fashionstore.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements ApplicationRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        loadRoles();
        loadAdminUser();
        loadCategories();
        loadProducts();
    }

    private void loadRoles() {
        for (ERole eRole : ERole.values()) {
            if (roleRepository.findByName(eRole).isEmpty()) {
                roleRepository.save(Role.builder().name(eRole).build());
                log.info("Created role: {}", eRole);
            }
        }
    }

    private void loadAdminUser() {
        String adminEmail = "admin@fashionstore.com";
        if (userRepository.existsByEmail(adminEmail)) {
            return;
        }
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Admin role not found"));
        Role customerRole = roleRepository.findByName(ERole.ROLE_CUSTOMER)
                .orElseThrow(() -> new RuntimeException("Customer role not found"));

        User admin = User.builder()
                .username("admin")
                .email(adminEmail)
                .password(passwordEncoder.encode("Admin@123"))
                .firstName("Admin")
                .lastName("User")
                .phone("9999999999")
                .roles(Set.of(adminRole, customerRole))
                .enabled(true)
                .build();
        userRepository.save(admin);
        log.info("Created admin user: {}", adminEmail);
    }

    private void loadCategories() {
        createCategoryIfNotExists("Men", "Men's clothing and accessories");
        createCategoryIfNotExists("Women", "Women's clothing and accessories");
        createCategoryIfNotExists("Kids", "Kids' clothing and accessories");
        createCategoryIfNotExists("Accessories", "Fashion accessories for everyone");
    }

    private void createCategoryIfNotExists(String name, String description) {
        if (categoryRepository.findByName(name).isEmpty()) {
            categoryRepository.save(Category.builder()
                    .name(name)
                    .description(description)
                    .build());
            log.info("Created category: {}", name);
        }
    }

    private void loadProducts() {
        if (productRepository.count() > 0) {
            return;
        }

        Category men = categoryRepository.findByName("Men")
                .orElseThrow(() -> new RuntimeException("Men category not found"));
        Category women = categoryRepository.findByName("Women")
                .orElseThrow(() -> new RuntimeException("Women category not found"));
        Category kids = categoryRepository.findByName("Kids")
                .orElseThrow(() -> new RuntimeException("Kids category not found"));

        productRepository.save(Product.builder()
                .name("Classic White T-Shirt")
                .description("Premium cotton white t-shirt, perfect for everyday wear.")
                .price(new BigDecimal("599.00"))
                .stockQuantity(100)
                .sizes(Set.of("S", "M", "L", "XL", "XXL"))
                .colors(Set.of("White", "Black", "Grey"))
                .category(men)
                .active(true)
                .build());

        productRepository.save(Product.builder()
                .name("Floral Summer Dress")
                .description("Beautiful floral print summer dress, lightweight and comfortable.")
                .price(new BigDecimal("1299.00"))
                .stockQuantity(50)
                .sizes(Set.of("XS", "S", "M", "L"))
                .colors(Set.of("Blue", "Pink", "Yellow"))
                .category(women)
                .active(true)
                .build());

        productRepository.save(Product.builder()
                .name("Kids Cartoon Hoodie")
                .description("Warm and cozy hoodie with fun cartoon prints for kids.")
                .price(new BigDecimal("799.00"))
                .stockQuantity(75)
                .sizes(Set.of("3-4Y", "5-6Y", "7-8Y", "9-10Y"))
                .colors(Set.of("Red", "Blue", "Green"))
                .category(kids)
                .active(true)
                .build());

        productRepository.save(Product.builder()
                .name("Men's Slim Fit Jeans")
                .description("Modern slim fit jeans with stretch fabric for comfort.")
                .price(new BigDecimal("1899.00"))
                .stockQuantity(60)
                .sizes(Set.of("28", "30", "32", "34", "36"))
                .colors(Set.of("Blue", "Black", "Dark Grey"))
                .category(men)
                .active(true)
                .build());

        log.info("Created sample products");
    }
}
