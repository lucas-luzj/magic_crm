package com.magic.crm.service.impl;

import com.magic.crm.dto.ProductDTO;
import com.magic.crm.dto.ProductSearchDTO;
import com.magic.crm.entity.Product;
import com.magic.crm.repository.ProductRepository;
import com.magic.crm.service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 产品服务实现类
 */
@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Override
    public Product createProduct(ProductDTO productDTO) {
        // 检查产品编码是否已存在
        if (productRepository.existsByCode(productDTO.getCode())) {
            throw new RuntimeException("产品编码已存在: " + productDTO.getCode());
        }
        
        // 检查产品名称是否已存在
        if (productRepository.existsByName(productDTO.getName())) {
            throw new RuntimeException("产品名称已存在: " + productDTO.getName());
        }
        
        Product product = new Product();
        BeanUtils.copyProperties(productDTO, product);
        
        // 设置创建信息
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        product.setDeleted(false);
        product.setStatus("ACTIVE");
        
        return productRepository.save(product);
    }
    
    @Override
    public Product updateProduct(UUID id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("产品不存在，ID: " + id));
        
        // 检查产品编码是否已存在（排除自己）
        if (StringUtils.hasText(productDTO.getCode()) && 
            !productDTO.getCode().equals(product.getCode()) && 
            productRepository.existsByCode(productDTO.getCode())) {
            throw new RuntimeException("产品编码已存在: " + productDTO.getCode());
        }
        
        // 检查产品名称是否已存在（排除自己）
        if (StringUtils.hasText(productDTO.getName()) && 
            !productDTO.getName().equals(product.getName()) && 
            productRepository.existsByName(productDTO.getName())) {
            throw new RuntimeException("产品名称已存在: " + productDTO.getName());
        }
        
        BeanUtils.copyProperties(productDTO, product, "id", "createdAt", "createdBy");
        product.setUpdatedAt(LocalDateTime.now());
        
        return productRepository.save(product);
    }
    
    @Override
    public Product getProductById(UUID id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("产品不存在，ID: " + id));
    }
    
    @Override
    public Product getProductByCode(String code) {
        return productRepository.findByCode(code)
            .orElseThrow(() -> new RuntimeException("产品不存在，编码: " + code));
    }
    
    @Override
    public void deleteProduct(UUID id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("产品不存在，ID: " + id));
        
        product.setDeleted(true);
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);
    }
    
    @Override
    public void deleteProducts(List<UUID> ids) {
        List<Product> products = productRepository.findAllById(ids);
        for (Product product : products) {
            product.setDeleted(true);
            product.setUpdatedAt(LocalDateTime.now());
        }
        productRepository.saveAll(products);
    }
    
    @Override
    public Page<Product> searchProducts(ProductSearchDTO searchDTO, Pageable pageable) {
        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 基本条件
            predicates.add(cb.equal(root.get("deleted"), false));
            
            if (StringUtils.hasText(searchDTO.getName())) {
                predicates.add(cb.like(cb.lower(root.get("name")), 
                    "%" + searchDTO.getName().toLowerCase() + "%"));
            }
            
            if (StringUtils.hasText(searchDTO.getCode())) {
                predicates.add(cb.like(cb.lower(root.get("code")), 
                    "%" + searchDTO.getCode().toLowerCase() + "%"));
            }
            
            if (StringUtils.hasText(searchDTO.getBrand())) {
                predicates.add(cb.like(cb.lower(root.get("brand")), 
                    "%" + searchDTO.getBrand().toLowerCase() + "%"));
            }
            
            if (StringUtils.hasText(searchDTO.getModel())) {
                predicates.add(cb.like(cb.lower(root.get("model")), 
                    "%" + searchDTO.getModel().toLowerCase() + "%"));
            }
            
            if (searchDTO.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("categoryId"), searchDTO.getCategoryId()));
            }
            
            if (StringUtils.hasText(searchDTO.getStatus())) {
                predicates.add(cb.equal(root.get("status"), searchDTO.getStatus()));
            }
            
            if (searchDTO.getIsSparePart() != null) {
                predicates.add(cb.equal(root.get("isSparePart"), searchDTO.getIsSparePart()));
            }
            
            if (searchDTO.getIsBundle() != null) {
                predicates.add(cb.equal(root.get("isBundle"), searchDTO.getIsBundle()));
            }
            
            if (searchDTO.getIsCustomizable() != null) {
                predicates.add(cb.equal(root.get("isCustomizable"), searchDTO.getIsCustomizable()));
            }
            
            if (StringUtils.hasText(searchDTO.getUnitOfMeasure())) {
                predicates.add(cb.equal(root.get("unitOfMeasure"), searchDTO.getUnitOfMeasure()));
            }
            
            // 关键词搜索
            if (StringUtils.hasText(searchDTO.getKeyword())) {
                String keyword = searchDTO.getKeyword().toLowerCase();
                Predicate namePredicate = cb.like(cb.lower(root.get("name")), "%" + keyword + "%");
                Predicate codePredicate = cb.like(cb.lower(root.get("code")), "%" + keyword + "%");
                Predicate brandPredicate = cb.like(cb.lower(root.get("brand")), "%" + keyword + "%");
                Predicate modelPredicate = cb.like(cb.lower(root.get("model")), "%" + keyword + "%");
                predicates.add(cb.or(namePredicate, codePredicate, brandPredicate, modelPredicate));
            }
            
            // 时间范围查询
            if (searchDTO.getCreatedStartTime() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), searchDTO.getCreatedStartTime()));
            }
            
            if (searchDTO.getCreatedEndTime() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), searchDTO.getCreatedEndTime()));
            }
            
            if (searchDTO.getCreatedBy() != null) {
                predicates.add(cb.equal(root.get("createdBy"), searchDTO.getCreatedBy()));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return productRepository.findAll(spec, pageable);
    }
    
    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
    
    @Override
    public Page<Product> getProductsByCategory(UUID categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable);
    }
    
    @Override
    public List<Product> getProductsByBrand(String brand) {
        return productRepository.findByBrand(brand);
    }
    
    @Override
    public List<Product> getProductsByStatus(String status) {
        return productRepository.findByStatus(status);
    }
    
    @Override
    public List<Product> getSparePartProducts() {
        return productRepository.findByIsSparePartTrue();
    }
    
    @Override
    public List<Product> getBundleProducts() {
        return productRepository.findByIsBundleTrue();
    }
    
    @Override
    public List<Product> getCustomizableProducts() {
        return productRepository.findByIsCustomizableTrue();
    }
    
    @Override
    public List<Product> getLowStockProducts() {
        return productRepository.findLowStockProducts();
    }
    
    @Override
    public List<Product> getOutOfStockProducts() {
        return productRepository.findOutOfStockProducts();
    }
    
    @Override
    public String generateProductCode() {
        String prefix = "PROD";
        String timestamp = String.valueOf(System.currentTimeMillis());
        return prefix + timestamp.substring(timestamp.length() - 8);
    }
    
    @Override
    public List<Product> importProducts(List<ProductDTO> productDTOs) {
        List<Product> products = new ArrayList<>();
        for (ProductDTO dto : productDTOs) {
            try {
                Product product = createProduct(dto);
                products.add(product);
            } catch (Exception e) {
                // 记录导入失败的数据
                System.err.println("导入产品失败: " + dto.getName() + ", 错误: " + e.getMessage());
            }
        }
        return products;
    }
    
    @Override
    public List<Product> exportProducts(ProductSearchDTO searchDTO) {
        // 使用分页查询所有数据
        Pageable pageable = Pageable.unpaged();
        Page<Product> page = searchProducts(searchDTO, pageable);
        return page.getContent();
    }
    
    @Override
    public ProductStatistics getProductStatistics() {
        ProductStatistics stats = new ProductStatistics();
        
        // 总数量
        stats.setTotalCount(productRepository.countActiveProducts());
        
        // 按状态统计
        List<Product> activeProducts = productRepository.findByStatus("ACTIVE");
        stats.setActiveCount((long) activeProducts.size());
        
        List<Product> inactiveProducts = productRepository.findByStatus("INACTIVE");
        stats.setInactiveCount((long) inactiveProducts.size());
        
        List<Product> discontinuedProducts = productRepository.findByStatus("DISCONTINUED");
        stats.setDiscontinuedCount((long) discontinuedProducts.size());
        
        // 按类型统计
        List<Product> sparePartProducts = productRepository.findByIsSparePartTrue();
        stats.setSparePartCount((long) sparePartProducts.size());
        
        List<Product> bundleProducts = productRepository.findByIsBundleTrue();
        stats.setBundleCount((long) bundleProducts.size());
        
        List<Product> customizableProducts = productRepository.findByIsCustomizableTrue();
        stats.setCustomizableCount((long) customizableProducts.size());
        
        // 库存统计
        List<Product> lowStockProducts = productRepository.findLowStockProducts();
        stats.setLowStockCount((long) lowStockProducts.size());
        
        List<Product> outOfStockProducts = productRepository.findOutOfStockProducts();
        stats.setOutOfStockCount((long) outOfStockProducts.size());
        
        return stats;
    }
    
    @Override
    public List<Product> checkDuplicates(String name, String code, String brand, String model) {
        List<Product> duplicates = new ArrayList<>();
        
        if (StringUtils.hasText(name)) {
            Optional<Product> byName = productRepository.findByName(name);
            if (byName.isPresent()) {
                duplicates.add(byName.get());
            }
        }
        
        if (StringUtils.hasText(code)) {
            Optional<Product> byCode = productRepository.findByCode(code);
            if (byCode.isPresent()) {
                duplicates.add(byCode.get());
            }
        }
        
        if (StringUtils.hasText(brand)) {
            List<Product> byBrand = productRepository.findByBrand(brand);
            duplicates.addAll(byBrand);
        }
        
        if (StringUtils.hasText(model)) {
            List<Product> byModel = productRepository.findByModel(model);
            duplicates.addAll(byModel);
        }
        
        return duplicates.stream().distinct().collect(Collectors.toList());
    }
    
    @Override
    public void updateProductStatus(UUID productId, String status) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("产品不存在，ID: " + productId));
        
        product.setStatus(status);
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);
    }
    
    @Override
    public void batchUpdateProductStatus(List<UUID> productIds, String status) {
        List<Product> products = productRepository.findAllById(productIds);
        for (Product product : products) {
            product.setStatus(status);
            product.setUpdatedAt(LocalDateTime.now());
        }
        productRepository.saveAll(products);
    }
}
