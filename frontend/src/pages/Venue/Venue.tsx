import React, { useEffect, useState } from "react";
import { Navigate } from "react-router-dom";
import Tabs from "@mui/material/Tabs";
import Tab from "@mui/material/Tab";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import { NavbarNotLoggedIn, NavbarLoggedIn } from "../../Navbar";
import {
  Alert,
  Grid,
  IconButton,
  InputAdornment,
  Snackbar,
  TextField,
} from "@mui/material";
import SearchIcon from "@mui/icons-material/Search";
import Divider from "@mui/material/Divider";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import CardMedia from "@mui/material/CardMedia";
import { CardActionArea } from "@mui/material";
import DisplayVenue from "./displayVenue";
import FilterListIcon from "@mui/icons-material/FilterList";
import TuneIcon from "@mui/icons-material/Tune";

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

function a11yProps(index: number) {
  return {
    id: `simple-tab-${index}`,
    "aria-controls": `simple-tabpanel-${index}`,
  };
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
    fetch(
      `${process.env.REACT_APP_BACKEND_URL}/venue`,
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
          console.log(data);
          const currVenuesArr = data.map((venue: any) => ({
            venueId: venue.venueId,
            venueName: venue.venueName,
            venueLocation: venue.venueLocation,
            venueImage: venue.venueImage,
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

  // const loadPastEvents = async () => {
  //   //call backend API
  //   fetch(
  //     `${process.env.REACT_APP_BACKEND_URL}/public/event/past?page=0&size=20`,
  //     {
  //       headers: {
  //         "Content-Type": "application/json",
  //       },
  //       method: "GET",
  //     }
  //   )
  //     .then(async (response) => {
  //       if (response.status == 200) {
  //         const apiResponse = await response.json();
  //         const data = apiResponse.data;
  //         const pastEvents = data.map((event: any) => ({
  //           eventId: event.eventId,
  //           eventName: event.eventName,
  //           eventDescription: event.eventDescription,
  //           eventImage: event.eventImage,
  //           eventType: event.eventType,
  //           eventDate: event.eventDate,
  //           totalTickets: event.totalTickets,
  //           eventVenue: event.eventVenue,
  //         }));

  //         setPastEvents(pastEvents);
  //       } else {
  //         //display alert, for fetch fail
  //         setOpenSnackbar(true);
  //         setAlertType("error");
  //         setAlertMsg(
  //           `Oops something went wrong! Code:${response.status}; Status Text : ${response.statusText}`
  //         );
  //       }
  //     })
  //     .catch((err) => {
  //       setOpenSnackbar(true);
  //       setAlertType("error");
  //       setAlertMsg(`Oops something went wrong! Error : ${err}`);
  //     });
  // };

  // const handleChange = (event: React.SyntheticEvent, newValue: number) => {
  //   setValue(newValue);
  // };

  return (
    <div>
      {token != null ? <NavbarLoggedIn /> : <NavbarNotLoggedIn />}
      {/* i dont know why cannt use percentage for height, i guess we have to use fixed px */}
      <Box
        sx={{
          height: "850px",
          overflow: "hidden",
          position: "relative",
          display: "flex",
          flexDirection: "column",
        }}
      >
        {/* Section 1 */}
        <Box
          sx={{
            width: "100%",
            position: "sticky",
            borderBottom: 1,
            mt: 5,
            borderColor: "#CACACA",
          }}
        >
          <Box sx= {{marginLeft: 22, marginTop: -3}}>
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
          {/* Search Bar */}
          <Box
            component="form"
            sx={{
              display: "flex",
              alignItems: "center",
              justifyContent: "space-between",
              paddingLeft: 15,
              paddingRight: 15,
              width: "50ch",
              marginTop: -6,
              marginBottom: 3,
              marginLeft: 120,
            }}
            noValidate
            autoComplete="off"
          >
            <TextField
              id="input-with-icon-textfield"
              size="small"
              label="Search"
              variant="outlined"
              fullWidth
              // onChange={handleSearch}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <SearchIcon />
                  </InputAdornment>
                ),
              }}
            />
            <IconButton
              aria-label="filter"
              // aria-describedby={id}
              // onClick={handleFilterClick}
              sx={{
                border: "1px solid #8E8E8E",
                borderRadius: "5px",
                marginLeft: 1,
                height: 39.5,
                width: 39.5,
                //   backgroundColor: open ? "#30685e" : "white",
                //   color: open ? "white" : "#30685e",
                ":hover": {
                  bgcolor: "#8E8E8E",
                  color: "white",
                },
              }}
            >
              <TuneIcon />
            </IconButton>
          </Box>
        </Box>

        {/* Section 2: current events */}
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
              {currVenues.map((venue: any, index) => (
                <React.Fragment key={index}>
                  {/* offset sm 1*/}
                  {/* <Grid item xs={12} sm={1} /> */}
                  <Grid item xs={11}>
                    <DisplayVenue venue={venue} />
                  </Grid>
                </React.Fragment>
              ))}
            </Grid>
          </CustomTabPanel>
          {/* Section 2: past events */}
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
