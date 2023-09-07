package com.authenticket.authenticket;

import com.authenticket.authenticket.model.TicketCategory;
import lombok.Data;

@Data
public class JSONFormat {
    private Integer eventId;
    private TicketCategoryJSON[] data;
}
