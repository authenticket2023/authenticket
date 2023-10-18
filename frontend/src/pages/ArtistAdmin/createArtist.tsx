import {
    Box, Typography, Modal,
    Grid, Button, TextField, ImageListItem, ImageList,
} from '@mui/material';
import React, { useState } from 'react';
import { Sheet } from '@mui/joy';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';

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
    const [modalOpen, setModalOpen] = React.useState(true);
    const [isClicked, setIsClicked] = useState(false);
    const [artistName, setArtistName]: any = React.useState(null);

    const handleConfirm = (event: any) => {
        event.preventDefault();

        if (!fileUploaded) {
            //show alert msg
            props.setOpenSnackbar(true);
            props.setAlertType('error');
            props.setAlertMsg("Need at least one event poster!!!");
        } else {
            handleCreateArtist();
        }
    };

    const handleCreateArtist = async () => {
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

                        //call update artist image
                        await updateArtistImage(data.artistId);
                    } else {
                        const eventResponse = await response.json();
                        props.setOpenSnackbar(true);
                        props.setAlertType('warning');
                        props.setAlertMsg(eventResponse.message);
                        //if transaction failed, enable clickable
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

    const handleArtistName = (event: any) => {
        const name = event.target.value;
        setArtistName(name);
    }

    const handleReload = () => {
        props.setReload(true);
    }

    //update artist image
    const updateArtistImage = async (artistID: any) => {
        const formData = new FormData();
        if (fileUploaded) {
            formData.append('artistImage', selectedFile);
        }
        formData.append('imageName', `${artistName}.jpeg`);
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
                    props.setAlertMsg(`Artist has been successfully created!`);
                    setModalOpen(false);
                    handleReload();
                } else {
                    props.setOpenSnackbar(true);
                    props.setAlertType('error');
                    props.setAlertMsg(`${apiResponse['message']}`);
                }
            })
            .catch((err) => {
                //close the modal
                setModalOpen(false);
                window.alert(err);
            });
    }

    const handleCreateArtistModalClose = () => {
        setModalOpen(false);
        //to update parent element
        props.open(false);
    }


    // Generate a unique query parameter based on the current time
    const timestamp = new Date().getTime();

    return (
        <div>

            <Modal
                open={modalOpen}
                onClose={handleCreateArtistModalClose}
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
                        <Grid container spacing={2}>
                            <Grid item xs={6} sx={{ marginTop: 4 }}>
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
                                            Upload Artist Image
                                            <input
                                                type="file"
                                                hidden
                                                onChange={handleFileSelect}
                                                accept="image/*"
                                            />
                                        </Button>
                                    </Grid>
                                </Grid>
                            </Grid>
                            <Grid item xs={6}>
                                <Grid container spacing={2} sx={{ marginLeft: 2, marginTop: 2 }}>
                                    <Grid item xs={12}>
                                        {selectedFile != null && (
                                            <ImageListItem key="single">
                                                <img
                                                    src={`${URL.createObjectURL(selectedFile)}`}
                                                    srcSet={`${URL.createObjectURL(selectedFile)}`}
                                                    alt={`Selected 1`}
                                                    style={{ maxWidth: '300px', maxHeight: '250px', width: 'auto', height: 'auto' }}
                                                    loading="lazy"
                                                />
                                            </ImageListItem>
                                        )}
                                    </Grid>
                                </Grid>
                            </Grid>
                        </Grid>

                        {/* Basic information section */}
                        <Typography variant="h6" sx={{ mb: 2 }} textAlign='left'>Basic Information</Typography>
                        <Grid container spacing={3} sx={{ textAlign: "left" }}>
                            <Grid item xs={12} sm={4}>
                                <TextField
                                    required
                                    id="outlined-required"
                                    label="Artist name"
                                    fullWidth
                                    defaultValue={artistName}
                                    onChange={handleArtistName}
                                />
                            </Grid>
                        </Grid>

                        <Button color="success" variant="contained" sx={{ mt: 3 }} onClick={handleConfirm}>Create Artist</Button>

                    </Sheet>
                </Box>
            </Modal>

        </div>
    )
}