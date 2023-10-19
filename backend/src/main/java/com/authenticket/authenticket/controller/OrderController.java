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
import com.authenticket.authenticket.service.Utility;
import com.authenticket.authenticket.service.impl.OrderServiceImpl;
import com.authenticket.authenticket.service.impl.TicketServiceImpl;
import com.itextpdf.text.DocumentException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**This is the order controller class and the base path for this controller's endpoint is api/v2/order.*/

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
@RequestMapping("/api/v2/order")
public class OrderController extends Utility {
    public final OrderServiceImpl orderService;
    private final OrderRepository orderRepository;
    private final OrderDtoMapper orderDtoMapper;
    private final TicketServiceImpl ticketService;
    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;

    @Autowired
    public OrderController(OrderServiceImpl orderService,
                           OrderRepository orderRepository,
                           OrderDtoMapper orderDtoMapper,
                           TicketServiceImpl ticketService,
                           TicketRepository ticketRepository,
                           EventRepository eventRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.orderDtoMapper = orderDtoMapper;
        this.ticketService = ticketService;
        this.ticketRepository = ticketRepository;
        this.eventRepository = eventRepository;
    }

    @GetMapping("/test")
    public String test() {
        return "test successful";
    }

    /**
     * Retrieves a test PDF document and sends it as a response to the client.
     *
     * @return A ResponseEntity containing the PDF document as a byte array and the necessary headers
     * to trigger a download on the client-side.
     *
     * @throws RuntimeException If there's an error during the document retrieval, a runtime exception
     * is thrown to indicate the failure.
     */
    @GetMapping("/test-pdf")
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
        } catch (DocumentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves a second test PDF document and sends it as a response to the client.
     *
     * @return A ResponseEntity containing the PDF document as a byte array and the necessary headers
     * to trigger a download on the client-side.
     *
     * @throws RuntimeException If there's an error during the document retrieval, a runtime exception
     * is thrown to indicate the failure.
     */
    @GetMapping("/test-pdf2")
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
        } catch (DocumentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves an order by its unique identifier and returns it as an HTTP response.
     *
     * @param orderId The unique identifier of the order to retrieve.
     * @param request The HttpServletRequest containing the user's request information.
     *
     * @return A ResponseEntity containing the order information as a GeneralApiResponse if found. If the order
     * is not found, a NOT_FOUND status with an appropriate message is returned.
     *
     * @throws NonExistentException If the user is not an administrator and is not authorized to access the order,
     * a NonExistentException is thrown to prevent unauthorized access.
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<GeneralApiResponse<Object>> findById(@PathVariable(value = "orderId") Integer orderId,
                                                               @NonNull HttpServletRequest request) {
       boolean isAdmin = isAdminRequest(request);
       if (!isAdmin) {
           User user = retrieveUserFromRequest(request);

           if (!orderRepository.existsByOrderIdAndUser(orderId, user)) {
               throw new NonExistentException("Cannot view order of other users");
           }
       }

        OrderDisplayDto orderDisplayDto = orderService.findById(orderId);
        if (orderDisplayDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generateApiResponse(null, String.format("Order with id %d not found", orderId)));
        }
        return ResponseEntity.ok(generateApiResponse(orderDisplayDto, String.format("Event %d successfully returned.", orderId)));
    }

    /**
     * Retrieves a list of orders associated with a specific user and returns them as an HTTP response.
     *
     * @param userId   The unique identifier of the user for whom to retrieve orders.
     * @param pageable Pageable object to control pagination of the result.
     * @param request  The HttpServletRequest containing the user's request information.
     *
     * @return A ResponseEntity containing a paginated list of orders associated with the user if found. If no orders
     * are found, a message indicating so is returned.
     *
     * @throws NonExistentException If the user is not an administrator and is not authorized to access orders
     * for other users, a NonExistentException is thrown to prevent unauthorized access.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<GeneralApiResponse<Object>> findAllOrderByUserId(@PathVariable(value = "userId") Integer userId,
                                                                           Pageable pageable,
                                                                           @NonNull HttpServletRequest request) {
        boolean isAdmin = isAdminRequest(request);
        if (!isAdmin) {
            User user = retrieveUserFromRequest(request);
            if (!Objects.equals(user.getUserId(), userId)) {
                throw new NonExistentException("Cannot view orders of other users");
            }
        }

        List<OrderDisplayDto> eventList = orderService.findAllOrderByUserId(userId, pageable);
        if (eventList == null || eventList.isEmpty()) {
            return ResponseEntity.ok(generateApiResponse(null, "No orders found"));
        }
        return ResponseEntity.ok(generateApiResponse(eventList, "orders successfully returned."));
    }

    /**
     * Retrieves a list of all orders and returns them as an HTTP response.
     *
     * @return A ResponseEntity containing a list of all orders if found. If no orders are found, a message
     * indicating so is returned.
     */
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

    /**
     * Retrieves the user associated with a specific order and returns them as an HTTP response.
     *
     * @param orderId The unique identifier of the order for which the associated user is to be retrieved.
     * @return A ResponseEntity containing the user information if found. If no user is found for the given order ID,
     * a message indicating so is returned.
     */
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

    /**
     * Creates a new order for ticket purchases and returns the order details as an HTTP response.
     *
     * @param eventId The unique identifier of the event for which the tickets are being purchased.
     * @param sectionId The identifier of the section where the tickets are allocated.
     * @param ticketsToPurchase The number of tickets to be purchased.
     * @param ticketHolderString A comma-separated list of unique ticket holder names (for enhanced events). Optional.
     * @param request The HTTP request object for the user creating the order.
     * @return A ResponseEntity containing the order details if the order creation is successful. If there are any
     * validation errors or issues during the process, an appropriate message is returned.
     *
     * @throws IllegalArgumentException If the provided ticket holder list is incorrect or lacks uniqueness,
     * or if the number of ticket holders doesn't match the number of tickets requested.
     */
    @PostMapping
    public ResponseEntity<GeneralApiResponse<Object>> saveOrder(@RequestParam(value = "eventId") Integer eventId,
                                                                @RequestParam(value = "sectionId") String sectionId,
                                                                @RequestParam(value = "ticketsToPurchase") Integer ticketsToPurchase,
                                                                @RequestParam(value = "ticketHolderString", required = false) String ticketHolderString,
                                                                @NonNull HttpServletRequest request) {
        User purchaser = retrieveUserFromRequest(request);
        checkIfEventExistsAndIsApprovedAndNotDeleted(eventId);

        Event event = eventRepository.findById(eventId).orElse(null);

        //check that ticket holder list is correct first
        List<String> ticketHolderList = new ArrayList<>();
        Set<String> uniqueTicketHoldersSet;
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

        List<Ticket> ticketList = ticketService.allocateSeats(eventId, sectionId, ticketsToPurchase);
        Double orderAmount = 0.0;
        for (Ticket ticket : ticketList) {
            orderAmount += ticket.getTicketPricing().getPrice();
        }
        Set<Ticket> ticketSet = new HashSet<>(ticketList);

        Order newOrder = new Order(null, orderAmount, LocalDate.now(), Order.Status.PROCESSING.getStatusValue(), purchaser, event, ticketSet);

        Order savedOrder = orderService.saveOrder(newOrder);

        List<Ticket> updatedTicketList = ticketList.stream()
                .peek(ticket -> ticket.setOrder(savedOrder))
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

    /**
     * Updates an existing order and returns the updated order details as an HTTP response.
     *
     * @param orderId The unique identifier of the order to be updated.
     * @param orderAmount The updated order amount for the order.
     * @param orderStatus The updated status of the order.
     * @param request The HTTP request object for the user updating the order.
     * @return A ResponseEntity containing the updated order details if the update is successful. If there are any
     * validation errors, or if the user is not authorized to update the order, an appropriate message is returned.
     *
     * @throws NonExistentException If the provided order identifier does not correspond to an existing order,
     * an exception is thrown with an error message.
     * @throws IllegalArgumentException If the user is attempting to update an order that does not belong to them,
     * an exception is thrown with an error message.
     */
    @PutMapping
    public ResponseEntity<GeneralApiResponse<Object>> updateOrder(@RequestParam(value = "orderId") Integer orderId,
                                                                  @RequestParam(value = "orderAmount") Double orderAmount,
                                                                  @RequestParam(value = "orderStatus") String orderStatus,
                                                                  @NonNull HttpServletRequest request) {
        User purchaser = retrieveUserFromRequest(request);

        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isEmpty()) {
            throw new NonExistentException("Order", orderId);
        }

        Order existingOrder = orderOptional.get();
        if (existingOrder.getUser() != purchaser) {
            throw new IllegalArgumentException("Unable to update other user's order");
        }

        OrderUpdateDto newOrder = new OrderUpdateDto(existingOrder.getOrderId(), orderAmount, existingOrder.getPurchaseDate(), orderStatus, purchaser);
        orderDtoMapper.update(newOrder, existingOrder);
        orderService.saveOrder(existingOrder);
        return ResponseEntity.ok(generateApiResponse(newOrder, "Order successfully updated"));
    }

    /**
     * Removes an order by its unique identifier and returns a response indicating the success of the operation.
     *
     * @param orderId The unique identifier of the order to be removed.
     * @return A ResponseEntity indicating the success of the removal operation. If the order is successfully removed,
     * the response includes a message indicating the successful removal. If the provided orderId does not correspond
     * to an existing order, an error message is returned.
     *
     * @throws NonExistentException If the provided orderId does not correspond to an existing order, an exception is
     * thrown with an error message.
     */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<GeneralApiResponse<Object>> removeOrder(@PathVariable(value = "orderId") Integer orderId) {
        if (orderRepository.findById(orderId).isEmpty()) {
            throw new NonExistentException("Order does not exist");
        }
        orderService.removeOrder(orderId);
        return ResponseEntity.ok(generateApiResponse(null, "Order removed successfully"));
    }

    /**
     * Cancels an order by its unique identifier and returns a response indicating the success of the operation.
     *
     * @param orderId The unique identifier of the order to be canceled.
     * @param request The HttpServletRequest containing the user's request information.
     * @return A ResponseEntity indicating the success of the order cancellation. If the order is successfully canceled,
     * the response includes a message indicating the successful cancellation. If the provided orderId does not correspond
     * to an existing order or the user lacks the necessary permissions, an error message is returned.
     *
     * @throws NonExistentException If the provided orderId does not correspond to an existing order, an exception is thrown
     * with an error message.
     * @throws IllegalArgumentException If the user does not have the necessary permissions to cancel the order, an exception
     * is thrown with an error message.
     */
    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<GeneralApiResponse<Object>> cancelOrder(@PathVariable(value = "orderId") Integer orderId,
                                                                  @NonNull HttpServletRequest request) {
        User user = retrieveUserFromRequest(request);

        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isEmpty()) {

            throw new NonExistentException("Order does not exist");
        }

        Order order = orderOptional.get();
        if (order.getUser() != user) {
           throw new IllegalArgumentException("Unable to cancel other user's order");
        }

        orderService.cancelOrder(order);

        return ResponseEntity.ok(generateApiResponse(null, "Order cancelled successfully"));
    }

    /**
     * Marks an order as completed based on its unique identifier and returns a response indicating the success of the operation.
     *
     * @param orderId The unique identifier of the order to be completed.
     * @param request The HTTP request from the user, containing user information.
     * @return A response entity indicating the success of marking the order as completed.
     * @throws NonExistentException If the provided orderId does not correspond to an existing order.
     * @throws IllegalArgumentException If the user attempts to complete another user's order or if the user's request is invalid.
     */
    @PutMapping("/complete/{orderId}")
    public ResponseEntity<GeneralApiResponse<Object>> complete(@PathVariable(value = "orderId") Integer orderId,
                                                               @NonNull HttpServletRequest request) {
        // throws error if request does not have a valid user
        retrieveUserFromRequest(request);

        if (orderRepository.findById(orderId).isEmpty()) {
            throw new NonExistentException("Order does not exist");
        }

        orderService.completeOrder(orderRepository.findById(orderId).get());


        return ResponseEntity.ok(generateApiResponse(null, "Order completed successfully"));
    }

    /**
     * Retrieves a list of orders associated with a specific event based on the event's unique identifier.
     *
     * @param eventId The unique identifier of the event for which you want to retrieve associated orders.
     * @param pageable Object containing pagination details for the result list.
     * @return A response entity containing a paginated list of orders associated with the specified event.
     * @throws NonExistentException If the provided eventId does not correspond to an existing event.
     */
    @GetMapping("/event/{eventId}")
    public ResponseEntity<GeneralApiResponse<Object>> findAllOrdersByEvent(Pageable pageable,@PathVariable(value = "eventId") Integer eventId) {
        Event event = eventRepository.findById(eventId).orElse(null);
        if(event == null){
            throw new NonExistentException("Event does not exist");
        }
        try {
            List<OrderDisplayDto> orderList = orderService.findAllOrderByEventId(pageable,eventId);
            if (orderList.isEmpty()) {
                return ResponseEntity.ok(generateApiResponse(orderList, String.format("No orders found for event id: %d.",eventId)));
            } else {
                return ResponseEntity.ok(generateApiResponse(orderList, String.format("Orders for event id: %d successfully returned.",eventId)));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(generateApiResponse(null, e.getMessage()));
        }
    }

}
