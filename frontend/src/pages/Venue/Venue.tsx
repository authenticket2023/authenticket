import React, { useEffect, useState } from "react";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import { NavbarNotLoggedIn, NavbarLoggedIn } from "../../Navbar";
import {
  Alert,
  Grid,
  Snackbar,
} from "@mui/material";
import DisplayVenue from "./displayVenue";
import { Footer } from "../../Footer";

interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

function compareVenueId(a: any, b: any){
  return a.venueId - b.venueId;
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

export const Venue = () => {
  const token = window.localStorage.getItem("accessToken");
  useEffect(() => {
    loadCurrEvents();
  }, []);

  //for alert
  //error , warning , info , success
  const [openSnackbar, setOpenSnackbar] = useState(false);
  const [alertType, setAlertType]: any = useState("info");
  const [alertMsg, setAlertMsg] = useState("");
  const handleSnackbarClose = () => {
    setOpenSnackbar(false);
  };

  //variables
  const [value, setValue] = useState(0);
  const [currVenues, setCurrVenues] = useState([]);
  const [pastEvents, setPastEvents] = useState([]);

  const loadCurrEvents = async () => {
    //call backend API
    fetch(`${process.env.REACT_APP_BACKEND_URL}/venue`, {
      headers: {
        "Content-Type": "application/json",
      },
      method: "GET",
    })
      .then(async (response) => {
        if (response.status == 200) {
          const apiResponse = await response.json();
          const data = apiResponse.data;
          console.log(data);
          const currVenuesArr = data.map((venue: any) => ({
            venueId: venue.venueId,
            venueName: venue.venueName,
            venueLocation: venue.venueLocation,
            venueImage: venue.venueImage,
            venueDescription: venue.venueDescription,
          }));

          setCurrVenues(currVenuesArr);
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

  return (
    <div>
      {token != null ? <NavbarLoggedIn /> : <NavbarNotLoggedIn />}
      <Box
        sx={{
          height: "850px",
          overflow: "hidden",
          position: "relative",
          display: "flex",
          flexDirection: "column",
        }}
      >
        <Box
          sx={{
            width: "100%",
            position: "sticky",
            borderBottom: 1,
            mt: 5,
            borderColor: "#CACACA",
          }}
        >
          <Box sx={{ marginLeft: 22, marginTop: -3 }}>
            <Typography
              variant="h3"
              sx={{
                textTransform: "none",
                font: "Roboto",
                fontSize: "26px",
                fontWeight: 600,
                marginTop: 2,
                marginBottom: 1,
              }}
            >
              Venues
            </Typography>
          </Box>
        </Box>

        <Box sx={{ overflowY: "auto", height: "calc(100% - 80px)" }}>
          <CustomTabPanel value={value} index={0}>
            <Grid
              container
              rowSpacing={2}
              columnSpacing={7}
              sx={{ mb: 10 }}
              alignItems="center"
              justifyContent="center"
            >
              {currVenues.sort(compareVenueId).map((venue: any, index) => (
                <React.Fragment key={index}>
                  <Grid item xs={11}>
                    <DisplayVenue venue={venue} />
                  </Grid>
                </React.Fragment>
              ))}
            </Grid>
          </CustomTabPanel>
        </Box>
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
