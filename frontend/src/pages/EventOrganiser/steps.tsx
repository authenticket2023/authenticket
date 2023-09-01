import {
    Box, Modal, Button, TextField, Avatar, Typography, Grid, TextareaAutosize, ImageList, ImageListItem, FormControl, InputLabel, Select, MenuItem, OutlinedInput, Checkbox, ListItemText
} from '@mui/material';
import React, { useEffect, useState } from 'react';
import BasicDatePicker from './dateElement';
import { Sheet } from '@mui/joy';
import dayjs, { Dayjs } from 'dayjs';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';

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
        <Box sx={{ mt: 2 }}>
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
                        <BasicDatePicker onDateChange={handleEventDate} value={eventDate} label="Event Date" />
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
                        <BasicDatePicker onDateChange={handleSaleDate} value={saleDate} label="Ticket Sale Date" />
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

//options for venue
const artist = [
    'Ludwig van Beethoven',
    'The Beatles',
    'Wolfgang Amadeus Mozart',
    'Bob Dylan',
    'Beyoncé',
    'Johann Sebastian Bach',
    'Elvis Presley',
    'Michael Jackson',
    'Miles Davis',
    'Prince',
];

export function VenueArtist(props: any) {

    useEffect(() => {
    }, []);

    const [venue, setVenue] = useState('');
    const [artistList, setartistList] = useState<string[]>([]);

    const handleVenue = (event: any) => {
        setVenue(event.target.value);
    };

    const handleArtist = (event: any) => {
        const {
            target: { value },
        } = event;
        setartistList(
            // On autofill we get a stringified value.
            typeof value === 'string' ? value.split(',') : value,
        );
    };

    return (
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
                                    value={venue}
                                    label="Event Venue"
                                    onChange={handleVenue}
                                >
                                    <MenuItem value={"Venue 1"}>Venue 1</MenuItem>
                                    <MenuItem value={"Venue 2"}>Venue 2</MenuItem>
                                    <MenuItem value={"Venue 3"}>Venue 3</MenuItem>
                                    <MenuItem value={"Venue 4"}>Venue 4</MenuItem>
                                    <MenuItem value={"Other"}>Other</MenuItem>
                                </Select>
                            </FormControl>
                        </Box>
                    </Grid>
                </Grid>

                <Grid container>
                    <Typography> Here will show 3D</Typography>
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
                                multiple
                                value={artistList}
                                onChange={handleArtist}
                                input={<OutlinedInput label="Artist" />}
                                renderValue={(selected) => selected.join(', ')}
                                MenuProps={MenuProps}
                            >
                                {artist.map((artist) => (
                                    <MenuItem key={artist} value={artist}>
                                        <Checkbox checked={artistList.indexOf(artist) > -1} />
                                        <ListItemText primary={artist} />
                                    </MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                    </Grid>
                </Grid>

            </Sheet>
        </Box>

    )
}

export function EventPoster(props: any) {

    useEffect(() => {
    }, []);

    const [selectedFiles, setSelectedFiles] = useState([]);
    const handleFileChange = (event: any) => {
        // Get the selected files from the input
        const files = event.target.files;
        setSelectedFiles(Array.from(files));
    };

    return (
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
                                    multiple
                                />
                            </Button>
                        </Grid>
                    </Grid>
                </Grid>
                <Grid item xs={6}>
                    <Grid container spacing={2} sx={{ marginLeft: 2 }}>
                        <Grid item xs={12}></Grid>
                        {selectedFiles.length > 0 && (
                            <ImageList sx={{ width: 500, height: "100%" }} cols={3} rowHeight={164} gap={2}>
                                {selectedFiles.map((file, index) => (
                                    <ImageListItem key={index}>
                                        <img
                                            src={`${URL.createObjectURL(file)}?w=164&h=164&fit=crop&auto=format`}
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

    )
}