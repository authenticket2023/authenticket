package com.authenticket.authenticket.service;

import com.authenticket.authenticket.dto.order.OrderDisplayDto;
import com.authenticket.authenticket.dto.order.OrderUpdateDto;
import com.authenticket.authenticket.dto.user.UserDisplayDto;
import com.authenticket.authenticket.model.Order;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    //get order by id
    OrderDisplayDto findById(Integer orderId);

    //get order by user
    List<OrderDisplayDto> findAllOrderByUserId(Integer userId, Pageable pageable);

    //get order by event
    List<OrderDisplayDto> findAllOrderByEventId(Pageable pageable, Integer eventId);

    //get all orders
    List<OrderDisplayDto> findAllOrder();

    //get user for order
    UserDisplayDto findUserByOrderId(Integer orderId);

    //initialise and create a new order
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
