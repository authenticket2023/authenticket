package com.authenticket.authenticket.dto.order;

import com.authenticket.authenticket.dto.event.EventDisplayDto;
import com.authenticket.authenticket.dto.ticket.TicketDisplayDto;
import com.authenticket.authenticket.dto.ticket.TicketDisplayDtoMapper;
import com.authenticket.authenticket.dto.user.UserDtoMapper;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.Order;
import com.authenticket.authenticket.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderDtoMapper implements Function<Order, OrderDisplayDto> {

    private final UserDtoMapper userDtoMapper;
    private final TicketRepository ticketRepository;
    private final TicketDisplayDtoMapper ticketDisplayDtoMapper;

    @Autowired
    public OrderDtoMapper(UserDtoMapper userDtoMapper,
                          TicketRepository ticketRepository,
                          TicketDisplayDtoMapper ticketDisplayDtoMapper) {
        this.userDtoMapper = userDtoMapper;
        this.ticketRepository = ticketRepository;
        this.ticketDisplayDtoMapper = ticketDisplayDtoMapper;
    }

    public OrderDisplayDto apply(Order order){
        Set<TicketDisplayDto> ticketSet = new HashSet<>();
        if(ticketRepository.findAllByOrder(order) != null){
            ticketSet = ticketDisplayDtoMapper.mapTicketDisplayDto(ticketRepository.findAllByOrder(order));
        }
        return new OrderDisplayDto(
                order.getOrderId(),
                order.getOrderAmount(),
                order.getPurchaseDate(),
                order.getOrderStatus(),
                userDtoMapper.apply(order.getUser()),
                ticketSet
        );
    }
    public List<OrderDisplayDto> mapOrderHistoryDto(List<Order> orderList) {
        return orderList.stream()
                .map(this)
                .collect(Collectors.toList());
    }

    public void update (OrderUpdateDto newOrder, Order oldOrder){
        if(newOrder.orderId() != null){
            oldOrder.setOrderId(newOrder.orderId());
        }
        if(newOrder.orderAmount() != null){
            oldOrder.setOrderAmount(newOrder.orderAmount());
        }
        if(newOrder.purchaseDate() != null){
            oldOrder.setPurchaseDate(newOrder.purchaseDate());
        }
        if(newOrder.orderStatus() != null){
            oldOrder.setOrderStatus(newOrder.orderStatus());
        }
        if(newOrder.user() != null){
            oldOrder.setUser(newOrder.user());
        }
    }

public List<OrderDisplayDto> map(List<Order> orderList) {
    return orderList.stream()
            .map(this::apply)
            .collect(Collectors.toList());
}
}
