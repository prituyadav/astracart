package com.astracart.product.repository;

import com.astracart.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    
    Optional<Product> findBySku(String sku);
    
    boolean existsBySku(String sku);
    
    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL AND p.active = true")
    Page<Product> findActiveProducts(Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL AND p.active = true AND p.name LIKE %:name%")
    Page<Product> findActiveProductsByNameContaining(@Param("name") String name, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL AND p.active = true AND p.category.id = :categoryId")
    Page<Product> findActiveProductsByCategory(@Param("categoryId") Long categoryId, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL AND p.active = true AND p.price BETWEEN :minPrice AND :maxPrice")
    Page<Product> findActiveProductsByPriceRange(@Param("minPrice") BigDecimal minPrice, 
                                                 @Param("maxPrice") BigDecimal maxPrice, 
                                                 Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL AND p.stockQuantity <= p.minStockLevel AND p.active = true")
    List<Product> findLowStockProducts();
    
    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL AND p.stockQuantity = 0 AND p.active = true")
    List<Product> findOutOfStockProducts();
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.deletedAt IS NULL AND p.active = true")
    long countActiveProducts();
    
    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL AND (p.name LIKE %:search% OR p.description LIKE %:search% OR p.sku LIKE %:search%)")
    Page<Product> searchProducts(@Param("search") String search, Pageable pageable);
}
