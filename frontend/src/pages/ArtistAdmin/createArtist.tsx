import {
    Box, Typography, Modal,
    Grid, Button, ListItemText, CardMedia, TextareaAutosize, TextField, FormControl, InputLabel, Select, MenuItem, OutlinedInput, Checkbox
} from '@mui/material';
import React, { useEffect, useState } from 'react';
import { Sheet } from '@mui/joy';

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

const delay = (ms: number) => new Promise(
    resolve => setTimeout(resolve, ms)
);

export default function CreateArtist(props: any) {

    const token = window.localStorage.getItem('accessToken');
    const [artistID, setartistID] = React.useState('');
    const [loaded, setLoaded]: any = React.useState(false);
    const [reviewOpen, setReviewOpen] = React.useState(true);
    const [isClicked, setIsClicked] = useState(false);
    const [artistName, setArtistName]: any = React.useState(null);
    const [artistImage, setArtistImage]: any = React.useState(null);
    const [imageName, setImageName]: any = React.useState(null);

    //for pop up message => error , warning , info , success
    const [openSnackbar, setOpenSnackbar] = useState(false);
    const [alertType, setAlertType]: any = useState('info');
    const [alertMsg, setAlertMsg] = useState('');
    const handleSnackbarClose = () => {
        setOpenSnackbar(false);
    };

    const handleCreateArtist = () => {
        if (!isClicked) {
            //Disable further clicks
            setIsClicked(true);

            //call backend to create events
            const formData = new FormData();
            formData.append('name', artistName);

            //TODO: add special requirement into formdata, pending BE
            //calling create event backend API
            //call create artist API first
            fetch(`${process.env.REACT_APP_BACKEND_URL}/artist`, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                },
                method: 'POST',
                body: formData
            })
                .then(async (response) => {
                    if (response.status == 200 || response.status == 201) {
                        //save artistId
                        const apiResponse = await response.json();
                        const data = apiResponse.data;

                        await delay(2000);
                        //call update artist image
                        updateArtistImage(data.artistId);
                    } else {
                        const eventResponse = await response.json();
                        setOpenSnackbar(true);
                        setAlertType('warning');
                        setAlertMsg(eventResponse.message);
                        //if transaction faile, enable clickable
                        setIsClicked(false);
                    }
                })
                .catch((err) => {
                    //if transaction faile, enable clickable
                    setIsClicked(false);
                    window.alert(err);
                });

        }
    };

    //handle image file selected
    const [selectedFile, setSelectedFile]: any = useState(null);
    const [fileUploaded, setFileUploaded] = useState(false);

    const handleFileSelect = (event: any) => {
        setFileUploaded(true);
        const file = event.target.files[0];
        setSelectedFile(file);
    };

    //update artist image
    const updateArtistImage = async (props: any) => {
      
            const formData = new FormData();
            if (fileUploaded) {
                formData.append('artistImage', selectedFile);
            }
            formData.append('imageName', imageName);
            formData.append('artistId', props.artistId);

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
                        setOpenSnackbar(true);
                        setAlertType('success');
                        setAlertMsg(`Artist has been successfully created!`);
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

    const handleReviewEventModalClose = () => {
        setReviewOpen(false);
        //to update parent element
        props.open(false);
    }

    const handleCreate = (event: any) => {
        event.preventDefault();
        handleCreateArtist();
    }

    // Generate a unique query parameter based on the current time
    const timestamp = new Date().getTime();

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
                            <Typography variant="h2" >Create Artist</Typography>
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

                            <Button color="success" variant="contained" sx={{ mt: 3 }} onClick={handleCreate}>Update</Button>

                        </Sheet>
                    </Box>
                </Modal>
                : null}

        </div>
    )
}