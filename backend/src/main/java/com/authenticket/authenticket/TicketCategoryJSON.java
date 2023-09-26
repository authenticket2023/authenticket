package com.authenticket.authenticket;

import lombok.Data;

@Data
public class TicketCategoryJSON {
    private Integer catId;
    private Double price;
    private Integer availableTickets;
    private Integer totalTicketsPerCat;
}
