import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import { Grid } from "@mui/material";
import Card from "@mui/material/Card";
import { CardActionArea } from "@mui/material";
import React, { useEffect, useState } from "react";

interface orderInfo {
  order: {
    orderId: number;
    eventId: number;
    eventName: string;
    eventDate: string;
    venueName: string;
    orderAmount: number;
    purchaseDate: string;
    orderStatus: string;
    ticketSet: any;
  };
}

interface ticketInfo {
  ticket: {
    ticketId: number;
    eventId: number;
    catId: string;
    sectionId: number;
    rowNo: number;
    seatNo: number;
    ticketHolder: string;
  };
  index: number;
}

function compareTicketId(a: any, b: any) {
  return a.ticketId - b.ticketId;
}

function DisplayTicket(props: ticketInfo) {
  return (
    <Box>
      <Grid container>
        <Grid item xs={2}>
          <Typography variant="subtitle1" sx={{ color: "grey", fontSize: 14 }}>
            {props.index + 1}: {props.ticket.ticketHolder}
          </Typography>
        </Grid>
        <Grid item xs={8}>
          <Typography variant="subtitle1" sx={{ color: "grey", fontSize: 14 }}>
            Category {props.ticket.catId}, Section {props.ticket.sectionId}, Row{" "}
            {props.ticket.rowNo}, Seat {props.ticket.seatNo}
          </Typography>
        </Grid>
      </Grid>
    </Box>
  );
}

export default function DisplayOrder(props: orderInfo) {
  return (
    <Card
      variant="outlined"
      elevation={0}
      sx={{ borderColor: "grey", borderRadius: 3, height: 390}}
    >
      <Box margin={3} paddingBottom={2} borderBottom={1} borderColor="grey">
        <Typography marginBottom={1} variant="h5" sx={{ fontWeight: "bold" }}>
          {" "}
          {props.order.eventName}{" "}
        </Typography>
        <Typography variant="subtitle1" sx={{ color: "grey", fontSize: 14 }}>
          Order ID: {props.order.orderId}
        </Typography>
        <Typography variant="subtitle1" sx={{ color: "grey", fontSize: 14 }}>
          Event Date: {props.order.eventDate}
        </Typography>
        <Typography variant="subtitle1" sx={{ color: "grey", fontSize: 14 }}>
          Location: {props.order.venueName}
        </Typography>
        <Typography variant="subtitle1" sx={{ color: "grey", fontSize: 14 }}>
          Purchased Date: {props.order.purchaseDate}
        </Typography>
        <Typography variant="subtitle1" sx={{ color: "grey", fontSize: 14 }}>
          Number of Tickets: {props.order.ticketSet.length}
        </Typography>
      </Box>
      <Box margin={3}>
        <Typography marginBottom={2} sx={{ color: "grey", fontSize: 14 }}>
          Ticket Information:
        </Typography>
        {props.order.ticketSet
          .sort(compareTicketId)
          .map((ticketInfo: any, index: number) => (
            <React.Fragment key={index}>
              <DisplayTicket ticket={ticketInfo} index={index}></DisplayTicket>
            </React.Fragment>
          ))}
      </Box>
    </Card>
  );
}
