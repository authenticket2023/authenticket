package com.authenticket.authenticket.controller;

import com.authenticket.authenticket.controller.response.GeneralApiResponse;
import com.authenticket.authenticket.dto.event.*;
import com.authenticket.authenticket.dto.order.OrderDisplayDto;
import com.authenticket.authenticket.dto.order.OrderDtoMapper;
import com.authenticket.authenticket.dto.order.OrderUpdateDto;
import com.authenticket.authenticket.dto.user.UserDisplayDto;
import com.authenticket.authenticket.dto.user.UserFullDisplayDto;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.Artist;
import com.authenticket.authenticket.model.Order;
import com.authenticket.authenticket.model.User;
import com.authenticket.authenticket.repository.OrderRepository;
import com.authenticket.authenticket.repository.UserRepository;
import com.authenticket.authenticket.service.Utility;
import com.authenticket.authenticket.service.impl.EmailServiceImpl;
import com.authenticket.authenticket.service.impl.OrderServiceImpl;
import com.authenticket.authenticket.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(
        origins = {
                "${authenticket.frontend-production-url}",
                "${authenticket.frontend-dev-url}",
                "${authenticket.loadbalancer-url}"
        },
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT},
        allowedHeaders = {"Authorization", "Cache-Control", "Content-Type"},
        allowCredentials = "true"
)
@RequestMapping("/api/order")
public class OrderController extends Utility {
    public final OrderServiceImpl orderService;
    private final OrderRepository orderRepository;
    private final OrderDtoMapper orderDtoMapper;
    private final UserRepository userRepository;
    private final EmailServiceImpl emailServiceImpl;

    @Autowired
    public OrderController(OrderServiceImpl orderService,
                           OrderRepository orderRepository,
                           OrderDtoMapper orderDtoMapper,
                           UserRepository userRepository,
                           EmailServiceImpl emailServiceImpl) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.orderDtoMapper = orderDtoMapper;
        this.userRepository = userRepository;
        this.emailServiceImpl = emailServiceImpl;
    }

    @GetMapping("/test")
    public String test() {
        return "test successful";
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<GeneralApiResponse<Object>> findById(@PathVariable(value = "orderId") Integer orderId){
        OrderDisplayDto orderDisplayDto = orderService.findById(orderId);
        if (orderDisplayDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generateApiResponse(null, String.format("Order with id %d not found", orderId)));
        }
        return ResponseEntity.ok(generateApiResponse(orderDisplayDto, String.format("Event %d successfully returned.", orderId)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<GeneralApiResponse<Object>> findAllOrderByUserId(@PathVariable(value = "userId") Integer userId, Pageable pageable) {
        List<OrderDisplayDto> eventList = orderService.findAllOrderByUserId(userId, pageable);
        if (eventList == null || eventList.isEmpty()) {
            return ResponseEntity.ok(generateApiResponse(null, "No featured events found"));
        }
        return ResponseEntity.ok(generateApiResponse(eventList, "Featured events successfully returned."));
    }

    @GetMapping
    public ResponseEntity<GeneralApiResponse<Object>> findAllOrder() {
        try {
            List<OrderDisplayDto> orderList = orderService.findAllOrder();
            if (orderList.isEmpty()) {
                return ResponseEntity.ok(generateApiResponse(orderList, "No orders found."));
            } else {
                return ResponseEntity.ok(generateApiResponse(orderList, "orders successfully returned."));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(generateApiResponse(null, e.getMessage()));
        }
    }

    @GetMapping("/find-user")
    public ResponseEntity<GeneralApiResponse<Object>> findUserByOrderId(@RequestParam(value = "orderId") Integer orderId) {
        try {
            UserDisplayDto userDisplayDto = orderService.findUserByOrderId(orderId);
            if (userDisplayDto == null) {
                return ResponseEntity.ok(generateApiResponse(null, "No purchaser found."));
            } else {
                return ResponseEntity.ok(generateApiResponse(userDisplayDto, "purchaser successfully returned."));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(generateApiResponse(null, e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<GeneralApiResponse> saveOrder(@RequestParam(value = "orderAmount") Double orderAmount,
                                                        @RequestParam(value = "userId") Integer userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        System.out.println(userOptional);
        if(userOptional.isPresent()){
            User purchaser = userOptional.get();
            Order newOrder = new Order(null,  orderAmount, LocalDate.now(),purchaser);
            Order savedOrder = orderService.saveOrder(newOrder);
            System.out.println("testing");
            //yet to implement email
//            emailServiceImpl.send("Order was successful");
            return ResponseEntity.ok(generateApiResponse(savedOrder,"Order successfully recorded"));
        }
        System.out.println("testing1");
        throw new NonExistentException("User does not exist does not exist");
    }

    @PutMapping
    public ResponseEntity<GeneralApiResponse> updateOrder(@RequestParam(value = "orderId") Integer orderId,
                                                          @RequestParam(value = "orderAmount") Double orderAmount,
                                                          @RequestParam(value = "userId") Integer userId){
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if(userOptional.isPresent() && orderOptional.isPresent()){
            Order existingOrder = orderOptional.get();
            User purchaser = userOptional.get();
            OrderUpdateDto newOrder = new OrderUpdateDto(existingOrder.getOrderId(),  orderAmount, purchaser);
            orderDtoMapper.update(newOrder, existingOrder);
            orderService.saveOrder(existingOrder);
            return ResponseEntity.ok(generateApiResponse(newOrder,"Order successfully updated"));
        }
        else if(userOptional.isEmpty()){
            throw new NonExistentException("User", userId);
        }
        throw new NonExistentException("Order", orderId);
    }

}
