import { Grid, Typography } from '@mui/material';
import React, { useEffect, useState } from 'react';
import { NavbarLoggedIn } from '../../Navbar';
import { useParams } from 'react-router-dom';

export const CancelPage: React.FC = (): JSX.Element => {

  // Set parameters
  const { orderId } = useParams<{ orderId: string }>();

  const token = window.localStorage.getItem('accessToken');

  useEffect(() => {
    cancelOrder(orderId);
  }, []);

  //call backend to cancel order
  const cancelOrder = async (orderId: any) => {
        // Calling backend API
        fetch(`${process.env.REACT_APP_BACKEND_URL}/order/cancel/${orderId}`, {
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`,
          },
          method: 'PUT',
        })
          .then(async (response) => {
            if (response.status === 200) {
              console.log('Order completed');

            } else {
              // Handle error or pass to parent component
            }
          })
          .catch((err) => {
            window.alert(err);
          });
  };

  return (
    <div>
      <NavbarLoggedIn />
      <Grid>
        Cancelled
      </Grid>
    </div>
  )
}