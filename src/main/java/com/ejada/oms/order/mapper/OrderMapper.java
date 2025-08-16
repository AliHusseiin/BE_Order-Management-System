package com.ejada.oms.order.mapper;

import com.ejada.oms.order.dto.res.OrderItemResponse;
import com.ejada.oms.order.dto.res.OrderResponse;
import com.ejada.oms.order.dto.res.OrderSummary;
import com.ejada.oms.order.entity.Order;
import com.ejada.oms.order.entity.OrderItem;

import com.ejada.oms.customer.mapper.CustomerMapper;
import com.ejada.oms.product.mapper.ProductMapper;
import com.ejada.oms.invoice.mapper.InvoiceMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CustomerMapper.class, ProductMapper.class, InvoiceMapper.class})
public interface OrderMapper {

    @Mapping(source = "customer", target = "customer")
    @Mapping(source = "createdByUser.username", target = "createdByUsername")
    @Mapping(source = "orderItems", target = "orderItems")
    @Mapping(source = "invoice", target = "invoice")
    OrderResponse toResponse(Order order);

    @Mapping(source = "customer", target = "customer")
    OrderSummary toSummary(Order order);

    @Mapping(source = "product", target = "product")
    OrderItemResponse toResponse(OrderItem orderItem);
}
