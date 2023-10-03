package com.authenticket.authenticket.controller;

import com.authenticket.authenticket.controller.response.GeneralApiResponse;
import com.authenticket.authenticket.dto.order.OrderDisplayDto;
import com.authenticket.authenticket.dto.order.OrderDtoMapper;
import com.authenticket.authenticket.dto.order.OrderUpdateDto;
import com.authenticket.authenticket.dto.user.UserDisplayDto;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.Order;
import com.authenticket.authenticket.model.Ticket;
import com.authenticket.authenticket.model.User;
import com.authenticket.authenticket.repository.EventRepository;
import com.authenticket.authenticket.repository.OrderRepository;
import com.authenticket.authenticket.repository.TicketRepository;
import com.authenticket.authenticket.repository.UserRepository;
import com.authenticket.authenticket.service.Utility;
import com.authenticket.authenticket.service.impl.OrderServiceImpl;
import com.authenticket.authenticket.service.impl.TicketServiceImpl;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
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
    private final TicketServiceImpl ticketService;
    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;

    @Autowired
    public OrderController(OrderServiceImpl orderService,
                           OrderRepository orderRepository,
                           OrderDtoMapper orderDtoMapper,
                           UserRepository userRepository,
                           TicketServiceImpl ticketService,
                           TicketRepository ticketRepository,
                           EventRepository eventRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.orderDtoMapper = orderDtoMapper;
        this.userRepository = userRepository;
        this.ticketService = ticketService;
        this.ticketRepository = ticketRepository;
        this.eventRepository = eventRepository;
    }

    @GetMapping("/test")
    public String test() {
        return "test successful";
    }

    @GetMapping("/testPDF")
    public ResponseEntity<?> testPDF() {
        // retrieve contents of "C:/tmp/report.pdf" that were written in showHelp
        try {

            byte[] contents = orderService.test().getContentAsByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            // Here you have to set the actual filename of your pdf
            String filename = "output.pdf";
            headers.setContentDispositionFormData(filename, filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            ResponseEntity<byte[]> response = new ResponseEntity<>(contents, headers, HttpStatus.OK);
            return response;
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/testPDF2")
    public ResponseEntity<?> testPDF2() {
        // retrieve contents of "C:/tmp/report.pdf" that were written in showHelp
        try {

            byte[] contents = orderService.test2().getContentAsByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            // Here you have to set the actual filename of your pdf
            String filename = "output.pdf";
            headers.setContentDispositionFormData(filename, filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            ResponseEntity<byte[]> response = new ResponseEntity<>(contents, headers, HttpStatus.OK);
            return response;
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
                                                        @RequestParam(value = "sectionId") String sectionId,
                                                        @RequestParam(value = "ticketsToPurchase") Integer ticketsToPurchase,
                                                        @RequestParam(value = "ticketHolderString", required = false) String ticketHolderString) {
        Optional<User> userOptional = userRepository.findById(userId);

        checkIfEventExistsAndIsApprovedAndNotDeleted(eventId);

        Event event = eventRepository.findById(eventId).orElse(null);


        //check that ticket holder list is correct first
        List<String> ticketHolderList = new ArrayList<>();
        Set<String> uniqueTicketHoldersSet = new HashSet<>();
        if (event != null && event.getIsEnhanced()) {
            if (ticketHolderString == null) {
                throw new IllegalArgumentException("Ticket holder list not provided");
            }

            ticketHolderList = Arrays.stream(ticketHolderString.split(",")).toList();
            uniqueTicketHoldersSet = new HashSet<>(ticketHolderList);

            if (ticketHolderList.size() != uniqueTicketHoldersSet.size()) {
                throw new IllegalArgumentException("Ticket holder names must be unique!");
            }

            if (uniqueTicketHoldersSet.size() != ticketsToPurchase) {
                throw new IllegalArgumentException("Number of ticket holder provided not equal to number of tickets user wants to purchase");
            }
        }


        if (userOptional.isPresent()) {
            User purchaser = userOptional.get();
            List<Ticket> ticketList = ticketService.allocateSeats(eventId, sectionId, ticketsToPurchase);
            Double orderAmount = 0.0;
            for (Ticket ticket : ticketList) {
                orderAmount += ticket.getTicketPricing().getPrice();
            }
            Set<Ticket> ticketSet = new HashSet<>(ticketList);

            Order newOrder = new Order(null, orderAmount, LocalDate.now(), Order.Status.PROCESSING.getStatusValue(), purchaser, ticketSet);

            Order savedOrder = orderService.saveOrder(newOrder);

            List<Ticket> updatedTicketList = ticketList.stream()
                    .map(ticket -> {
                        ticket.setOrder(savedOrder);
                        return ticket;
                    })
                    .collect(Collectors.toList());


            //check if event is enhanced and if ticket holder provided is == ticketsToPurchase
            if (event != null && event.getIsEnhanced()) {

                for (int i = 0; i < ticketHolderList.size(); i++) {
                    String ticketHolder = ticketHolderList.get(i);
                    Ticket ticket = updatedTicketList.get(i);
                    ticket.setTicketHolder(ticketHolder);

                }
            }
            ticketRepository.saveAll(updatedTicketList);
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
    public ResponseEntity<GeneralApiResponse> removeTicketInOrder(@PathVariable(value = "orderId") Integer orderId) {
        if (orderRepository.findById(orderId).isEmpty()) {
            throw new NonExistentException("Order does not exist");
        }
        orderService.removeOrder(orderId);
        return ResponseEntity.ok(generateApiResponse(null, "Order removed successfully"));
    }

    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<GeneralApiResponse> cancelOrder(@PathVariable(value = "orderId") Integer orderId) {
        if (orderRepository.findById(orderId).isEmpty()) {
            throw new NonExistentException("Order does not exist");
        }

        orderService.cancelOrder(orderRepository.findById(orderId).get());
        return ResponseEntity.ok(generateApiResponse(null, "Order cancelled successfully"));
    }

    @PutMapping("/complete/{orderId}")
    public ResponseEntity<GeneralApiResponse> complete(@PathVariable(value = "orderId") Integer orderId) {
        if (orderRepository.findById(orderId).isEmpty()) {
            throw new NonExistentException("Order does not exist");
        }
        orderService.completeOrder(orderRepository.findById(orderId).get());
        return ResponseEntity.ok(generateApiResponse(null, "Order completed successfully"));
    }
}
