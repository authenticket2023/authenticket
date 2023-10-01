package com.authenticket.authenticket.service;

import com.authenticket.authenticket.dto.order.OrderDisplayDto;
import com.authenticket.authenticket.dto.order.OrderUpdateDto;
import com.authenticket.authenticket.dto.user.UserDisplayDto;
import com.authenticket.authenticket.dto.venue.VenueDisplayDto;
import com.authenticket.authenticket.model.Order;
import com.authenticket.authenticket.model.User;
import com.authenticket.authenticket.model.Venue;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    OrderDisplayDto findById(Integer orderId);
    List<OrderDisplayDto> findAllOrderByUserId(Integer userId, Pageable pageable);
    List<OrderDisplayDto> findAllOrder();
    UserDisplayDto findUserByOrderId(Integer orderId);
    Order saveOrder(Order order);
    Order updateOrder(OrderUpdateDto orderUpdateDto);
    void checkOrderPaymentStatus(Order order);
    void removeOrder(Integer orderId);
}
