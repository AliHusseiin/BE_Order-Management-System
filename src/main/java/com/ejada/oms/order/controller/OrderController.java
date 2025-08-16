package com.ejada.oms.order.controller;

import com.ejada.oms.core.config.properties.ApplicationProperties;
import com.ejada.oms.core.dto.ResponseDto;
import com.ejada.oms.core.dto.GeneralFilterDto;
import com.ejada.oms.order.dto.res.OrderResponse;
import com.ejada.oms.order.entity.Order;
import com.ejada.oms.order.dto.req.OrderCreateRequest;
import com.ejada.oms.order.mapper.OrderMapper;
import com.ejada.oms.order.service.OrderQueryService;
import com.ejada.oms.order.service.OrderCommandService;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/${app.api.version:v1}${app.api.endpoints.orders:/orders}")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Order Management", description = "Order management operations including CRUD, order approval workflow, and customer order tracking. All endpoints require ADMIN role.")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ADMIN')")
public class OrderController {

    private final OrderQueryService orderQueryService;
    private final OrderCommandService orderCommandService;
    private final OrderMapper orderMapper;
    private final ApplicationProperties applicationProperties;

    @GetMapping
    @Operation(
        summary = "Retrieve all orders with pagination and filtering",
        description = "Get paginated list of orders with advanced filtering and sorting capabilities. " +
                     "Supports filtering by order status, customer, date range, and total amount."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orders retrieved successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResponseDto.class),
                examples = @ExampleObject(value = """
                    {
                      "message": "Orders retrieved successfully",
                      "statusCode": 200,
                      "success": true,
                      "timestamp": "2025-08-15T18:50:59.240",
                      "data": {
                        "content": [{
                          "orderId": 1,
                          "customerId": 1,
                          "customerName": "John Doe",
                          "orderDate": "2025-08-15T10:30:00",
                          "status": "CONFIRMED",
                          "totalAmount": 1299.99,
                          "orderItems": [{
                            "productId": 1,
                            "productName": "Gaming Laptop",
                            "quantity": 1,
                            "unitPrice": 1299.99
                          }]
                        }],
                        "totalElements": 50,
                        "totalPages": 5,
                        "size": 10,
                        "number": 0
                      }
                    }"""))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<ResponseDto<Page<OrderResponse>>> getAll(GeneralFilterDto filterDto) {
        Page<Order> orders = orderQueryService.findAll(filterDto);
        Page<OrderResponse> response = orders.map(orderMapper::toResponse);
        return ResponseEntity.ok(ResponseDto.success(response, "Orders retrieved successfully"));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Retrieve order by ID",
        description = "Get detailed information about a specific order including all order items and customer details."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order found and retrieved successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResponseDto.class),
                examples = @ExampleObject(value = """
                    {
                      "message": "Order retrieved successfully",
                      "statusCode": 200,
                      "success": true,
                      "timestamp": "2025-08-15T18:50:59.240",
                      "data": {
                        "orderId": 1,
                        "customerId": 1,
                        "customerName": "John Doe",
                        "orderDate": "2025-08-15T10:30:00",
                        "status": "CONFIRMED",
                        "totalAmount": 1299.99,
                        "orderItems": [{
                          "productId": 1,
                          "productName": "Gaming Laptop",
                          "quantity": 1,
                          "unitPrice": 1299.99
                        }]
                      }
                    }"""))),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<ResponseDto<OrderResponse>> getById(
            @Parameter(description = "Order ID", required = true, example = "1")
            @PathVariable Long id) {
        Order order = orderCommandService.findById(id);
        return ResponseEntity.ok(ResponseDto.success(orderMapper.toResponse(order), "Order retrieved successfully"));
    }

    @PostMapping
    @Operation(
        summary = "Create a new order for customer",
        description = "**[Required API]** Admin creates a new order on behalf of a customer. " +
                     "Order is created in PENDING status and requires approval to be confirmed."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Order created successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResponseDto.class),
                examples = @ExampleObject(value = """
                    {
                      "message": "Order created successfully",
                      "statusCode": 201,
                      "success": true,
                      "timestamp": "2025-08-15T18:50:59.240",
                      "data": {
                        "orderId": 5,
                        "customerId": 2,
                        "customerName": "Jane Smith",
                        "orderDate": "2025-08-15T18:50:59",
                        "status": "PENDING",
                        "totalAmount": 159.98,
                        "orderItems": [{
                          "productId": 2,
                          "productName": "Gaming Mouse",
                          "quantity": 2,
                          "unitPrice": 79.99
                        }]
                      }
                    }"""))),
        @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input data or validation errors"),
        @ApiResponse(responseCode = "404", description = "Customer or Product not found"),
        @ApiResponse(responseCode = "409", description = "Conflict - Insufficient stock for requested products"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<ResponseDto<OrderResponse>> create(
            @RequestBody(description = "Order creation request", required = true,
                content = @Content(schema = @Schema(implementation = OrderCreateRequest.class),
                    examples = @ExampleObject(value = """
                        {
                          "customerId": 2,
                          "orderItems": [{
                            "productId": 2,
                            "quantity": 2
                          }]
                        }""")))
            @Valid @org.springframework.web.bind.annotation.RequestBody OrderCreateRequest request) {
        Order order = orderCommandService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.success(orderMapper.toResponse(order), "Order created successfully", HttpStatus.CREATED));
    }

    @PutMapping("/{id}/approve")
    @Operation(
        summary = "Approve order (PENDING → CONFIRMED)",
        description = "**[Required API]** Admin approves a pending order, changing its status from PENDING to CONFIRMED. " +
                     "Once approved, the order will generate an invoice automatically."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order approved successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResponseDto.class),
                examples = @ExampleObject(value = """
                    {
                      "message": "Order approved successfully",
                      "statusCode": 200,
                      "success": true,
                      "timestamp": "2025-08-15T18:50:59.240",
                      "data": {
                        "orderId": 5,
                        "customerId": 2,
                        "customerName": "Jane Smith",
                        "orderDate": "2025-08-15T18:50:59",
                        "status": "CONFIRMED",
                        "totalAmount": 159.98,
                        "orderItems": [{
                          "productId": 2,
                          "productName": "Gaming Mouse",
                          "quantity": 2,
                          "unitPrice": 79.99
                        }]
                      }
                    }"""))),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "400", description = "Bad Request - Order is not in PENDING status"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<ResponseDto<OrderResponse>> approve(
            @Parameter(description = "Order ID to approve", required = true, example = "1")
            @PathVariable Long id) {
        Order order = orderCommandService.approve(id);
        return ResponseEntity.ok(ResponseDto.success(orderMapper.toResponse(order), "Order approved successfully"));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(
        summary = "Retrieve orders of a specific customer",
        description = "**[Required API]** Get paginated list of orders for a specific customer. " +
                     "Returns all orders placed by the customer regardless of status."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer orders retrieved successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResponseDto.class),
                examples = @ExampleObject(value = """
                    {
                      "message": "Customer orders retrieved successfully",
                      "statusCode": 200,
                      "success": true,
                      "timestamp": "2025-08-15T18:50:59.240",
                      "data": {
                        "content": [{
                          "orderId": 1,
                          "customerId": 1,
                          "customerName": "John Doe",
                          "orderDate": "2025-08-15T10:30:00",
                          "status": "CONFIRMED",
                          "totalAmount": 1299.99,
                          "orderItems": [{
                            "productId": 1,
                            "productName": "Gaming Laptop",
                            "quantity": 1,
                            "unitPrice": 1299.99
                          }]
                        }],
                        "totalElements": 3,
                        "totalPages": 1,
                        "size": 10,
                        "number": 0
                      }
                    }"""))),
        @ApiResponse(responseCode = "404", description = "Customer not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<ResponseDto<Page<OrderResponse>>> getOrdersOfCustomer(
            @Parameter(description = "Customer ID", required = true, example = "1")
            @PathVariable Long customerId,
            
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") Integer page,
            
            @Parameter(description = "Number of items per page. If not provided, uses default from configuration", example = "20")
            @RequestParam(required = false) Integer size) {

        // Use configuration values with fallbacks
        int pageSize = size != null ? size : applicationProperties.getPagination().getDefaultPageSize();
        
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Order> orders = orderQueryService.findByCustomerId(customerId, pageable);
        Page<OrderResponse> response = orders.map(orderMapper::toResponse);
        return ResponseEntity.ok(ResponseDto.success(response, "Customer orders retrieved successfully"));
    }

}
