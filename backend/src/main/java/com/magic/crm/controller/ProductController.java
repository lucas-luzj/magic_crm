package com.magic.crm.controller;

import com.magic.crm.dto.ProductDTO;
import com.magic.crm.dto.ProductSearchDTO;
import com.magic.crm.entity.Product;
import com.magic.crm.service.ProductService;
import com.magic.crm.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * 产品管理控制器
 */
@RestController
@RequestMapping("/api/products")
@Tag(name = "产品管理")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @PostMapping
    @Operation(summary = "创建产品")
    @PreAuthorize("hasAuthority('product:create')")
    public ApiResponse<Product> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        Product product = productService.createProduct(productDTO);
        return ApiResponse.success(product, "产品创建成功");
    }
    
    @PutMapping("/{id}")
    @Operation(summary("更新产品")
    @PreAuthorize("hasAuthority('product:update')")
    public ApiResponse<Product> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody ProductDTO productDTO) {
        Product product = productService.updateProduct(id, productDTO);
        return ApiResponse.success(product, "产品更新成功");
    }
    
    @GetMapping("/{id}")
    @Operation(summary("获取产品详情")
    @PreAuthorize("hasAuthority('product:view')")
    public ApiResponse<Product> getProduct(@PathVariable UUID id) {
        Product product = productService.getProductById(id);
        return ApiResponse.success(product);
    }
    
    @GetMapping("/code/{code}")
    @Operation(summary("根据编码获取产品")
    @PreAuthorize("hasAuthority('product:view')")
    public ApiResponse<Product> getProductByCode(@PathVariable String code) {
        Product product = productService.getProductByCode(code);
        return ApiResponse.success(product);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary("删除产品")
    @PreAuthorize("hasAuthority('product:delete')")
    public ApiResponse<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ApiResponse.success(null, "产品删除成功");
    }
    
    @DeleteMapping("/batch")
    @Operation(summary("批量删除产品")
    @PreAuthorize("hasAuthority('product:delete')")
    public ApiResponse<Void> deleteProducts(@RequestBody List<UUID> ids) {
        productService.deleteProducts(ids);
        return ApiResponse.success(null, "产品批量删除成功");
    }
    
    @PostMapping("/search")
    @Operation(summary("搜索产品")
    @PreAuthorize("hasAuthority('product:view')")
    public ApiResponse<Page<Product>> searchProducts(
            @RequestBody ProductSearchDTO searchDTO,
            Pageable pageable) {
        Page<Product> products = productService.searchProducts(searchDTO, pageable);
        return ApiResponse.success(products);
    }
    
    @GetMapping
    @Operation(summary("获取所有产品")
    @PreAuthorize("hasAuthority('product:view')")
    public ApiResponse<Page<Product>> getAllProducts(Pageable pageable) {
        Page<Product> products = productService.getAllProducts(pageable);
        return ApiResponse.success(products);
    }
    
    @GetMapping("/category/{categoryId}")
    @Operation(summary("根据分类获取产品")
    @PreAuthorize("hasAuthority('product:view')")
    public ApiResponse<Page<Product>> getProductsByCategory(
            @PathVariable UUID categoryId,
            Pageable pageable) {
        Page<Product> products = productService.getProductsByCategory(categoryId, pageable);
        return ApiResponse.success(products);
    }
    
    @GetMapping("/brand/{brand}")
    @Operation(summary("根据品牌获取产品")
    @PreAuthorize("hasAuthority('product:view')")
    public ApiResponse<List<Product>> getProductsByBrand(@PathVariable String brand) {
        List<Product> products = productService.getProductsByBrand(brand);
        return ApiResponse.success(products);
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary("根据状态获取产品")
    @PreAuthorize("hasAuthority('product:view')")
    public ApiResponse<List<Product>> getProductsByStatus(@PathVariable String status) {
        List<Product> products = productService.getProductsByStatus(status);
        return ApiResponse.success(products);
    }
    
    @GetMapping("/spare-parts")
    @Operation(summary("获取备件产品")
    @PreAuthorize("hasAuthority('product:view')")
    public ApiResponse<List<Product>> getSparePartProducts() {
        List<Product> products = productService.getSparePartProducts();
        return ApiResponse.success(products);
    }
    
    @GetMapping("/bundles")
    @Operation(summary("获取套餐产品")
    @PreAuthorize("hasAuthority('product:view')")
    public ApiResponse<List<Product>> getBundleProducts() {
        List<Product> products = productService.getBundleProducts();
        return ApiResponse.success(products);
    }
    
    @GetMapping("/customizable")
    @Operation(summary("获取可定制产品")
    @PreAuthorize("hasAuthority('product:view')")
    public ApiResponse<List<Product>> getCustomizableProducts() {
        List<Product> products = productService.getCustomizableProducts();
        return ApiResponse.success(products);
    }
    
    @GetMapping("/low-stock")
    @Operation(summary("获取低库存产品")
    @PreAuthorize("hasAuthority('product:view')")
    public ApiResponse<List<Product>> getLowStockProducts() {
        List<Product> products = productService.getLowStockProducts();
        return ApiResponse.success(products);
    }
    
    @GetMapping("/out-of-stock")
    @Operation(summary("获取缺货产品")
    @PreAuthorize("hasAuthority('product:view')")
    public ApiResponse<List<Product>> getOutOfStockProducts() {
        List<Product> products = productService.getOutOfStockProducts();
        return ApiResponse.success(products);
    }
    
    @GetMapping("/generate-code")
    @Operation(summary("生成产品编码")
    @PreAuthorize("hasAuthority('product:create')")
    public ApiResponse<String> generateProductCode() {
        String code = productService.generateProductCode();
        return ApiResponse.success(code);
    }
    
    @PostMapping("/import")
    @Operation(summary("导入产品")
    @PreAuthorize("hasAuthority('product:create')")
    public ApiResponse<List<Product>> importProducts(@RequestBody List<ProductDTO> productDTOs) {
        List<Product> products = productService.importProducts(productDTOs);
        return ApiResponse.success(products, "产品导入成功");
    }
    
    @PostMapping("/export")
    @Operation(summary("导出产品")
    @PreAuthorize("hasAuthority('product:view')")
    public ApiResponse<List<Product>> exportProducts(@RequestBody ProductSearchDTO searchDTO) {
        List<Product> products = productService.exportProducts(searchDTO);
        return ApiResponse.success(products);
    }
    
    @GetMapping("/statistics")
    @Operation(summary("获取产品统计信息")
    @PreAuthorize("hasAuthority('product:view')")
    public ApiResponse<ProductService.ProductStatistics> getProductStatistics() {
        ProductService.ProductStatistics statistics = productService.getProductStatistics();
        return ApiResponse.success(statistics);
    }
    
    @PostMapping("/check-duplicates")
    @Operation(summary("检查产品重复")
    @PreAuthorize("hasAuthority('product:view')")
    public ApiResponse<List<Product>> checkDuplicates(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model) {
        List<Product> duplicates = productService.checkDuplicates(name, code, brand, model);
        return ApiResponse.success(duplicates);
    }
    
    @PutMapping("/{id}/status")
    @Operation(summary("更新产品状态")
    @PreAuthorize("hasAuthority('product:update')")
    public ApiResponse<Void> updateProductStatus(
            @PathVariable UUID id,
            @RequestParam String status) {
        productService.updateProductStatus(id, status);
        return ApiResponse.success(null, "产品状态更新成功");
    }
    
    @PutMapping("/batch/status")
    @Operation(summary("批量更新产品状态")
    @PreAuthorize("hasAuthority('product:update')")
    public ApiResponse<Void> batchUpdateProductStatus(
            @RequestBody List<UUID> productIds,
            @RequestParam String status) {
        productService.batchUpdateProductStatus(productIds, status);
        return ApiResponse.success(null, "产品状态批量更新成功");
    }
}
