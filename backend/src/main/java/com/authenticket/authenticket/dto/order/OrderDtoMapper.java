package com.authenticket.authenticket.dto.order;

import com.authenticket.authenticket.dto.event.FeaturedEventDto;
import com.authenticket.authenticket.dto.user.UserDtoMapper;
import com.authenticket.authenticket.model.FeaturedEvent;
import com.authenticket.authenticket.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderDtoMapper implements Function<Order, OrderDisplayDto> {

    private final UserDtoMapper userDtoMapper;

    @Autowired
    public OrderDtoMapper(UserDtoMapper userDtoMapper) {
        this.userDtoMapper = userDtoMapper;
    }

    public OrderDisplayDto apply(Order order){
        return new OrderDisplayDto(
                order.getOrderId(),
                order.getOrderAmount(),
                order.getPurchaseDate(),
                userDtoMapper.apply(order.getUser())
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
        if(newOrder.user() != null){
            oldOrder.setUser(newOrder.user());
        }
    }
//    public OrderDisplayDto applyOrderDto(Order order) {
//        return new OrderDisplayDto(
//                order.getOrderId(),
//                order.getOrderAmount(),
//                order.getPurchaseDate(),
//                userDtoMapper.apply(order.getUser())
//        );
//    }

//    public OrderDisplayDto applyArtistDisplayDto(Object[] obj){
//        return new OrderDisplayDto(
//                (Integer) obj[0],
//                (String) obj[1],
//                (String) obj[2]
//        );
//    }
//
//    public Set<OrderDisplayDto> mapArtistDisplayDto(List<Object[]> artistObjects) {
//
//        return artistObjects.stream()
//                .map(this::applyArtistDisplayDto)
//                .collect(Collectors.toSet());
//    }
}
