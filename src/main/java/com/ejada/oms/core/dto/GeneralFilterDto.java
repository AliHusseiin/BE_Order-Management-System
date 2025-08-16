package com.ejada.oms.core.dto;

import jakarta.validation.constraints.Min;
import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

/**
 *  filter DTO for dynamic filtering across all entities.
 * Supports multiple filter criteria, global search, and flexible sorting.
 * 
 * @author Ali Hussein
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeneralFilterDto {
    
    /**
     * Multiple filter criteria for precise filtering
     */
    @Builder.Default
    private List<FilterCriteria> filters = new ArrayList<>();
    
    /**
     * Global search term applied to searchable fields
     */
    private String globalSearch;
    
    /**
     * Pagination and sorting configuration
     */
    @Min(value = 0, message = "Page number must be 0 or greater")
    @Builder.Default
    private int page = 0;
    
    @Min(value = 1, message = "Page size must be 1 or greater")
    @Builder.Default
    private int size = 10;
    
    @Builder.Default
    private String sortBy = "id";
    
    @Builder.Default
    private String sortDirection = "desc";
    
    /**
     * Create Spring Data Pageable from filter parameters
     */
    public Pageable getPageable() {
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(page, size, Sort.by(new Sort.Order(direction, sortBy).ignoreCase()));
    }
    
    /**
     * Individual filter criterion with field, operator, and value
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FilterCriteria {
        
        /**
         * Entity field name to filter on
         */
        private String field;
        
        /**
         * Comparison operator
         */
        @Builder.Default
        private FilterOperator operator = FilterOperator.EQUALS;
        
        /**
         * Filter value
         */
        private String value;
        
        /**
         * Additional value for range operations
         */
        private String value2;
    }
    
    /**
     * Supported filter operators
     */
    public enum FilterOperator {
        EQUALS,
        NOT_EQUALS,
        CONTAINS,
        NOT_CONTAINS,
        STARTS_WITH,
        ENDS_WITH,
        GREATER_THAN,
        GREATER_THAN_OR_EQUAL,
        LESS_THAN,
        LESS_THAN_OR_EQUAL,
        BETWEEN,
        IN,
        NOT_IN,
        IS_NULL,
        IS_NOT_NULL,
        IS_TRUE,
        IS_FALSE
    }
}