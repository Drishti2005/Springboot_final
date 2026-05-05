package trivedi.gmail.com.BulkOrderManagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Financial profile for a Retailer.
 * Tracks credit limit and outstanding balance to gate checkout.
 */
@Entity
@Table(name = "retailer_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RetailerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @NotBlank
    @Column(name = "company_name", nullable = false)
    private String companyName;

    @NotBlank
    @Column(name = "gst_id", nullable = false, unique = true)
    private String gstId;

    /** Maximum credit extended to this retailer. */
    @NotNull
    @DecimalMin("0.00")
    @Column(name = "credit_limit", nullable = false, precision = 14, scale = 2)
    private BigDecimal creditLimit;

    /**
     * Sum of all unpaid invoices.
     * Checkout is blocked when (outstandingBalance + orderTotal) > creditLimit.
     */
    @NotNull
    @DecimalMin("0.00")
    @Column(name = "outstanding_balance", nullable = false, precision = 14, scale = 2)
    @Builder.Default
    private BigDecimal outstandingBalance = BigDecimal.ZERO;

    /** Billing address. */
    private String address;

    private String phone;
}
