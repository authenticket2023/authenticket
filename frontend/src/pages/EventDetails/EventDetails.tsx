import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { NavbarNotLoggedIn, NavbarLoggedIn } from '../../Navbar';
import { useParams } from 'react-router-dom';
import { Box } from '@mui/system';
import { Grid, Typography } from '@mui/material';
import { Button } from 'react-bootstrap';
import Tabs from '@mui/material/Tabs';
import Tab from '@mui/material/Tab';
import Avatar from '@mui/material/Avatar';
import { InfoBox, InfoBoxEnhanced } from './InfoBox';
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import { format } from 'date-fns';

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
      'aria-controls': `simple-tabpanel-${index}`,
    };
  }

  function formatDate(dateString: string): string {
    const date = new Date(dateString);
    return format(date, "dd MMMM yyyy");
  }

  const formatTime = (timeString: string): string => {
    const date = new Date(timeString);
    let hours = date.getHours();
    let minutes = date.getMinutes();
  
    // Convert hours to 12-hour format
    const modifier = hours >= 12 ? 'pm' : 'am';
    hours = hours % 12 || 12;
  
    // Format minutes with leading zero if necessary
    const formattedMinutes = minutes.toString().padStart(2, '0');
  
    // Concatenate hours, minutes, and AM/PM modifier
    return `${hours}.${formattedMinutes}${modifier}`;
  };
  


export const EventDetails: React.FC = (): JSX.Element => {
    useEffect(() => {
        loadEventDetails();
    }, []);

    const token = window.localStorage.getItem('accessToken');

    //set parameters
    const { eventId } = useParams<{ eventId: string }>();

    //set variables
    const [eventDetails, setEventDetails]: any = React.useState();
    const [value, setValue] = useState(0);
    const [artistDetails, setArtistDetails]: any = React.useState([]);

    const loadEventDetails = async () => {
        // //calling backend API
        fetch(`${process.env.REACT_APP_BACKEND_URL}/public/event/${eventId}`, {
          headers: {
            'Content-Type': 'application/json',
          },
          method: 'GET',
        })
          .then(async (response) => {
            if (response.status == 200) {
              const apiResponse = await response.json();
              const data = apiResponse.data;
              // console.log(data);
              setEventDetails(data);
            //   const artistDetails = data.artists.map((artist: any) => ({
            //     artistId: artist.artistId,
            //     artistName: artist.artistName,
            //     artistImage: artist.artistImage
            //   }))
              const array = data.artists;
              // console.log(array[0].artistId);
              setArtistDetails(array);
            //   console.log(data);
            // console.log(artistDetails[0].artistName);
            // {artistDetails.map((artist: any) => {
            //   console.log(artist);
            // })}
            } else {
              //passing to parent component
            }
          })
          .catch((err) => {
            window.alert(err);
          });
      }

      const handleChange = (event: React.SyntheticEvent, newValue: number) => {
        setValue(newValue);
      };
    
    return (
    <div>
      {token != null ? <NavbarLoggedIn /> : <NavbarNotLoggedIn />}
      
      <Box>
        {eventDetails && (
            <div style={{ background: 'black', height: '300px', display: 'flex', flexDirection: 'column' }}>
            <div style={{ display: 'flex', justifyContent: 'center', marginBottom: '15px' }}>
                <img
                src={`https://authenticket.s3.ap-southeast-1.amazonaws.com/event_images/${eventDetails.eventImage}`}
                style={{
                    maxHeight: '200px',
                }}
                alt="Event Image"
                />
            </div>
            <div style={{display:'flex', flexDirection:'row', marginBottom:0}}>
                <Typography sx={{ color: 'white', fontWeight: 500, fontFamily: 'Roboto', fontSize: '28px', marginLeft: 10 }}>
                    {eventDetails.eventName}
                </Typography>
                <Button variant="outlined" style={{ backgroundColor: 'black', borderColor: '#FF5C35', color: '#FF5C35', fontSize:'15px', marginLeft:10, height:"34px", marginTop:3}}>
                    {eventDetails.type}
                </Button>
            </div>
            <Typography sx={{ color: 'white', fontWeight: 300, fontFamily: 'Roboto', fontSize: '16px', marginLeft: 10, marginTop: 0 }}>
                by {eventDetails.organiser.name}
            </Typography>
            </div>
        )}

        {/* Content Tabs */}
        {eventDetails && (
        <Box sx={{ display: 'flex', justifyContent: 'center', minHeight: '100vh' }}>
            <Box sx={{ width: '90%' }}>
                <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
                <Tabs value={value} onChange={handleChange} aria-label="basic tabs example" textColor='inherit' TabIndicatorProps={{ style: {background: 'black'} }}>
                    <Tab label="General Info" {...a11yProps(0)} />
                    <Tab label="Ticket Pricing" {...a11yProps(1)} />
                    <Tab label="Ticket Sales" {...a11yProps(2)} />
                    <Tab label="Organiser Info" {...a11yProps(3)} />
                </Tabs>
                </Box>
                <CustomTabPanel value={value} index={0}>
                  <Grid container spacing={12} style={{}}>
                    <Grid item xs={8}>
                      <Typography style={{font:'Roboto', fontWeight:500, fontSize:'20px'}}>
                          Event Description
                      </Typography>
                      <Typography style={{font:'Roboto', fontWeight:300, fontSize:'16px', marginBottom:20}}>
                          {eventDetails.eventDescription}
                      </Typography>
                      <Typography style={{font:'Roboto', fontWeight:500, fontSize:'20px'}}>
                          Event Information
                      </Typography>
                      <Typography style={{font:'Roboto', fontWeight:300, fontSize:'16px', marginBottom:20}}>
                          {eventDetails.otherEventInfo}
                      </Typography>
                      <Typography style={{font:'Roboto', fontWeight:500, fontSize:'20px'}}>
                          The Artists
                      </Typography>
                      <Grid container spacing={2}>
                          {artistDetails.map((artist: any) => (
                              <div style={{display:'flex', flexDirection:'row'}}>
                                  <Avatar 
                                      alt={artist.artistName} 
                                      src={`${process.env.REACT_APP_S3_URL}/artists/${artist.artistImage}`}
                                      style={{height:'85px', width:'85px', marginTop:30, marginLeft:35}}
                                  />
                                  <Typography style={{font:'Roboto', marginTop:55, marginLeft:12, fontSize:'18px', fontWeight:500}}>
                                      {artist.artistName}
                                  </Typography>
                              </div>
                          ))}
                      </Grid>
                      </Grid>
                    
                    {/* Do check here if its enhanced or not enhanced, if it is use InfoBoxEnhanced */}
                    <Grid item xs={4}>
                      <InfoBox
                        eventDate={eventDetails.eventDate}
                        venueName={eventDetails.venue.venueName}
                      />

                    </Grid>
                  </Grid>
                </CustomTabPanel>
                <CustomTabPanel value={value} index={1}>
                Item Two
                </CustomTabPanel>
                <CustomTabPanel value={value} index={2}>
                Item Three
                </CustomTabPanel>
                <CustomTabPanel value={value} index={3}>
                Item Four
                </CustomTabPanel>
            </Box>
            </Box>
            )}
        </Box>

 
      {/* <p>Event ID: {eventId}</p> */}
    </div>

    )
}