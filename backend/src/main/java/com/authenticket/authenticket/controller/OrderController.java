package com.authenticket.authenticket.controller;

import com.authenticket.authenticket.controller.response.GeneralApiResponse;
import com.authenticket.authenticket.dto.order.OrderDisplayDto;
import com.authenticket.authenticket.dto.order.OrderDtoMapper;
import com.authenticket.authenticket.dto.order.OrderUpdateDto;
import com.authenticket.authenticket.dto.user.UserDisplayDto;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.Order;
import com.authenticket.authenticket.model.Ticket;
import com.authenticket.authenticket.model.User;
import com.authenticket.authenticket.repository.OrderRepository;
import com.authenticket.authenticket.repository.TicketRepository;
import com.authenticket.authenticket.repository.UserRepository;
import com.authenticket.authenticket.service.Utility;
import com.authenticket.authenticket.service.impl.EmailServiceImpl;
import com.authenticket.authenticket.service.impl.OrderServiceImpl;
import com.authenticket.authenticket.service.impl.TicketServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    private final TicketServiceImpl ticketService;
    private final TicketRepository ticketRepository;

    @Autowired
    public OrderController(OrderServiceImpl orderService,
                           OrderRepository orderRepository,
                           OrderDtoMapper orderDtoMapper,
                           UserRepository userRepository,
                           EmailServiceImpl emailServiceImpl,
                           TicketServiceImpl ticketService,
                           TicketRepository ticketRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.orderDtoMapper = orderDtoMapper;
        this.userRepository = userRepository;
        this.emailServiceImpl = emailServiceImpl;
        this.ticketService = ticketService;
        this.ticketRepository = ticketRepository;
    }

    @GetMapping("/test")
    public String test() {
        return "test successful";
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<GeneralApiResponse<Object>> findById(@PathVariable(value = "orderId") Integer orderId) {
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
            return ResponseEntity.ok(generateApiResponse(null, "No orders found"));
        }
        return ResponseEntity.ok(generateApiResponse(eventList, "orders successfully returned."));
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
    public ResponseEntity<GeneralApiResponse> saveOrder(@RequestParam(value = "userId") Integer userId,
                                                        @RequestParam(value = "eventId") Integer eventId,
                                                        @RequestParam(value = "sectionId") Integer sectionId,
                                                        @RequestParam(value = "ticketsToPurchase") Integer ticketsToPurchase) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User purchaser = userOptional.get();
            List<Ticket> ticketList = ticketService.allocateSeats(eventId, sectionId, ticketsToPurchase);
            Double orderAmount = 0.0;
            for (Ticket ticket : ticketList) {
                orderAmount += ticket.getTicketPricing().getPrice();
            }
            Set<Ticket> ticketSet = new HashSet<>();

            Order newOrder = new Order(null, orderAmount, LocalDate.now(), Order.Status.PROCESSING.getStatusValue(), purchaser, ticketSet);

            Order savedOrder = orderService.saveOrder(newOrder);

            Set<Ticket> updatedTicketSet = ticketList.stream()
                    .map(ticket -> {
                        ticket.setOrder(savedOrder);
                        return ticket;
                    })
                    .collect(Collectors.toSet());
            ticketRepository.saveAll(updatedTicketSet);

            OrderDisplayDto savedOrderDto = orderDtoMapper.apply(orderRepository.findById(savedOrder.getOrderId()).get());

            return ResponseEntity.ok(generateApiResponse(savedOrderDto, "Order successfully recorded"));
        }
        throw new NonExistentException("User does not exist does not exist");
    }

    @PutMapping
    public ResponseEntity<GeneralApiResponse> updateOrder(@RequestParam(value = "orderId") Integer orderId,
                                                          @RequestParam(value = "orderAmount") Double orderAmount,
                                                          @RequestParam(value = "orderStatus") String orderStatus,
                                                          @RequestParam(value = "userId") Integer userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (userOptional.isPresent() && orderOptional.isPresent()) {
            Order existingOrder = orderOptional.get();
            User purchaser = userOptional.get();
            OrderUpdateDto newOrder = new OrderUpdateDto(existingOrder.getOrderId(), orderAmount, existingOrder.getPurchaseDate(), orderStatus, purchaser);
            orderDtoMapper.update(newOrder, existingOrder);
            orderService.saveOrder(existingOrder);
            return ResponseEntity.ok(generateApiResponse(newOrder, "Order successfully updated"));
        } else if (userOptional.isEmpty()) {
            throw new NonExistentException("User", userId);
        }
        throw new NonExistentException("Order", orderId);
    }

    @PutMapping("/add-ticket")
    public ResponseEntity<GeneralApiResponse> addTicketToOrder(@RequestParam(value = "ticketId") Integer ticketId,
                                                               @RequestParam(value = "orderId") Integer orderId) {
        return ResponseEntity.ok(generateApiResponse(orderService.addTicketToOrder(ticketId, orderId), "Ticket added successfully"));
    }

    @PutMapping("/remove-ticket")
    public ResponseEntity<GeneralApiResponse> removeTicketInOrder(@RequestParam(value = "ticketId") Integer ticketId,
                                                                  @RequestParam(value = "orderId") Integer orderId) {
        return ResponseEntity.ok(generateApiResponse(orderService.removeTicketInOrder(ticketId, orderId), "Ticket added successfully"));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<GeneralApiResponse> removeTicketInOrder(@PathVariable(value = "orderId")Integer orderId) {
        if(orderRepository.findById(orderId).isEmpty()){
            throw new NonExistentException("Order does not exist");
        }
        orderService.removeOrder(orderId);
        return ResponseEntity.ok(generateApiResponse(null, "Order removed successfully"));
    }

    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<GeneralApiResponse> cancelOrder(@PathVariable(value = "orderId")Integer orderId) {
        if(orderRepository.findById(orderId).isEmpty()){
            throw new NonExistentException("Order does not exist");
        }
        orderService.cancelOrder(orderRepository.findById(orderId).get());
        return ResponseEntity.ok(generateApiResponse(null, "Order cancelled successfully"));
    }


}
