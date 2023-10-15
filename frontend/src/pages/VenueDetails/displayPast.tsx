import React, { useEffect, useState } from "react";
import { Navigate } from "react-router-dom";
import Tabs from "@mui/material/Tabs";
import Tab from "@mui/material/Tab";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import { Button, Grid, InputAdornment, TextField } from "@mui/material";
import SearchIcon from "@mui/icons-material/Search";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import CardMedia from "@mui/material/CardMedia";
import { CardActionArea } from "@mui/material";
import { format } from "date-fns";
import GroupsIcon from "@mui/icons-material/Groups";

interface eventInfo {
  event: {
    eventId: number;
    eventName: string;
    eventDescription: string;
    eventImage: string;
    eventType: string;
    eventDate: string;
    totalTickets: number;
    eventVenue: string;
  };
}

function formatDate(dateString: string): string {
  const date = new Date(dateString);
  return format(date, "dd MMMM yyyy, h:mm a");
}

export default function displayPast(props: eventInfo) {
  return (
    <Card
      sx={{
        minHeight: 180,
        mt: 3,
        maxHeight: 180,
        backgroundColor: "#F8F8F8",
        borderRadius: "2px",
        backgroundImage: `url('${process.env.REACT_APP_S3_URL}/event_images/${props.event.eventImage}')`,
        backgroundSize: "cover",
        backgroundRepeat: "no-repeat",
        backgroundPosition: "center",
      }}
    >
      <CardActionArea
        href={`/EventDetails/${props.event.eventId}`}
      >
        <Box sx = {{minHeight: 180}}>
        </Box>
      </CardActionArea>
    </Card>
  );
}
