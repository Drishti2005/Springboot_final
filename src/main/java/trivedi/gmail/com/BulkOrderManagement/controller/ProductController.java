package trivedi.gmail.com.BulkOrderManagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import trivedi.gmail.com.BulkOrderManagement.entity.Product;
import trivedi.gmail.com.BulkOrderManagement.entity.TieredPrice;
import trivedi.gmail.com.BulkOrderManagement.exception.ResourceNotFoundException;
import trivedi.gmail.com.BulkOrderManagement.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product Catalog", description = "Product management and catalog browsing")
public class ProductController {

    private final ProductRepository productRepository;

    @GetMapping
    @Operation(summary = "List all products")
    public ResponseEntity<List<Product>> listAll() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id)));
    }

    @GetMapping("/sku/{sku}")
    @Operation(summary = "Get product by SKU")
    public ResponseEntity<Product> getBySku(@PathVariable String sku) {
        return ResponseEntity.ok(productRepository.findBySku(sku)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "sku", sku)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('WHOLESALER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a new product (Admin/Wholesaler only)")
    public ResponseEntity<Product> create(@Valid @RequestBody Product product) {
        // Detach tiers, save product first, then re-attach with proper FK
        List<TieredPrice> tiers = new ArrayList<>(product.getTieredPrices());
        product.getTieredPrices().clear();

        Product saved = productRepository.save(product);

        // Now wire each tier to the saved product
        for (TieredPrice tier : tiers) {
            tier.setProduct(saved);
            saved.getTieredPrices().add(tier);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WHOLESALER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update a product")
    public ResponseEntity<Product> update(@PathVariable Long id, @Valid @RequestBody Product updated) {
        Product existing = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        existing.setName(updated.getName());
        existing.setSku(updated.getSku());
        existing.setDescription(updated.getDescription());
        existing.setBasePrice(updated.getBasePrice());
        existing.setMoq(updated.getMoq());
        existing.setStockQuantity(updated.getStockQuantity());

        // Re-wire tiers
        existing.getTieredPrices().clear();
        for (TieredPrice tier : updated.getTieredPrices()) {
            tier.setProduct(existing);
            existing.getTieredPrices().add(tier);
        }

        return ResponseEntity.ok(productRepository.save(existing));
    }
}
