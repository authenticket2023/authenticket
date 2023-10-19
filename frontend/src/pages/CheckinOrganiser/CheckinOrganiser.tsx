import Webcam from 'react-webcam';
import { CameraOptions, useFaceDetection } from 'react-use-face-detection';
import FaceDetection from '@mediapipe/face_detection';
import { Camera } from '@mediapipe/camera_utils';
import { NavbarOrganiser } from '../../Navbar';
import { Navigate } from 'react-router-dom';
import { Alert, Box, Button, FormControl, Grid, ImageList, ImageListItem, InputLabel, MenuItem, Select, Snackbar, Typography } from '@mui/material';
import React, { useCallback, useEffect, useState } from 'react';

const width = 500;
const height = 500;

export const CheckinOrganiser = (): JSX.Element => {
    const { webcamRef, boundingBox, isLoading, detected, facesDetected }: any = useFaceDetection({
        faceDetectionOptions: {
            model: 'short',
        },
        faceDetection: new FaceDetection.FaceDetection({
            locateFile: (file) => `https://cdn.jsdelivr.net/npm/@mediapipe/face_detection/${file}`,
        }),
        camera: ({ mediaSrc, onFrame }: CameraOptions) =>
            new Camera(mediaSrc, {
                onFrame,
                width,
                height,
            }),
    });
    //for pop up message => error , warning , info , success
    const [openSnackbar, setOpenSnackbar] = useState(false);
    const [alertType, setAlertType]: any = useState('info');
    const [alertMsg, setAlertMsg] = useState('');
    const handleSnackbarClose = () => {
        setOpenSnackbar(false);
    };

    const token = window.localStorage.getItem('accessToken');
    const currUserEmail: any = window.localStorage.getItem('email');
    const role = window.localStorage.getItem('role');
    const organiserId: any = window.localStorage.getItem('id')

    const [fetched, setFetched]: any = React.useState(false);
    const [eventList, setEventList]: any = React.useState([]);
    const [eventID, setEventID]: any = useState(null);
    //retrieve event under the current organiser from DB
    const eventFetcher = async () => {
        try {
            const response = await fetch(`${process.env.REACT_APP_BACKEND_URL}/event-organiser/events/${organiserId}`, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
                method: 'GET'
            });
            if (response.status !== 200) {
                //show alert msg
                setOpenSnackbar(true);
                setAlertType('error');
                setAlertMsg("error fetching data!!!");
            } else {
                const data = await response.json();
                const sortedArray = data['data'].sort((a: any, b: any) => b.eventId - a.eventId);
                setEventList(sortedArray);
                setEventID(sortedArray[0].eventId);
                setFetched(true);
            }
        } catch (err) {
            window.alert(err);
        }
    };

    const handleEvent = (event: any) => {
        setEventID(event.target.value);
    };


    const [image, setImage]: any = useState(null);
    const capture = useCallback((eventID : any) => {
        const imageSrc = webcamRef.current.getScreenshot();
        setImage(imageSrc);
        checkFace(imageSrc , eventID);
        setTakePicture(false);
    }, [webcamRef]);
    //for count down and screenshot
    const [takePicture, setTakePicture]: any = useState(false);
    const [countdown, setCountdown] = useState(-1);

    const [startCheckin, setStartCheckin] = useState(false);
    const handleStartCheckin = (event: any) => {
        setStartCheckin(!startCheckin);
        setCountdown(-1);
    };
    const buttonColor = startCheckin ? 'error' : 'success';

    const checkFace = (image: any , eventID : any) => {
        //convert from base64 to file
        const base64DataWithoutPrefix = image.replace(/^data:image\/\w+;base64,/, '');
        const binaryImageData = atob(base64DataWithoutPrefix);
        // Create a Uint8Array from the binary data
        const uint8Array = new Uint8Array(binaryImageData.length);
        for (let i = 0; i < binaryImageData.length; i++) {
            uint8Array[i] = binaryImageData.charCodeAt(i);
        }
        // Create a Blob from the Uint8Array
        const blob = new Blob([uint8Array], { type: 'image/png' }); // Change the type accordingly
        // Create a File object from the Blob
        const file = new File([blob], 'image.jpg', { type: 'image/png' });
        const formData = new FormData();
        formData.append('image', file);
        formData.append('eventID', eventID);
        formData.append('email', currUserEmail);
        //calling create event backend API
        fetch(`${process.env.REACT_APP_FACIAL_URL}/face/facial-verification`, {
            headers: {
                'Authorization': `Bearer ${token}`,
            },
            method: 'POST',
            body: formData,
        })
            .then(async (response) => {
                if (response.status == 200) {
                    const apiResponse = await response.json();
                    setOpenSnackbar(true);
                    setAlertType('success');
                    setAlertMsg(apiResponse.message);
                    setCountdown(-1);
                } else {
                    const apiResponse = await response.json();
                    setOpenSnackbar(true);
                    setAlertType('warning');
                    setAlertMsg(apiResponse.message);
                    setCountdown(-1);
                }
            })
            .catch((err) => {
                //if transaction faile, enable clickable
                window.alert(err);
            });
    };
    useEffect(() => {
        //fetch event for the organiser
        if (!fetched) {
            eventFetcher();
        }
        if (startCheckin) {
            //take screenshot
            if (takePicture) {
                capture(eventID);
            }
            if (countdown > 0) {
                const timer = setTimeout(() => {
                    setCountdown(countdown - 1);
                }, 1000); // Countdown every 1 second (1000 milliseconds)
                return () => clearTimeout(timer);
            }

            if (countdown == 0) {
                setTakePicture(true);
            }
            if (!detected) {
                setCountdown(-1);
            }
            if (facesDetected == 1 && countdown == -1) {
                setCountdown(5);
            }
        }
    }, [facesDetected, takePicture, countdown, detected, startCheckin]);

    return (
        <Box>
            {
                token != null && role == 'ORGANISER' ?
                    <Navigate to="/CheckinOrganiser" /> : <Navigate to="/Forbidden" />
            }
            < NavbarOrganiser />
            <Grid container spacing={3} sx={{ mt: 3, ml: 3, }}>
                <Grid item xs={12} sm={4}>
                    <Box sx={{ minWidth: 120 }}>
                        <FormControl fullWidth>
                            <InputLabel shrink={Boolean(eventID)}>Event</InputLabel>
                            <Select
                                value={eventID}
                                label="Event"
                                onChange={handleEvent}
                                required
                            >
                                {eventList.map((event: any) => (
                                    <MenuItem key={event.eventId} value={event.eventId}>{event.eventName} ({event.eventId})</MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                    </Box>
                    <Typography sx={{ mb: 1, mt: 1 }}>{`Face Detected: ${detected}`}</Typography>
                    <Typography sx={{ mb: 1 }}>{`Number of faces detected: ${facesDetected}`}</Typography>
                </Grid>
                <Grid item xs={12} sm={4}>
                    <Button variant="contained" color={buttonColor} onClick={handleStartCheckin} sx={{ height: '55px' }}>
                        {startCheckin ? 'Stop Checkin' : 'Start Checkin'}
                    </Button>
                </Grid>
            </Grid>


            <Grid container spacing={1} sx={{ alignContent: "center", display: 'flex', justifyContent: 'center' }}>
                <Grid item xs={12} sm={5}>
                    <Box style={{ width, height, position: 'relative' }}>
                        {boundingBox.map((box: any, index: any) => (
                            <div
                                key={`${index + 1}`}
                                style={{
                                    border: '2px solid yellow',
                                    position: 'absolute',
                                    top: `${box.yCenter * 100}%`,
                                    left: `${box.xCenter * 100}%`,
                                    width: `${box.width * 100}%`,
                                    height: `${box.height * 100}%`,
                                    zIndex: 1,
                                }} />
                        ))}
                        {countdown > 0 ?
                            <div style={{
                                position: 'absolute',
                                top: '10px', // Adjust the top position as needed
                                left: '10px', // Adjust the left position as needed
                                fontSize: '48px', // Adjust the font size as needed
                                fontWeight: 'bold',
                                color: 'red',
                                zIndex: 1,
                            }}>
                                <p>{countdown}</p>
                            </div>
                            : null}
                        <Webcam
                            ref={webcamRef}
                            audio={false}
                            forceScreenshotSourceSize
                            screenshotFormat="image/png"
                            style={{
                                height,
                                width,
                            }}
                        />
                    </Box>
                </Grid>
                {image ? (
                    <img src={image} alt="webcam" width="700" height="700" />
                ) : null}
            </Grid>
            {/* success / error feedback */}
            <Snackbar open={openSnackbar} autoHideDuration={2000} onClose={handleSnackbarClose}>
                <Alert onClose={handleSnackbarClose} severity={alertType} sx={{ width: '100%' }}>
                    {alertMsg}
                </Alert>
            </Snackbar>
        </Box>
    );
};