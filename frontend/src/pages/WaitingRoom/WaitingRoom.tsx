import { Box, Button, CircularProgress, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, Grid, Typography } from '@mui/material';
import React, { useEffect, useState } from 'react';
import { Navigate, useParams } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';
import backgroundImg from './backgroundImage.png';
import { styled } from '@mui/material/styles';
import LinearProgress, { linearProgressClasses } from '@mui/material/LinearProgress';
import IconButton from '@mui/material/IconButton';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';


const centerContainerStyle = {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    height: '100vh',
};

const BorderLinearProgress = styled(LinearProgress)(({ theme }) => ({
    height: 10,
    borderRadius: 5,
    [`&.${linearProgressClasses.colorPrimary}`]: {
        backgroundColor: theme.palette.grey[theme.palette.mode === 'light' ? 200 : 800],
    },
    [`& .${linearProgressClasses.bar}`]: {
        borderRadius: 5,
        backgroundColor: theme.palette.mode === 'light' ? '#1a90ff' : '#308fe8',
    },
}));

export const WaitingRoom = () => {
    let navigate = useNavigate();

    const token = window.localStorage.getItem('accessToken');
    //get eventId from the URL
    const { eventId }: any = useParams();
    const [queuePosition, setQueuePosition]: any = useState(-1);
    const [totalQueue, setTotalQueue]: any = useState(-1);
    const [firstTime, setFirstTime]: any = useState(false);
    const [showConfirmDialog, setShowConfirmDialog] = useState(false);

    const handleConfirmLeaveQueue = () => {
        leaveQueue();
        setShowConfirmDialog(false);
        navigate(`/EventDetails/${eventId}`);
    };

    const handleCancelLeaveQueue = () => {
        setShowConfirmDialog(false);
    };

    //get queue position
    const checkQueuePosition = async () => {
        setFirstTime(true);
        // //calling backend API
        fetch(`${process.env.REACT_APP_BACKEND_URL}/event/queue-position?eventId=${eventId}`, {
            headers: {
                'Authorization': `Bearer ${token}`,
            },
            method: 'GET'
        })
            .then(async (response) => {
                if (response.status == 200) {
                    const apiResponse = await response.json();
                    // if the position is 0 or less than 0, mean it is current user's turn, redirect to ticket purchase page
                    if (apiResponse.data == 0) {
                        navigate(`/TicketPurchase/${eventId}`);
                    } else if (apiResponse.data == -1) {
                        navigate(`/EventDetails/${eventId}`);
                    }
                    setQueuePosition(apiResponse.data);
                }
            })
            .catch((err) => {
                window.alert(err);
            });
    }

    //get queue position
    const checkTotalQueue = async () => {
        // //calling backend API
        fetch(`${process.env.REACT_APP_BACKEND_URL}/event/queue-total?eventId=${eventId}`, {
            headers: {
                'Authorization': `Bearer ${token}`,
            },
            method: 'GET'
        })
            .then(async (response) => {
                if (response.status == 200) {
                    const apiResponse = await response.json();
                    setTotalQueue(apiResponse.data);
                }
            })
            .catch((err) => {
                window.alert(err);
            });
    }

    //leave queue
    const leaveQueue = async () => {
        const formData = new FormData();
        formData.append('eventId', eventId);
        // //calling backend API
        fetch(`${process.env.REACT_APP_BACKEND_URL}/event/leave-queue`, {
            headers: {
                'Authorization': `Bearer ${token}`,
            },
            method: 'PUT',
            body: formData
        })
            .catch((err) => {
                window.alert(err);
            });
    }


    const handleGoBack = () => {
        setShowConfirmDialog(true);
    };

    const CHECK_INTERVAL = 10000;
    useEffect(() => {
        if (token == null) {
            navigate(`/Forbidden`);
        }
        //only run this once
        if (!firstTime) {
            //to load the queue position for the current user and total queue number
            checkQueuePosition();
            checkTotalQueue();
        }

        // Set up an interval to periodically call checkQueuePosition every 10 seconds
        const queuePositionIntervalId = setInterval(checkQueuePosition, CHECK_INTERVAL);
        const totalQueueIntervalId = setInterval(checkTotalQueue, CHECK_INTERVAL);
        // Clean up the interval when the component unmounts
        return () => {
            clearInterval(queuePositionIntervalId);
            clearInterval(totalQueueIntervalId);
        };

    }, [eventId]);


    return (
        <Box
            sx={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                justifyContent: 'center',
                height: '100vh',
                backgroundImage: `url(${backgroundImg})`,
                backgroundSize: 'cover',
                backgroundPosition: 'center',

            }}
        >

            <Grid
                sx={{
                    position: 'relative',
                    mt: '25%',
                    mb: '25%',
                    ml: '35%',
                    mr: '35%',
                    minHeight: '700px',
                    minWidth: '400px',
                    maxHeight: '800px',
                    maxWidth: '600px',
                    backgroundColor: 'rgba(255, 255, 255, 0.7)',
                }}
                container spacing={1}
            >
                <Grid item xs={12} sm={12}>

                    <IconButton color="primary" onClick={handleGoBack} >
                        <ArrowBackIcon sx={{ fontSize: 45 }} />
                    </IconButton>
                </Grid>
                <Grid item xs={12} sm={12}>
                    <Typography variant='h3' sx={{ textAlign: 'center', mb: 5 }}>
                        AuthenTicket
                    </Typography>
                    <Typography variant='h5' sx={{ textAlign: 'center', mb: 5 }}>
                        You are now in line
                    </Typography>
                    <Typography variant='body1' sx={{ textAlign: 'center' }}>
                        You are now in line. When it is your turn, you will be redirected and have 10 minutes to purchase the ticket.
                    </Typography>
                    <Grid item xs={12} sm={12} sx={{
                        mt: '15%',
                        ml: '5%',
                        mr: '5%',
                    }}>
                        <BorderLinearProgress variant="determinate" value={((totalQueue - queuePosition) / totalQueue) * 100 + 1} />
                    </Grid>
                    <Grid item xs={12} sm={12} sx={{
                        display: 'relative',
                        justifyContent: 'center',
                        alignItems: 'center',
                        ml: '5%',
                        mr: '5%',
                        height: '150px',
                        backgroundColor: 'gray',
                    }}>
                        <Typography variant='body1' sx={{ textAlign: 'left', mt:10}}>
                            Your number in line : <span style={{ color: 'green', fontWeight: 'bold' }}>{queuePosition}</span>
                        </Typography>
                        <Typography variant='body1' sx={{ textAlign: 'left', mt:2}}>
                            Number of people in the queue : <span style={{ color: 'green', fontWeight: 'bold' }}>{totalQueue}</span>
                        </Typography>
                        <Typography variant='body1' sx={{ textAlign: 'left', mt: 2 }}>
                            Your estimated wait time is : <span style={{ color: 'green', fontWeight: 'bold' }}>{queuePosition  * 5} minutes </span>
                        </Typography>
                    </Grid>


                </Grid>

            </Grid>
            <Dialog open={showConfirmDialog} onClose={handleCancelLeaveQueue}>
                <DialogTitle>Confirm Leaving Queue</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Are you sure you want to leave the queue?
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCancelLeaveQueue} color="error">
                        Cancel
                    </Button>
                    <Button onClick={handleConfirmLeaveQueue} color="primary">
                        Confirm
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>

    )
}