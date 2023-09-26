import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { NavbarOrganiser } from '../../Navbar';

export const HomeOrganiser = () => {
    const token = window.localStorage.getItem('accessToken');
    const role = window.localStorage.getItem('role');

    useEffect(() => {
    }, []);
    
    return (
        <div>
            {
                token != null && role == 'ORGANISER' ?
                <Navigate to="/HomeOrganiser" /> :  <Navigate to="/Forbidden" />
            }
            < NavbarOrganiser />
            <h1>
                HomeOrganiser Page under construction ...
            </h1>
        </div>

    )
}