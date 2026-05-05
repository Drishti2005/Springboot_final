package trivedi.gmail.com.BulkOrderManagement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trivedi.gmail.com.BulkOrderManagement.dto.*;
import trivedi.gmail.com.BulkOrderManagement.entity.*;
import trivedi.gmail.com.BulkOrderManagement.exception.*;
import trivedi.gmail.com.BulkOrderManagement.repository.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckoutService {

    private final PricingEngine pricingEngine;
    private final InvoiceService invoiceService;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final RetailerProfileRepository retailerProfileRepository;
    private final jakarta.persistence.EntityManager entityManager;

    @Transactional
    public Order checkout(User retailer, CheckoutRequest request) {

        RetailerProfile profile = retailerProfileRepository.findByUserId(retailer.getId())
            .orElseThrow(() -> new ResourceNotFoundException("RetailerProfile", "userId", retailer.getId()));

        BigDecimal orderTotal = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();

        // ── Validate and price each item ─────────────────────────────────────
        for (CheckoutRequest.OrderItemRequest req : request.getItems()) {
            Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", req.getProductId()));

            if (product.getStockQuantity() < req.getQuantity()) {
                throw new InsufficientStockException(product.getSku(), req.getQuantity(), product.getStockQuantity());
            }

            PriceQuoteResponse quote = pricingEngine.calculatePrice(req.getProductId(), req.getQuantity());

            items.add(OrderItem.builder()
                .product(product)
                .quantity(req.getQuantity())
                .unitPrice(quote.getUnitPrice())
                .appliedTier(quote.getAppliedTier())
                .build());

            orderTotal = orderTotal.add(quote.getLineTotal());
        }

        // ── Credit limit check ────────────────────────────────────────────────
        BigDecimal projected = profile.getOutstandingBalance().add(orderTotal);
        if (projected.compareTo(profile.getCreditLimit()) > 0) {
            throw new CreditLimitExceededException(orderTotal, profile.getOutstandingBalance(), profile.getCreditLimit());
        }

        // ── Save order first (no items yet) ──────────────────────────────────
        Order order = Order.builder()
            .retailer(retailer)
            .status(OrderStatus.CONFIRMED)
            .totalAmount(orderTotal)
            .build();
        order = orderRepository.save(order);

        // ── Wire items to order and deduct stock ──────────────────────────────
        for (OrderItem item : items) {
            item.setOrder(order);
            order.getItems().add(item);
            Product p = item.getProduct();
            p.setStockQuantity(p.getStockQuantity() - item.getQuantity());
            productRepository.save(p);
        }
        order = orderRepository.save(order);

        // ── Update outstanding balance ─────────────────────────────────────────
        profile.setOutstandingBalance(projected);
        retailerProfileRepository.save(profile);

        // ── Flush so invoice FK is visible in same transaction ────────────────
        entityManager.flush();

        // ── Generate PDF invoice ──────────────────────────────────────────────
        invoiceService.generateInvoice(order);

        log.info("Checkout complete. Order #{} total: {}", order.getId(), orderTotal);
        return order;
    }
}
