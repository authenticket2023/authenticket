import {
    Box, Modal, Button, TextField, Avatar, Typography, Grid, TextareaAutosize
} from '@mui/material';
import React, { useEffect, useState } from 'react';
import BasicDatePicker from './dateElement';
import { Sheet } from '@mui/joy';
import dayjs, { Dayjs } from 'dayjs';

export function EventDetails(props: any) {

    useEffect(() => {
    }, []);
    //get today's date with format YYYY-MM-DD HH:mm:ss
    const today = new Date(),
    date = today.getFullYear() + '-' + (today.getMonth() + 1) + '-' + today.getDate() + ' ' + today.getHours() + ':' + today.getHours() + ':' + today.getMinutes();

    const [name, setName] = useState('');
    const [eventDate, setEventDate] = React.useState<Dayjs>(dayjs(date));
    const [saleDate, setSaleDate] = React.useState<Dayjs>(dayjs(date));
    const [eventDescription, setEventDescription] = useState('');
    const [otherInfo, setOtherInfo] = useState('');
    const [ticketNumber, setTicketNumber] = useState(0);

    const handleName = (event: any) => {
        setName(event.target.value);
    };

    const handleEventDescription = (event: any) => {
        setEventDescription(event.target.value);
    };

    const handleEventDate = (newDate: Dayjs | null) => {
        setEventDate(dayjs(newDate))
    };

    const handleTicketNumber = (event: any) => {
        setTicketNumber(event.target.value);
    };

    const handleSaleDate = (newDate: Dayjs | null) => {
        setSaleDate(dayjs(newDate))
    };
    
    const handleOtherInfo = (event: any) => {
        setOtherInfo(event.target.value);
    };
 

    return (
        <Box>
            <Sheet>
                <Typography variant="h6" gutterBottom sx={{ mb: 1 }}>
                    Basic Information
                </Typography>
                <Grid container spacing={3}>
                    <Grid item xs={12} sm={8}>
                        <TextField
                            required
                            label="Event name"
                            fullWidth
                            value={name}
                            onChange={handleName}
                        />
                    </Grid>

                    <Grid item xs={12} sm={4}>
                        <BasicDatePicker onDateChange={handleEventDate} value={eventDate} label="Event Date"/>
                    </Grid>

                    <Grid item xs={12} sm={12}>
                        <TextareaAutosize minRows="7" onChange={handleEventDescription}
                            style={{ width: "100%", fontSize: "inherit", font: "inherit", border: "1px solid light-grey", borderRadius: 4 }}
                            id='Description' className='StyledTextarea' value={eventDescription} placeholder="Event Description" />
                    </Grid>
                </Grid>
                <Typography variant="h6" gutterBottom sx={{ mb: 1, mt: 1 }}>
                    Ticket Information
                </Typography>
                <Grid container spacing={3}>
                    <Grid item xs={12} sm={8}>
                        <TextField
                            type='number'
                            inputProps={{ inputMode: 'numeric', pattern: '[0-9]*' }} 
                            required
                            label="Number of Ticket Avaliable"
                            fullWidth
                            value={ticketNumber}
                            onChange={handleTicketNumber}
                        />
                    </Grid>
                    <Grid item xs={12} sm={4}>
                        <BasicDatePicker onDateChange={handleSaleDate} value={saleDate} label="Ticket Sale Date"/>
                    </Grid>
                </Grid>

                <Typography variant="h6" gutterBottom sx={{ mb: 1, mt: 1 }}>
                    Other Information
                </Typography>
                <Grid container spacing={3}>
                    <Grid item xs={12} sm={12}>
                        <TextareaAutosize minRows="7" onChange={handleOtherInfo}
                            style={{ width: "100%", fontSize: "inherit", font: "inherit", border: "1px solid light-grey", borderRadius: 4 }}
                            className='StyledTextarea' value={otherInfo} placeholder="Other Event Info" />
                    </Grid>

                </Grid> 
            </Sheet>
        </Box>

    )
}

export function VenueArtist(props: any) {

    useEffect(() => {
    }, []);


    return (
        <Box>
            <Typography>
                VenueArtist
            </Typography>
        </Box>

    )
}

export function EventPoster(props: any) {

    useEffect(() => {
    }, []);


    return (
        <Box>
            <Typography>
                EventPoster
            </Typography>
        </Box>

    )
}