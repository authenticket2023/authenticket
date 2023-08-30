import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { NavbarNotLoggedIn } from '../../Navbar';
import { styled, alpha } from '@mui/material/styles';
import Paper from '@mui/material/Paper';
import Typography from '@mui/material/Typography';
import Grid from '@mui/material/Grid';
import Box from '@mui/material/Box';
import SearchIcon from '@mui/icons-material/Search';
import InputBase from '@mui/material/InputBase';
import { grey } from '@mui/material/colors';
import Container from '@mui/material';

export const Home = () => {
    useEffect(() => {
    }, []);

    const Search = styled('div')(({ theme }) => ({
        position: 'relative',
        borderRadius: theme.shape.borderRadius,
        backgroundColor: alpha(theme.palette.common.white, 0.5),
        '&:hover': {
            backgroundColor: alpha(theme.palette.common.white, 0.6),
        },
        marginLeft: 0,
        width: '100%',
        [theme.breakpoints.up('sm')]: {
            marginLeft: theme.spacing(1),
            width: '100%',
        },
    }));

    const SearchIconWrapper = styled('div')(({ theme }) => ({
        padding: theme.spacing(0, 2),
        height: '100%',
        position: 'absolute',
        pointerEvents: 'none',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
    }));

    const StyledInputBase = styled(InputBase)(({ theme }) => ({
        color: 'black',
        '& .MuiInputBase-input': {
            padding: theme.spacing(1, 1, 1, 0),
            // vertical padding + font size from searchIcon
            paddingLeft: `calc(1em + ${theme.spacing(5)})`,
        },
    }));


    return (
        <>
            <div>
                < NavbarNotLoggedIn />
                <Paper>
                </Paper>
                <Paper elevation={2}
                    sx={{
                        position: 'relative',
                        backgroundColor: 'grey.800',
                        color: '#fff',
                        mb: 4,
                        backgroundSize: 'cover',
                        backgroundRepeat: 'no-repeat',
                        backgroundPosition: 'center',
                        backgroundImage: `url(https://i.imgur.com/UKi8jbp.png)`,
                    }}
                >
                    <Box
                        sx={{
                            position: 'relative',
                            top: 0,
                            bottom: 0,
                            right: 0,
                            left: 0,
                            backgroundColor: 'rgba(0,0,0,.3)',
                        }}
                    />
                    <Grid container spacing={4} alignItems="center" justifyContent="center">
                        <Grid item md={6}>
                            <Box
                                sx={{
                                    position: 'relative',
                                    p: { xs: 3, md: 6 },
                                    pr: { md: 0 },
                                }}
                            >
                                <br />
                                <br />
                                <Typography component="h1" variant="h3" color="inherit" gutterBottom align="center">
                                    Unlock Unforgettable Experiences
                                </Typography>
                                <Typography component="h1" variant="h6" color="inherit" gutterBottom align="center">
                                    your gateway to premier event adventures
                                </Typography>
                                <br />
                                <br />
                                <br />
                                <Search>
                                    <SearchIconWrapper>
                                        <SearchIcon sx={{color: "#3b3b3b"}} />
                                    </SearchIconWrapper>
                                    <StyledInputBase
                                        placeholder="Search…"
                                        inputProps={{ 'aria-label': 'search' }}
                                        fullWidth
                                    />
                                </Search>
                                <br />
                                <br />
                                <br />
                            </Box>
                        </Grid>
                    </Grid>
                </Paper>
            </div>
        </>
    )
}