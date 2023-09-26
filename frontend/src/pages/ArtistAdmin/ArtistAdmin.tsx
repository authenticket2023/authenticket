import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { NavbarAdmin } from '../../Navbar';

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
            <h1>
                ArtistAdmin Page under construction ...
            </h1>
        </div>

    )
}