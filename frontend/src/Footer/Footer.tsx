import * as React from "react";
import { Box, Typography, Grid } from '@mui/material';
import { Link, useLocation, useNavigate } from "react-router-dom";


export const Footer = (props: any) => {

    let navigate = useNavigate();

    const handleHome = () => {
        navigate('/');
    }

    const handleEvents = () => {
        navigate('/Event');
    }

    const handleVenues = () => {
        navigate('/Venue');
    }

    const handleFAQ = () => {
        navigate('/FAQ');
    }

    return (
            <Box height={300} bgcolor={'#EEEEEE'} padding={6} paddingLeft={15}>
                <Box borderBottom={1} height={200} borderColor={'#888888'}>
                <Grid container>
                    <Grid item xs = {2}>
                        <Typography color={'#888888'} sx={{fontWeight: 'bold'}}>
                            Contact us
                        </Typography>
                        <Box padding = {1}>
                            <Typography fontSize={14} color={'#888888'}>
                                Email: authenticket@gmail.com
                            </Typography>
                            <Typography fontSize={14} marginTop={1} color={'#888888'}>
                                Phone: +65 9619 5770
                            </Typography>
                        </Box>
                    </Grid>
                    <Grid item xs = {2}>
                        <Typography color={'#888888'} sx={{fontWeight: 'bold'}}>
                            Navigation
                        </Typography>
                        <Box padding = {1}>
                            <Typography fontSize={14} color={'#888888'} onClick={handleHome} sx={{cursor: 'pointer'}}>
                                Home
                            </Typography>
                            <Typography fontSize={14} marginTop={1} color={'#888888'} onClick={handleEvents} sx={{cursor: 'pointer'}}>
                                Events
                            </Typography>
                            <Typography fontSize={14} marginTop={1} color={'#888888'} onClick={handleVenues} sx={{cursor: 'pointer'}}>
                                Venues
                            </Typography>
                        </Box>
                    </Grid>
                    <Grid item xs = {2}>
                        <Typography color={'#888888'} sx={{fontWeight: 'bold'}}>
                            Support
                        </Typography>
                        <Box padding = {1}>
                            <Typography fontSize={14} color={'#888888'} onClick={handleFAQ}  sx={{cursor: 'pointer'}}>
                                FAQ
                            </Typography>
                        </Box>
                    </Grid>
                </Grid>
                </Box>
                <Typography fontSize={12} marginTop={2} color={'#888888'}>
                    © Copyright Authenticket 2023
                </Typography>
            </Box>
    )
}