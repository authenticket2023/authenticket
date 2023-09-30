package com.authenticket.authenticket.dto.order;

import com.authenticket.authenticket.model.User;

import java.time.LocalDate;

public record OrderUpdateDto (Integer orderId,
                              Double orderAmount,
                              LocalDate purchaseDate,
                              String orderStatus,
                              User user
                              ){
}
