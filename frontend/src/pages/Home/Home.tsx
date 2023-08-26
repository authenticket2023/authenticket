import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { NavbarNotLoggedIn } from '../../Navbar';

export const Home = () => {
    useEffect(() => {
    }, []);

    return (
        <div>
            < NavbarNotLoggedIn />
            <h1>
                Home Page under construction ...
            </h1>
        </div>

    )
}