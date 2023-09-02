package com.authenticket.authenticket.dto.admin;

public record AdminUpdateDto(
        Integer adminId,
        String name,
        String email,
        //if password is changed
        String password
) {
}
