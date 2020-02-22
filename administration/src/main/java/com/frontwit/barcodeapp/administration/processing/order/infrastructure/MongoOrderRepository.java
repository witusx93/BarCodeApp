package com.frontwit.barcodeapp.administration.processing.order.infrastructure;

import com.frontwit.barcodeapp.administration.processing.order.model.Order;
import com.frontwit.barcodeapp.administration.processing.order.model.OrderRepository;
import com.frontwit.barcodeapp.administration.processing.order.model.UpdateStagePolicy;
import com.frontwit.barcodeapp.administration.processing.shared.OrderId;
import com.frontwit.barcodeapp.administration.processing.synchronization.CheckSynchronizedOrder;
import com.frontwit.barcodeapp.administration.processing.synchronization.SaveSynchronizedOrder;
import com.frontwit.barcodeapp.administration.processing.synchronization.TargetOrder;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Optional;

@AllArgsConstructor
public class MongoOrderRepository implements OrderRepository, SaveSynchronizedOrder, CheckSynchronizedOrder {

    private MongoTemplate mongoTemplate;
    private UpdateStagePolicy updateStagePolicy;

    @Override
    public void save(TargetOrder targetOrder) {
        mongoTemplate.save(new OrderEntity(targetOrder));
    }

    @Override
    public Optional<Order> findBy(OrderId orderId) {
        return findById(orderId.getId())
                .map(entity -> entity.toDomainModel(updateStagePolicy));
    }

    @Override
    public void save(Order order) {
        Optional.ofNullable(mongoTemplate.findById(order.getOrderId().getId(), OrderEntity.class))
                .ifPresent(entity -> {
                    entity.update(order);
                    mongoTemplate.save(entity);
                });
    }

    @Override
    public boolean isSynchronized(OrderId orderId) {
        return findById(orderId.getId()).isPresent();
    }

    private Optional<OrderEntity> findById(Long id) {
        return Optional.ofNullable(mongoTemplate.findById(id, OrderEntity.class));
    }
}
