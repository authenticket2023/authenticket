import {
    Box, Modal, Button, TextField, Avatar, Typography, Grid, TextareaAutosize, ImageList, ImageListItem, FormControl, InputLabel, Select, MenuItem, OutlinedInput, Checkbox, ListItemText, InputAdornment, FormGroup, Switch, FormControlLabel
} from '@mui/material';
import React, { useEffect, useState } from 'react';
import BasicDatePicker from '../../utility/dateElement';
import { Sheet } from '@mui/joy';
import dayjs, { Dayjs } from 'dayjs';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';

export function EventDetails(props: any) {
    useEffect(() => {

        if (eventTypeList.length == 0) {
            eventTypeFetcher();
        }

    }, [props.venue]);
    const handleEventName = (event: any) => {
        props.setEventName(event.target.value);
    };

    const handleEventDescription = (event: any) => {
        props.setEventDescription(event.target.value);
    };

    const handleEventDate = (newDate: Dayjs | null) => {
        props.setEventDate(dayjs(newDate))
    };

    const handleSaleDate = (newDate: Dayjs | null) => {
        props.setSaleDate(dayjs(newDate))
    };

    const handleFacialCheckin = (event: any) => {
        props.setFacialCheckIn(event.target.value);
        if (props.facialCheckIn === true) {
            props.setFacialCheckIn(false);
        } else {
            props.setFacialCheckIn(true);
        }
    };

    const handlePresale = (event: any) => {
        props.setPresale(event.target.value);
        if (props.presale === true) {
            props.setPresale(false);
        } else {
            props.setPresale(true);
        }
    };

    const handleOtherInfo = (event: any) => {
        props.setOtherInfo(event.target.value);
    };
    const token = window.localStorage.getItem('accessToken');
    const [eventTypeList, setEventTypeList]: any = React.useState([]);
    //retrieve artists from DB
    const eventTypeFetcher = async () => {
        try {
            const response = await fetch(`${process.env.REACT_APP_BACKEND_URL}/event-type`, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
                method: 'GET'
            });

            if (response.status !== 200) {
                //show alert msg
                props.setOpenSnackbar(true);
                props.setAlertType('error');
                props.setAlertMsg("error fetching data!!!");
            } else {
                const data = await response.json();
                setEventTypeList(data['data']);
            }
        } catch (err) {
            window.alert(err);
        }
    };
    const handleEventType = (event: any) => {
        props.setEventType(event.target.value);
    };

    const handleConfirm = (event: any) => {
        event.preventDefault();
        //input validation
        if (!(dayjs(props.eventDate).isAfter(dayjs(props.currentDateTime)) && dayjs(props.saleDate).isAfter(dayjs(props.currentDateTime)))) {
            //show alert msg
            props.setOpenSnackbar(true);
            props.setAlertType('error');
            props.setAlertMsg("Invlid event/sale date!!!");
        } else if (dayjs(props.eventDate).isBefore(dayjs(props.saleDate))) {
            //show alert msg
            props.setOpenSnackbar(true);
            props.setAlertType('error');
            props.setAlertMsg("Sale date cannot be after Event date!!!");
        } else if (props.ticketNumberVIP < 0 || props.ticketNumberCat1 < 0 || props.ticketNumberCat2 < 0 || props.ticketNumberCat3 < 0 || props.ticketNumberCat4 < 0) {
            //show alert msg
            props.setOpenSnackbar(true);
            props.setAlertType('error');
            props.setAlertMsg("Invlid number of ticket avaliable: cannot be less than 0!!!");
        } else {
            //mean all the input is correct
            props.handleComplete();
        }
    };


    return (
        <form onSubmit={(handleConfirm)}>
            <Box sx={{ mt: 2 }}>
                <Sheet>
                      {/* Section 1 */}
                    <Typography variant="h6" gutterBottom sx={{ mb: 1 }}>
                        Basic Information
                    </Typography>
                    <Grid container spacing={3}>
                        <Grid item xs={12} sm={4}>
                            <TextField
                                required
                                label="Event name"
                                fullWidth
                                value={props.eventName}
                                onChange={handleEventName}
                            />
                        </Grid>
                        <Grid item xs={12} sm={2}>
                            <Box sx={{ minWidth: 120 }}>
                                <FormControl fullWidth>
                                    <InputLabel>Event Type</InputLabel>
                                    <Select
                                        value={props.eventType}
                                        label="Event Type"
                                        onChange={handleEventType}
                                        required
                                    >
                                        {eventTypeList.map((eventType: any) => (
                                            <MenuItem key={eventType.eventTypeId} value={eventType.eventTypeId}>{eventType.eventTypeName}</MenuItem>
                                        ))}
                                    </Select>
                                </FormControl>
                            </Box>
                        </Grid>
                        <Grid item xs={12} sm={3}>
                            <BasicDatePicker onDateChange={handleEventDate} value={dayjs(props.eventDate)} label="Event Date" />
                        </Grid>
                        <Grid item xs={12} sm={3}>
                            <BasicDatePicker onDateChange={handleSaleDate} value={dayjs(props.saleDate)} label="Ticket Sale Date" />
                        </Grid>
                        <Grid item xs={12} sm={12}>
                            <TextareaAutosize required minRows="7" onChange={handleEventDescription}
                                style={{ width: "100%", fontSize: "inherit", font: "inherit", border: "1px solid light-grey", borderRadius: 4 }}
                                id='Description' className='StyledTextarea' value={props.eventDescription} placeholder="Event Description" />
                        </Grid>
                    </Grid>
                    {/* Section 2 */}
                    <Typography variant="h6" gutterBottom sx={{ mb: 1, mt: 1 }}>
                        Special Requirement
                    </Typography>
                    <Grid item xs={12} sm={3}>
                        <FormControlLabel control={<Switch checked={props.facialCheckIn} onChange={handleFacialCheckin} />} sx={{mr:5}} label="Facial Verification(Prevent ticket scalpers)" />
                        <FormControlLabel control={<Switch checked={props.presale} onChange={handlePresale} />} label="Offer presale (*Hot event)" />
                    </Grid>
                    {/* Section 3 */}
                    <Typography variant="h6" gutterBottom sx={{ mb: 1, mt: 1 }}>
                        Other Information
                    </Typography>
                    <Grid item xs={12} sm={12}>
                        <TextareaAutosize minRows="7" onChange={handleOtherInfo}
                            style={{ width: "100%", fontSize: "inherit", font: "inherit", border: "1px solid light-grey", borderRadius: 4 }}
                            className='StyledTextarea' value={props.otherInfo} placeholder="Other Event Info" />
                    </Grid>

                </Sheet>
                <Sheet sx={{ alignItems: "center", mt: 2, mb: 2 }}>
                    <Button type='submit' fullWidth variant="contained" sx={{ p: 1.5, textTransform: "none", fontSize: "16px" }}>COMPLETE STEP</Button>
                </Sheet>
            </Box>
        </form>
    )
}

//for venue dropdown checkbox
const ITEM_HEIGHT = 48;
const ITEM_PADDING_TOP = 8;
const MenuProps = {
    PaperProps: {
        style: {
            maxHeight: ITEM_HEIGHT * 4.5 + ITEM_PADDING_TOP,
            width: 250,
        },
    },
};

export function VenueArtist(props: any) {

    //to detect change on props.venue
    useEffect(() => {
        if (props.venue == '999') {
            setShowOtherVenue(true);
        } else {
            setShowOtherVenue(false);
        }
        if (artistList.length == 0) {
            artistFetcher();
        }
        if (venueList.length == 0) {
            venueFetcher();
        }
    }, [props.venue]);

    const [artistList, setArtistList]: any = React.useState([]);
    const [venueList, setVenueList]: any = React.useState([]);

    const token = window.localStorage.getItem('accessToken');
    //retrieve artists from DB
    const artistFetcher = async () => {
        try {
            const response = await fetch(`${process.env.REACT_APP_BACKEND_URL}/artist`, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
                method: 'GET'
            });

            if (response.status !== 200) {
                //show alert msg
                props.setOpenSnackbar(true);
                props.setAlertType('error');
                props.setAlertMsg("error fetching data!!!");
            } else {
                const data = await response.json();
                setArtistList(data['data']);
            }
        } catch (err) {
            window.alert(err);
        }
    };

    //retrieve venue from DB
    const venueFetcher = async () => {
        try {
            const token = window.localStorage.getItem('accessToken');
            const response = await fetch(`${process.env.REACT_APP_BACKEND_URL}/venue`, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
                method: 'GET'
            });
            if (response.status !== 200) {
                //show alert msg
                props.setOpenSnackbar(true);
                props.setAlertType('error');
                props.setAlertMsg("error fetching data!!!");
            } else {
                const data = await response.json();
                setVenueList(data['data']);
            }
        } catch (err) {
            window.alert(err);
        }
    };


    const [showOtherVenue, setShowOtherVenue] = useState(false);
    const handleVenue = (event: any) => {
        props.setVenue(event.target.value);
    };

    const handleOtherVenue = (event: any) => {
        props.setOtherVenue(event.target.value);
    };

    const handleVIPPrice = (event: any) => {
        props.setVIPPrice(event.target.value);
    };

    const handleCat1Price = (event: any) => {
        props.setCat1Price(event.target.value);
    };

    const handleCat2Price = (event: any) => {
        props.setCat2Price(event.target.value);
    };

    const handleCat3Price = (event: any) => {
        props.setCat3Price(event.target.value);
    };

    const handleCat4Price = (event: any) => {
        props.setCat4Price(event.target.value);
    };

    const handleArtist = (event: any) => {
        const {
            target: { value },
        } = event;
        props.setartistList(
            // On autofill we get a stringified value.
            typeof value === 'string' ? value.split(',') : value,
        );
    };

    const handleConfirm = (event: any) => {
        event.preventDefault();
        props.handleComplete();
    };

    return (
        <form onSubmit={handleConfirm}>
            <Box sx={{ mt: 2 }}>
                {/* Venue */}
                <Sheet sx={{ marginTop: 2 }}>
                    <Typography variant="h6" gutterBottom sx={{ marginBottom: 2 }}>
                        Veneue
                    </Typography>
                    <Grid container spacing={3}>
                        <Grid item xs={12} sm={4}>
                            <Box sx={{ minWidth: 120 }}>
                                <FormControl fullWidth>
                                    <InputLabel>Event Venue</InputLabel>
                                    <Select
                                        value={props.venue}
                                        label="Event Venue"
                                        onChange={handleVenue}
                                        required
                                    >
                                        {venueList.map((venue: any) => (
                                            <MenuItem key={venue.venueId} value={venue.venueId}>{venue.venueName}</MenuItem>
                                        ))}
                                    </Select>
                                </FormControl>
                            </Box>
                        </Grid>
                        {showOtherVenue ?
                            <Grid item xs={12} sm={4}>
                                <TextField
                                    required
                                    label="Other venue"
                                    fullWidth
                                    value={props.otherVenue}
                                    onChange={handleOtherVenue}
                                />
                            </Grid>
                            : null}
                    </Grid>

                    <Grid container>
                        <Typography> Here will show 3D</Typography>
                    </Grid>
                    {/* Ticket Price */}
                    <Typography variant="h6" gutterBottom sx={{ mb: 1, mt: 1 }}>
                        Ticket Price
                    </Typography>
                    <Grid container spacing={3}>
                        {/* first row */}
                        <Grid item xs={12} sm={4}>
                            <FormControl fullWidth>
                                <InputLabel >VIP Price</InputLabel>
                                <OutlinedInput
                                    type='number'
                                    inputProps={{ inputMode: 'numeric', pattern: '[0-9]*' }}
                                    startAdornment={<InputAdornment position="start">$</InputAdornment>}
                                    label="VIPPrice"
                                    value={Number(props.VIPPrice)}
                                    onChange={handleVIPPrice}
                                />
                            </FormControl>
                        </Grid>
                        <Grid item xs={12} sm={4}>
                            <FormControl fullWidth>
                                <InputLabel >Cat1 Price</InputLabel>
                                <OutlinedInput
                                    type='number'
                                    inputProps={{ inputMode: 'numeric', pattern: '[0-9]*' }}
                                    startAdornment={<InputAdornment position="start">$</InputAdornment>}
                                    label="VIPPrice"
                                    value={Number(props.cat1Price)}
                                    onChange={handleCat1Price}
                                />
                            </FormControl>
                        </Grid>
                        <Grid item xs={12} sm={4}>
                            <FormControl fullWidth>
                                <InputLabel >Cat2 Price</InputLabel>
                                <OutlinedInput
                                    type='number'
                                    inputProps={{ inputMode: 'numeric', pattern: '[0-9]*' }}
                                    startAdornment={<InputAdornment position="start">$</InputAdornment>}
                                    label="VIPPrice"
                                    value={Number(props.cat2Price)}
                                    onChange={handleCat2Price}
                                />
                            </FormControl>
                        </Grid>
                        {/* second row*/}
                        <Grid item xs={12} sm={4}>
                            <FormControl fullWidth>
                                <InputLabel >Cat3 Price</InputLabel>
                                <OutlinedInput
                                    type='number'
                                    inputProps={{ inputMode: 'numeric', pattern: '[0-9]*' }}
                                    startAdornment={<InputAdornment position="start">$</InputAdornment>}
                                    label="VIPPrice"
                                    value={Number(props.cat3Price)}
                                    onChange={handleCat3Price}
                                />
                            </FormControl>
                        </Grid>
                        <Grid item xs={12} sm={4}>
                            <FormControl fullWidth>
                                <InputLabel >Cat4 Price</InputLabel>
                                <OutlinedInput
                                    type='number'
                                    inputProps={{ inputMode: 'numeric', pattern: '[0-9]*' }}
                                    startAdornment={<InputAdornment position="start">$</InputAdornment>}
                                    label="VIPPrice"
                                    value={Number(props.cat4Price)}
                                    onChange={handleCat4Price}
                                />
                            </FormControl>
                        </Grid>
                    </Grid>
                </Sheet>
                {/* Artist */}
                <Sheet sx={{ marginTop: 2 }}>
                    <Typography variant="h6" gutterBottom sx={{ marginBottom: 2 }}>
                        Artist
                    </Typography>
                    <Grid container spacing={3}>
                        <Grid item xs={12} sm={9}>
                            <FormControl sx={{}} fullWidth>
                                <InputLabel >Artist</InputLabel>
                                <Select
                                    required
                                    multiple
                                    value={props.artistList}
                                    onChange={handleArtist}
                                    input={<OutlinedInput label="Artist" />}
                                    renderValue={(selected) =>
                                        selected
                                            .map((selectedId: any) => {
                                                const selectedArtist = artistList.find((artist: any) => artist.artistId === selectedId);
                                                return selectedArtist ? selectedArtist.artistName : '';
                                            })
                                            .join(', ')
                                    }
                                    MenuProps={MenuProps}
                                >
                                    {artistList.map((data: any) => (
                                        <MenuItem key={data?.artistId} value={data?.artistId}>
                                            <Checkbox checked={props.artistList.indexOf(data.artistId) > -1} />
                                            <ListItemText primary={data?.artistName} />
                                        </MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                        </Grid>

                    </Grid>
                </Sheet>
            </Box>
            <Sheet sx={{ alignItems: "center", mt: 2, mb: 2 }}>
                <Button type='submit' fullWidth variant="contained" sx={{ p: 1.5, textTransform: "none", fontSize: "16px" }}>COMPLETE STEP</Button>
            </Sheet>
        </form>
    )
}

export function EventPoster(props: any) {

    useEffect(() => {
    }, []);
    const [fileUploaded, setFileUploaded] = useState(false);
    const handleFileChange = (event: any) => {
        // Get the selected files from the input
        setFileUploaded(true);
        const files = event.target.files;
        props.setSelectedFiles(Array.from(files));
    };

    const handleConfirm = (event: any) => {
        event.preventDefault();

        if (!fileUploaded) {
            //show alert msg
            props.setOpenSnackbar(true);
            props.setAlertType('error');
            props.setAlertMsg("Need at least one event poster!!!");
        } else {
            props.handleComplete();
        }
    };

    return (
        <form onSubmit={handleConfirm}>
            <Box sx={{ mt: 2 }}>
                <Grid container spacing={2}>
                    <Grid item xs={6} sx={{}}>
                        <Grid container spacing={2} sx={{}}>
                            <Grid item xs={12}>

                                <Button
                                    variant="outlined"
                                    component="label"
                                    sx={{ marginBottom: 2 }}
                                    size='large'
                                    startIcon={<CloudUploadIcon />}
                                    fullWidth
                                >
                                    Upload Event Poster
                                    <input
                                        type="file"
                                        hidden
                                        onChange={handleFileChange}
                                        accept="image/*"
                                    />
                                </Button>
                            </Grid>
                        </Grid>
                    </Grid>
                    <Grid item xs={6}>
                        <Grid container spacing={2} sx={{ marginLeft: 2 }}>
                            <Grid item xs={12}></Grid>
                            {props.selectedFiles != null && (
                                <ImageList sx={{ width: 575, height: "100%" }} cols={1} rowHeight={250}>
                                    {props.selectedFiles.map((file: any, index: any) => (
                                        <ImageListItem key={index}>
                                            <img
                                                src={`${URL.createObjectURL(file)}?w=575&h=250&fit=crop&auto=format`}
                                                srcSet={`${URL.createObjectURL(file)}`}
                                                alt={`Selected ${index + 1}`}
                                                loading="lazy"
                                            />
                                        </ImageListItem>
                                    ))}
                                </ImageList>

                            )}
                        </Grid>
                    </Grid>
                </Grid>
            </Box>
            <Sheet sx={{ alignItems: "center", mt: 2, mb: 2 }}>
                <Button type='submit' fullWidth variant="contained" sx={{ p: 1.5, textTransform: "none", fontSize: "16px" }}>COMPLETE STEP</Button>
            </Sheet>
        </form>
    )
}