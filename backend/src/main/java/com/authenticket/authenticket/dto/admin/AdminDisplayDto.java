package com.authenticket.authenticket.dto.admin;

//currently only hides the deleted at field
public record AdminDisplayDto(Integer adminId,
                              String name,
                              String email,
                              String role
//                       LocalDateTime deletedAt
) {
}
