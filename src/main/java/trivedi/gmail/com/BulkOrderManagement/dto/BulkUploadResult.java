package trivedi.gmail.com.BulkOrderManagement.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BulkUploadResult {
    private int totalRows;
    private int successCount;
    private int failureCount;
    private List<String> errors;
    private Long orderId;
}
