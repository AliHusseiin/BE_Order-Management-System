package com.ejada.oms.customer.controller;

import com.ejada.oms.core.dto.ResponseDto;
import com.ejada.oms.core.dto.GeneralFilterDto;
import com.ejada.oms.customer.dto.req.CustomerCreateRequest;
import com.ejada.oms.customer.dto.res.CustomerResponse;
import com.ejada.oms.customer.entity.Customer;
import com.ejada.oms.customer.mapper.CustomerMapper;
import com.ejada.oms.customer.service.CustomerQueryService;
import com.ejada.oms.customer.service.CustomerCommandService;
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

@RestController
@RequestMapping("/${app.api.version:v1}${app.api.endpoints.customers:/customers}")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Customer Management", description = "Customer CRUD operations for managing customer data. All endpoints require ADMIN role.")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ADMIN')")
public class CustomerController {

    private final CustomerQueryService customerQueryService;
    private final CustomerCommandService customerCommandService;
    private final CustomerMapper customerMapper;

    @GetMapping
    @Operation(summary = "Retrieve all customers with filtering and pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customers retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<ResponseDto<Page<CustomerResponse>>> getAll(GeneralFilterDto filterDto) {
        Page<Customer> customers = customerQueryService.findAll(filterDto);
        Page<CustomerResponse> response = customers.map(customerMapper::toResponse);
        return ResponseEntity.ok(ResponseDto.success(response, "Customers retrieved successfully"));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Retrieve customer by ID",
        description = "Get detailed information about a specific customer using their unique identifier."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer found and retrieved successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResponseDto.class),
                examples = @ExampleObject(value = """
                    {
                      "message": "Customer retrieved successfully",
                      "statusCode": 200,
                      "success": true,
                      "timestamp": "2025-08-15T18:50:59.240",
                      "data": {
                        "customerId": 1,
                        "customerName": "John Doe",
                        "email": "john.doe@example.com",
                        "phoneNumber": "+1234567890",
                        "address": "123 Main St, City, Country"
                      }
                    }"""))),
        @ApiResponse(responseCode = "404", description = "Customer not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<ResponseDto<CustomerResponse>> getById(
            @Parameter(description = "Customer ID", required = true, example = "1")
            @PathVariable Long id) {
        Customer customer = customerCommandService.findById(id);
        return ResponseEntity.ok(ResponseDto.success(customerMapper.toResponse(customer), "Customer retrieved successfully"));
    }

    @PostMapping
    @Operation(
        summary = "Create a new customer",
        description = "Create a new customer in the system. All fields are required except address which is optional."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Customer created successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResponseDto.class),
                examples = @ExampleObject(value = """
                    {
                      "message": "Customer created successfully",
                      "statusCode": 201,
                      "success": true,
                      "timestamp": "2025-08-15T18:50:59.240",
                      "data": {
                        "customerId": 5,
                        "customerName": "Jane Smith",
                        "email": "jane.smith@example.com",
                        "phoneNumber": "+1987654321",
                        "address": "456 Oak Ave, Town, Country"
                      }
                    }"""))),
        @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input data or validation errors"),
        @ApiResponse(responseCode = "409", description = "Conflict - Customer with this email already exists"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<ResponseDto<CustomerResponse>> create(
            @RequestBody(description = "Customer creation request", required = true,
                content = @Content(schema = @Schema(implementation = CustomerCreateRequest.class),
                    examples = @ExampleObject(value = """
                        {
                          "customerName": "Jane Smith",
                          "email": "jane.smith@example.com",
                          "phoneNumber": "+1987654321",
                          "address": "456 Oak Ave, Town, Country"
                        }""")))
            @Valid @org.springframework.web.bind.annotation.RequestBody CustomerCreateRequest request) {
        Customer customer = customerCommandService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.success(customerMapper.toResponse(customer), "Customer created successfully", HttpStatus.CREATED));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete a customer",
        description = "Permanently delete a customer from the system. This action cannot be undone. " +
                     "Note: Customer cannot be deleted if they have associated orders."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer deleted successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResponseDto.class),
                examples = @ExampleObject(value = """
                    {
                      "message": "Customer deleted successfully",
                      "statusCode": 200,
                      "success": true,
                      "timestamp": "2025-08-15T18:50:59.240",
                      "data": null
                    }"""))),
        @ApiResponse(responseCode = "404", description = "Customer not found"),
        @ApiResponse(responseCode = "409", description = "Conflict - Customer cannot be deleted (has associated orders)"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<ResponseDto<Void>> delete(
            @Parameter(description = "Customer ID to delete", required = true, example = "1")
            @PathVariable Long id) {
        customerCommandService.delete(id);
        return ResponseEntity.ok(ResponseDto.success("Customer deleted successfully"));
    }

}
