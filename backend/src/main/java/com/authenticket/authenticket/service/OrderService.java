package com.authenticket.authenticket.service;

import com.authenticket.authenticket.dto.order.OrderDisplayDto;
import com.authenticket.authenticket.dto.order.OrderUpdateDto;
import com.authenticket.authenticket.dto.user.UserDisplayDto;
import com.authenticket.authenticket.model.Order;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    OrderDisplayDto findById(Integer orderId);
    List<OrderDisplayDto> findAllOrderByUserId(Integer userId, Pageable pageable);
    List<OrderDisplayDto> findAllOrder();
    UserDisplayDto findUserByOrderId(Integer orderId);
    Order saveOrder(Order order);
    Order updateOrder(OrderUpdateDto orderUpdateDto);
    OrderDisplayDto addTicketToOrder(Integer ticketId, Integer orderId);
    OrderDisplayDto removeTicketInOrder(Integer ticketId, Integer orderId);
    void checkOrderPaymentStatus(Order order);
    void removeOrder(Integer orderId);
    void cancelOrder(Order order);
    void cancelAllOrder(List<Order> orderList);
    Order completeOrder(Order order);
    void scheduleCancelProcessingOrder();
}
