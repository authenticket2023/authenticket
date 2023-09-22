package com.authenticket.authenticket.dto.eventOrganiser;

import com.authenticket.authenticket.model.Admin;

public record EventOrganiserUpdateDto(Integer organiserId,
                                      String name,
                                      String description,
                                      String password,
                                      Boolean enabled,
                                      String reviewStatus,
                                      String reviewRemarks,
                                      Admin reviewedBy
) {
}
