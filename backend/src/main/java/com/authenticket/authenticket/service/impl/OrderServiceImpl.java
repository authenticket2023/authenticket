package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.order.OrderDisplayDto;
import com.authenticket.authenticket.dto.order.OrderDtoMapper;
import com.authenticket.authenticket.dto.order.OrderUpdateDto;
import com.authenticket.authenticket.dto.user.UserDisplayDto;
import com.authenticket.authenticket.dto.user.UserDtoMapper;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.Order;
import com.authenticket.authenticket.model.User;
import com.authenticket.authenticket.repository.OrderRepository;
import com.authenticket.authenticket.repository.UserRepository;
import com.authenticket.authenticket.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private final OrderDtoMapper orderDtoMapper;

    private final UserDtoMapper userDtoMapper;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            UserRepository userRepository,
                            OrderDtoMapper orderDtoMapper,
                            UserDtoMapper userDtoMapper) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.orderDtoMapper = orderDtoMapper;
        this.userDtoMapper = userDtoMapper;
    }

    @Override
    public OrderDisplayDto findById(Integer orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            OrderDisplayDto orderDisplayDto = orderDtoMapper.apply(order);
            return orderDisplayDto;
        }
        return null;
    }

    @Override
    public List<OrderDisplayDto> findAllOrderByUserId(Integer userId, Pageable pageable) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()){
            Page<Order> orderHistory = orderRepository.findByUser(userOptional.get(), pageable);
//                .stream()
//                .map(orderDtoMapper)
//                .collect(Collectors.toList());
            return orderDtoMapper.mapOrderHistoryDto(orderHistory.getContent());
        }
        throw new NonExistentException("User", userId);
    }

    @Override
    public List<OrderDisplayDto> findAllOrder() {
        return orderRepository.findAll()
                .stream()
                .map(orderDtoMapper)
                .collect(Collectors.toList());
    }

    @Override
    public UserDisplayDto findUserByOrderId(Integer orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            OrderDisplayDto orderDisplayDto = orderDtoMapper.apply(order);
            return orderDisplayDto.purchaser();
        }
        return null;
    }

    @Override
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public Order updateOrder(OrderUpdateDto orderUpdateDto){
        Optional<Order> orderOptional = orderRepository.findById(orderUpdateDto.orderId());

        if (orderOptional.isPresent()) {
            Order existingOrder = orderOptional.get();
            orderDtoMapper.update(orderUpdateDto, existingOrder);
            orderRepository.save(existingOrder);

            return existingOrder;
        }
        throw new NonExistentException("Order", orderUpdateDto.orderId());
    }
}
