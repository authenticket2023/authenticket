import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { NavbarNotLoggedIn, NavbarOrganiser, NavbarAdmin } from '../../Navbar';

export const Home = () => {
    useEffect(() => {
    }, []);

    return (
        <div>
            < NavbarNotLoggedIn />
            < NavbarOrganiser />
            < NavbarAdmin />
            <h1>
                Home Page under construction ...
            </h1>
        </div>

    )
}