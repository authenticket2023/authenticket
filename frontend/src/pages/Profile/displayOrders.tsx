import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import { Grid } from "@mui/material";
import Card from "@mui/material/Card";
import { CardActionArea } from "@mui/material";
import React, { useEffect, useState } from "react";
import IconButton, { IconButtonProps } from "@mui/material/IconButton";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import { styled } from "@mui/material/styles";
import Collapse from "@mui/material/Collapse";
import CardContent from "@mui/material/CardContent";
import { format } from 'date-fns';

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

interface ExpandMoreProps extends IconButtonProps {
  expand: boolean;
}

const ExpandMore = styled((props: ExpandMoreProps) => {
  const { expand, ...other } = props;
  return <IconButton {...other} />;
})(({ theme, expand }: { theme: any; expand: any }) => ({
  transform: !expand ? "rotate(0deg)" : "rotate(180deg)",
  marginLeft: "auto",
  transition: theme.transitions.create("transform", {
    duration: theme.transitions.duration.shortest,
  }),
}));

function compareTicketId(a: any, b: any) {
  return a.ticketId - b.ticketId;
}

function formatDateTime(dateString: string): string {
  const date = new Date(dateString);
  return format(date, "dd MMMM yyyy, h:mm a");
}

function formatDate(dateString: string): string {
  const date = new Date(dateString);
  return format(date, "dd MMMM yyyy");
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
  const [expanded, setExpanded] = React.useState(false);

  const handleExpandClick = () => {
    setExpanded(!expanded);
  };
  return (
    <Card
      variant="outlined"
      elevation={0}
      sx={{ borderColor: "grey", borderRadius: 3 }}
    >
      <Box margin={3} marginBottom={2} paddingBottom={2} borderBottom={1} borderColor="grey">
        <Grid container>
          <Grid item xs={8}>
            <Typography
              marginBottom={1}
              variant="h5"
              sx={{ fontWeight: "bold" }}
            >
              {" "}
              {props.order.eventName}{" "}
            </Typography>
            <Typography
              variant="subtitle1"
              sx={{ color: "grey", fontSize: 14 }}
            >
              Order ID: {props.order.orderId}
            </Typography>
            <Typography
              variant="subtitle1"
              sx={{ color: "grey", fontSize: 14 }}
            >
              Event Date: {formatDateTime(props.order.eventDate)}
            </Typography>
            <Typography
              variant="subtitle1"
              sx={{ color: "grey", fontSize: 14 }}
            >
              Location: {props.order.venueName}
            </Typography>
            <Typography
              variant="subtitle1"
              sx={{ color: "grey", fontSize: 14 }}
            >
              Purchased Date: {formatDate(props.order.purchaseDate)}
            </Typography>
            <Typography
              variant="subtitle1"
              sx={{ color: "grey", fontSize: 14 }}
            >
              Number of Tickets: {props.order.ticketSet.length}
            </Typography>
          </Grid>
          <Grid item xs = {4}>
            <Box sx={{display: 'flex', justifyContent:'flex-end'}}>

            <ExpandMore
              expand={expanded}
              onClick={handleExpandClick}
              aria-expanded={expanded}
              aria-label="show more"
              >
              <ExpandMoreIcon />
            </ExpandMore>
              </Box>
          </Grid>
        </Grid>
      </Box>

      <Collapse in={expanded} timeout="auto" unmountOnExit>
        <CardContent >
          <Typography marginBottom={2} sx={{ color: "grey", fontSize: 14 }}>
            Ticket Information:
          </Typography>
          {props.order.ticketSet
            .sort(compareTicketId)
            .map((ticketInfo: any, index: number) => (
              <React.Fragment key={index}>
                <DisplayTicket
                  ticket={ticketInfo}
                  index={index}
                ></DisplayTicket>
              </React.Fragment>
            ))}
        </CardContent>
      </Collapse>
    </Card>
  );
}
