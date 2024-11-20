package com.order.CreationService.Service;

import com.order.CreationService.Repository.OrderRepository;
import com.order.CreationService.ExceptionHandler.BadRequestException;
import com.order.CreationService.Model.Order;
import com.order.CreationService.Model.OrderItem;
import com.order.CreationService.Model.OrderItemRequest;
import com.order.CreationService.Model.OrderRequest;
import com.order.CreationService.OrderStatus.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(OrderRequest orderRequest) {
        logger.info("Creating a new order with {} items", orderRequest.getItems().size());

        validateOrderRequest(orderRequest);

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.CREATED);

        List<OrderItem> items = orderRequest.getItems().stream()
                .map(requestItem -> {
                    logger.debug("Adding item: productCode={}, quantity={}, unitPrice={}",
                            requestItem.getProductCode(), requestItem.getQuantity(), requestItem.getUnitPrice());

                    OrderItem item = new OrderItem();
                    item.setProductCode(requestItem.getProductCode());
                    item.setQuantity(requestItem.getQuantity());
                    item.setUnitPrice(requestItem.getUnitPrice());
                    item.setOrder(order);
                    return item;
                }).collect(Collectors.toList());

        order.setItems(items);
        order.setTotalAmount(calculateTotalAmount(items));

        Order savedOrder = orderRepository.save(order);
        logger.info("Order created successfully with orderNumber={}", savedOrder.getOrderNumber());

        return savedOrder;
    }

    private void validateOrderRequest(OrderRequest orderRequest) {
        if (orderRequest.getItems() == null || orderRequest.getItems().isEmpty()) {
            logger.error("Validation failed: Order must have at least one item");
            throw new BadRequestException("Order must have at least one item.");
        }

        for (OrderItemRequest item : orderRequest.getItems()) {
            if (item.getQuantity() <= 0) {
                logger.error("Validation failed: Quantity must be positive for productCode={}", item.getProductCode());
                throw new BadRequestException("Quantity must be positive.");
            }
            if (item.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
                logger.error("Validation failed: Unit price must be positive for productCode={}", item.getProductCode());
                throw new BadRequestException("Unit price must be positive.");
            }
            if (item.getProductCode() == null || item.getProductCode().isEmpty()) {
                logger.error("Validation failed: Product code must not be empty");
                throw new BadRequestException("Product code must not be empty.");
            }
        }
    }

    private BigDecimal calculateTotalAmount(List<OrderItem> items) {
        BigDecimal total = items.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        logger.debug("Total amount calculated: {}", total);
        return total;
    }
}

