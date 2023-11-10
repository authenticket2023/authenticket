import {
    Box, Typography, Modal,
    Grid, Button, ListItemText, CardMedia, TextareaAutosize, TextField, FormControl, InputLabel, Select, MenuItem, OutlinedInput, Checkbox, ListItemAvatar, Avatar
} from '@mui/material';
import React, { useEffect, useState } from 'react';
import { Sheet } from '@mui/joy';
import BasicDatePicker from '../../utility/dateElement';
import dayjs, { Dayjs } from 'dayjs';

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

const TimestampConverter = (timestamp: number) => {
    // Create a Date object from the timestamp
    const date = new Date(timestamp);

    // Extract components
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0'); // Month is 0-based
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');

    const formattedTimestamp = `${year}-${month}-${day}T${hours}:${minutes}:00`;

    return formattedTimestamp;
}

export default function UpdateEvent(props: any) {
    const token = window.localStorage.getItem('accessToken');
    const adminID: any = window.localStorage.getItem('id');
    const [eventID, setEventID] = React.useState(props.eventID);
    const [eventDetail, setEventDetail]: any = React.useState();
    const [loaded, setLoaded]: any = React.useState(false);
    const [reviewOpen, setReviewOpen] = React.useState(true);
    const [eventName, setEventName]: any = React.useState(null);
    const handleReviewEventModalClose = () => {
        setReviewOpen(false);
        //to update parent element
        props.open(false);
    }

    const loadEventDetailByID = async () => {
        // //calling backend API
        fetch(`${process.env.REACT_APP_BACKEND_URL}/public/event/${eventID}`, {
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
                    //convert event type to id for dropdown
                    if (data.eventType == 'Musical') {
                        setEventType('1');
                    } else if (data.eventType == 'Concert') {
                        setEventType('2');
                    } else if (data.eventType == 'Sports') {
                        setEventType('3');
                    } else {
                        setEventType('4');
                    }
                    //set data for display
                    setEventName(data.eventName)
                    setEventDate(dayjs(data.eventDate));
                    setSaleDate(dayjs(data.ticketSaleDate));
                    setEventDescription(data.eventDescription);
                    setOtherInfo(data.otherEventInfo);
                    setLoaded(true);
                    //push selected artist
                    data.artists.map((artist: any) => {
                        selectedArtistList.push(Number(artist.artistId));
                    });
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

    const handleEventName = (event: any) => {
        setEventName(event.target.value);
    };

    const [eventTypeList, setEventTypeList]: any = React.useState([]);
    const [eventType, setEventType]: any = React.useState();
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
                setEventTypeList(data['data'].sort((a: any, b: any) => a.eventTypeId - b.eventTypeId));
            }
        } catch (err) {
            window.alert(err);
        }
    };
    const handleEventType = (event: any) => {
        setEventType(event.target.value);
    };

    const [artistList, setArtistList]: any = React.useState([]);
    const [selectedArtistList, setSelectedArtistList]: any = React.useState([]);

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
                console.log(data)
                setArtistList(data['data'].sort((a: any, b: any) => a.artistId - b.artistId));
            }
        } catch (err) {
            window.alert(err);
        }
    };

    const handleArtist = (event: any) => {
        const {
            target: { value },
        } = event;
        setSelectedArtistList(
            // On autofill we get a stringified value.
            typeof value === 'string' ? value.split(',') : value,
        );
    };

    //get today's date with format YYYY-MM-DD HH:mm:ss
    const today = new Date(),
        currentDateTime = today.getFullYear() + '-' + (today.getMonth() + 1) + '-' + today.getDate() + ' ' + today.getHours() + ':' + today.getMinutes() + ':' + today.getSeconds();


    const [eventDate, setEventDate] = React.useState<Dayjs>();
    const [saleDate, setSaleDate] = React.useState<Dayjs>();

    const handleEventDate = (newDate: Dayjs | null) => {
        setEventDate(dayjs(newDate))
    };

    const handleSaleDate = (newDate: Dayjs | null) => {
        setSaleDate(dayjs(newDate))
    };

    const [eventDescription, setEventDescription] = useState('');
    const [otherInfo, setOtherInfo] = useState('');

    const handleEventDescription = (event: any) => {
        setEventDescription(event.target.value);
    };

    const handleOtherInfo = (event: any) => {
        setOtherInfo(event.target.value);
    };

    const [selectedFile, setSelectedFile]: any = useState(null);
    const [fileUploaded, setFileUploaded] = useState(false);

    const handleFileSelect = (event: any) => {
        setFileUploaded(true);
        const file = event.target.files[0];
        setSelectedFile(file);
    };

    const updateArtists = async () => {
        let artistIdString = '';
        //change selected artists ID into '1,2,3,4' format
        selectedArtistList.map((item: any) => {
            artistIdString += `${item},`;
        });

        const formData = new FormData();
        formData.append('eventId', eventID);
        formData.append('artistIdString', artistIdString);
        fetch(`${process.env.REACT_APP_BACKEND_URL}/event/update-artist`, {
            headers: {
                'Authorization': `Bearer ${token}`,
            },
            method: 'PUT',
            body: formData
        })
            .then(async (response) => {
                const apiResponse = await response.json();
                if (response.status == 200) {
                    props.setOpenSnackbar(true);
                    props.setAlertType('success');
                    props.setAlertMsg(`Information for Event ID ${eventID} has been successfully updated!`);
                    setReviewOpen(false);
                    //to update parent element
                    props.open(false);
                    props.setReload(true);
                } else {
                    props.setOpenSnackbar(true);
                    props.setAlertType('error');
                    props.setAlertMsg(`${apiResponse['message']}`);
                }
            })
            .catch((err) => {
                //close the modal
                setReviewOpen(false);
                //to update parent element
                props.open(false);
                props.setReload(true);
                window.alert(err);
            });

    }

    //update event details
    const updateEventStatus = async () => {

        if (!(dayjs(eventDate).isAfter(dayjs(currentDateTime)) && dayjs(saleDate).isAfter(dayjs(currentDateTime)))) {
            //show alert msg
            props.setOpenSnackbar(true);
            props.setAlertType('error');
            props.setAlertMsg("Invlid event/sale date!!!");
        } else {
            const formData = new FormData();
            formData.append('eventId', eventID);
            formData.append('eventName', eventName);
            formData.append('eventDescription', eventDescription);
            formData.append('eventDate', TimestampConverter(Number(eventDate)));
            formData.append('ticketSaleDate', TimestampConverter(Number(saleDate)));
            formData.append('otherEventInfo', otherInfo);
            formData.append('typeId', eventType);
            if (fileUploaded) {
                formData.append('file', selectedFile);
            }

            fetch(`${process.env.REACT_APP_BACKEND_URL}/event`, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                },
                method: 'PUT',
                body: formData
            })
                .then(async (response) => {
                    const apiResponse = await response.json();
                    if (response.status == 200) {
                        updateArtists();
                    } else {
                        props.setOpenSnackbar(true);
                        props.setAlertType('error');
                        props.setAlertMsg(`${apiResponse['message']}`);
                    }
                })
                .catch((err) => {
                    //close the modal
                    setReviewOpen(false);
                    //to update parent element
                    props.open(false);
                    props.setReload(true);
                    window.alert(err);
                });
        }
    }

    const handleUpdate = (event: any) => {
        event.preventDefault();
        updateEventStatus();
    }

    // Generate a unique query parameter based on the current time
    const timestamp = new Date().getTime();

    useEffect(() => {
        if (!loaded) {
            loadEventDetailByID();
            eventTypeFetcher();
            artistFetcher();
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
                    <Box sx={{ ...style, width: 1000, mt: '15%', mb: '15%', height: 800 }} textAlign='center'>
                        <Sheet>
                            <Typography variant="h2" >Update Event Details - #{eventDetail.eventId}</Typography>
                            <input
                                type="file"
                                accept="image/*"
                                onChange={handleFileSelect}
                                style={{ display: 'none' }} // Hide the input element
                                id="fileInput"
                            />
                            <label htmlFor="fileInput">
                                {/* This label will act as a button to trigger the file input */}
                                <CardMedia
                                    component="img"
                                    height="400"
                                    alt="Event Poster"
                                    src={selectedFile ? URL.createObjectURL(selectedFile) : `https://authenticket.s3.ap-southeast-1.amazonaws.com/event_images/${eventDetail.eventImage}?timestamp=${timestamp}`}
                                    sx={{ padding: "1em 1em 0 1em", objectFit: "contain", cursor: "pointer" }}
                                />
                            </label>

                            {/* Basic information section */}
                            <Typography variant="h6" sx={{ mb: 2 }} textAlign='left'>Basic Information</Typography>
                            <Grid container spacing={3} sx={{ textAlign: "left" }}>
                                <Grid item xs={12} sm={4}>
                                    <TextField
                                        required
                                        label="Event name"
                                        fullWidth
                                        value={eventName}
                                        onChange={handleEventName}
                                    />
                                </Grid>

                                <Grid item xs={12} sm={2}>
                                    <Box sx={{ minWidth: 120 }}>
                                        <FormControl fullWidth>
                                            <InputLabel>Event Type</InputLabel>
                                            <Select
                                                value={eventType}
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
                                    <BasicDatePicker onDateChange={handleEventDate} value={dayjs(eventDate)} label="Event Date" />
                                </Grid>
                                <Grid item xs={12} sm={3}>
                                    <BasicDatePicker onDateChange={handleSaleDate} value={dayjs(saleDate)} label="Ticket Sale Date" />
                                </Grid>

                                <Grid item xs={12} sm={12}>
                                    <TextareaAutosize required minRows="7" onChange={handleEventDescription}
                                        style={{ width: "100%", fontSize: "inherit", font: "inherit", border: "1px solid light-grey", borderRadius: 4 }}
                                        id='Description' className='StyledTextarea' value={eventDescription} placeholder="Event Description" />
                                </Grid>

                                <Grid item xs={12} sm={12}>
                                    <TextareaAutosize minRows="7" onChange={handleOtherInfo}
                                        style={{ width: "100%", fontSize: "inherit", font: "inherit", border: "1px solid light-grey", borderRadius: 4 }}
                                        className='StyledTextarea' value={otherInfo} placeholder="Other Event Info" />
                                </Grid>
                            </Grid>
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
                                                value={selectedArtistList}
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
                                                        <Checkbox checked={selectedArtistList.indexOf(data.artistId) > -1} />
                                                        <ListItemText primary={data?.artistName} />
                                                        <ListItemAvatar>
                                                            <Avatar src={`${process.env.REACT_APP_S3_URL}/artists/${data?.artistImage}`} alt={data?.artistName} />
                                                        </ListItemAvatar>
                                                    </MenuItem>
                                                ))}
                                            </Select>
                                        </FormControl>
                                    </Grid>

                                </Grid>
                            </Sheet>

                            <Button color="success" variant="contained" sx={{ mt: 3 }} onClick={handleUpdate}>Update</Button>

                        </Sheet>
                    </Box>
                </Modal>
                : null}

        </div>
    )
}