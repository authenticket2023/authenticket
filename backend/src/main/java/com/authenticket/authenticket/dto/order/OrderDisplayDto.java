package com.authenticket.authenticket.dto.order;

import com.authenticket.authenticket.dto.user.UserDisplayDto;
import com.authenticket.authenticket.dto.user.UserFullDisplayDto;
import com.authenticket.authenticket.model.User;

import java.time.LocalDate;
import java.util.Set;

public record OrderDisplayDto(
        Integer orderId,
        Double orderAmount,
        LocalDate purchaseDate,
        UserDisplayDto purchaser
) {
}



