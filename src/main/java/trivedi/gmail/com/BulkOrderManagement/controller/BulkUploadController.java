package trivedi.gmail.com.BulkOrderManagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import trivedi.gmail.com.BulkOrderManagement.dto.BulkUploadResult;
import trivedi.gmail.com.BulkOrderManagement.entity.User;
import trivedi.gmail.com.BulkOrderManagement.service.BulkUploadService;

@RestController
@RequestMapping("/api/bulk")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Bulk Upload", description = "CSV bulk order upload for Retailers")
public class BulkUploadController {

    private final BulkUploadService bulkUploadService;

    /**
     * POST /api/bulk/upload
     * Accepts a CSV file with columns: SKU, Quantity
     * Validates SKUs, stock, and MOQ, then places an order for all valid rows.
     */
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    @Operation(summary = "Upload a CSV file to bulk-create an order",
               description = "CSV format: SKU (column 1), Quantity (column 2). First row is treated as header.")
    public ResponseEntity<BulkUploadResult> uploadCsv(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User retailer) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(
                BulkUploadResult.builder()
                    .totalRows(0).successCount(0).failureCount(0)
                    .errors(java.util.List.of("Uploaded file is empty"))
                    .build()
            );
        }

        BulkUploadResult result = bulkUploadService.processCsvUpload(file, retailer);
        return ResponseEntity.ok(result);
    }
}
