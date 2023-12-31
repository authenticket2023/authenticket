package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.file.FileNameRecord;
import com.authenticket.authenticket.dto.order.OrderDisplayDto;
import com.authenticket.authenticket.dto.order.OrderDtoMapper;
import com.authenticket.authenticket.dto.order.OrderUpdateDto;
import com.authenticket.authenticket.dto.ticket.TicketDisplayDto;
import com.authenticket.authenticket.dto.ticket.TicketDisplayDtoMapper;
import com.authenticket.authenticket.dto.user.UserDisplayDto;
import com.authenticket.authenticket.exception.AlreadyExistsException;
import com.authenticket.authenticket.exception.ApiRequestException;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.Order;
import com.authenticket.authenticket.model.Ticket;
import com.authenticket.authenticket.model.User;
import com.authenticket.authenticket.repository.OrderRepository;
import com.authenticket.authenticket.repository.TicketRepository;
import com.authenticket.authenticket.repository.UserRepository;
import com.authenticket.authenticket.service.EmailService;
import com.authenticket.authenticket.service.OrderService;
import com.authenticket.authenticket.service.PDFGenerator;
import com.authenticket.authenticket.service.QueueService;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private final OrderDtoMapper orderDtoMapper;

    private final TicketDisplayDtoMapper ticketDisplayDtoMapper;

    private final TicketRepository ticketRepository;

    private final TaskScheduler taskScheduler;

    private final EmailService emailService;

    private final PDFGenerator pdfGenerator;

    private final QueueService queueService;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            UserRepository userRepository,
                            OrderDtoMapper orderDtoMapper,
                            TicketDisplayDtoMapper ticketDisplayDtoMapper,
                            TicketRepository ticketRepository,
                            TaskScheduler taskScheduler,
                            EmailService emailService,
                            PDFGenerator pdfGenerator,
                            QueueService queueService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.orderDtoMapper = orderDtoMapper;
        this.ticketDisplayDtoMapper = ticketDisplayDtoMapper;
        this.ticketRepository = ticketRepository;
        this.taskScheduler = taskScheduler;
        this.emailService = emailService;
        this.pdfGenerator = pdfGenerator;
        this.queueService = queueService;
    }

    /**
     * Retrieves an order by its unique identifier.
     *
     * @param orderId The unique identifier of the order to retrieve.
     * @return An OrderDisplayDto containing information about the order if found, or null if no order with the given ID exists.
     */
    @Override
    public OrderDisplayDto findById(Integer orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        return orderOptional.map(orderDtoMapper).orElse(null);
    }

    /**
     * Retrieves a list of orders associated with a specific user.
     *
     * @param userId    The unique identifier of the user whose orders are to be retrieved.
     * @param pageable  Pagination and sorting information for the result list.
     * @return A list of OrderDisplayDto containing information about the user's orders, as paginated by the provided pageable, or throws a NonExistentException if no user with the given ID is found.
     * @throws NonExistentException if the user with the specified ID does not exist.
     */
    @Override
    public List<OrderDisplayDto> findAllOrderByUserId(Integer userId, Pageable pageable) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            Page<Order> orderHistory = orderRepository.findByUserAndOrderStatusOrderByOrderIdDesc(userOptional.get(), Order.Status.SUCCESS.getStatusValue(), pageable);
            return orderDtoMapper.mapOrderHistoryDto(orderHistory.getContent());
        }
        throw new NonExistentException("User", userId);
    }

    /**
     * Retrieves a list of all orders in the system.
     *
     * @return A list of OrderDisplayDto containing information about all orders in the system.
     */
    @Override
    public List<OrderDisplayDto> findAllOrder() {
        return orderRepository.findAll()
                .stream()
                .map(orderDtoMapper)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a UserDisplayDto by searching for an Order using its identifier.
     *
     * @param orderId The identifier of the Order.
     * @return A UserDisplayDto representing the purchaser of the specified Order.
     */
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

    /**
     * Saves an Order and schedules a task to check its payment status after 10 minutes.
     *
     * @param order The Order to be saved.
     * @return The saved Order.
     */
    @Override
    public Order saveOrder(Order order) {

        Order savedOrder = orderRepository.save(order);


        // Set the scheduled check time for 10 minutes from now
        LocalDateTime scheduledCheckTime = LocalDateTime.now().plusMinutes(10);

        // Schedule a task to check this order after 10 minutes
        taskScheduler.schedule(() -> checkOrderPaymentStatus(order),
                Date.from(scheduledCheckTime.atZone(ZoneId.systemDefault()).toInstant()));
        return savedOrder;
    }

    /**
     * Updates an existing Order with the information provided in the OrderUpdateDto.
     *
     * @param orderUpdateDto The data for updating the Order.
     * @return The updated Order.
     */
    @Override
    public Order updateOrder(OrderUpdateDto orderUpdateDto) {
        Optional<Order> orderOptional = orderRepository.findById(orderUpdateDto.orderId());

        if (orderOptional.isPresent()) {
            Order existingOrder = orderOptional.get();
            orderDtoMapper.update(orderUpdateDto, existingOrder);
            orderRepository.save(existingOrder);

            return existingOrder;
        }
        throw new NonExistentException("Order", orderUpdateDto.orderId());
    }

    /**
     * Adds a Ticket to an existing Order by linking the Ticket and Order together.
     *
     * @param ticketId The identifier of the Ticket to add to the Order.
     * @param orderId  The identifier of the Order to which the Ticket will be added.
     * @return An OrderDisplayDto representing the updated Order.
     */
    @Override
    public OrderDisplayDto addTicketToOrder(Integer ticketId, Integer orderId) {
        Optional<Ticket> ticketOptional = ticketRepository.findById(ticketId);
        Optional<Order> orderOptional = orderRepository.findById(orderId);

        if (ticketOptional.isPresent() && orderOptional.isPresent()) {
            Ticket ticket = ticketOptional.get();
            Order order = orderOptional.get();
            String ticketHolder = ticket.getTicketHolder();
            if (ticket.getOrder() != null) {
                if (!ticket.getOrder().getOrderId().equals(orderId)) {
                    throw new AlreadyExistsException("Ticket already linked to any another order");
                } else {
                    throw new AlreadyExistsException("Ticket already linked to this order");
                }
            }
            List<Object[]> ticketList = orderRepository.getTicketByOrderId(orderId);
            List<TicketDisplayDto> tickets = ticketDisplayDtoMapper.mapTicketObjects(ticketList);
            for (TicketDisplayDto ticketIter : tickets) {
                if (ticketIter.ticketHolder() != null && ticketIter.ticketHolder().equals(ticketHolder)) {
                    throw new AlreadyExistsException("User already owns one of the tickets linked to order");
                }
            }


            order.addTicket(ticket);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
            ticket.setOrder(order);
            ticket.setUpdatedAt(LocalDateTime.now());
            ticketRepository.save(ticket);
            return orderDtoMapper.apply(order);
        } else if (ticketOptional.isEmpty()) {
            throw new NonExistentException("Ticket does not exist");
        } else {
            throw new NonExistentException("Order does not exist");
        }
    }

    /**
     * Removes a Ticket from an existing Order by updating the Order to no longer include the Ticket.
     *
     * @param ticketId The identifier of the Ticket to remove from the Order.
     * @param orderId  The identifier of the Order from which the Ticket will be removed.
     * @return An OrderDisplayDto representing the updated Order.
     */
    @Override
    public OrderDisplayDto removeTicketInOrder(Integer ticketId, Integer orderId) {
        Optional<Ticket> ticketOptional = ticketRepository.findById(ticketId);
        Optional<Order> orderOptional = orderRepository.findById(orderId);

        if (ticketOptional.isPresent() && orderOptional.isPresent()) {
            Order order = orderOptional.get();

            order.removeTicket(ticketId);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
            ticketRepository.deleteById(ticketId);
            return orderDtoMapper.apply(order);
        } else if (ticketOptional.isEmpty()) {
            throw new NonExistentException("Ticket does not exist");
        } else {
            throw new NonExistentException("Order does not exist");
        }
    }

    /**
     * Checks the payment status of the given Order and takes appropriate actions if the payment status is "Processing."
     *
     * @param order The Order to check.
     */
    @Override
    public void checkOrderPaymentStatus(Order order) {
        Order realTimeOrder = orderRepository.findById(order.getOrderId()).orElse(null);
        if (realTimeOrder != null) {
            // Check the payment status of the order
            if (realTimeOrder.getOrderStatus().equals(Order.Status.PROCESSING.getStatusValue())) {

                // Remove or mark the order as canceled, depending on your business logic
                cancelOrder(order);
            }
        } else {
            throw new NonExistentException("Order does not exist");
        }
    }

    /**
     * Cancels an Order by updating its status, marking it as "CANCELLED," and removing linked tickets.
     *
     * @param order The Order to be canceled.
     */
    @Override
    public void cancelOrder(Order order){
        //updating status to cancelled
        order.setTicketSet(new HashSet<>());
        order.setOrderStatus(Order.Status.CANCELLED.getStatusValue());
        order.setDeletedAt(LocalDateTime.now());
        orderRepository.save(order);

        //removing linked tickets
        List<Ticket> ticketSet = ticketRepository.findAllByOrder(order);
        ticketRepository.deleteAllInBatch(ticketSet);
    }

    /**
     * Cancels a list of orders in a batch process.
     *
     * @param orderList A list of orders to be canceled.
     */
    private void cancelAllOrder(List<Order> orderList) {
        // Updating the status of all orders to "CANCELLED"
        for (Order order : orderList) {
//            order.setTicketSet(new HashSet<>());
            order.setOrderStatus(Order.Status.CANCELLED.getStatusValue());
            order.setDeletedAt(LocalDateTime.now());
        }

        // Save the updated orders in a single batch
        orderRepository.saveAll(orderList);

        // Removing linked tickets for all orders in a single batch
        List<Ticket> ticketsToRemove = ticketRepository.findAllByOrderIn(orderList);
        ticketRepository.deleteAllInBatch(ticketsToRemove);
    }

    /**

     * Completes an Order by updating its status to "SUCCESS" and generates PDFs for the order and its linked tickets.
     *
     * @param order The Order to be marked as "SUCCESS."
     * @return The updated Order.
     */
    @Override
    public Order completeOrder(Order order) {
        //updating status to success
        order.setOrderStatus(Order.Status.SUCCESS.getStatusValue());
        Order updatedOrder = orderRepository.save(order);

        //add here generate ticket pdf and email call
        User user = order.getUser();
        ArrayList<FileNameRecord> pdfList = new ArrayList<>();
        try{
            FileNameRecord orderPdf = new FileNameRecord("Order_" + order.getOrderId() + ".pdf", pdfGenerator.generateOrderDetails(order));
            pdfList.add(orderPdf);
            for (Ticket t : order.getTicketSet()){
                FileNameRecord ticketPdf = new FileNameRecord("Ticket_" + t.getTicketId() + ".pdf", pdfGenerator.generateTicketQRCode(t, order.getEvent().getEventDate().plusDays(1)));
                pdfList.add(ticketPdf);
            }
            emailService.send(user.getEmail(), "Order Completed", "Dear " + user.getName() + ", \nThank you for your order, please refer to the documents attached for the event.", pdfList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiRequestException(e.getMessage());
        }

        try {
            queueService.removeFromQueue(user, order.getEvent());
        } catch (NonExistentException e) {
            // ignore if presale, as presale does not need queue
        }
        return updatedOrder;
    }

    /**
     * Removes an Order by its ID.
     *
     * @param orderId The ID of the Order to be removed.
     */
    @Override
    public void removeOrder(Integer orderId) {
        orderRepository.deleteOrderById(orderId);
    }

    /**
     * Schedules a task to cancel all processing orders that have expired every 12 hours.
     * Processing orders that have been created for more than 10 minutes are considered expired.
     */
    @Override
    @Scheduled(fixedRate = 12 * 60 * 60 * 1000) // 12 hours in milliseconds
    public void scheduleCancelProcessingOrder() {
        LocalDateTime currentTime = LocalDateTime.now();

        //filter out processing orders that has passed its expiry time (10 minutes after created at time)
        List<Order> ordersToCancel = orderRepository.findAllByOrderStatus(Order.Status.PROCESSING.getStatusValue())
                .stream()
                .filter(order -> order.getCreatedAt().plusMinutes(10).isBefore(currentTime))
                .toList();

        //cancel all the filtered out orders
        cancelAllOrder(ordersToCancel);
    }

    /**
     * Retrieves a list of OrderDisplayDto objects for all orders associated with a specific event.
     *
     * @param pageable The pageable object to control pagination.
     * @param eventId  The ID of the associated event.
     * @return A list of OrderDisplayDto objects for the specified event.
     */
    @Override
    public List<OrderDisplayDto> findAllOrderByEventId(Pageable pageable, Integer eventId){
        List<OrderDisplayDto> orderDisplayDtoList =orderDtoMapper.map(orderRepository.findAllByEventEventId(eventId,pageable));
        return orderDisplayDtoList;
    }
}
