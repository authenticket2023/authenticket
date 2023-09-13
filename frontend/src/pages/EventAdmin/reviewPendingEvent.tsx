import {
    Box, Typography, Modal,
    Grid, Button, TextField, Avatar, FormControl,
    InputLabel, Select, MenuItem, InputAdornment, FormGroup, FormControlLabel,
    Switch, OutlinedInput, Checkbox, ListItemText, List, ListItem, styled, CardMedia
} from '@mui/material';
import React, { useEffect } from 'react';
import { Sheet } from '@mui/joy';
import ListItemAvatar from '@mui/material/ListItemAvatar';

const style = {
    position: 'absolute' as 'absolute',
    top: '25%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    width: 400,
    bgcolor: 'background.paper',
    border: '2px solid #000',
    overflow: 'scroll',
    boxShadow: 24,
    p: 4,
};

export default function ReviewEvent(props: any) {
    const [eventID, setEventID] = React.useState(props.eventID);
    const [eventDetail, setEventDetail]: any = React.useState();
    const [loaded, setLoaded]: any = React.useState(false);
    const [reviewOpen, setReviewOpen] = React.useState(true);
    const handleReviewEventModalClose = () => {
        setReviewOpen(false);
        //to update parent element
        props.open(false);
    }

    const loadEventDetailByID = async () => {
        // //calling backend API
        fetch(`${process.env.REACT_APP_BACKEND_DEV_URL}/event/homepage/${eventID}`, {
            headers: {
                'Content-Type': 'application/json',
            },
            method: 'GET',
        })
            .then(async (response) => {
                if (response.status == 200) {
                    const apiResponse = await response.json();
                    const data = apiResponse.data;
                    setEventDetail(data);
                    console.log(data);
                    setLoaded(true);
                } else {
                    //passing to parent component
                    //show alert msg
                    props.setOpenSnackbar(true);
                    props.setAlertType('error');
                    props.setAlertMsg(`Fetch data failed, code: ${response.status}`);
                }
            })
            .catch((err) => {
                window.alert(err);
            });
    }

    useEffect(() => {
        if (!loaded) {
            loadEventDetailByID();
        }
    }, []);


    return (
        <div>
            {loaded ?

                <Modal
                    open={reviewOpen}
                    onClose={handleReviewEventModalClose}

                    sx={{
                        position: 'absolute',
                        overflow: 'scroll',
                        height: '100%',
                        display: 'box',
                    }}
                >
                    <Box sx={{ ...style, width: 800, mt:'15%', mb:'15%',height: 800 }} textAlign='center'>
                        <Sheet>
                            <Typography variant="h2" >{eventDetail.eventName}</Typography>
                            <Grid container spacing={3} sx={{ textAlign: "left" }}>
                                {/* first row */}
                                <Grid item xs={12} sm={6}>
                                    <Typography variant="h6">Description</Typography>
                                    <Typography>{eventDetail.eventDescription}</Typography>
                                </Grid>
                                <Grid item xs={12} sm={6}>
                                    <Typography variant="h6">Other Info</Typography>
                                    <Typography>{eventDetail.otherEventInfo}</Typography>
                                </Grid>
                                {/* second row */}
                                <Grid item xs={12} sm={6}>
                                    <Typography variant="h6">Event Date</Typography>
                                    <Typography>{new Date(eventDetail.eventDate).toLocaleString('en-us', { year: "numeric", month: "short", day: "numeric", hourCycle: "h24", hour: "numeric", minute: "numeric" })}</Typography>
                                </Grid>
                                <Grid item xs={12} sm={6}>
                                    <Typography variant="h6">Ticket Sale Date</Typography>
                                    <Typography>{new Date(eventDetail.ticketSaleDate).toLocaleString('en-us', { year: "numeric", month: "short", day: "numeric", hourCycle: "h24", hour: "numeric", minute: "numeric" })}</Typography>
                                </Grid>
                                {/* third row */}
                                <Grid item xs={12} sm={6}>
                                    <Typography variant="h6">Event Venue</Typography>
                                    <Typography>{eventDetail.venue.venueName}</Typography>
                                    <Typography>{eventDetail.venue.venueLocation}</Typography>
                                </Grid>
                                <Grid item xs={12} md={6}>
                                    <Typography variant="h6">
                                        Organiser Info
                                    </Typography>
                                    <Typography>Name: {eventDetail.organiser.name}</Typography>
                                    <Typography>Email: {eventDetail.organiser.email}</Typography>
                                </Grid>
                                {/* Artists */}
                                <Grid item xs={12} md={12} container justifyContent="center">
                                    <Typography variant="h6">Artists</Typography>
                                    <Grid container spacing={2} justifyContent="center">
                                        {eventDetail.artists.map((artist: any, index: any) => (
                                            <Grid item xs={12} sm={6} key={index}>
                                                <Box display="flex" flexDirection="column" alignItems="center">
                                                    <ListItem>
                                                        <ListItemAvatar>
                                                            <Avatar src={`https://authenticket.s3.ap-southeast-1.amazonaws.com/artists/${artist.artistImage}`} />
                                                        </ListItemAvatar>
                                                        <ListItemText primary={artist.artistName} />
                                                    </ListItem>
                                                </Box>
                                            </Grid>
                                        ))}
                                    </Grid>
                                </Grid>
                                <Grid item xs={12} md={12}>
                                    <CardMedia
                                        component="img"
                                        height="400"
                                        alt="Event Poster"
                                        src={`https://authenticket.s3.ap-southeast-1.amazonaws.com/event_images/${eventDetail.eventImage}`}
                                        sx={{ padding: "1em 1em 0 1em", objectFit: "contain" }}
                                    />
                                </Grid>


                            </Grid>
                            <Sheet sx={{ alignItems: "center", mb: 5, mt: 5 }}>
                                <Button color="error" variant="contained" >Reject</Button>
                                <Button color="success" variant="contained" sx={{ ml: 10 }} >Accpet</Button>
                            </Sheet>
                        </Sheet>
                    </Box>
                </Modal>
                : null}

        </div>
    )
}