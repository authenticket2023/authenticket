import React, { useEffect, useState } from 'react';
import { Box } from '@mui/system';
import { Grid, Typography } from '@mui/material';
import { Button } from 'react-bootstrap';
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import { format } from 'date-fns';

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

export const InfoBox = (props: any) => {

    return (
        <Box style={{background:'#F0F0F0', height:'350px', width:'300px', borderRadius:'8px', display:'flex', justifyContent:'left', flexDirection:'column'}}>
                        <div style={{display:'flex', flexDirection:'row', marginTop:35, marginLeft:40}}>
                          <CalendarMonthIcon style={{ fontSize:'30px' }}/>
                          <Typography style={{ marginTop:4, marginLeft:8 }}>
                            {formatDate(props.eventDate)}
                          </Typography>
                        </div>
                        <div style={{display:'flex', flexDirection:'row', marginLeft:40, marginTop:11 }}>
                          <AccessTimeIcon style={{ fontSize:'30px' }}/>
                          <Typography style={{ marginTop:2.5, marginLeft:8 }}>
                            {formatTime(props.eventDate)}
                          </Typography>
                        </div>
                        <div style={{display:'flex', flexDirection:'row', marginLeft:40, marginTop:11 }}>
                          <LocationOnIcon style={{ fontSize:'30px' }}/>
                          <Typography style={{ marginTop:3.5, marginLeft:8 }}>
                            {props.venueName}
                          </Typography>
                        </div>
                        <div style={{display:'flex', marginTop:27, flexDirection:'column', alignItems:'center' }}>
                          <Button
                            variant="outlined"
                            style={{
                              backgroundColor: '#F0F0F0',
                              color: '#FF5C35', // Text color
                              border: '2px solid #FF5C35', // Add a border
                              width:'250px'
                            }}
                          >
                          View Seatmap
                        </Button>
                        <Button
                            variant="outlined"
                            style={{
                              backgroundColor: '#F0F0F0',
                              color: '#FF5C35', // Text color
                              border: '2px solid #FF5C35', // Add a border
                              width:'250px',
                              marginTop:8
                            }}
                          >
                          View Venue
                        </Button>
                        <Button
                            variant="contained"
                            style={{
                              backgroundColor: '#FF5C35',
                              color: 'white', // Text color
                              width:'250px',
                              marginTop:8
                            }}
                          >
                          Buy Tickets
                        </Button>
                        </div>
                      </Box>
    )
}

export const InfoBoxEnhanced = (props: any) => {
    
    return (
        <Box style={{background:'#F0F0F0', height:'400px', width:'300px', borderRadius:'8px', display:'flex', justifyContent:'left', flexDirection:'column'}}>
                        <div style={{display:'flex', flexDirection:'row', marginTop:35, marginLeft:40}}>
                          <CalendarMonthIcon style={{ fontSize:'30px' }}/>
                          <Typography style={{ marginTop:4, marginLeft:8 }}>
                            {formatDate(props.eventDate)}
                          </Typography>
                        </div>
                        <div style={{display:'flex', flexDirection:'row', marginLeft:40, marginTop:11 }}>
                          <AccessTimeIcon style={{ fontSize:'30px' }}/>
                          <Typography style={{ marginTop:2.5, marginLeft:8 }}>
                            {formatTime(props.eventDate)}
                          </Typography>
                        </div>
                        <div style={{display:'flex', flexDirection:'row', marginLeft:40, marginTop:11 }}>
                          <LocationOnIcon style={{ fontSize:'30px' }}/>
                          <Typography style={{ marginTop:3.5, marginLeft:8 }}>
                            {props.venueName}
                          </Typography>
                        </div>
                        <div style={{display:'flex', marginTop:27, flexDirection:'column', alignItems:'center' }}>
                          <Button
                            variant="outlined"
                            style={{
                              backgroundColor: '#F0F0F0',
                              color: '#FF5C35', // Text color
                              border: '2px solid #FF5C35', // Add a border
                              width:'250px'
                            }}
                          >
                          View Seatmap
                        </Button>
                        <Button
                            variant="outlined"
                            style={{
                              backgroundColor: '#F0F0F0',
                              color: '#FF5C35', // Text color
                              border: '2px solid #FF5C35', // Add a border
                              width:'250px',
                              marginTop:8
                            }}
                          >
                          View Venue
                        </Button>
                        <Button
                            variant="contained"
                            style={{
                              backgroundColor: '#FF5C35',
                              color: 'white', // Text color
                              width:'250px',
                              marginTop:8
                            }}
                          >
                          Indicate Interest
                        </Button>
                        <Button
                            variant="contained"
                            style={{
                              backgroundColor: '#FF5C35',
                              color: 'white', // Text color
                              width:'250px',
                              marginTop:8
                            }}
                          >
                          Buy Tickets
                        </Button>
                        </div>
                      </Box>
    )
}