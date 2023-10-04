import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { NavbarLoggedIn, NavbarNotLoggedIn } from '../../Navbar';
import { SGStad } from '../../utility/seatMap/SeatMap'


export const Venue = () => {
    useEffect(() => {
    }, []);
    const token = window.localStorage.getItem('accessToken');

    return (
        <div>
            {token != null ? <NavbarLoggedIn /> : <NavbarNotLoggedIn />}
            <h1>
                Venue Page under construction ...
            </h1>
                <SGStad />
        </div>

    )
}