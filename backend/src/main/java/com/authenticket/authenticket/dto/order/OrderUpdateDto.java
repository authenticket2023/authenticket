package com.authenticket.authenticket.dto.order;

import com.authenticket.authenticket.model.User;

import java.time.LocalDate;

/**
 * A data transfer object (DTO) used for updating order information.
 */
public record OrderUpdateDto(
        /**
         * The unique identifier of the order to be updated.
         */
        Integer orderId,

        /**
         * The updated total order amount.
         */
        Double orderAmount,

        /**
         * The updated purchase date of the order.
         */
        LocalDate purchaseDate,

        /**
         * The updated status of the order (e.g., pending, completed, etc.).
         */
        String orderStatus,

        /**
         * The user associated with this order, represented as a User object.
         */
        User user
) {
}
