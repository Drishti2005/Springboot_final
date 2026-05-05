package trivedi.gmail.com.BulkOrderManagement.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * A single pricing tier for a product.
 * Example: buy >= 50 units → $9.50 each; buy >= 100 units → $8.75 each.
 */
@Entity
@Table(name = "tiered_prices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TieredPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference
    private Product product;

    /** Minimum quantity to qualify for this tier. */
    @Min(1)
    @Column(name = "min_quantity", nullable = false)
    private int minQuantity;

    /** Unit price at this tier. */
    @NotNull
    @DecimalMin("0.01")
    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    /** Human-readable label, e.g. "Tier 1", "Tier 2". */
    private String tierLabel;
}
