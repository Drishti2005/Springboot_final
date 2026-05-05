package trivedi.gmail.com.BulkOrderManagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import trivedi.gmail.com.BulkOrderManagement.dto.CheckoutRequest;
import trivedi.gmail.com.BulkOrderManagement.dto.OrderResponse;
import trivedi.gmail.com.BulkOrderManagement.entity.*;
import trivedi.gmail.com.BulkOrderManagement.exception.ResourceNotFoundException;
import trivedi.gmail.com.BulkOrderManagement.repository.*;
import trivedi.gmail.com.BulkOrderManagement.service.CheckoutService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Orders", description = "Order placement, history, and quick re-order")
public class OrderController {

    private final CheckoutService checkoutService;
    private final OrderRepository orderRepository;
    private final InvoiceRepository invoiceRepository;

    @PostMapping("/checkout")
    @Operation(summary = "Place an order")
    public ResponseEntity<OrderResponse> checkout(
            @AuthenticationPrincipal User retailer,
            @Valid @RequestBody CheckoutRequest request) {
        Order order = checkoutService.checkout(retailer, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(order));
    }

    @GetMapping
    @Operation(summary = "Get order history")
    public ResponseEntity<List<OrderResponse>> myOrders(@AuthenticationPrincipal User retailer) {
        List<Order> orders = orderRepository.findByRetailerIdOrderByOrderDateDesc(retailer.getId());
        return ResponseEntity.ok(orders.stream().map(OrderResponse::from).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a specific order")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        return ResponseEntity.ok(OrderResponse.from(order));
    }

    @GetMapping("/last")
    @Operation(summary = "Get last order for Quick Re-order")
    public ResponseEntity<OrderResponse> lastOrder(@AuthenticationPrincipal User retailer) {
        Order order = orderRepository.findLastOrderByRetailerId(retailer.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Order", "retailerId", retailer.getId()));
        return ResponseEntity.ok(OrderResponse.from(order));
    }

    @GetMapping("/{id}/invoice")
    @Operation(summary = "Download PDF invoice")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long id) {
        Invoice invoice = invoiceRepository.findByOrderId(id)
            .orElseThrow(() -> new ResourceNotFoundException("Invoice", "orderId", id));
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + invoice.getInvoiceNumber() + ".pdf\"")
            .body(invoice.getPdfData());
    }
}
