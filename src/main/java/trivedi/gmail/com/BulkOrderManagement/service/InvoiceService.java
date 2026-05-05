package trivedi.gmail.com.BulkOrderManagement.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trivedi.gmail.com.BulkOrderManagement.entity.*;
import trivedi.gmail.com.BulkOrderManagement.exception.ResourceNotFoundException;
import trivedi.gmail.com.BulkOrderManagement.repository.InvoiceRepository;
import trivedi.gmail.com.BulkOrderManagement.repository.RetailerProfileRepository;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceService {

    private static final BigDecimal GST_RATE = new BigDecimal("0.18");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");

    private final InvoiceRepository invoiceRepository;
    private final RetailerProfileRepository retailerProfileRepository;

    @Transactional
    public Invoice generateInvoice(Order order) {
        RetailerProfile profile = retailerProfileRepository.findByUserId(order.getRetailer().getId())
            .orElseThrow(() -> new ResourceNotFoundException("RetailerProfile", "userId", order.getRetailer().getId()));

        BigDecimal subtotal = order.getItems().stream()
            .map(OrderItem::getLineTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal gstAmount = subtotal.multiply(GST_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(gstAmount);

        byte[] pdfBytes = buildPdf(order, profile, subtotal, gstAmount, total);

        Invoice invoice = Invoice.builder()
            .order(order)
            .invoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .subtotal(subtotal)
            .gstRate(GST_RATE)
            .gstAmount(gstAmount)
            .totalAmount(total)
            .pdfData(pdfBytes)
            .build();

        return invoiceRepository.save(invoice);
    }

    private byte[] buildPdf(Order order, RetailerProfile profile,
                             BigDecimal subtotal, BigDecimal gstAmount, BigDecimal total) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // ── Header ──────────────────────────────────────────────────────
            document.add(new Paragraph("B2B WHOLESALE INVOICE")
                .setBold().setFontSize(20).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("─────────────────────────────────────────────────────")
                .setTextAlignment(TextAlignment.CENTER).setFontSize(10));
            document.add(new Paragraph(" "));

            // ── Invoice Meta ─────────────────────────────────────────────────
            document.add(new Paragraph("Invoice #: " + "INV-PENDING").setFontSize(11));
            document.add(new Paragraph("Order #: " + order.getId()).setFontSize(11));
            document.add(new Paragraph("Date: " + order.getOrderDate().format(DATE_FMT)).setFontSize(11));
            document.add(new Paragraph(" "));

            // ── Retailer Info ────────────────────────────────────────────────
            document.add(new Paragraph("Bill To:").setBold().setFontSize(12));
            document.add(new Paragraph(profile.getCompanyName()).setFontSize(11));
            document.add(new Paragraph("GST ID: " + profile.getGstId()).setFontSize(11));
            if (profile.getAddress() != null)
                document.add(new Paragraph("Address: " + profile.getAddress()).setFontSize(11));
            document.add(new Paragraph(" "));

            // ── Line Items Table ─────────────────────────────────────────────
            float[] colWidths = {3, 1, 2, 2, 2};
            Table table = new Table(UnitValue.createPercentArray(colWidths)).useAllAvailableWidth();

            String[] headers = {"Product (SKU)", "Qty", "Unit Price", "Tier Applied", "Line Total"};
            for (String h : headers) {
                table.addHeaderCell(new Cell()
                    .add(new Paragraph(h).setBold().setFontSize(10))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            }

            for (OrderItem item : order.getItems()) {
                table.addCell(new Cell().add(new Paragraph(
                    item.getProduct().getName() + "\n(" + item.getProduct().getSku() + ")")
                    .setFontSize(10)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getQuantity())).setFontSize(10)));
                table.addCell(new Cell().add(new Paragraph("Rs." + item.getUnitPrice()).setFontSize(10)));
                table.addCell(new Cell().add(new Paragraph(
                    item.getAppliedTier() != null ? item.getAppliedTier() : "Base").setFontSize(10)));
                table.addCell(new Cell().add(new Paragraph("Rs." + item.getLineTotal()).setFontSize(10)));
            }
            document.add(table);
            document.add(new Paragraph(" "));

            // ── Totals ───────────────────────────────────────────────────────
            document.add(new Paragraph("Subtotal:  Rs." + subtotal)
                .setTextAlignment(TextAlignment.RIGHT).setFontSize(11));
            document.add(new Paragraph("GST (18%): Rs." + gstAmount)
                .setTextAlignment(TextAlignment.RIGHT).setFontSize(11));
            document.add(new Paragraph("─────────────────────────────────────────────────────")
                .setTextAlignment(TextAlignment.RIGHT).setFontSize(10));
            document.add(new Paragraph("TOTAL:     Rs." + total)
                .setBold().setFontSize(14).setTextAlignment(TextAlignment.RIGHT));

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Thank you for your business!")
                .setTextAlignment(TextAlignment.CENTER).setFontSize(10)
                .setItalic());

            document.close();
        } catch (Exception e) {
            log.error("PDF generation failed for order {}: {}", order.getId(), e.getMessage(), e);
            throw new RuntimeException("PDF generation failed: " + e.getMessage(), e);
        }
        return baos.toByteArray();
    }
}
