package trivedi.gmail.com.BulkOrderManagement.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import trivedi.gmail.com.BulkOrderManagement.dto.*;
import trivedi.gmail.com.BulkOrderManagement.entity.*;
import trivedi.gmail.com.BulkOrderManagement.repository.*;

import java.io.*;
import java.util.*;

/**
 * Member 3 — Bulk CSV Upload
 *
 * Accepts a CSV with columns: SKU, Quantity
 * Validates:
 *  - SKU exists in the product catalog
 *  - Requested quantity <= stock
 *  - Quantity >= MOQ
 * Builds a CheckoutRequest from all valid rows and delegates to CheckoutService.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BulkUploadService {

    private final ProductRepository productRepository;
    private final CheckoutService checkoutService;

    @Transactional
    public BulkUploadResult processCsvUpload(MultipartFile file, User retailer) {
        List<String> errors = new ArrayList<>();
        List<CheckoutRequest.OrderItemRequest> validItems = new ArrayList<>();
        int totalRows = 0;

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] header = reader.readNext(); // skip header row
            if (header == null) {
                return BulkUploadResult.builder()
                    .totalRows(0).successCount(0).failureCount(0)
                    .errors(List.of("CSV file is empty")).build();
            }

            String[] row;
            int lineNum = 1;

            while ((row = reader.readNext()) != null) {
                lineNum++;
                totalRows++;

                if (row.length < 2) {
                    errors.add("Line " + lineNum + ": insufficient columns (expected SKU, Quantity)");
                    continue;
                }

                String sku = row[0].trim();
                String qtyStr = row[1].trim();

                // Parse quantity
                int quantity;
                try {
                    quantity = Integer.parseInt(qtyStr);
                    if (quantity <= 0) throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    errors.add("Line " + lineNum + ": invalid quantity '" + qtyStr + "' for SKU " + sku);
                    continue;
                }

                // Validate SKU
                Optional<Product> productOpt = productRepository.findBySku(sku);
                if (productOpt.isEmpty()) {
                    errors.add("Line " + lineNum + ": SKU '" + sku + "' not found in catalog");
                    continue;
                }

                Product product = productOpt.get();

                // MOQ check
                if (quantity < product.getMoq()) {
                    errors.add("Line " + lineNum + ": quantity " + quantity
                        + " for SKU '" + sku + "' is below MOQ of " + product.getMoq());
                    continue;
                }

                // Stock check
                if (product.getStockQuantity() < quantity) {
                    errors.add("Line " + lineNum + ": insufficient stock for SKU '" + sku
                        + "'. Requested: " + quantity + ", Available: " + product.getStockQuantity());
                    continue;
                }

                CheckoutRequest.OrderItemRequest item = new CheckoutRequest.OrderItemRequest();
                item.setProductId(product.getId());
                item.setQuantity(quantity);
                validItems.add(item);
            }

        } catch (IOException | CsvValidationException e) {
            log.error("CSV parsing error", e);
            return BulkUploadResult.builder()
                .totalRows(totalRows).successCount(0).failureCount(totalRows)
                .errors(List.of("Failed to parse CSV: " + e.getMessage())).build();
        }

        if (validItems.isEmpty()) {
            return BulkUploadResult.builder()
                .totalRows(totalRows).successCount(0).failureCount(totalRows)
                .errors(errors).build();
        }

        // Delegate to CheckoutService
        CheckoutRequest checkoutRequest = new CheckoutRequest();
        checkoutRequest.setItems(validItems);
        Order order = checkoutService.checkout(retailer, checkoutRequest);

        int successCount = validItems.size();
        int failureCount = totalRows - successCount;

        return BulkUploadResult.builder()
            .totalRows(totalRows)
            .successCount(successCount)
            .failureCount(failureCount)
            .errors(errors)
            .orderId(order.getId())
            .build();
    }
}
