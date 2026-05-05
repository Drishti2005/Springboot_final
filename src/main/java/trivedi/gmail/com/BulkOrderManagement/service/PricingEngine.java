package trivedi.gmail.com.BulkOrderManagement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import trivedi.gmail.com.BulkOrderManagement.dto.PriceQuoteResponse;
import trivedi.gmail.com.BulkOrderManagement.entity.Product;
import trivedi.gmail.com.BulkOrderManagement.entity.TieredPrice;
import trivedi.gmail.com.BulkOrderManagement.exception.MinimumOrderException;
import trivedi.gmail.com.BulkOrderManagement.exception.ResourceNotFoundException;
import trivedi.gmail.com.BulkOrderManagement.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

/**
 * Member 1 — Core Pricing Engine
 *
 * Resolves the correct unit price for a given (productId, quantity) pair
 * by walking the tiered price ladder from highest to lowest threshold.
 *
 * Rules:
 *  1. quantity < MOQ  → throw MinimumOrderException
 *  2. Walk tiers (sorted DESC by minQuantity); first tier where quantity >= minQuantity wins.
 *  3. No tier matched → fall back to product.basePrice.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PricingEngine {

    private final ProductRepository productRepository;

    /**
     * Calculate the unit price for a given product and quantity.
     *
     * @param productId target product
     * @param quantity  requested quantity
     * @return a fully populated PriceQuoteResponse
     */
    public PriceQuoteResponse calculatePrice(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        // ── MOQ check ────────────────────────────────────────────────────────
        if (quantity < product.getMoq()) {
            throw new MinimumOrderException(product.getSku(), quantity, product.getMoq());
        }

        // ── Tier resolution (highest threshold first) ────────────────────────
        List<TieredPrice> tiers = product.getTieredPrices().stream()
            .sorted(Comparator.comparingInt(TieredPrice::getMinQuantity).reversed())
            .toList();

        BigDecimal unitPrice = product.getBasePrice();
        String appliedTier  = "Base Price";

        for (TieredPrice tier : tiers) {
            if (quantity >= tier.getMinQuantity()) {
                unitPrice   = tier.getUnitPrice();
                appliedTier = tier.getTierLabel() != null ? tier.getTierLabel()
                                                          : "Min " + tier.getMinQuantity() + " units";
                log.debug("Tier matched for product {}: {} @ {}", productId, appliedTier, unitPrice);
                break;
            }
        }

        BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));

        return new PriceQuoteResponse(
            product.getId(),
            product.getSku(),
            quantity,
            unitPrice,
            lineTotal,
            appliedTier
        );
    }
}
