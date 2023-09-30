package com.authenticket.authenticket.repository;

import com.authenticket.authenticket.dto.order.OrderDisplayDto;
import com.authenticket.authenticket.model.Admin;
import com.authenticket.authenticket.model.FeaturedEvent;
import com.authenticket.authenticket.model.Order;
import com.authenticket.authenticket.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Page<Order> findByUser(User user, Pageable pageable);

    @Query(nativeQuery = true,
            value = "SELECT " +
                    "T.ticket_id, " +
                    "T.category_id, " +
                    "T.ticket_id, " +
                    "T.section_id, " +
                    "T.row_no, " +
                    "T.seat_no, " +
                    "T.ticket_holder, " +
                    "T.order_id " +
                    "FROM " +
                    "dev.Ticket AS T " +
                    "JOIN " +
                    "dev.Order AS O ON T.order_id = O.order_id " +
                    "WHERE O.order_id = :orderId")
    List<Object[]> getTicketByOrderId(@Param("orderId") Integer orderId);
}
