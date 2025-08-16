package com.ejada.oms.product.controller;

import com.ejada.oms.core.dto.ResponseDto;
import com.ejada.oms.core.dto.GeneralFilterDto;
import com.ejada.oms.product.dto.res.ProductResponse;
import com.ejada.oms.product.entity.Product;
import com.ejada.oms.product.dto.req.ProductCreateRequest;
import com.ejada.oms.product.mapper.ProductMapper;
import com.ejada.oms.product.service.ProductQueryService;
import com.ejada.oms.product.service.ProductCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Product management controller for CRUD operations.
 * 
 * @author Ali Hussein
 */
@RestController
@RequestMapping("/${app.api.version:v1}${app.api.endpoints.products:/products}")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Product Management", description = "Product CRUD operations")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ADMIN')")
public class ProductController {

    private final ProductQueryService productQueryService;
    private final ProductCommandService productCommandService;
    private final ProductMapper productMapper;

    @GetMapping
    @Operation(summary = "Retrieve all products with filtering and pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<ResponseDto<Page<ProductResponse>>> getAll(GeneralFilterDto filterDto) {
        Page<Product> products = productQueryService.findAll(filterDto);
        Page<ProductResponse> response = products.map(productMapper::toResponse);
        return ResponseEntity.ok(ResponseDto.success(response, "Products retrieved successfully"));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Retrieve product by ID",
        description = "Get detailed information about a specific product using its unique identifier."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product found and retrieved successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResponseDto.class),
                examples = @ExampleObject(value = """
                    {
                      "message": "Product retrieved successfully",
                      "statusCode": 200,
                      "success": true,
                      "timestamp": "2025-08-15T18:50:59.240",
                      "data": {
                        "productId": 1,
                        "productName": "Gaming Laptop",
                        "description": "High-performance gaming laptop with RTX graphics",
                        "price": 1299.99,
                        "stockQuantity": 50,
                        "category": "Electronics"
                      }
                    }"""))),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<ResponseDto<ProductResponse>> getById(
            @Parameter(description = "Product ID", required = true, example = "1")
            @PathVariable Long id) {
        Product product = productCommandService.findById(id);
        return ResponseEntity.ok(ResponseDto.success(productMapper.toResponse(product), "Product retrieved successfully"));
    }

    @PostMapping
    @Operation(
        summary = "Create a new product",
        description = "Create a new product in the inventory system. All fields are required except description which is optional."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product created successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResponseDto.class),
                examples = @ExampleObject(value = """
                    {
                      "message": "Product created successfully",
                      "statusCode": 201,
                      "success": true,
                      "timestamp": "2025-08-15T18:50:59.240",
                      "data": {
                        "productId": 5,
                        "productName": "Gaming Mouse",
                        "description": "High precision gaming mouse with RGB lighting",
                        "price": 79.99,
                        "stockQuantity": 100,
                        "category": "Electronics"
                      }
                    }"""))),
        @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input data or validation errors"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<ResponseDto<ProductResponse>> create(
            @RequestBody(description = "Product creation request", required = true,
                content = @Content(schema = @Schema(implementation = ProductCreateRequest.class),
                    examples = @ExampleObject(value = """
                        {
                          "productName": "Gaming Mouse",
                          "description": "High precision gaming mouse with RGB lighting",
                          "price": 79.99,
                          "stockQuantity": 100,
                          "category": "Electronics"
                        }""")))
            @Valid @org.springframework.web.bind.annotation.RequestBody ProductCreateRequest request) {
        Product product = productCommandService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.success(productMapper.toResponse(product), "Product created successfully", HttpStatus.CREATED));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete a product",
        description = "Permanently delete a product from the inventory system. This action cannot be undone."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product deleted successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResponseDto.class),
                examples = @ExampleObject(value = """
                    {
                      "message": "Product deleted successfully",
                      "statusCode": 200,
                      "success": true,
                      "timestamp": "2025-08-15T18:50:59.240",
                      "data": null
                    }"""))),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required"),
        @ApiResponse(responseCode = "409", description = "Conflict - Product cannot be deleted (e.g., referenced in orders)")
    })
    public ResponseEntity<ResponseDto<Void>> delete(
            @Parameter(description = "Product ID to delete", required = true, example = "1")
            @PathVariable Long id) {
        productCommandService.delete(id);
        return ResponseEntity.ok(ResponseDto.success("Product deleted successfully"));
    }

}
