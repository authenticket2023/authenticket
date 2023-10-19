import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { NavbarAdmin } from '../../Navbar';
import { Box } from '@mui/material';
import { AllArtist } from './allArtist';

export const ArtistAdmin = () => {
    useEffect(() => {
    }, []);
    const token = window.localStorage.getItem('accessToken');
    const role = window.localStorage.getItem('role');

    return (
        <div>
            {
                token != null && role == 'ADMIN' ?
                 <Navigate to="/ArtistAdmin" /> :  <Navigate to="/Forbidden" />
            }
            < NavbarAdmin />
            <Box sx={{margin: 5}}>
                <AllArtist />
            </Box>
        </div>

    )
}