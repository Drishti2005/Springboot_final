package trivedi.gmail.com.BulkOrderManagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when an order quantity is below the product's MOQ.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MinimumOrderException extends RuntimeException {

    public MinimumOrderException(String sku, int requested, int moq) {
        super(String.format(
            "Order quantity %d for SKU '%s' is below the Minimum Order Quantity of %d.",
            requested, sku, moq
        ));
    }
}
