import React, { useEffect, useState } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import { NavbarOrganiser } from '../../Navbar';
import { AllEvent } from './allEvent'
import { Box } from '@mui/material';

export const HomeOrganiser = () => {
    const token = window.localStorage.getItem('accessToken');
    const role = window.localStorage.getItem('role');
    const [refresh, setRefresh]: any = useState(true);

    useEffect(() => {
    }, []);

    return (
        <div>
            {
                token != null && role == 'ORGANISER' ?
                    <Navigate to="/HomeOrganiser" /> : <Navigate to="/Forbidden" />
            }
            < NavbarOrganiser />
            <Box sx={{margin: 5}}>
                <AllEvent />
            </Box>
        </div>

    )
}