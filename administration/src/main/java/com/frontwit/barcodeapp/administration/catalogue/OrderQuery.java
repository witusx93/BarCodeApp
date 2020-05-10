package com.frontwit.barcodeapp.administration.catalogue;

import com.frontwit.barcodeapp.administration.catalogue.dto.*;
import com.frontwit.barcodeapp.administration.processing.front.infrastructure.persistence.FrontEntity;
import com.frontwit.barcodeapp.administration.processing.order.infrastructure.OrderEntity;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@AllArgsConstructor
@Component
public class OrderQuery {

    private MongoTemplate mongoTemplate;
    private CriteriaBuilder criteriaBuilder;

    public OrderDetailDto find(long id) {
        var frontDtos = findFrontsForOrderId(id);
        return Optional.ofNullable(mongoTemplate.findById(id, OrderEntity.class))
                .map(entity -> entity.detailsDto(frontDtos))
                .orElseThrow(() -> new IllegalArgumentException(format("No order for id %s", id)));
    }

    List<OrdersForCustomerDto> findOrdersForRoute(String route) {
        var query = routeQuery(route);
        var orderEntities = mongoTemplate.find(query, OrderEntity.class);
        return orderEntities.stream()
                .collect(Collectors.groupingBy(OrderEntity::getCustomer))
                .entrySet()
                .stream()
                .map(entry -> new OrdersForCustomerDto(entry.getKey(), mapToOrderInfoDto(entry.getValue()), ""))
                .collect(toList());
    }

    private Query routeQuery(String route) {
        return new Query(new Criteria()
                .and("route").regex(format("%s", route), "i")
                .and("completed").is(false)
                .and("packed").is(true));
    }

    private List<OrderInfoDto> mapToOrderInfoDto(List<OrderEntity> entities) {
        return entities.stream()
                .map(OrderEntity::deliveryOrderDto)
                .collect(toList());
    }

    private List<FrontDto> findFrontsForOrderId(long orderId) {
        var query = new Query(new Criteria("orderId").is(orderId));
        var frontEntities = mongoTemplate.find(query, FrontEntity.class);
        return frontEntities.stream()
                .map(FrontEntity::dto)
                .collect(toList());
    }

    Page<OrderDto> find(Pageable pageable, OrderSearchCriteria searchCriteria) {
        var criteria = criteriaBuilder.build(searchCriteria);
        var query = new Query(criteria).with(pageable).with(Sort.by(DESC, "lastProcessedOn"));
        var orders = mongoTemplate.find(query, OrderEntity.class);
        return PageableExecutionUtils
                .getPage(orders, pageable, () -> mongoTemplate.count(new Query(criteria), OrderEntity.class))
                .map(OrderEntity::dto);
    }

    Page<ReminderDto> findDeadlines(Pageable pageable) {
        var criteria = Criteria.where("deadline").ne(null).and("completed").is(false);
        var query = new Query(criteria).with(pageable).with(Sort.by(ASC, "deadline"));
        var orders = mongoTemplate.find(query, OrderEntity.class);

        return PageableExecutionUtils
                .getPage(orders, pageable, () -> mongoTemplate.count(new Query(criteria), OrderEntity.class))
                .map(OrderEntity::reminderDto);
    }
}
