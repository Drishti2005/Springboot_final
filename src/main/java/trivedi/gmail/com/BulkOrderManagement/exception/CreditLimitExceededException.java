package trivedi.gmail.com.BulkOrderManagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.math.BigDecimal;

/**
 * Thrown when a checkout would push the retailer over their credit limit.
 */
@ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
public class CreditLimitExceededException extends RuntimeException {

    public CreditLimitExceededException(BigDecimal orderTotal, BigDecimal outstanding, BigDecimal limit) {
        super(String.format(
            "Order total %.2f + outstanding balance %.2f = %.2f exceeds credit limit of %.2f.",
            orderTotal, outstanding, orderTotal.add(outstanding), limit
        ));
    }
}
