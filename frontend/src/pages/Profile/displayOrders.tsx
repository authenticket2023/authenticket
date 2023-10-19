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
    ticketSet: any[],
  };
}

export default function DisplayOrder(props: orderInfo) {

  
  return (
    <Card variant="outlined"
      elevation={0}
      sx={{ borderColor: "grey", borderRadius: 3 }}>
        {/* {props.order.purchaseDate} */}
    </Card>
  );
}
