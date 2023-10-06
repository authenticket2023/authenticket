import React from 'react'
import { Navigate } from 'react-router-dom';
import { NavbarLoggedIn } from '../../Navbar';

export const SuccessPage = () => {
    const token = window.localStorage.getItem('accessToken');

    return (
        <div>
            <NavbarLoggedIn /> 
        </div>
    )
}