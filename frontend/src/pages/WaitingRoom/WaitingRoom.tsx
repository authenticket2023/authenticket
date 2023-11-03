import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';

export const WaitingRoom = () => {
    let navigate = useNavigate();

    const token = window.localStorage.getItem('accessToken');
    //leave queue
    const leaveQueue = async (eventId: any) => {
        const formData = new FormData();
        formData.append('eventId', eventId);
        // //calling backend API
        fetch(`${process.env.REACT_APP_BACKEND_URL}/event/leave-queue`, {
            headers: {
                'Authorization': `Bearer ${token}`,
            },
            method: 'PUT',
            body: formData
        })
            .then(async (response) => {
                if (response.status == 200) {
                    const apiResponse = await response.json();
                    console.log(apiResponse.message);
                }
            })
            .catch((err) => {
                window.alert(err);
            });
    }
    useEffect(() => {
        if (token == null) {
            navigate(`/Forbidden`);
        }
    }, []);

    
    return (
        <div>

            <h1>
                waiting room Page under construction ...
            </h1>
        </div>

    )
}