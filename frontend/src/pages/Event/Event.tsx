import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { NavbarNotLoggedIn, NavbarLoggedIn } from '../../Navbar';

export const Event = () => {
    useEffect(() => {
    }, []);
    const token = window.localStorage.getItem('accessToken');
    
    return (
        <div>
           {token != null ? <NavbarLoggedIn /> : <NavbarNotLoggedIn />}
            <h1>
                Event Page under construction ...
            </h1>
        </div>

    )
}