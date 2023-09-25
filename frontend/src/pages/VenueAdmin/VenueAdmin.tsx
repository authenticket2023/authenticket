import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { NavbarAdmin } from '../../Navbar';

export const VenueAdmin = () => {
    useEffect(() => {
    }, []);
    const token = window.localStorage.getItem('accessToken');
    const role = window.localStorage.getItem('role');

    return (
        <div>
            {
                token != null && role == 'ADMIN' ?
                 <Navigate to="/VenueAdmin" /> :  <Navigate to="/Forbidden" />
            }
            < NavbarAdmin />
            <h1>
                VenueAdmin Page under construction ...
            </h1>
        </div>

    )
}