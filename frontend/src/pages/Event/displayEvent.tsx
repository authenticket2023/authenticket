import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import Tabs from '@mui/material/Tabs';
import Tab from '@mui/material/Tab';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';
import { NavbarNotLoggedIn } from '../../Navbar';
import { Grid, InputAdornment, TextField } from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import Divider from '@mui/material/Divider';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import CardMedia from '@mui/material/CardMedia';
import { CardActionArea } from '@mui/material';
import { format } from 'date-fns';

interface eventInfo {
    event:{
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
        // <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
            <Card sx={{ maxWidth: 500 }}>
                <CardActionArea>
                    <CardMedia
                        component="img"
                        height="140"
                        image={`https://authenticket.s3.ap-southeast-1.amazonaws.com/event_images/${props.event.eventImage}`}
                        alt="event image"
                    />
                    <CardContent>
                        <Typography gutterBottom variant="h5" component="div" style={{font:'roboto', fontWeight:500, fontSize:'24px', marginTop:-5}}>
                        {props.event.eventName}
                        </Typography>
                        {/* <Box sx={{backgroundColor:'white', borderColor:'orange'}}>
                            <Typography>
                                {props.event.eventType}
                            </Typography>
                        </Box> */}
                        <Typography variant="body2" color="text.secondary">
                            Date
                        </Typography>
                        <Box>
                            <Typography variant="body2" color="black">
                                {formatDate(props.event.eventDate)}
                            </Typography>
                            <Box>
                                <Typography>
                                    {props.event.totalTickets}
                                </Typography>
                            </Box>
                        </Box>
                    </CardContent>
                </CardActionArea>
            </Card>
        // </div>
    )
}