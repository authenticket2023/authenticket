import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { NavbarNotLoggedIn } from '../../Navbar';


export const About = () => {
    useEffect(() => {
    }, []);

    return (
    <div>
        < NavbarNotLoggedIn />
        <h1>
            About Page under construction ...
        </h1>
    </div>
    )
}