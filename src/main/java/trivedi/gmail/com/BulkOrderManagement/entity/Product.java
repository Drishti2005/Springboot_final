package trivedi.gmail.com.BulkOrderManagement.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a wholesale product with a Minimum Order Quantity (MOQ)
 * and a list of tiered pricing rules.
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String sku;

    private String description;

    /** Base / list price before any tier discount. */
    @NotNull
    @DecimalMin("0.01")
    @Column(name = "base_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice;

    /** Minimum Order Quantity — orders below this are rejected. */
    @Min(1)
    @Column(nullable = false)
    private int moq;

    /** Current stock level. */
    @Min(0)
    @Column(nullable = false)
    private int stockQuantity;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("minQuantity ASC")
    @JsonManagedReference
    @Builder.Default
    private List<TieredPrice> tieredPrices = new ArrayList<>();
}
