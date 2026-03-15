package com.astracart.product.service;

import com.astracart.product.dto.ProductRequest;
import com.astracart.product.dto.ProductResponse;
import com.astracart.product.entity.Category;
import com.astracart.product.entity.Product;
import com.astracart.product.exception.ProductNotFoundException;
import com.astracart.product.exception.ProductAlreadyExistsException;
import com.astracart.product.exception.CategoryNotFoundException;
import com.astracart.product.exception.InsufficientStockException;
import com.astracart.product.repository.CategoryRepository;
import com.astracart.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public ProductResponse createProduct(ProductRequest productRequest) {
        if (productRepository.existsBySku(productRequest.getSku())) {
            throw new ProductAlreadyExistsException("Product with SKU " + productRequest.getSku() + " already exists");
        }

        Product product = convertToEntity(productRequest);
        
        if (productRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(productRequest.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + productRequest.getCategoryId()));
            product.setCategory(category);
        }

        Product savedProduct = productRepository.save(product);
        return convertToResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        
        if (product.isDeleted()) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        
        return convertToResponse(product);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with SKU: " + sku));
        
        if (product.isDeleted()) {
            throw new ProductNotFoundException("Product not found with SKU: " + sku);
        }
        
        return convertToResponse(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllActiveProducts(Pageable pageable) {
        return productRepository.findActiveProducts(pageable)
                .map(this::convertToResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(String search, Pageable pageable) {
        return productRepository.searchProducts(search, pageable)
                .map(this::convertToResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findActiveProductsByCategory(categoryId, pageable)
                .map(this::convertToResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.findActiveProductsByPriceRange(minPrice, maxPrice, pageable)
                .map(this::convertToResponse);
    }

    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        if (existingProduct.isDeleted()) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }

        if (!existingProduct.getSku().equals(productRequest.getSku()) && 
            productRepository.existsBySku(productRequest.getSku())) {
            throw new ProductAlreadyExistsException("Product with SKU " + productRequest.getSku() + " already exists");
        }

        updateProductFromRequest(existingProduct, productRequest);

        if (productRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(productRequest.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + productRequest.getCategoryId()));
            existingProduct.setCategory(category);
        }

        Product updatedProduct = productRepository.save(existingProduct);
        return convertToResponse(updatedProduct);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        if (product.isDeleted()) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }

        product.softDelete();
        productRepository.save(product);
    }

    public ProductResponse updateStock(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        if (product.isDeleted()) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }

        if (quantity < 0) {
            product.decreaseStock(Math.abs(quantity));
        } else {
            product.increaseStock(quantity);
        }

        Product updatedProduct = productRepository.save(product);
        return convertToResponse(updatedProduct);
    }

    public void decreaseStock(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        if (product.isDeleted()) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }

        try {
            product.decreaseStock(quantity);
            productRepository.save(product);
        } catch (IllegalArgumentException e) {
            throw new InsufficientStockException(e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getLowStockProducts() {
        return productRepository.findLowStockProducts().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getOutOfStockProducts() {
        return productRepository.findOutOfStockProducts().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private Product convertToEntity(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setSku(request.getSku());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setMinStockLevel(request.getMinStockLevel());
        product.setWeight(request.getWeight());
        product.setDimensions(request.getDimensions());
        product.setActive(request.isActive());
        return product;
    }

    private void updateProductFromRequest(Product product, ProductRequest request) {
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setSku(request.getSku());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setMinStockLevel(request.getMinStockLevel());
        product.setWeight(request.getWeight());
        product.setDimensions(request.getDimensions());
        product.setActive(request.isActive());
    }

    private ProductResponse convertToResponse(Product product) {
        CategoryDto categoryDto = null;
        if (product.getCategory() != null) {
            categoryDto = new CategoryDto(
                    product.getCategory().getId(),
                    product.getCategory().getName(),
                    product.getCategory().getDescription(),
                    product.getCategory().getParent() != null ? product.getCategory().getParent().getId() : null,
                    product.getCategory().getParent() != null ? product.getCategory().getParent().getName() : null,
                    product.getCategory().isActive(),
                    0, // Will be populated if needed
                    product.getCategory().getCreatedAt(),
                    product.getCategory().getUpdatedAt()
            );
        }

        List<ProductImageDto> imageDtos = product.getImages().stream()
                .map(image -> new ProductImageDto(
                        image.getId(),
                        image.getImageUrl(),
                        image.getAltText(),
                        image.getDisplayOrder(),
                        image.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getSku(),
                product.getPrice(),
                categoryDto,
                product.getStockQuantity(),
                product.getMinStockLevel(),
                product.getWeight(),
                product.getDimensions(),
                product.isActive(),
                product.isInStock(),
                product.isLowStock(),
                imageDtos,
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
