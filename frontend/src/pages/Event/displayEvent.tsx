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
        eventLocation: string;
    }
}

function formatDate(dateString: string): string {
    const date = new Date(dateString);
    return format(date, "dd MMMM yyyy, h:mm a");
}


export default function dsiplayEvent(props: eventInfo) {

    return (
        <Card sx={{ maxWidth: 600, minHeight: 300, minWidth: 600, mt: 3, backgroundColor: '#F5F5F5' }} >
            <CardActionArea>
                <CardMedia
                    component="img"
                    height='200'
                    style={{ objectFit: 'scale-down' }}
                    image={`https://authenticket.s3.ap-southeast-1.amazonaws.com/event_images/${props.event.eventImage}`}
                    alt="event image"
                />
                <CardContent>
                    <Typography gutterBottom variant="h5" component="div" style={{ font: 'roboto', fontWeight: 500, fontSize: '24px', marginTop: -5 }}>
                        {props.event.eventName}
                    </Typography>

                    <Grid item xs={12} sm={6} sx={{ mb: 2, display: 'flex' }}>
                        <Button variant="outlined" sx={{ backgroundColor: 'white', borderColor: '#FF5C35',mr:2, color: '#FF5C35',}} > {props.event.eventLocation}</Button>
                        <Button variant="outlined" sx={{ backgroundColor: 'white', borderColor: '#FF5C35' , color: '#FF5C35', }}> {props.event.eventType}</Button>
                    </Grid>
                    <Typography variant="body2" color="text.secondary">
                        Date
                    </Typography>
                    <Grid container spacing={2}>
                        <Grid item xs={12} sm={6}>
                            <Box>
                                <Typography variant="body2" color="black">
                                    {formatDate(props.event.eventDate)}
                                </Typography>
                            </Box>
                        </Grid>
                        <Grid item xs={12} sm={4} />
                        <Grid item xs={12} sm={2}>
                            <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                <GroupsIcon />
                                <Typography sx={{ ml: 1 }} variant="body2" color="black">
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