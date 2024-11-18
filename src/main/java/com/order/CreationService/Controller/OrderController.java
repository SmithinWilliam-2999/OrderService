package com.order.CreationService.Controller;

import com.order.CreationService.Model.Order;
import com.order.CreationService.Model.OrderItem;
import com.order.CreationService.Service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    @Autowired
    OrderService orderService;


    @PostMapping
    @RequestMapping("/orders")
    public ResponseEntity<Order> CreateOrder (@RequestBody OrderItem orderItem) {
        log.info("Inside the order service controller with parameters: "+orderItem.toString());
        Order createOrderItem = null;
        try {
            createOrderItem = orderService.createOrder(orderItem);
            return new ResponseEntity<>(createOrderItem, HttpStatus.CREATED);
        } catch (IllegalArgumentException exception) {
            return new ResponseEntity<>(createOrderItem, HttpStatus.BAD_REQUEST);
        }
    }
}
