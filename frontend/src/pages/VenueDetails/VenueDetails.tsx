import React, { useEffect, useState } from "react";
import { Navigate } from "react-router-dom";
import { NavbarNotLoggedIn, NavbarLoggedIn } from "../../Navbar";
import { useParams } from "react-router-dom";
import { Box } from "@mui/system";
import { Grid, List, ListItem, Typography } from "@mui/material";
import { Button } from "react-bootstrap";
import Tabs from "@mui/material/Tabs";
import Tab from "@mui/material/Tab";
import Avatar from "@mui/material/Avatar";
import CalendarMonthIcon from "@mui/icons-material/CalendarMonth";
import AccessTimeIcon from "@mui/icons-material/AccessTime";
import LocationOnIcon from "@mui/icons-material/LocationOn";
import MailOutlineIcon from "@mui/icons-material/MailOutline";
import { format } from "date-fns";
import { SGStad } from '../../utility/seatMap/SeatMap';
import { InitMap } from './VenueMap';
import CapitolMap from './CapitolMap.png';  

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

export const VenueDetails: React.FC = (): JSX.Element => {
  useEffect(() => {
    loadVenueDetails();
  }, []);

  const token = window.localStorage.getItem("accessToken");

  //set parameters
  const { venueId } = useParams<{ venueId: string }>();

  //set variables
  const [venueDetails, setVenueDetails]: any = React.useState();
  const [value, setValue] = useState(0);
  const colorArray = ["#E5E23D", "#D74A50", "#30A1D3", "#E08D24", "#5BB443"];

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
              backgroundImage: `url('https://authenticket.s3.ap-southeast-1.amazonaws.com/venue_image/${venueDetails.venueImage}')`,
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

              {/* Tab 1: general Info */}
              <CustomTabPanel value={value} index={0}>
                <Grid container spacing={12} style={{}}>
                  <Grid item xs={6}>
                    <Typography sx={{ fontWeight: "bold" }}>
                      Venue Details
                    </Typography>
                    <Typography>{venueDetails.venueDescription}</Typography>
                  </Grid>
                  <Grid item xs={6}></Grid>
                </Grid>
              </CustomTabPanel>

              {/* Tab 2: Ticket Pricing */}
              <CustomTabPanel value={value} index={1}>
                <Grid container>
                  <Grid item xs = {12} justifyContent={'center'} alignItems={'center'} marginLeft={20}>
                <Box alignItems={'center'} justifyItems={'center'}>
                <SGStad id={venueDetails.venueId} setSelectedSection={loadVenueDetails}/>
                </Box>
                  </Grid>
                </Grid>
              </CustomTabPanel>

              {/* Tab 3: Ticket Sales */}
              <CustomTabPanel value={value} index={2}>
                {/* <InitMap venueId = {venueId}></InitMap> */}
                <Box sx={{backgroundImage: `./CapitolMap.png`, width: 400, height: 400} }>
                </Box>
                <img src='./CapitolMap.png'></img>
                </CustomTabPanel>

              {/* Tab 4: Organiser Info */}
              {/* <CustomTabPanel value={value} index={3}>
                <Grid container spacing={12} style={{}}>
                    <Grid item xs={8}>
                      <Typography style={{font:'Roboto', fontWeight:500, fontSize:'18px'}}>
                          About the Organiser
                      </Typography>
                      <div style={{display:'flex', flexDirection:'row', marginBottom:30}}>
                        <img 
                          alt={venueDetails.organiser.name} 
                          src={`${process.env.REACT_APP_S3_URL}/event_organiser_profile/${venueDetails.organiser.logoImage}`}
                          style={{height:'70px', width:'80px', marginTop:25, marginLeft:35}}
                        />
                        <Typography style={{font:'Roboto', marginTop:49, marginLeft:12, fontSize:'14px', fontWeight:500}}>
                          {venueDetails.organiser.name}
                        </Typography>
                      </div>
                      <Typography style={{font:'Roboto', fontWeight:300, fontSize:'15px', marginBottom:30}}>
                        {venueDetails.organiser.description}
                      </Typography>
                      <Typography style={{font:'Roboto', fontWeight:500, fontSize:'18px'}}>
                          Contact Information
                      </Typography>
                      <div style={{display:'flex', flexDirection:'row', marginTop:10, marginLeft:15 }}>
                        <MailOutlineIcon style={{fontSize:'25px'}}/>
                        <Typography style={{font:'Roboto', fontWeight:300, fontSize:'15px', marginBottom:30, marginLeft:6}}>
                          {venueDetails.organiser.email}
                        </Typography>
                      </div>
                    </Grid>
                     */}
              {/* Do check here if its enhanced or not enhanced, if it is use InfoBoxEnhanced */}
              {/* <Grid item xs={4}>
                      <InfoBox
                        eventDate={venueDetails.eventDate}
                        venueName={venueDetails.venue.venueName}
                      />
                    </Grid>
                  </Grid>
                </CustomTabPanel> */}
            </Box>
          </Box>
        )}
      </Box>
      {/* <p>Event ID: {eventId}</p> */}
    </div>
  );
};
