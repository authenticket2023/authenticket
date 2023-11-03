import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { NavbarNotLoggedIn, NavbarLoggedIn } from '../../Navbar';
import { useParams } from 'react-router-dom';
import { Box } from '@mui/system';
import { Alert, Snackbar, Typography } from '@mui/material';
import { PurchaseSteps, PurchaseStepsFace } from './PurchaseSteps';
import { Button } from 'react-bootstrap';

export const TicketPurchase: React.FC = (): JSX.Element => {
  const today: any = new Date();

  let navigate = useNavigate();
  useEffect(() => {
    if (token == null) {
      navigate(`/Forbidden`);
    }
    loadEventDetails();
  }, []);

  //for pop up message => error , warning , info , success
  const [openSnackbar, setOpenSnackbar] = useState(false);
  const [alertType, setAlertType]: any = useState('info');
  const [alertMsg, setAlertMsg] = useState('');
  const handleSnackbarClose = () => {
    setOpenSnackbar(false);
  };

  const token = window.localStorage.getItem('accessToken');
  //check queue position if not 0 redirect to waiting room 
  const checkQueuePosition = async (eventId: any) => {
    // //calling backend API
    fetch(`${process.env.REACT_APP_BACKEND_URL}/event/queue-position?eventId=${eventId}`, {
      headers: {
        'Authorization': `Bearer ${token}`,
      },
      method: 'GET',
    })
      .then(async (response) => {
        if (response.status == 200) {
          const apiResponse = await response.json();
          if (apiResponse.data > 0) {
            navigate(`/WaitingRoom/${eventId}`);
          }
        }
      })
      .catch((err) => {
        window.alert(err);
      });
  }

  //enter queue
  const enterQueue = async (eventId: any) => {
    const formData = new FormData();
    formData.append('eventId', eventId);
    // //calling backend API
    fetch(`${process.env.REACT_APP_BACKEND_URL}/event/enter-queue`, {
      headers: {
        'Authorization': `Bearer ${token}`,
      },
      method: 'PUT',
      body: formData
    })
      .then(async (response) => {
        if (response.status == 201 || response.status == 200) {
          const apiResponse = await response.json();
          if (apiResponse.data > 0)
            navigate(`/WaitingRoom/${eventId}`);
        }
      })
      .catch((err) => {
        window.alert(err);
      });
  }

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
          const eventDate: any = new Date(data.eventDate);
          // for past event
          if (today > eventDate) {
            navigate(`/Forbidden`);
          }
          setEventDetails(data);
          const arr = data.ticketCategory
          const categoryArray = arr.sort((a: { categoryId: number; }, b: { categoryId: number; }) => a.categoryId - b.categoryId);
          setCategoryDetails(categoryArray);
          //call enter queue method and check queue

          enterQueue(eventId);
          checkQueuePosition(eventId);
          
        } else {

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
            <div style={{ marginTop: 25 }}>
              <div style={{ display: 'flex', flexDirection: 'row', marginBottom: 0 }}>
                <Typography sx={{ color: 'white', fontWeight: 500, fontFamily: 'Roboto', fontSize: '26px', marginLeft: 10 }}>
                  {eventDetails.eventName}
                </Typography>
                <Button variant="outlined" style={{ backgroundColor: 'black', borderColor: '#FF5C35', color: '#FF5C35', fontSize: '12px', marginLeft: 10, height: "28px", marginTop: 3 }}>
                  {eventDetails.type}
                </Button>
              </div>
              <Typography sx={{ color: 'white', fontWeight: 300, fontFamily: 'Roboto', fontSize: '14px', marginLeft: 10, marginTop: 0 }}>
                by {eventDetails.organiser.name}
              </Typography>
            </div>
          </div>
          <div style={{ marginTop: 30 }}>
            {eventDetails.isEnhanced ? (
              <PurchaseStepsFace
                eventDetails={eventDetails}
                categoryDetails={categoryDetails}
              />
            ) : (
              <PurchaseSteps
                eventDetails={eventDetails}
                categoryDetails={categoryDetails}
              />
            )}
          </div>
          {/* success / error feedback */}
          <Snackbar open={openSnackbar} autoHideDuration={2000} onClose={handleSnackbarClose}>
            <Alert onClose={handleSnackbarClose} severity={alertType} sx={{ width: '100%' }}>
              {alertMsg}
            </Alert>
          </Snackbar>
        </Box>
      )}

    </div>
  )
}