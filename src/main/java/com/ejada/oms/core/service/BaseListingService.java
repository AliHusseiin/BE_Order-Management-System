package com.ejada.oms.core.service;

import com.ejada.oms.core.dto.GeneralFilterDto;
import com.ejada.oms.core.specification.DynamicSpecificationBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Base service class for listing operations with pagination and sorting.
 * Simplified version without complex specifications.
 */
@Slf4j
@RequiredArgsConstructor
public abstract class BaseListingService<T, ID> {

    protected abstract JpaRepository<T, ID> getRepository();
    protected abstract JpaSpecificationExecutor<T> getSpecificationRepository();
    protected abstract Class<T> getEntityClass();

    /**
     * Find all entities with GeneralFilterDto using dynamic specifications
     */
    public Page<T> findAll(GeneralFilterDto filterDto) {
        log.debug("Finding all entities with GeneralFilterDto: {}", filterDto);

        Specification<T> spec = DynamicSpecificationBuilder.buildSpecification(filterDto, getEntityClass());
        Pageable pageable = filterDto.getPageable();
        Page<T> result = getSpecificationRepository().findAll(spec, pageable);
        
        log.debug("Found {} entities out of {} total", result.getNumberOfElements(), result.getTotalElements());
        return result;
    }



    /**
     * Find all entities without pagination
     */
    public List<T> findAll() {
        log.debug("Finding all entities without pagination");
        return getRepository().findAll();
    }



}