package com.ejada.oms.core.specification;

import com.ejada.oms.core.dto.GeneralFilterDto;
import com.ejada.oms.core.dto.GeneralFilterDto.FilterCriteria;
import com.ejada.oms.core.dto.GeneralFilterDto.FilterOperator;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Dynamic specification builder for creating JPA specifications from GeneralFilterDto.
 * Supports type-safe filtering across all entity types using metamodel.
 * 
 * @author Ali Hussein
 */
@Slf4j
public class DynamicSpecificationBuilder {

    /**
     * Build specification from GeneralFilterDto
     */
    public static <T> Specification<T> buildSpecification(GeneralFilterDto filterDto, Class<T> entityClass) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            
            // Apply individual filter criteria
            if (filterDto.getFilters() != null) {
                for (FilterCriteria criteria : filterDto.getFilters()) {
                    Predicate criterionPredicate = buildCriterionPredicate(criteria, root, query, criteriaBuilder);
                    if (criterionPredicate != null) {
                        predicate = criteriaBuilder.and(predicate, criterionPredicate);
                    }
                }
            }
            
            // Apply global search if specified
            if (StringUtils.hasText(filterDto.getGlobalSearch())) {
                Predicate globalSearchPredicate = buildGlobalSearchPredicate(
                    filterDto.getGlobalSearch(), root, query, criteriaBuilder, entityClass);
                if (globalSearchPredicate != null) {
                    predicate = criteriaBuilder.and(predicate, globalSearchPredicate);
                }
            }
            
            return predicate;
        };
    }
    
    /**
     * Build predicate for individual filter criterion
     */
    private static <T> Predicate buildCriterionPredicate(
            FilterCriteria criteria, Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        
        if (!StringUtils.hasText(criteria.getField())) {
            return null;
        }
        
        try {
            Path<?> fieldPath = getFieldPath(root, criteria.getField());
            if (fieldPath == null) {
                log.warn("Field '{}' not found in entity", criteria.getField());
                return null;
            }
            
            return buildPredicateByOperator(criteria, fieldPath, cb);
            
        } catch (Exception e) {
            log.warn("Error building predicate for field '{}': {}", criteria.getField(), e.getMessage());
            return null;
        }
    }
    
    /**
     * Get field path supporting nested properties (e.g., "customer.firstName")
     */
    private static <T> Path<?> getFieldPath(Root<T> root, String fieldName) {
        try {
            if (fieldName.contains(".")) {
                String[] parts = fieldName.split("\\.");
                Path<?> path = root;
                for (String part : parts) {
                    path = path.get(part);
                }
                return path;
            } else {
                return root.get(fieldName);
            }
        } catch (Exception e) {
            log.warn("Could not resolve field path '{}': {}", fieldName, e.getMessage());
            return null;
        }
    }
    
    /**
     * Build predicate based on operator type
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Predicate buildPredicateByOperator(
            FilterCriteria criteria, Path<?> fieldPath, CriteriaBuilder cb) {
        
        FilterOperator operator = criteria.getOperator();
        String value = criteria.getValue();
        String value2 = criteria.getValue2();
        
        if (operator == FilterOperator.IS_NULL) {
            return cb.isNull(fieldPath);
        }
        
        if (operator == FilterOperator.IS_NOT_NULL) {
            return cb.isNotNull(fieldPath);
        }
        
        if (operator == FilterOperator.IS_TRUE) {
            return cb.isTrue((Expression<Boolean>) fieldPath);
        }
        
        if (operator == FilterOperator.IS_FALSE) {
            return cb.isFalse((Expression<Boolean>) fieldPath);
        }
        
        if (!StringUtils.hasText(value)) {
            return null;
        }
        
        Class<?> fieldType = fieldPath.getJavaType();
        Object convertedValue = convertValue(value, fieldType);
        
        if (convertedValue == null) {
            return null;
        }
        
        switch (operator) {
            case EQUALS:
                return cb.equal(fieldPath, convertedValue);
                
            case NOT_EQUALS:
                return cb.notEqual(fieldPath, convertedValue);
                
            case CONTAINS:
                if (fieldType == String.class) {
                    return cb.like(cb.lower((Expression<String>) fieldPath), 
                        "%" + value.toLowerCase() + "%");
                }
                break;
                
            case NOT_CONTAINS:
                if (fieldType == String.class) {
                    return cb.notLike(cb.lower((Expression<String>) fieldPath), 
                        "%" + value.toLowerCase() + "%");
                }
                break;
                
            case STARTS_WITH:
                if (fieldType == String.class) {
                    return cb.like(cb.lower((Expression<String>) fieldPath), 
                        value.toLowerCase() + "%");
                }
                break;
                
            case ENDS_WITH:
                if (fieldType == String.class) {
                    return cb.like(cb.lower((Expression<String>) fieldPath), 
                        "%" + value.toLowerCase());
                }
                break;
                
            case GREATER_THAN:
                if (Comparable.class.isAssignableFrom(fieldType)) {
                    return cb.greaterThan((Expression<Comparable>) fieldPath, (Comparable) convertedValue);
                }
                break;
                
            case GREATER_THAN_OR_EQUAL:
                if (Comparable.class.isAssignableFrom(fieldType)) {
                    return cb.greaterThanOrEqualTo((Expression<Comparable>) fieldPath, (Comparable) convertedValue);
                }
                break;
                
            case LESS_THAN:
                if (Comparable.class.isAssignableFrom(fieldType)) {
                    return cb.lessThan((Expression<Comparable>) fieldPath, (Comparable) convertedValue);
                }
                break;
                
            case LESS_THAN_OR_EQUAL:
                if (Comparable.class.isAssignableFrom(fieldType)) {
                    return cb.lessThanOrEqualTo((Expression<Comparable>) fieldPath, (Comparable) convertedValue);
                }
                break;
                
            case BETWEEN:
                if (Comparable.class.isAssignableFrom(fieldType) && StringUtils.hasText(value2)) {
                    Object convertedValue2 = convertValue(value2, fieldType);
                    if (convertedValue2 != null) {
                        return cb.between((Expression<Comparable>) fieldPath, 
                            (Comparable) convertedValue, (Comparable) convertedValue2);
                    }
                }
                break;
                
            case IN:
                List<String> values = Arrays.asList(value.split(","));
                return fieldPath.in(values.stream()
                    .map(v -> convertValue(v.trim(), fieldType))
                    .filter(v -> v != null)
                    .toArray());
                
            case NOT_IN:
                List<String> notValues = Arrays.asList(value.split(","));
                return cb.not(fieldPath.in(notValues.stream()
                    .map(v -> convertValue(v.trim(), fieldType))
                    .filter(v -> v != null)
                    .toArray()));
        }
        
        return null;
    }
    
    /**
     * Convert string value to appropriate type
     */
    private static Object convertValue(String value, Class<?> targetType) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        
        try {
            if (targetType == String.class) {
                return value;
            } else if (targetType == Integer.class || targetType == int.class) {
                return Integer.valueOf(value);
            } else if (targetType == Long.class || targetType == long.class) {
                return Long.valueOf(value);
            } else if (targetType == Double.class || targetType == double.class) {
                return Double.valueOf(value);
            } else if (targetType == Float.class || targetType == float.class) {
                return Float.valueOf(value);
            } else if (targetType == BigDecimal.class) {
                return new BigDecimal(value);
            } else if (targetType == Boolean.class || targetType == boolean.class) {
                return Boolean.valueOf(value);
            } else if (targetType == LocalDateTime.class) {
                return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } else if (targetType == LocalDate.class) {
                return LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
            } else if (targetType.isEnum()) {
                return Enum.valueOf((Class<Enum>) targetType, value.toUpperCase());
            }
        } catch (Exception e) {
            log.warn("Could not convert value '{}' to type {}: {}", value, targetType.getSimpleName(), e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Build global search predicate for searchable string fields
     */
    private static <T> Predicate buildGlobalSearchPredicate(
            String searchTerm, Root<T> root, CriteriaQuery<?> query, 
            CriteriaBuilder cb, Class<T> entityClass) {
        
        try {
            EntityType<T> entityType = root.getModel();
            Set<Attribute<? super T, ?>> attributes = entityType.getAttributes();
            
            Predicate searchPredicate = cb.disjunction();
            
            for (Attribute<? super T, ?> attribute : attributes) {
                if (attribute.getJavaType() == String.class) {
                    Path<String> stringPath = root.get(attribute.getName());
                    Predicate likePredicate = cb.like(
                        cb.lower(stringPath), 
                        "%" + searchTerm.toLowerCase() + "%"
                    );
                    searchPredicate = cb.or(searchPredicate, likePredicate);
                }
            }
            
            return searchPredicate;
            
        } catch (Exception e) {
            log.warn("Error building global search predicate: {}", e.getMessage());
            return null;
        }
    }
}