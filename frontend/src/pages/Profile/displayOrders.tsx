import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import { Grid } from "@mui/material";
import Card from "@mui/material/Card";
import { CardActionArea } from "@mui/material";

interface orderInfo {
  order: {
    orderId: number,
    orderAmount: number,
    purchaseDate: string,
    orderStatus: string,
    tickets: {
      ticketId: number,
      eventId: number,
      catId: number,
      sectionId: string,
      rowNo: number,
      seatNo: number,
      ticketHolder: string,
    },
  };
}

export default function displayVenue(props: orderInfo) {
  return (
    <Card>
      
    </Card>
  );
}
