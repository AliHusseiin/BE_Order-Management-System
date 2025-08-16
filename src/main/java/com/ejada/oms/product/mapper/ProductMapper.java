package com.ejada.oms.product.mapper;

import com.ejada.oms.product.dto.res.ProductResponse;
import com.ejada.oms.product.dto.res.ProductSummary;
import com.ejada.oms.product.entity.Product;
import com.ejada.oms.product.dto.req.ProductCreateRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductResponse toResponse(Product product);

    ProductSummary toSummary(Product product);

    Product toEntity(ProductCreateRequest request);
}