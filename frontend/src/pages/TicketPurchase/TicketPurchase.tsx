import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { NavbarNotLoggedIn, NavbarLoggedIn } from '../../Navbar';
import { useParams } from 'react-router-dom';
import { Box } from '@mui/system';
import { Grid, List, ListItem, Typography } from '@mui/material';
import { Button } from 'react-bootstrap';
import { PurchaseSteps, PurchaseStepsFace } from './PurchaseSteps';

export const TicketPurcase: React.FC = (): JSX.Element => {
    useEffect(() => {
        loadEventDetails();
    }, []);

    const token = window.localStorage.getItem('accessToken');

    //set parameters
    const { eventId } = useParams<{ eventId: string }>();

    //set variables
    const [eventDetails, setEventDetails]: any = React.useState();
    const [categoryDetails, setCategoryDetails]: any = React.useState([]);

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
              // console.log(data);
              setEventDetails(data);
            //   const artistDetails = data.artists.map((artist: any) => ({
            //     artistId: artist.artistId,
            //     artistName: artist.artistName,
            //     artistImage: artist.artistImage
            //   }))

              const arr = data.ticketCategory
              const categoryArray = arr.sort((a: { categoryId: number; }, b: { categoryId: number; }) => a.categoryId - b.categoryId);
              setCategoryDetails(categoryArray);
              {categoryDetails.map((cat: any) => {
                console.log(cat);
              })}
            } else {
              //passing to parent component
            }
          })
          .catch((err) => {
            window.alert(err);
          });
      }

    return (
        // have to change this, by right user MUST be logged in in order to purchase tickets
        <div>
            {token != null ? <NavbarLoggedIn /> : <NavbarNotLoggedIn />}

            {/* header */}
            {eventDetails && (
                <Box>
                    <div style={{ background: 'black', height: '110px', display: 'flex', flexDirection: 'column', }}>
                        <div style={{marginTop:25}}>
                            <div style={{display:'flex', flexDirection:'row', marginBottom:0}}>
                                <Typography sx={{ color: 'white', fontWeight: 500, fontFamily: 'Roboto', fontSize: '26px', marginLeft: 10 }}>
                                    {eventDetails.eventName}
                                </Typography>
                                <Button variant="outlined" style={{ backgroundColor: 'black', borderColor: '#FF5C35', color: '#FF5C35', fontSize:'12px', marginLeft:10, height:"28px", marginTop:3}}>
                                    {eventDetails.type}
                                </Button>
                            </div>
                            <Typography sx={{ color: 'white', fontWeight: 300, fontFamily: 'Roboto', fontSize: '14px', marginLeft: 10, marginTop: 0 }}>
                                by {eventDetails.organiser.name}
                            </Typography>
                        </div>
                    </div>
                    <div style={{marginTop:30}}>                   
                        <PurchaseStepsFace
                            eventDetails={eventDetails}
                            categoryDetails={categoryDetails}
                        />
                    </div>
                </Box>
            )}
        </div>
    )
}