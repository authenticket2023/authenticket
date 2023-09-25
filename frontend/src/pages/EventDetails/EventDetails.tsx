import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { NavbarNotLoggedIn, NavbarLoggedIn } from '../../Navbar';
import { useParams } from 'react-router-dom';
import { Box } from '@mui/system';
import { Typography } from '@mui/material';


export const EventDetails: React.FC = (): JSX.Element => {
    useEffect(() => {
        loadEventDetails();
    }, []);

    const token = window.localStorage.getItem('accessToken');
    const { eventId } = useParams<{ eventId: string }>();

    const [eventDetails, setEventDetails]: any = React.useState();

    const loadEventDetails = async () => {
        // //calling backend API
        fetch(`${process.env.REACT_APP_BACKEND_URL}/public/event/${eventId}`, {
          headers: {
            'Content-Type': 'application/json',
          },
          method: 'GET',
        })
          .then(async (response) => {
            if (response.status == 200) {
              const apiResponse = await response.json();
              const data = apiResponse.data;
              setEventDetails(data);
              console.log(data);
            } else {
              //passing to parent component
            }
          })
          .catch((err) => {
            window.alert(err);
          });
      }
    
    return (
    <div>
      {token != null ? <NavbarLoggedIn /> : <NavbarNotLoggedIn />}
      
      <Box>
            <div style={{ background: 'black', height: '300px', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center' }}>
                {eventDetails && (
                <div style={{ display: 'flex', justifyContent: 'center', marginBottom: '15px' }}>
                    <img
                    src={`https://authenticket.s3.ap-southeast-1.amazonaws.com/event_images/${eventDetails.eventImage}`}
                    style={{
                        maxHeight: '200px',
                    }}
                    alt="Event Image"
                    />
                </div>
                )}
                <Typography sx={{ color: 'white', fontWeight: 500, fontFamily: 'Roboto', fontSize: '28px', textAlign: 'left' }}>
                {eventDetails && eventDetails.eventName}
                </Typography>
            </div>
        </Box>
 
      {/* <p>Event ID: {eventId}</p> */}
    </div>

    )
}