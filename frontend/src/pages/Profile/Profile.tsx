import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { NavbarNotLoggedIn } from '../../Navbar';
import { Alert, Avatar, Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, Grid, IconButton, Snackbar, TextField, Typography } from '@mui/material';

export const Profile = () => {
    useEffect(() => {
    }, []);

    return (
        <div>
            < NavbarNotLoggedIn />
            <h1>
                Support Page under construction ...
            </h1>
        </div>
    )
}