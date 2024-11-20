package com.order.CreationService.Controller;

import com.order.CreationService.Model.Order;
import com.order.CreationService.Model.OrderRequest;
import com.order.CreationService.Service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest orderRequest) {
        logger.info("Received request to create an order");
        Order order = orderService.createOrder(orderRequest);
        logger.info("Order created with orderNumber "+ order.getOrderNumber());
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }
}

