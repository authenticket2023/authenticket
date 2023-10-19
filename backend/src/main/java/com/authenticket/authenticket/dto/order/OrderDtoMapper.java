package com.authenticket.authenticket.dto.order;

import com.authenticket.authenticket.dto.event.EventDisplayDto;
import com.authenticket.authenticket.dto.eventticketcategory.EventTicketCategoryDisplayDto;
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

/**
 * A mapper class for converting Order entities to OrderDisplayDto objects.
 * {@link OrderDisplayDto} DTOs and performing updates on order entities.
 */
@Service
public class OrderDtoMapper implements Function<Order, OrderDisplayDto> {

    private final UserDtoMapper userDtoMapper;
    private final TicketRepository ticketRepository;
    private final TicketDisplayDtoMapper ticketDisplayDtoMapper;

    /**
     * Constructs an OrderDtoMapper with the specified dependencies.
     *
     * @param userDtoMapper          The mapper for converting User entities to UserDisplayDto objects.
     * @param ticketRepository       The repository for managing Ticket entities.
     * @param ticketDisplayDtoMapper The mapper for converting Ticket entities to TicketDisplayDto objects.
     */
    @Autowired
    public OrderDtoMapper(UserDtoMapper userDtoMapper,
                          TicketRepository ticketRepository,
                          TicketDisplayDtoMapper ticketDisplayDtoMapper) {
        this.userDtoMapper = userDtoMapper;
        this.ticketRepository = ticketRepository;
        this.ticketDisplayDtoMapper = ticketDisplayDtoMapper;
    }

    /**
     * Converts an Order entity to an OrderDisplayDto object.
     *
     * @param order The Order entity to be converted.
     * @return An OrderDisplayDto object representing the same data.
     */
    public OrderDisplayDto apply(Order order) {
        Set<TicketDisplayDto> ticketSet = new HashSet<>();
        if (ticketRepository.findAllByOrder(order) != null) {
            ticketSet = ticketDisplayDtoMapper.mapTicketDisplayDto(ticketRepository.findAllByOrder(order));
        }

        Event event = order.getEvent();
        return new OrderDisplayDto(
                order.getOrderId(),
                event.getEventId(),
                event.getEventName(),
                event.getEventDate(),
                event.getVenue().getVenueLocation(),
                order.getOrderAmount(),
                order.getPurchaseDate(),
                order.getOrderStatus(),
                userDtoMapper.apply(order.getUser()),
                ticketSet
        );
    }

    /**
     * Maps a list of Order entities to a list of OrderDisplayDto objects.
     *
     * @param orderList The list of Order entities to be mapped.
     * @return A list of OrderDisplayDto objects representing the same data.
     */
    public List<OrderDisplayDto> mapOrderHistoryDto(List<Order> orderList) {
        return orderList.stream()
                .map(this)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing Order entity with data from an OrderUpdateDto.
     *
     * @param newOrder The OrderUpdateDto containing the updated data.
     * @param oldOrder The existing Order entity to be updated.
     */
    public void update(OrderUpdateDto newOrder, Order oldOrder) {
        if (newOrder.orderId() != null) {
            oldOrder.setOrderId(newOrder.orderId());
        }
        if (newOrder.orderAmount() != null) {
            oldOrder.setOrderAmount(newOrder.orderAmount());
        }
        if (newOrder.purchaseDate() != null) {
            oldOrder.setPurchaseDate(newOrder.purchaseDate());
        }
        if (newOrder.orderStatus() != null) {
            oldOrder.setOrderStatus(newOrder.orderStatus());
        }
        if (newOrder.user() != null) {
            oldOrder.setUser(newOrder.user());
        }
    }

    /**
     * Maps a list of Order entities to a list of OrderDisplayDto objects.
     *
     * @param orderList The list of Order entities to be mapped.
     * @return A list of OrderDisplayDto objects representing the same data.
     */
    public List<OrderDisplayDto> map(List<Order> orderList) {
        return orderList.stream()
                .map(this::apply)
                .collect(Collectors.toList());
    }
}
