package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.order.OrderDisplayDto;
import com.authenticket.authenticket.dto.order.OrderDtoMapper;
import com.authenticket.authenticket.dto.ticket.TicketDisplayDto;
import com.authenticket.authenticket.dto.order.OrderUpdateDto;
import com.authenticket.authenticket.dto.ticket.TicketDisplayDtoMapper;
import com.authenticket.authenticket.dto.user.UserDisplayDto;
import com.authenticket.authenticket.dto.user.UserDtoMapper;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.*;
import com.authenticket.authenticket.repository.OrderRepository;
import com.authenticket.authenticket.repository.TicketRepository;
import com.authenticket.authenticket.repository.UserRepository;
import com.authenticket.authenticket.service.PDFGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TaskScheduler taskScheduler;

    @Mock
    private JavaMailSenderImpl javaMailSender;

    @InjectMocks
    private QRCodeGeneratorImpl qrCodeGenerator;

    @InjectMocks
    private QueueServiceImpl queueService;

    @InjectMocks
    private JwtServiceImpl jwtService;

    private OrderServiceImpl underTest;

    @BeforeEach
    public void setUp() {
        UserDtoMapper userDtoMapper = new UserDtoMapper(passwordEncoder);
        TicketDisplayDtoMapper ticketDisplayDtoMapper = new TicketDisplayDtoMapper();
        OrderDtoMapper orderDtoMapper = new OrderDtoMapper(userDtoMapper, ticketRepository, ticketDisplayDtoMapper);
        PDFGenerator pdfGenerator = new PDFGeneratorImpl(qrCodeGenerator, jwtService);
        EmailServiceImpl emailService = new EmailServiceImpl(javaMailSender);
        underTest = new OrderServiceImpl(
                orderRepository,
                userRepository,
                orderDtoMapper,
                ticketDisplayDtoMapper,
                ticketRepository,
                taskScheduler,
                emailService,
                pdfGenerator,
                queueService);
    }

    @Test
    public void testFindById_ValidOrderId() {
        // Arrange
        User user = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .password("password123")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .profileImage("profile.jpg")
                .enabled(true)
                .build();

        Event event = Event.builder()
                .eventName("Sample Event")
                .eventDescription("This is a sample event description.")
                .eventDate(LocalDateTime.now().plusDays(7))
                .otherEventInfo("Additional event information goes here.")
                .eventImage("event.jpg")
                .ticketSaleDate(LocalDateTime.now().plusDays(1))
                .reviewStatus(Event.ReviewStatus.APPROVED.getStatusValue())
                .reviewRemarks("Event is approved")
                .isEnhanced(true)
                .hasPresale(true)
                .hasPresaleUsers(true)
                .organiser(EventOrganiser.builder().build())
                .venue(new Venue())
                .artists(new HashSet<>())
                .eventType(new EventType())
                .build();

        Order order = Order.builder()
                .orderId(1)
                .orderAmount(100.0)
                .purchaseDate(LocalDate.now())
                .orderStatus(Order.Status.PROCESSING.getStatusValue())
                .user(user)
                .event(event)
                .build();


        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));

        // Act
        OrderDisplayDto foundOrder = underTest.findById(order.getOrderId());

        // Assert
        assertNotNull(foundOrder);
        assertEquals(order.getOrderId(), foundOrder.orderId());
        // Add additional assertions based on your implementation.
    }

    @Test
    public void testFindById_NonExistentOrderId() {
        // Arrange
        int orderId = -1;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        System.out.print(underTest.findById(orderId));

        // Act and Assert
        assertNull(underTest.findById(orderId));
        // Add additional assertions based on your implementation.
    }

    @Test
    public void testFindAllOrderByUserId_ExistingUser() {
        // Arrange
        int userId = 1;
        Pageable pageable = mock(Pageable.class);

        User user = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .password("password123")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .profileImage("profile.jpg")
                .enabled(true)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        List<Order> orders = new ArrayList<>(); // Create a list of mock orders

        Event event = Event.builder()
                .eventName("Sample Event")
                .eventDescription("This is a sample event description.")
                .eventDate(LocalDateTime.now().plusDays(7))
                .otherEventInfo("Additional event information goes here.")
                .eventImage("event.jpg")
                .ticketSaleDate(LocalDateTime.now().plusDays(1))
                .reviewStatus(Event.ReviewStatus.APPROVED.getStatusValue())
                .reviewRemarks("Event is approved")
                .isEnhanced(true)
                .hasPresale(true)
                .hasPresaleUsers(true)
                .organiser(EventOrganiser.builder().build())
                .venue(new Venue())
                .artists(new HashSet<>())
                .eventType(new EventType())
                .build();

        Order order = Order.builder()
                .orderId(1)
                .orderAmount(100.0)
                .purchaseDate(LocalDate.now())
                .orderStatus(Order.Status.PROCESSING.getStatusValue())
                .user(user)
                .event(event)
                .build();

        orders.add(order);

        Page<Order> pageOfOrders = new PageImpl<>(orders);
        when(orderRepository.findByUser(user, pageable)).thenReturn(pageOfOrders);

        // Act
        List<OrderDisplayDto> result = underTest.findAllOrderByUserId(userId, pageable);

        // Assert
        assertNotNull(result);
    }

    @Test
    public void testFindAllOrderByUserId_nonExistentUser() {
        Integer userId = 1;
        Pageable pageable = mock(Pageable.class);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NonExistentException.class, () -> underTest.findAllOrderByUserId(userId, pageable));
    }

    @Test
    public void testFindAllOrder() {
        // Arrange
        List<Order> orders = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        LocalDateTime currentDateTime = LocalDateTime.now();
        User user = User.builder()
                .userId(1)
                .name("John Doe")
                .email("john.doe@example.com")
                .password("password123")
                .dateOfBirth(currentDate)
                .profileImage("profile.jpg")
                .enabled(true)
                .build();

        Event event = Event.builder()
                .eventName("Sample Event")
                .eventDescription("This is a sample event description.")
                .eventDate(currentDateTime.plusDays(7))
                .otherEventInfo("Additional event information goes here.")
                .eventImage("event.jpg")
                .ticketSaleDate(currentDateTime.plusDays(1))
                .reviewStatus(Event.ReviewStatus.APPROVED.getStatusValue())
                .reviewRemarks("Event is approved")
                .isEnhanced(true)
                .hasPresale(true)
                .hasPresaleUsers(true)
                .organiser(EventOrganiser.builder().build())
                .venue(new Venue())
                .artists(new HashSet<>())
                .eventType(new EventType())
                .build();

        Order order = Order.builder()
                .orderId(1)
                .orderAmount(100.0)
                .purchaseDate(currentDate)
                .orderStatus(Order.Status.PROCESSING.getStatusValue())
                .user(user)
                .event(event)
                .build();

        orders.add(order);
        
        when(orderRepository.findAll()).thenReturn(orders);

        List<OrderDisplayDto> expectedOrderDisplayDtos = new ArrayList<>();
        expectedOrderDisplayDtos.add(new OrderDisplayDto(
                order.getOrderId(),
                order.getEvent().getEventId(),
                order.getEvent().getEventName(),
                order.getEvent().getEventDate(),
                order.getEvent().getVenue().getVenueName(),
                order.getOrderAmount(),
                order.getPurchaseDate(),
                order.getOrderStatus(),
                new UserDisplayDto(
                        user.getUserId(),
                        user.getName(),
                        user.getEmail(),
                        user.getDateOfBirth(),
                        user.getProfileImage(),
                        "USER"
                ),
                new HashSet<>()
        ));

        // Act
        List<OrderDisplayDto> result = underTest.findAllOrder();

        // Assert
        assertEquals(expectedOrderDisplayDtos, result);
    }

    @Test
    public void testFindUserByOrderId_OrderExists() {
        // Arrange
        List<Order> orders = new ArrayList<>();
        Set<TicketDisplayDto> ticketDisplayDtos = new HashSet<>();

        User user = User.builder()
                .userId(1)
                .name("John Doe")
                .email("john.doe@example.com")
                .password("password123")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .profileImage("profile.jpg")
                .enabled(true)
                .build();

        Event event = Event.builder()
                .eventName("Sample Event")
                .eventDescription("This is a sample event description.")
                .eventDate(LocalDateTime.now().plusDays(7))
                .otherEventInfo("Additional event information goes here.")
                .eventImage("event.jpg")
                .ticketSaleDate(LocalDateTime.now().plusDays(1))
                .reviewStatus(Event.ReviewStatus.APPROVED.getStatusValue())
                .reviewRemarks("Event is approved")
                .isEnhanced(true)
                .hasPresale(true)
                .hasPresaleUsers(true)
                .organiser(EventOrganiser.builder().build())
                .venue(new Venue())
                .artists(new HashSet<>())
                .eventType(new EventType())
                .build();

        Order order = Order.builder()
                .orderId(1)
                .orderAmount(100.0)
                .purchaseDate(LocalDate.now())
                .orderStatus(Order.Status.PROCESSING.getStatusValue())
                .user(user)
                .event(event)
                .build();

        orders.add(order);

        TicketCategory ticketCategory = TicketCategory.builder().categoryId(1).categoryName("Test Category").build();

        Ticket ticket = Ticket.builder()
                .ticketId(1)
                .ticketPricing(new TicketPricing(ticketCategory,event, 10.0))
                .section(new Section())
                .rowNo(10)
                .seatNo(20)
                .ticketHolder("John Doe")
                .order(order)
                .checkedIn(Boolean.FALSE)
                .build();


        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));

        UserDisplayDto userDisplayDto = new UserDisplayDto(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getDateOfBirth(),
                user.getProfileImage(),
                "USER"
        );

        TicketDisplayDto ticketDisplayDto = new TicketDisplayDto(
                ticket.getTicketId(),
                order.getEvent().getEventId(),
                ticket.getTicketPricing().getCat().getCategoryId(),
                ticket.getSection().getSectionId(),
                ticket.getRowNo(),
                ticket.getSeatNo(),
                ticket.getTicketHolder(),
                ticket.getOrder().getOrderId(),
                ticket.getCheckedIn()
        );
        ticketDisplayDtos.add(ticketDisplayDto);

        List<OrderDisplayDto> expectedOrderDisplayDtos = new ArrayList<>();
        expectedOrderDisplayDtos.add(new OrderDisplayDto(
                order.getOrderId(),
                order.getEvent().getEventId(),
                order.getEvent().getEventName(),
                order.getEvent().getEventDate(),
                order.getEvent().getVenue().getVenueName(),
                order.getOrderAmount(),
                order.getPurchaseDate(),
                order.getOrderStatus(),
                userDisplayDto,
                ticketDisplayDtos
        ));

        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));

        // Act
        UserDisplayDto result = underTest.findUserByOrderId(order.getOrderId());

        // Assert
        assertEquals(expectedOrderDisplayDtos.get(0).purchaser(), result);
    }

    @Test
    public void testFindUserByOrderId_OrderDoesNotExist() {
        // Arrange
        Integer orderId = 1;

        // Mock the behavior of the orderRepository to return an empty Optional
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act
        UserDisplayDto result = underTest.findUserByOrderId(orderId);

        // Assert
        assertNull(result);
    }

    @Test
    public void testSaveOrder() {
        // Arrange

        User user = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .password("password123")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .profileImage("profile.jpg")
                .enabled(true)
                .build();

        Event event = Event.builder()
                .eventName("Sample Event")
                .eventDescription("This is a sample event description.")
                .eventDate(LocalDateTime.now().plusDays(7))
                .otherEventInfo("Additional event information goes here.")
                .eventImage("event.jpg")
                .ticketSaleDate(LocalDateTime.now().plusDays(1))
                .reviewStatus(Event.ReviewStatus.APPROVED.getStatusValue())
                .reviewRemarks("Event is approved")
                .isEnhanced(true)
                .hasPresale(true)
                .hasPresaleUsers(true)
                .organiser(EventOrganiser.builder().build())
                .venue(new Venue())
                .artists(new HashSet<>())
                .eventType(new EventType())
                .build();

        Order order = Order.builder()
                .orderId(1)
                .orderAmount(100.0)
                .purchaseDate(LocalDate.now())
                .orderStatus(Order.Status.PROCESSING.getStatusValue())
                .user(user)
                .event(event)
                .build();


        // Mock the behavior of orderRepository.save() to return the saved order
        when(orderRepository.save(order)).thenReturn(order);

        // Act
        Order savedOrder = underTest.saveOrder(order);

        // Assert
        assertNotNull(savedOrder);

    }

    @Test
    public void testUpdateOrder_OrderExists() {
        // Arrange
        User user = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .password("password123")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .profileImage("profile.jpg")
                .enabled(true)
                .build();

        Event event = Event.builder()
                .eventName("Sample Event")
                .eventDescription("This is a sample event description.")
                .eventDate(LocalDateTime.now().plusDays(7))
                .otherEventInfo("Additional event information goes here.")
                .eventImage("event.jpg")
                .ticketSaleDate(LocalDateTime.now().plusDays(1))
                .reviewStatus(Event.ReviewStatus.APPROVED.getStatusValue())
                .reviewRemarks("Event is approved")
                .isEnhanced(true)
                .hasPresale(true)
                .hasPresaleUsers(true)
                .organiser(EventOrganiser.builder().build())
                .venue(new Venue())
                .artists(new HashSet<>())
                .eventType(new EventType())
                .build();

        Order order = Order.builder()
                .orderId(1)
                .orderAmount(100.0)
                .purchaseDate(LocalDate.now())
                .orderStatus(Order.Status.PROCESSING.getStatusValue())
                .user(user)
                .event(event)
                .build();

        OrderUpdateDto orderUpdateDto = new OrderUpdateDto(
                order.getOrderId(),
                order.getOrderAmount(),
                order.getPurchaseDate(),
                order.getOrderStatus(),
                user
        );

        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));

        // Act
        Order updatedOrder = underTest.updateOrder(orderUpdateDto);

        // Assert
        assertNotNull(updatedOrder);

        // Verify that orderRepository.save(existingOrder) is called once
        verify(orderRepository, times(1)).save(order);

    }

    @Test
    public void testUpdateOrder_OrderDoesNotExist() {
        // Arrange
        User user = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .password("password123")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .profileImage("profile.jpg")
                .enabled(true)
                .build();

        OrderUpdateDto orderUpdateDto = new OrderUpdateDto(
                2, // Non-existing order ID
                150.0,
                LocalDate.now(),
                "COMPLETED",
                user
        );
    }

    @Test
    public void testRemoveTicketInOrder_TicketAndOrderExist() {
        // Arrange
        List<Order> orders = new ArrayList<>();
        Set<TicketDisplayDto> ticketDisplayDtos = new HashSet<>();

        User user = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .password("password123")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .profileImage("profile.jpg")
                .enabled(true)
                .build();

        Event event = Event.builder()
                .eventName("Sample Event")
                .eventDescription("This is a sample event description.")
                .eventDate(LocalDateTime.now().plusDays(7))
                .otherEventInfo("Additional event information goes here.")
                .eventImage("event.jpg")
                .ticketSaleDate(LocalDateTime.now().plusDays(1))
                .reviewStatus(Event.ReviewStatus.APPROVED.getStatusValue())
                .reviewRemarks("Event is approved")
                .isEnhanced(true)
                .hasPresale(true)
                .hasPresaleUsers(true)
                .organiser(EventOrganiser.builder().build())
                .venue(new Venue())
                .artists(new HashSet<>())
                .eventType(new EventType())
                .build();

        Set<Ticket> ticketSet = new HashSet<>();
        Order order = Order.builder()
                .orderId(1)
                .orderAmount(100.0)
                .purchaseDate(LocalDate.now())
                .orderStatus(Order.Status.PROCESSING.getStatusValue())
                .user(user)
                .event(event)
                .ticketSet(ticketSet)
                .build();

        orders.add(order);

        TicketCategory ticketCategory = TicketCategory.builder().categoryId(1).categoryName("Test Category").build();

        Ticket ticket = Ticket.builder()
                .ticketId(1)
                .ticketPricing(new TicketPricing(ticketCategory,event, 10.0))
                .section(new Section())
                .rowNo(10)
                .seatNo(20)
                .ticketHolder("John Doe")
                .order(order)
                .checkedIn(Boolean.FALSE)
                .build();

        UserDisplayDto userDisplayDto = new UserDisplayDto(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getDateOfBirth(),
                user.getProfileImage(),
                "user"
        );

        TicketDisplayDto ticketDisplayDto = new TicketDisplayDto(
                ticket.getTicketId(),
                order.getEvent().getEventId(),
                ticket.getTicketPricing().getCat().getCategoryId(),
                ticket.getSection().getSectionId(),
                ticket.getRowNo(),
                ticket.getSeatNo(),
                ticket.getTicketHolder(),
                ticket.getOrder().getOrderId(),
                ticket.getCheckedIn()
        );
        ticketDisplayDtos.add(ticketDisplayDto);

        List<OrderDisplayDto> expectedOrderDisplayDtos = new ArrayList<>();
        expectedOrderDisplayDtos.add(new OrderDisplayDto(
                order.getOrderId(),
                order.getEvent().getEventId(),
                order.getEvent().getEventName(),
                order.getEvent().getEventDate(),
                order.getEvent().getVenue().getVenueName(),
                order.getOrderAmount(),
                order.getPurchaseDate(),
                order.getOrderStatus(),
                userDisplayDto,
                ticketDisplayDtos
        ));

        when(ticketRepository.findById(ticket.getTicketId())).thenReturn(Optional.of(ticket));
        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));

        // Act
        OrderDisplayDto result = underTest.removeTicketInOrder(ticket.getTicketId(), order.getOrderId());

        // Assert
        assertNotNull(result);
        verify(orderRepository, times(1)).save(order);
        verify(ticketRepository, times(1)).deleteById(ticket.getTicketId());
    }

    @Test
    public void testCheckOrderPaymentStatus_OrderExistsAndProcessing() {
        // Arrange
        List<Order> orders = new ArrayList<>();
        Set<TicketDisplayDto> ticketDisplayDtos = new HashSet<>();

        User user = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .password("password123")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .profileImage("profile.jpg")
                .enabled(true)
                .build();

        Event event = Event.builder()
                .eventName("Sample Event")
                .eventDescription("This is a sample event description.")
                .eventDate(LocalDateTime.now().plusDays(7))
                .otherEventInfo("Additional event information goes here.")
                .eventImage("event.jpg")
                .ticketSaleDate(LocalDateTime.now().plusDays(1))
                .reviewStatus(Event.ReviewStatus.APPROVED.getStatusValue())
                .reviewRemarks("Event is approved")
                .isEnhanced(true)
                .hasPresale(true)
                .hasPresaleUsers(true)
                .organiser(EventOrganiser.builder().build())
                .venue(new Venue())
                .artists(new HashSet<>())
                .eventType(new EventType())
                .build();
        Set<Ticket> ticketSet = new HashSet<>();
        Order order = Order.builder()
                .orderId(1)
                .orderAmount(100.0)
                .purchaseDate(LocalDate.now())
                .orderStatus(Order.Status.PROCESSING.getStatusValue())
                .user(user)
                .event(event)
                .ticketSet(ticketSet)
                .build();

        orders.add(order);

        TicketCategory ticketCategory = TicketCategory.builder().categoryId(1).categoryName("Test Category").build();

        Ticket ticket = Ticket.builder()
                .ticketId(1)
                .ticketPricing(new TicketPricing(ticketCategory,event, 10.0))
                .section(new Section())
                .rowNo(10)
                .seatNo(20)
                .ticketHolder("John Doe")
                .order(order)
                .checkedIn(Boolean.FALSE)
                .build();

        UserDisplayDto userDisplayDto = new UserDisplayDto(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getDateOfBirth(),
                user.getProfileImage(),
                "user"
        );

        TicketDisplayDto ticketDisplayDto = new TicketDisplayDto(
                ticket.getTicketId(),
                order.getEvent().getEventId(),
                ticket.getTicketPricing().getCat().getCategoryId(),
                ticket.getSection().getSectionId(),
                ticket.getRowNo(),
                ticket.getSeatNo(),
                ticket.getTicketHolder(),
                ticket.getOrder().getOrderId(),
                ticket.getCheckedIn()
        );
        ticketDisplayDtos.add(ticketDisplayDto);

        List<OrderDisplayDto> expectedOrderDisplayDtos = new ArrayList<>();
        expectedOrderDisplayDtos.add(new OrderDisplayDto(
                order.getOrderId(),
                order.getEvent().getEventId(),
                order.getEvent().getEventName(),
                order.getEvent().getEventDate(),
                order.getEvent().getVenue().getVenueName(),
                order.getOrderAmount(),
                order.getPurchaseDate(),
                order.getOrderStatus(),
                userDisplayDto,
                ticketDisplayDtos
        ));

        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));

        // Act
        underTest.checkOrderPaymentStatus(order);

        // Assert
        assertNotNull(order.getOrderStatus());
    }
}