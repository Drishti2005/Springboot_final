package trivedi.gmail.com.BulkOrderManagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trivedi.gmail.com.BulkOrderManagement.dto.*;
import trivedi.gmail.com.BulkOrderManagement.service.PricingEngine;

@RestController
@RequestMapping("/api/pricing")
@RequiredArgsConstructor
@Tag(name = "Pricing Engine", description = "Tiered price calculation — blocked for unapproved Retailers")
@SecurityRequirement(name = "bearerAuth")
public class PricingController {

    private final PricingEngine pricingEngine;

    @PostMapping("/quote")
    @Operation(summary = "Get a price quote for a product and quantity")
    public ResponseEntity<PriceQuoteResponse> getQuote(@Valid @RequestBody PriceQuoteRequest request) {
        return ResponseEntity.ok(pricingEngine.calculatePrice(request.getProductId(), request.getQuantity()));
    }

    @GetMapping("/quote")
    @Operation(summary = "Get a price quote via query params")
    public ResponseEntity<PriceQuoteResponse> getQuoteParams(
            @RequestParam Long productId,
            @RequestParam int quantity) {
        return ResponseEntity.ok(pricingEngine.calculatePrice(productId, quantity));
    }
}
