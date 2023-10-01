import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { NavbarNotLoggedIn, NavbarLoggedIn } from '../../Navbar';
import { useParams } from 'react-router-dom';
import { Box } from '@mui/system';
import { Grid, List, ListItem, Typography } from '@mui/material';
import { Button } from 'react-bootstrap';
import Tabs from '@mui/material/Tabs';
import Tab from '@mui/material/Tab';
import Avatar from '@mui/material/Avatar';
import { InfoBox, InfoBoxEnhanced } from './InfoBox';
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import MailOutlineIcon from '@mui/icons-material/MailOutline';
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
    const [categoryDetails, setCategoryDetails]: any = React.useState([]);
    const colorArray = ['#E5E23D', '#D74A50', '#30A1D3', '#E08D24', '#5BB443'];

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
              const artistArray = data.artists;
              // console.log(array[0].artistId);
              setArtistDetails(artistArray);
            //   console.log(data);
            // console.log(artistDetails[0].artistName);
            // {artistDetails.map((artist: any) => {
            //   console.log(artist);
            // })}

              const arr = data.ticketCategory
              const categoryArray = arr.sort((a: { categoryId: number; }, b: { categoryId: number; }) => a.categoryId - b.categoryId);
              setCategoryDetails(categoryArray);
              {categoryDetails.map((cat: any) => {
                console.log(cat);
              })}
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

                {/* Tab 1: general Info */}
                <CustomTabPanel value={value} index={0}>
                  <Grid container spacing={12} style={{}}>
                    <Grid item xs={8}>
                      <Typography style={{font:'Roboto', fontWeight:500, fontSize:'18px'}}>
                          Event Description
                      </Typography>
                      <Typography style={{font:'Roboto', fontWeight:300, fontSize:'15px', marginBottom:30}}>
                          {eventDetails.eventDescription}
                      </Typography>
                      <Typography style={{font:'Roboto', fontWeight:500, fontSize:'18px'}}>
                          Event Information
                      </Typography>
                      <Typography style={{font:'Roboto', fontWeight:300, fontSize:'15px', marginBottom:30}}>
                          {eventDetails.otherEventInfo}
                      </Typography>
                      <Typography style={{font:'Roboto', fontWeight:500, fontSize:'18px'}}>
                          Artists
                      </Typography>
                      <Grid container spacing={2}>
                          {artistDetails.map((artist: any) => (
                              <Grid style={{display:'flex', flexDirection:'row'}} xs={4}>
                                  <Avatar 
                                      alt={artist.artistName} 
                                      src={`${process.env.REACT_APP_S3_URL}/artists/${artist.artistImage}`}
                                      style={{height:'65px', width:'65px', marginTop:30, marginLeft:35}}
                                  />
                                  <Typography style={{font:'Roboto', marginTop:52, marginLeft:12, fontSize:'14px', fontWeight:500}}>
                                      {artist.artistName}
                                  </Typography>
                              </Grid>
                          ))}
                      </Grid>
                      </Grid>
                    
                    {/* Do check here if its enhanced or not enhanced, if it is use InfoBoxEnhanced */}
                    <Grid item xs={4}>
                      <InfoBox
                        eventDate={eventDetails.eventDate}
                        venueName={eventDetails.venue.venueName}
                        eventId={eventDetails.eventId}
                      />

                    </Grid>
                  </Grid>
                </CustomTabPanel>

                {/* Tab 2: Ticket Pricing */}
                <CustomTabPanel value={value} index={1}>

                <Grid container spacing={12} style={{}}>
                    <Grid item xs={8}>
                      <Typography style={{font:'Roboto', fontWeight:500, fontSize:'18px'}}>
                          Ticket Pricing
                      </Typography>
                      <img 
                        src={`${process.env.REACT_APP_S3_URL}/venue_image/${eventDetails.venue.venueImage}`}
                        style={{
                            maxHeight: '800px',
                            marginLeft:250, marginBottom:0
                        }}
                        alt="Seatmap Image"
                      />
                      <div style={{height:'50px', width:'350px', marginTop:-500, marginLeft:300}}>
                      <Grid container spacing={2}>
                        {categoryDetails.map((cat: any) => (
                          <Grid item key={cat.categoryId} xs={6} display='flex' flexDirection='row'> {/* xs={6} makes each item take up half the row */}
                            <div style={{ background: colorArray[cat.categoryId - 1], height: '20px', width: '20px', borderRadius: '5px' }} />
                            <Typography style={{ color: 'black' , marginLeft:10}}>
                              {cat.categoryName} - ${cat.price}
                            </Typography>
                          </Grid>
                        ))}
                      </Grid>
                      </div>
                    </Grid>
                    
                    {/* Do check here if its enhanced or not enhanced, if it is use InfoBoxEnhanced */}
                    <Grid item xs={4}>
                      <InfoBox
                        eventDate={eventDetails.eventDate}
                        venueName={eventDetails.venue.venueName}
                        eventId={eventDetails.eventId}
                      />
                    </Grid>
                  </Grid>
                </CustomTabPanel>

                {/* Tab 3: Ticket Sales */}
                <CustomTabPanel value={value} index={2}>
                <Grid container spacing={12} style={{}}>
                    <Grid item xs={8}>
                      <Typography style={{font:'Roboto', fontWeight:500, fontSize:'18px'}}>
                          Ticket Sales
                      </Typography>
                      <Typography style={{marginTop:5, font:'Roboto', fontWeight:300, fontSize:'15px', marginBottom:30 }}>
                        starts on <strong style={{fontWeight:500}}>{formatDate(eventDetails.ticketSaleDate)}</strong>
                      </Typography>
                      <Typography style={{font:'Roboto', fontWeight:500, fontSize:'18px'}}>
                          Notes
                      </Typography>
                      <Typography>
                        <List sx={{
                            listStyleType: 'disc',
                            listStylePosition: 'inside'
                          }}>
                          <ListItem sx={{ display: 'list-item', marginTop:-2, font:'Roboto', fontWeight:300, fontSize:'15px' }}>
                            Each account can purchase a maximum of 5 tickets
                          </ListItem>
                          <ListItem sx={{ display: 'list-item', marginTop:-2, font:'Roboto', fontWeight:300, fontSize:'15px' }}>
                            If the event has a presale, please indicate your interest to be considered for the presale
                          </ListItem>
                          <ListItem sx={{ display: 'list-item', marginTop:-2, font:'Roboto', fontWeight:300, fontSize:'15px' }}>
                            Presale takes place 1 day before the actual sales
                          </ListItem>
                          <ListItem sx={{ display: 'list-item', marginTop:-2, font:'Roboto', fontWeight:300, fontSize:'15px' }}>
                            No refunds or exchanging of tickets will be allowed
                          </ListItem>
                        </List>
                      </Typography>
                    </Grid>
                    
                    {/* Do check here if its enhanced or not enhanced, if it is use InfoBoxEnhanced */}
                    <Grid item xs={4}>
                      <InfoBox
                        eventDate={eventDetails.eventDate}
                        venueName={eventDetails.venue.venueName}
                        eventId={eventDetails.eventId}
                      />
                    </Grid>
                  </Grid>
                </CustomTabPanel>

                {/* Tab 4: Organiser Info */}
                <CustomTabPanel value={value} index={3}>
                <Grid container spacing={12} style={{}}>
                    <Grid item xs={8}>
                      <Typography style={{font:'Roboto', fontWeight:500, fontSize:'18px'}}>
                          About the Organiser
                      </Typography>
                      <div style={{display:'flex', flexDirection:'row', marginBottom:30}}>
                        <img 
                          alt={eventDetails.organiser.name} 
                          src={`${process.env.REACT_APP_S3_URL}/event_organiser_profile/${eventDetails.organiser.logoImage}`}
                          style={{height:'70px', width:'80px', marginTop:25, marginLeft:35}}
                        />
                        <Typography style={{font:'Roboto', marginTop:49, marginLeft:12, fontSize:'14px', fontWeight:500}}>
                          {eventDetails.organiser.name}
                        </Typography>
                      </div>
                      <Typography style={{font:'Roboto', fontWeight:300, fontSize:'15px', marginBottom:30}}>
                        {eventDetails.organiser.description}
                      </Typography>
                      <Typography style={{font:'Roboto', fontWeight:500, fontSize:'18px'}}>
                          Contact Information
                      </Typography>
                      <div style={{display:'flex', flexDirection:'row', marginTop:10, marginLeft:15 }}>
                        <MailOutlineIcon style={{fontSize:'25px'}}/>
                        <Typography style={{font:'Roboto', fontWeight:300, fontSize:'15px', marginBottom:30, marginLeft:6}}>
                          {eventDetails.organiser.email}
                        </Typography>
                      </div>
                    </Grid>
                    
                    {/* Do check here if its enhanced or not enhanced, if it is use InfoBoxEnhanced */}
                    <Grid item xs={4}>
                      <InfoBox
                        eventDate={eventDetails.eventDate}
                        venueName={eventDetails.venue.venueName}
                        eventId={eventDetails.eventId}
                      />
                    </Grid>
                  </Grid>
                </CustomTabPanel>
            </Box>
            </Box>
            )}
        </Box>

 
      {/* <p>Event ID: {eventId}</p> */}
    </div>

    )
}