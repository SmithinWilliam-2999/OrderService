package com.order.CreationService.DAO;

import com.order.CreationService.Model.Order;
import com.order.CreationService.Model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Repository extends JpaRepository <Order, Long> {
}
