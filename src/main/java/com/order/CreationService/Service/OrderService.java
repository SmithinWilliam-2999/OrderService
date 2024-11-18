package com.order.CreationService.Service;

import com.order.CreationService.DAO.Repository;
import com.order.CreationService.Model.Order;
import com.order.CreationService.Model.OrderItem;
import com.order.CreationService.OrderStatus.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.UUID;

@Service
@Slf4j
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final Repository repository;

    public OrderService(Repository repository) {
        this.repository = repository;
    }

    @Transactional
    public Order createOrder(OrderItem orderItem) {
        log.info("Inside the order service ");

        String orderNumber = "ORD-"+ UUID.randomUUID().toString();
        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setStatus(OrderStatus.CREATED);
        BigDecimal totalAmount = BigDecimal.ZERO;

        for(OrderItem item : orderItem.getOrder().getItems()) {

            if(item.getProductCode().isEmpty()){
                throw new IllegalArgumentException("Product code must not be empty");
            }
            if(item.getQuantity()<=0){
                throw new IllegalArgumentException("Quantity must be positive");
            }
            if(item.getUnitPrice().compareTo(BigDecimal.ZERO)<=0){
                throw new IllegalArgumentException("Unit Price must be positive");
            }

            OrderItem ordItem = new OrderItem();
            ordItem.setProductCode(orderItem.getProductCode());
            ordItem.setUnitPrice(orderItem.getUnitPrice());
            ordItem.setQuantity(orderItem.getQuantity());

            order.getItems().add(ordItem);

            totalAmount = totalAmount.add(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        order.setTotalAmount(totalAmount);
        log.info("Final response :  "+order.toString());
        return repository.save(order);
    }
}
