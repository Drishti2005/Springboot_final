package trivedi.gmail.com.BulkOrderManagement.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PriceQuoteRequest {

    @NotNull
    private Long productId;

    @Min(1)
    private int quantity;
}
