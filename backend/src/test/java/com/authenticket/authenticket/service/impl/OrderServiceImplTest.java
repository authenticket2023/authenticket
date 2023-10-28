package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.order.OrderDtoMapper;
import com.authenticket.authenticket.dto.ticket.TicketDisplayDtoMapper;
import com.authenticket.authenticket.repository.OrderRepository;
import com.authenticket.authenticket.repository.TicketRepository;
import com.authenticket.authenticket.repository.UserRepository;
import com.authenticket.authenticket.service.EmailService;
import com.authenticket.authenticket.service.PDFGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.TaskScheduler;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceImplTest {

    private OrderServiceImpl underTest;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TaskScheduler taskScheduler;

    @Mock
    private JavaMailSenderImpl javaMailSender;

    @InjectMocks
    private EmailService emailService;

    @InjectMocks
    private PDFGenerator pdfGenerator;

    @InjectMocks
    private TicketDisplayDtoMapper ticketDisplayDtoMapper;

    @InjectMocks
    private OrderDtoMapper orderDtoMapper;

    @Test
    void findById() {
        underTest = new OrderServiceImpl(orderRepository,
                userRepository,
                orderDtoMapper,
                ticketDisplayDtoMapper,
                ticketRepository,
                taskScheduler,
                emailService,
                pdfGenerator
        );
    }

    @Test
    void findAllOrderByUserId() {
    }

    @Test
    void findAllOrder() {
    }

    @Test
    void findUserByOrderId() {
    }

    @Test
    void saveOrder() {
    }

    @Test
    void updateOrder() {
    }

    @Test
    void addTicketToOrder() {
    }

    @Test
    void removeTicketInOrder() {
    }

    @Test
    void checkOrderPaymentStatus() {
    }

    @Test
    void cancelOrder() {
    }

    @Test
    void cancelAllOrder() {
    }

    @Test
    void completeOrder() {
    }

    @Test
    void removeOrder() {
    }

    @Test
    void scheduleCancelProcessingOrder() {
    }

    @Test
    void findAllOrderByEventId() {
    }
}