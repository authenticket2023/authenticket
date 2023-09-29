package com.authenticket.authenticket.dto.order;

import com.authenticket.authenticket.model.User;

public record OrderUpdateDto (Integer orderId,
                              Double orderAmount,
                              User user
                              ){
}
