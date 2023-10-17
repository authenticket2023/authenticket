import {
    Box, Typography, Modal,
    Grid, Button, ListItemText, CardMedia, TextareaAutosize, TextField, FormControl, InputLabel, Select, MenuItem, OutlinedInput, Checkbox
} from '@mui/material';
import React, { useEffect, useState } from 'react';
import { Sheet } from '@mui/joy';
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

export default function UpdateArtist(props: any) {

    const token = window.localStorage.getItem('accessToken');
    const [artistID, setartistID] = React.useState(props.artistID);
    const [artistDetail, setArtistDetail]: any = React.useState();
    const [loaded, setLoaded]: any = React.useState(false);
    const [reviewOpen, setReviewOpen] = React.useState(true);
    const [artistName, setArtistName]: any = React.useState(null);
    const [artistImage, setArtistImage]: any = React.useState(null);
    const [imageName, setImageName]: any = React.useState(null);

    const handleReviewEventModalClose = () => {
        setReviewOpen(false);
        //to update parent element
        props.open(false);
    }

    const loadArtistDetailByID = async () => {
        // //calling backend API
        fetch(`${process.env.REACT_APP_BACKEND_URL}/artist/${artistID}`, {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
            method: 'GET',
        })
            .then(async (response) => {
                if (response.status == 200) {
                    const apiResponse = await response.json();
                    const data = apiResponse.data;
                    setArtistDetail(data);
                    //set data for display
                    setArtistName(data.artistName);
                    setArtistImage(data.artistImage);
                    // setCreatedDate(dayjs(data.eventDate));
                    // setUpdatedDate(dayjs(data.eventDate));
                    // setDeletedDate(dayjs(data.eventDate));
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

    //handle image file selected
    const [selectedFile, setSelectedFile]: any = useState(null);
    const [fileUploaded, setFileUploaded] = useState(false);

    const handleFileSelect = (event: any) => {
        setFileUploaded(true);
        const file = event.target.files[0];
        setSelectedFile(file);
    };

    //update artist image
    const updateArtistStatus = async () => {
      
            const formData = new FormData();
            if (fileUploaded) {
                formData.append('artistImage', selectedFile);
            }
            formData.append('imageName', imageName);
            formData.append('artistId', artistID);

            fetch(`${process.env.REACT_APP_BACKEND_URL}/artist/image`, {
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
                        props.setAlertMsg(`Artist ID ${artistID} has been successfully updated!`);
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

    const handleUpdate = (event: any) => {
        event.preventDefault();
        updateArtistStatus();
    }

    const handleImageName = (event: any) => {
        const name = event.target.value;
        setImageName(name);
    }

    // Generate a unique query parameter based on the current time
    const timestamp = new Date().getTime();

    useEffect(() => {
        if (!loaded) {
            loadArtistDetailByID();
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
                            <Typography variant="h2" >Update Artist Image - #{artistDetail.artistId}</Typography>
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
                                    src={selectedFile ? URL.createObjectURL(selectedFile) : `https://authenticket.s3.ap-southeast-1.amazonaws.com/artists/${artistImage}?timestamp=${timestamp}`}
                                    sx={{ padding: "1em 1em 0 1em", objectFit: "contain", cursor: "pointer" }}
                                />
                            </label>

                            {/* Basic information section */}
                            <Typography variant="h6" sx={{ mb: 2 }} textAlign='left'>Basic Information</Typography>
                            <Grid container spacing={3} sx={{ textAlign: "left" }}>
                                <Grid item xs={12} sm={4}>
                                    <TextField
                                        disabled
                                        id="outlined-disabled"
                                        label="Artist name"
                                        fullWidth
                                        defaultValue={artistName}
                                    />
                                </Grid>

                                <Grid item xs={12} sm={4}>
                                    <TextField
                                        required
                                        id="outlined-required"
                                        label="Image name"
                                        fullWidth
                                        defaultValue={imageName}
                                        onChange={handleImageName}
                                    />
                                </Grid>

                                {/* add dates in disabled fields */}
                                {/* <Grid item xs={12} sm={2}>
                                    <TextField
                                        required
                                        id="outlined-disabled"
                                        label="Artist name"
                                        fullWidth
                                        defaultValue={artistName}
                                    />
                                </Grid> */}
                            </Grid>

                            <Button color="success" variant="contained" sx={{ mt: 3 }} onClick={handleUpdate}>Update</Button>

                        </Sheet>
                    </Box>
                </Modal>
                : null}

        </div>
    )
}