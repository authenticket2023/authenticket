import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import { Grid } from "@mui/material";
import Card from "@mui/material/Card";
import { CardActionArea } from "@mui/material";

interface venueInfo {
  venue: {
    venueId: number;
    venueName: string;
    venueLocation: string;
    venueImage: string;
    venueDescription: string;
  };
}

export default function displayVenue(props: venueInfo) {
  return (
    <Card
      sx={{
        minHeight: 200,
        mt: 3,
        maxHeight: 200,
        backgroundColor: "#ffffff",
        borderRadius: "5px",
        backgroundImage: `url('${process.env.REACT_APP_S3_URL}/venue_image/${props.venue.venueImage}')`,
        backgroundSize: "480px",
        backgroundRepeat: "no-repeat",
        backgroundPositionY: "center",
      }}
    >
      <CardActionArea href={`/VenueDetails/${props.venue.venueId}`}>
        <Grid container>
          <Grid item xs={4} height={200}>
            <Box
              style={{
                backgroundImage:
                  "linear-gradient(to right, rgba(0,0,0,0), rgba(0,0,0,1)",
                minHeight: 200,
              }}
            ></Box>
          </Grid>
          <Grid item xs={8} height={200}>
            <Box
              style={{ background: "#000000", minHeight: 200 }}
              paddingTop={1}
              paddingLeft={8}
              paddingRight={8}
            >
              <Typography color="white" variant="h4" fontWeight="500">
                {props.venue.venueName}
              </Typography>
              <Typography color="white" marginTop={-1} variant="subtitle2">
                {props.venue.venueLocation}
              </Typography>
              <Typography
                color="white"
                sx={{
                  overflow: "hidden",
                  textOverflow: "ellipsis",
                  display: "-webkit-box",
                  WebkitLineClamp: "5",
                  WebkitBoxOrient: "vertical",
                }}
              >
                {props.venue.venueDescription}
              </Typography>
            </Box>
          </Grid>
        </Grid>
      </CardActionArea>
    </Card>
  );
}
