package com.ejada.oms.invoice.controller;

import com.ejada.oms.core.dto.ResponseDto;
import com.ejada.oms.core.dto.GeneralFilterDto;
import com.ejada.oms.invoice.dto.res.InvoiceResponse;
import com.ejada.oms.invoice.entity.Invoice;
import com.ejada.oms.invoice.mapper.InvoiceMapper;
import com.ejada.oms.invoice.service.InvoiceQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/${app.api.version:v1}${app.api.endpoints.invoices:/invoices}")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Invoice Management", description = "Invoice operations for retrieving and managing invoices generated from confirmed orders. All endpoints require ADMIN role.")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ADMIN')")
public class InvoiceController {

    private final InvoiceQueryService invoiceQueryService;
    private final InvoiceMapper invoiceMapper;

    @GetMapping
    @Operation(
        summary = "Retrieve all invoices with pagination and filtering",
        description = "Get paginated list of invoices with advanced filtering and sorting capabilities. " +
                     "Supports filtering by invoice date, amount range, customer, and order ID."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Invoices retrieved successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResponseDto.class),
                examples = @ExampleObject(value = """
                    {
                      "message": "Invoices retrieved successfully",
                      "statusCode": 200,
                      "success": true,
                      "timestamp": "2025-08-15T18:50:59.240",
                      "data": {
                        "content": [{
                          "invoiceId": 1,
                          "invoiceNumber": "INV-2025-001",
                          "orderId": 1,
                          "customerId": 1,
                          "customerName": "John Doe",
                          "invoiceDate": "2025-08-15T11:00:00",
                          "totalAmount": 1299.99,
                          "taxAmount": 169.99,
                          "netAmount": 1129.99,
                          "status": "GENERATED"
                        }],
                        "totalElements": 45,
                        "totalPages": 5,
                        "size": 10,
                        "number": 0
                      }
                    }"""))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<ResponseDto<Page<InvoiceResponse>>> getAll(GeneralFilterDto filterDto) {
        Page<Invoice> invoices = invoiceQueryService.findAll(filterDto);
        Page<InvoiceResponse> response = invoices.map(invoiceMapper::toResponse);
        return ResponseEntity.ok(ResponseDto.success(response, "Invoices retrieved successfully"));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Retrieve invoice by ID",
        description = "Get detailed information about a specific invoice including all line items and tax calculations."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Invoice found and retrieved successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResponseDto.class),
                examples = @ExampleObject(value = """
                    {
                      "message": "Invoice retrieved successfully",
                      "statusCode": 200,
                      "success": true,
                      "timestamp": "2025-08-15T18:50:59.240",
                      "data": {
                        "invoiceId": 1,
                        "invoiceNumber": "INV-2025-001",
                        "orderId": 1,
                        "customerId": 1,
                        "customerName": "John Doe",
                        "invoiceDate": "2025-08-15T11:00:00",
                        "totalAmount": 1299.99,
                        "taxAmount": 169.99,
                        "netAmount": 1129.99,
                        "status": "GENERATED",
                        "invoiceItems": [{
                          "productId": 1,
                          "productName": "Gaming Laptop",
                          "quantity": 1,
                          "unitPrice": 1299.99,
                          "lineTotal": 1299.99
                        }]
                      }
                    }"""))),
        @ApiResponse(responseCode = "404", description = "Invoice not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<ResponseDto<InvoiceResponse>> getById(
            @Parameter(description = "Invoice ID", required = true, example = "1")
            @PathVariable Long id) {
        Invoice invoice = invoiceQueryService.findById(id);
        return ResponseEntity.ok(ResponseDto.success(invoiceMapper.toResponse(invoice), "Invoice retrieved successfully"));
    }

    @GetMapping("/order/{orderId}")
    @Operation(
        summary = "Retrieve invoice for a specific order",
        description = "**[Required API]** Get the invoice associated with a specific order. " +
                     "Invoices are automatically generated when orders are approved/confirmed."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order invoice retrieved successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResponseDto.class),
                examples = @ExampleObject(value = """
                    {
                      "message": "Order invoice retrieved successfully",
                      "statusCode": 200,
                      "success": true,
                      "timestamp": "2025-08-15T18:50:59.240",
                      "data": {
                        "invoiceId": 1,
                        "invoiceNumber": "INV-2025-001",
                        "orderId": 1,
                        "customerId": 1,
                        "customerName": "John Doe",
                        "invoiceDate": "2025-08-15T11:00:00",
                        "totalAmount": 1299.99,
                        "taxAmount": 169.99,
                        "netAmount": 1129.99,
                        "status": "GENERATED",
                        "invoiceItems": [{
                          "productId": 1,
                          "productName": "Gaming Laptop",
                          "quantity": 1,
                          "unitPrice": 1299.99,
                          "lineTotal": 1299.99
                        }]
                      }
                    }"""))),
        @ApiResponse(responseCode = "404", description = "Order not found or invoice not generated for this order"),
        @ApiResponse(responseCode = "400", description = "Bad Request - Order is not in confirmed status"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<ResponseDto<InvoiceResponse>> getInvoiceOfOrder(
            @Parameter(description = "Order ID", required = true, example = "1")
            @PathVariable Long orderId) {
        Invoice invoice = invoiceQueryService.findByOrderId(orderId);
        return ResponseEntity.ok(ResponseDto.success(invoiceMapper.toResponse(invoice), "Order invoice retrieved successfully"));
    }
}