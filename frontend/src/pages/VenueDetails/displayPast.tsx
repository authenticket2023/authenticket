import Box from "@mui/material/Box";
import Card from "@mui/material/Card";
import { CardActionArea } from "@mui/material";

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

export default function displayPast(props: eventInfo) {
  return (
    <Box>
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
        <CardActionArea href={`/EventDetails/${props.event.eventId}`}>
          <Box sx={{ minHeight: 180 }}></Box>
        </CardActionArea>
      </Card>
      {props.event.eventName}
    </Box>
  );
}
