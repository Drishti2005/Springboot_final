package trivedi.gmail.com.BulkOrderManagement.dto;

import lombok.Data;
import trivedi.gmail.com.BulkOrderManagement.entity.Order;
import trivedi.gmail.com.BulkOrderManagement.entity.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class OrderResponse {
    private Long id;
    private String status;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private List<ItemDto> items;

    @Data
    public static class ItemDto {
        private Long id;
        private ProductDto product;
        private int quantity;
        private BigDecimal unitPrice;
        private String appliedTier;
        private BigDecimal lineTotal;
    }

    @Data
    public static class ProductDto {
        private Long id;
        private String name;
        private String sku;
    }

    public static OrderResponse from(Order order) {
        OrderResponse r = new OrderResponse();
        r.setId(order.getId());
        r.setStatus(order.getStatus().name());
        r.setOrderDate(order.getOrderDate());
        r.setTotalAmount(order.getTotalAmount());
        r.setItems(order.getItems().stream().map(item -> {
            ItemDto dto = new ItemDto();
            dto.setId(item.getId());
            dto.setQuantity(item.getQuantity());
            dto.setUnitPrice(item.getUnitPrice());
            dto.setAppliedTier(item.getAppliedTier());
            dto.setLineTotal(item.getLineTotal());
            ProductDto p = new ProductDto();
            p.setId(item.getProduct().getId());
            p.setName(item.getProduct().getName());
            p.setSku(item.getProduct().getSku());
            dto.setProduct(p);
            return dto;
        }).collect(Collectors.toList()));
        return r;
    }
}
