import React, { useEffect, useState } from "react";
import { NavbarNotLoggedIn, NavbarLoggedIn } from "../../Navbar";
import { useParams } from "react-router-dom";
import { Box } from "@mui/system";
import { Grid, Typography } from "@mui/material";
import Tabs from "@mui/material/Tabs";
import Tab from "@mui/material/Tab";
import { Alert, Snackbar } from "@mui/material";
import { SGStad } from "../../utility/seatMap/SeatMap";
import { InitMap } from "./VenueMap";
import DisplayEvent from "./displayEvent";
import CircularProgress from "@mui/material/CircularProgress";
import DisplayPast from "./displayPast";

interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

function CustomTabPanel(props: TabPanelProps) {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`simple-tabpanel-${index}`}
      aria-labelledby={`simple-tab-${index}`}
      {...other}
    >
      {value === index && (
        <Box sx={{ p: 3 }}>
          <Typography>{children}</Typography>
        </Box>
      )}
    </div>
  );
}

const alternate: number[] = [0, 20];

function a11yProps(index: number) {
  return {
    id: `simple-tab-${index}`,
    "aria-controls": `simple-tabpanel-${index}`,
  };
}

export const VenueDetails: React.FC = (): JSX.Element => {
  useEffect(() => {
    loadVenueDetails();
  }, []);

  const token = window.localStorage.getItem("accessToken");
  const [openSnackbar, setOpenSnackbar] = useState(false);
  const [alertType, setAlertType]: any = useState("info");
  const [alertMsg, setAlertMsg] = useState("");
  const handleSnackbarClose = () => {
    setOpenSnackbar(false);
  };

  const [past, setPast]: any = useState([]);
  const [pastLoad, setPastLoad]: any = useState(false);

  const loadPast = async () => {
    //call backend API
    fetch(
      `${process.env.REACT_APP_BACKEND_URL}/public/event/by-venue/past/${venueId}?page=0&size=3`,
      {
        headers: {
          "Content-Type": "application/json",
        },
        method: "GET",
      }
    )
      .then(async (response) => {
        if (response.status == 200) {
          const apiResponse = await response.json();
          const data = apiResponse.data;
          const pastArr = data.map((event: any) => ({
            eventId: event.eventId,
            eventName: event.eventName,
            eventDescription: event.eventDescription,
            eventImage: event.eventImage,
            eventType: event.eventType,
            eventDate: event.eventDate,
            totalTickets: event.totalTickets,
            eventVenue: event.eventVenue,
          }));
          setPast(pastArr);
          setPastLoad(true);
        } else {
          //display alert, for fetch fail
          setOpenSnackbar(true);
          setAlertType("error");
          setAlertMsg(
            `Oops something went wrong! Code:${response.status}; Status Text : ${response.statusText}`
          );
        }
      })
      .catch((err) => {
        setOpenSnackbar(true);
        setAlertType("error");
        setAlertMsg(`Oops something went wrong! Error : ${err}`);
      });
  };

  //variables
  const [related, setRelated]: any = useState([]);
  const [relatedLoad, setRelatedLoad]: any = useState(false);
  const loadRelated = async () => {
    //call backend API
    fetch(
      `${process.env.REACT_APP_BACKEND_URL}/public/event/by-venue/1?page=0&size=25`,
      {
        headers: {
          "Content-Type": "application/json",
        },
        method: "GET",
      }
    )
      .then(async (response) => {
        if (response.status == 200) {
          const apiResponse = await response.json();
          const data = apiResponse.data;
          const relatedArr = data.map((event: any) => ({
            eventId: event.eventId,
            eventName: event.eventName,
            eventDescription: event.eventDescription,
            eventImage: event.eventImage,
            eventType: event.eventType,
            eventDate: event.eventDate,
            totalTickets: event.totalTickets,
            eventVenue: event.eventVenue,
          }));
          setRelated(relatedArr);
          setRelatedLoad(true);
        } else {
          //display alert, for fetch fail
          setOpenSnackbar(true);
          setAlertType("error");
          setAlertMsg(
            `Oops something went wrong! Code:${response.status}; Status Text : ${response.statusText}`
          );
        }
      })
      .catch((err) => {
        setOpenSnackbar(true);
        setAlertType("error");
        setAlertMsg(`Oops something went wrong! Error : ${err}`);
      });
  };

  //set parameters
  const { venueId } = useParams<{ venueId: string }>();

  //set variables
  const [venueDetails, setVenueDetails]: any = React.useState();
  const [value, setValue] = useState(0);

  const loadVenueDetails = async () => {
    // //calling backend API
    fetch(`${process.env.REACT_APP_BACKEND_URL}/venue/${venueId}`, {
      headers: {
        "Content-Type": "application/json",
      },
      method: "GET",
    })
      .then(async (response) => {
        if (response.status == 200) {
          const apiResponse = await response.json();
          const data = apiResponse;
          setVenueDetails(data);
          console.log(data);
          loadRelated();
          loadPast();
        } else {
          //passing to parent component
        }
      })
      .catch((err) => {
        window.alert(err);
      });
  };

  const handleChange = (event: React.SyntheticEvent, newValue: number) => {
    setValue(newValue);
  };

  return (
    <div>
      {token != null ? <NavbarLoggedIn /> : <NavbarNotLoggedIn />}
      <Box>
        {venueDetails && (
          <div
            style={{
              height: "300px",
              display: "flex",
              justifyContent: "center",
              backgroundPositionY: "center",
              backgroundSize: "cover",
              backgroundImage: `url('${process.env.REACT_APP_S3_URL}/venue_image/${venueDetails.venueImage}')`,
              flexDirection: "column",
            }}
          >
            <div
              style={{
                display: "flex",
                minHeight: "240px",
                backgroundImage:
                  "linear-gradient( rgba(0,0,0,0), rgba(0,0,0,1)",
              }}
            ></div>
            <div
              style={{
                display: "flex",
                flexDirection: "row",
                backgroundColor: "black",
                paddingBottom: 10,
              }}
            >
              <Box color="black">
                <Typography
                  sx={{
                    color: "white",
                    fontWeight: 500,
                    fontFamily: "Roboto",
                    fontSize: "28px",
                    marginLeft: 10,
                    paddingBottom: 1,
                    backgroundColor: "black",
                    backgroundSize: "cover",
                  }}
                >
                  {venueDetails.venueName}
                </Typography>
              </Box>
            </div>
          </div>
        )}

        {/* Content Tabs */}
        {venueDetails && (
          <Box
            sx={{
              display: "flex",
              justifyContent: "center",
              minHeight: "100vh",
            }}
          >
            <Box sx={{ width: "90%" }}>
              <Box sx={{ borderBottom: 1, borderColor: "divider" }}>
                <Tabs
                  value={value}
                  onChange={handleChange}
                  aria-label="basic tabs example"
                  textColor="inherit"
                  TabIndicatorProps={{ style: { background: "black" } }}
                >
                  <Tab label="General Info" {...a11yProps(0)} />
                  <Tab label="Seating" {...a11yProps(1)} />
                  <Tab label="Location" {...a11yProps(2)} />
                  <Tab label="Upcoming Events" {...a11yProps(3)} />
                </Tabs>
              </Box>

              {/* Tab 1: General Info */}
              <CustomTabPanel value={value} index={0}>
                <Grid container spacing={12} style={{}}>
                  <Grid item xs={6}>
                    <Typography sx={{ fontWeight: "bold" }}>
                      Venue Details
                    </Typography>
                    <Typography>{venueDetails.venueDescription}</Typography>
                  </Grid>
                  <Grid
                    container
                    item
                    xs={6}
                    direction={"column"}
                    justifyContent={"center"}
                  >
                    <Typography marginLeft={8} sx={{ fontWeight: "bold" }}>
                      Past Events
                    </Typography>
                    {past.map((event: any, index: any) => (
                      <React.Fragment key={index}>
                        <Box paddingLeft={8} paddingRight={8}>
                          <Grid item xs={12}>
                            <DisplayPast event={event} />
                          </Grid>
                        </Box>
                      </React.Fragment>
                    ))}
                  </Grid>
                </Grid>
              </CustomTabPanel>

              {/* Tab 2: Seating */}
              <CustomTabPanel value={value} index={1}>
                <Grid container>
                  <Grid
                    item
                    xs={12}
                    justifyContent={"center"}
                    alignItems={"center"}
                    marginLeft={20}
                  >
                    <Box alignItems={"center"} justifyItems={"center"}>
                      <SGStad
                        id={venueDetails.venueId}
                        setSelectedSection={loadVenueDetails}
                      />
                    </Box>
                  </Grid>
                </Grid>
              </CustomTabPanel>

              {/* Tab 3: Location */}
              <CustomTabPanel value={value} index={2}>
                <Typography sx={{ fontWeight: "bold" }}>Address:</Typography>
                <Typography marginBottom={2}>
                  {venueDetails.venueLocation}
                </Typography>
                <InitMap venueId={venueDetails.venueId}></InitMap>
              </CustomTabPanel>

              {/* Tab 4: Future Events */}
              <CustomTabPanel value={value} index={3}>
                <Grid
                  container
                  rowSpacing={2}
                  columnSpacing={7}
                  sx={{ mb: 10 }}
                  alignItems="center"
                  justifyContent="center"
                >
                  {related.map((event: any, index: any) => (
                    <React.Fragment key={index}>
                      <Grid item xs={5}>
                        <DisplayEvent event={event} />
                      </Grid>
                    </React.Fragment>
                  ))}
                </Grid>
                {/* show if no future events */}
                {related.length == 0 ? (
                  <Box
                    sx={{
                      display: "flex",
                      justifyContent: "center",
                      alignItems: "center",
                    }}
                  >
                    <Typography variant="h4">
                      Stay alert, more exciting events are on the horizon
                    </Typography>
                    <Box marginLeft={3}>
                      <CircularProgress></CircularProgress>
                    </Box>
                  </Box>
                ) : null}
              </CustomTabPanel>
            </Box>
          </Box>
        )}
      </Box>
      <Snackbar
        open={openSnackbar}
        autoHideDuration={4000}
        onClose={handleSnackbarClose}
      >
        <Alert
          onClose={handleSnackbarClose}
          severity={alertType}
          sx={{ width: "100%" }}
        >
          {alertMsg}
        </Alert>
      </Snackbar>
    </div>
  );
};
