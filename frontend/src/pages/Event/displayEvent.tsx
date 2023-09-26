import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import Tabs from '@mui/material/Tabs';
import Tab from '@mui/material/Tab';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';
import { Button, Grid, InputAdornment, TextField } from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import CardMedia from '@mui/material/CardMedia';
import { CardActionArea } from '@mui/material';
import { format } from 'date-fns';
import GroupsIcon from '@mui/icons-material/Groups';

interface eventInfo {
    event: {
        eventId: number;
        eventName: string;
        eventDescription: string;
        eventImage: string;
        eventType: string;
        eventDate: string;
        totalTickets: number;
        eventVenue: string;
    }
}

function formatDate(dateString: string): string {
    const date = new Date(dateString);
    return format(date, "dd MMMM yyyy, h:mm a");
}


export default function dsiplayEvent(props: eventInfo) {

    return (
        <Card sx={{ minHeight: 260 ,mt: 3, maxHeight: 260, backgroundColor: '#F8F8F8', borderRadius:'10px', }} >
            <CardActionArea href={`/EventDetails/${props.event.eventId}`}>
                <CardMedia
                    component="img"
                    height='110'
                    style={{ objectFit: 'scale-down' }}
                    image={`https://authenticket.s3.ap-southeast-1.amazonaws.com/event_images/${props.event.eventImage}`}
                    alt="event image"
                />
                <CardContent style={{justifyContent:'left', alignItems:'left', marginLeft:18}}>
                    <Typography gutterBottom variant="h5" component="div" style={{ font: 'roboto', fontWeight: 500, fontSize: '20px', marginTop: -5 }}>
                        {props.event.eventName}
                    </Typography>

                    <Grid item xs={12} sm={12} sx={{ mb: 2, display: 'flex' }}>
                        <Button variant="outlined" sx={{ backgroundColor: 'white', borderColor: '#FF5C35',mr:1, color: '#FF5C35', fontSize:'12px', size:'small', height:'30px'}} > {props.event.eventVenue}</Button>
                        <Button variant="outlined" sx={{ backgroundColor: 'white', borderColor: '#FF5C35' , color: '#FF5C35', fontSize:'12px', size:'small', height:'30px'}}> {props.event.eventType}</Button>
                    </Grid>
                    <Typography variant="body2" color="text.secondary">
                        Date
                    </Typography>
                    <Grid container spacing={2}>
                        <Grid item xs={12} sm={6}>
                            <Box>
                                <Typography variant="body2" color="black" style={{fontWeight:500}}>
                                    {formatDate(props.event.eventDate)}
                                </Typography>
                            </Box>
                        </Grid>
                        <Grid item xs={12} sm={4} />
                        <Grid item xs={12} sm={2}>
                            <Box sx={{ display: 'flex', alignItems: 'center' , marginRight:10, }}>
                                <GroupsIcon style={{color:'#8E8E8E'}}/>
                                <Typography sx={{ ml: 1 }} variant="body2" style={{color:'#8E8E8E', fontWeight:500, marginLeft:5, marginTop:1.5}}>
                                    {props.event.totalTickets}
                                </Typography>
                            </Box>
                        </Grid>
                    </Grid>
                </CardContent>
            </CardActionArea>
        </Card >
    )
}