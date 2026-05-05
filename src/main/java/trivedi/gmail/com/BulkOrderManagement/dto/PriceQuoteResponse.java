package trivedi.gmail.com.BulkOrderManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PriceQuoteResponse {
    private Long productId;
    private String sku;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
    private String appliedTier;
}
