import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { NavbarNotLoggedIn } from '../../Navbar';

export const Event = () => {
    useEffect(() => {
    }, []);

    return (
        <div>
            < NavbarNotLoggedIn />
            <h1>
                Event Page under construction ...
            </h1>
        </div>

    )
}