import React, { useEffect, useState } from 'react';
import { Box } from '@mui/system';
import { Grid, Snackbar, Typography, Alert, Button } from '@mui/material';
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import { format } from 'date-fns';

function formatDate(dateString: string): string {
  const date = new Date(dateString);
  return format(date, "dd MMMM yyyy");
}

const formatTime = (timeString: string): string => {
  const date = new Date(timeString);
  let hours = date.getHours();
  let minutes = date.getMinutes();

  // Convert hours to 12-hour format
  const modifier = hours >= 12 ? 'pm' : 'am';
  hours = hours % 12 || 12;

  // Format minutes with leading zero if necessary
  const formattedMinutes = minutes.toString().padStart(2, '0');

  // Concatenate hours, minutes, and AM/PM modifier
  return `${hours}.${formattedMinutes}${modifier}`;
};


export const InfoBox = (props: any) => {

  const token = window.localStorage.getItem('accessToken');
  const userID: any = window.localStorage.getItem('id');
  const [preSaleStatus, setPreSaleStatus] = useState(false);
  const [isSelectedForPreSale, setIsSelectedForPreSale] = useState(false);
  const [availableTicket, setAvailableTicket] = useState(false);

  // Get today's date and time
  // Create a Date object for the eventDate
  const ticketSaleDateTime: any = new Date(props.ticketSaleDate);
  const today: any = new Date();
  // Calculate the difference in milliseconds between today and the eventDate
  const timeDifference = ticketSaleDateTime - today;

  // Calculate the number of milliseconds in one day
  const oneDayInMilliseconds = 24 * 60 * 60 * 1000;

  // Check if today is one day before the ticketSaleDateTime
  // False => means current date time is before D-1 day => display indicate interest button
  const isOneDayBeforeSaleDate = timeDifference <= oneDayInMilliseconds;
  // Check if today is after the ticketSaleDateTime
  const isTodayAfterSaleDate = today > ticketSaleDateTime;

  // need login
  const loadPreSaleStatus = async () => {
    if (token === null || userID === null) {
      return;
    }
    fetch(`${process.env.REACT_APP_BACKEND_URL}/event/checkPresaleStatus?eventId=${props.eventId}&userId=${userID}`, {
      headers: {
        'Authorization': `Bearer ${token}`,
      },
      method: 'GET',
    })
      .then(async (response) => {
        if (response.status == 200) {
          const apiResponse = await response.json();
          console.log(apiResponse)
          setPreSaleStatus(apiResponse.data);
        }
      })
      .catch((err) => {
        window.alert(err);
      });
  }

  // need login
  // Not done
  const loadIsSelectedForPreSale = async () => {
    if (token === null || userID === null) {
      return;
    }
    fetch(`${process.env.REACT_APP_BACKEND_URL}/event/checkPresaleStatus?eventId=${props.eventId}&userId=${userID}`, {
      headers: {
        'Authorization': `Bearer ${token}`,
      },
      method: 'GET',
    })
      .then(async (response) => {
        if (response.status == 200) {
          const apiResponse = await response.json();
          console.log(apiResponse)
          setIsSelectedForPreSale(apiResponse.data);
        }
      })
      .catch((err) => {
        window.alert(err);
      });
  }

  // dont need login
  // Not done
  const loadAvailableTicket= async () => {
    fetch(`${process.env.REACT_APP_BACKEND_URL}/event/checkPresaleStatus?eventId=${props.eventId}`, {
      headers: {
        'Authorization': `Bearer ${token}`,
      },
      method: 'GET',
    })
      .then(async (response) => {
        if (response.status == 200) {
          const apiResponse = await response.json();
          console.log(apiResponse)
          setAvailableTicket(apiResponse.data);
        }
      })
      .catch((err) => {
        window.alert(err);
      });
  }

  const handleBuyPresaleTicket = () => {
    console.log('handleIndicateInterest');
    //check if logged in
    if (token === null && userID === null) {
      setOpenSnackbar(true);
      setAlertType('warning');
      setAlertMsg('Please log in before indicate interest!');
      return;
    }
    //TODO
    console.log('redirect to purchasing pre sale ticket page');
  };

  const handleIndicateInterest = () => {
    if (token === null || userID === null) {
      setOpenSnackbar(true);
      setAlertType('warning');
      setAlertMsg('Please log in before indicate interest!');
      return;
    }
    const formData = new FormData();
    formData.append('eventId', props.eventId);
    formData.append('userId', userID);
    fetch(`${process.env.REACT_APP_BACKEND_URL}/event/indicateInterest`, {
      headers: {
        'Authorization': `Bearer ${token}`,
      },
      method: 'PUT',
      body: formData
    })
      .then(async (response) => {
        if (response.status == 200 || response.status == 201) {
          const apiResponse = await response.json();
          setPreSaleStatus(true);
          setOpenSnackbar(true);
          setAlertType('success');
          setAlertMsg(apiResponse.message);
        } else {
          const apiResponse = await response.json();
          setOpenSnackbar(true);
          setAlertType('warning');
          setAlertMsg(apiResponse.message);
        }
      })
      .catch((err) => {
        window.alert(err);
      });
  };

  const handleBuyTicket = () => {
    //TODO
    console.log('redirect to buy ticket page, need check whether logged in or not')
  };

  const handleViewVenue = () => {
    //TODO
    console.log('redirect to view venue page')
  };

  const handleSeatMap = () => {
    props.setValue(1);
  };

  //for pop up message => error , warning , info , success
  const [openSnackbar, setOpenSnackbar] = useState(false);
  const [alertType, setAlertType]: any = useState('info');
  const [alertMsg, setAlertMsg] = useState('');
  const handleSnackbarClose = () => {
    setOpenSnackbar(false);
  };

  useEffect(() => {
    if(token != null) {
      //load only if user logged in
      loadPreSaleStatus();
      loadIsSelectedForPreSale();
    } else {
      loadAvailableTicket();
    }
  }, []);

  return (
    <Grid container style={{ background: '#F0F0F0', height: '350px', width: '300px', borderRadius: '8px', display: 'flex', justifyContent: 'left', flexDirection: 'column' }}>
      <Grid style={{ display: 'flex', flexDirection: 'row', marginTop: 35, marginLeft: 40 }}>
        <CalendarMonthIcon style={{ fontSize: '30px' }} />
        <Typography style={{ marginTop: 4, marginLeft: 8 }}>
          {formatDate(props.eventDate)}
        </Typography>
      </Grid>
      <Grid style={{ display: 'flex', flexDirection: 'row', marginLeft: 40, marginTop: 10 }}>
        <AccessTimeIcon style={{ fontSize: '30px' }} />
        <Typography style={{ marginTop: 2.5, marginLeft: 8 }}>
          {formatTime(props.eventDate)}
        </Typography>
      </Grid>
      <Grid style={{ display: 'flex', flexDirection: 'row', marginLeft: 40, marginTop: 9 }}>
        <LocationOnIcon style={{ fontSize: '30px' }} />
        <Typography style={{ marginTop: 3.5, marginLeft: 8 }}>
          {props.venueName}
        </Typography>
      </Grid>
      <Grid style={{ display: 'flex', marginTop: 22, flexDirection: 'column', alignItems: 'center' }}>
        <Button
          variant="outlined"
          style={{
            backgroundColor: '#F0F0F0',
            color: '#FF5C35', // Text color
            border: '2px solid #FF5C35', // Add a border
            width: '250px'
          }}
          onClick={handleSeatMap}
        >
          View Seatmap
        </Button>
        <Button
          variant="outlined"
          style={{
            backgroundColor: '#F0F0F0',
            color: '#FF5C35', // Text color
            border: '2px solid #FF5C35', // Add a border
            width: '250px',
            marginTop: 8
          }}
          onClick={handleViewVenue}
        >
          View Venue
        </Button>
        {/* check which button to show */}
        {!isOneDayBeforeSaleDate && (
          <Button
            variant="contained"
            style={{
              backgroundColor: preSaleStatus ? 'green' : '#FF5C35',
              color: 'white', // Text color
              width: '250px',
              marginTop: 8
            }}
            onClick={handleIndicateInterest}
            disabled={preSaleStatus}
          >
            {preSaleStatus ? 'Interest Expressed' : 'Indicate Interest'}
          </Button>
        )}
        {(isOneDayBeforeSaleDate && !isTodayAfterSaleDate) && (
          <Button
            variant="contained"
            style={{
              backgroundColor: !isSelectedForPreSale ? 'grey' : '#FF5C35',
              color: 'white', // Text color
              width: '250px',
              marginTop: 8
            }}
            onClick={handleBuyPresaleTicket}
            disabled={!isSelectedForPreSale}
          >
            {userID == null ? 'Login to Check Access'
              :
              <Typography>
                {isSelectedForPreSale ? 'Buy Presale Ticket' : 'Stay tuned'}
              </Typography>
            }
          </Button>
        )}
        {(isOneDayBeforeSaleDate && isTodayAfterSaleDate) && (
          <Button
            variant="contained"
            style={{
              backgroundColor: !availableTicket ? 'grey' : '#FF5C35',
              color: 'white', // Text color
              width: '250px',
              marginTop: 8
            }}
            onClick={handleBuyTicket}
            disabled={!availableTicket}
          >
            {availableTicket ? 'Buy Tickets' : 'Sold out'}
          </Button>
        )}
      </Grid>
      {/* error feedback */}
      <Snackbar open={openSnackbar} autoHideDuration={3000} onClose={handleSnackbarClose}>
        <Alert onClose={handleSnackbarClose} severity={alertType} sx={{ width: '100%' }}>
          {alertMsg}
        </Alert>
      </Snackbar>
    </Grid>
  )
}

export const InfoBoxEnhanced = (props: any) => {

  return (
    <Box style={{ background: '#F0F0F0', height: '400px', width: '300px', borderRadius: '8px', display: 'flex', justifyContent: 'left', flexDirection: 'column' }}>
      <div style={{ display: 'flex', flexDirection: 'row', marginTop: 35, marginLeft: 40 }}>
        <CalendarMonthIcon style={{ fontSize: '30px' }} />
        <Typography style={{ marginTop: 4, marginLeft: 8 }}>
          {formatDate(props.eventDate)}
        </Typography>
      </div>
      <div style={{ display: 'flex', flexDirection: 'row', marginLeft: 40, marginTop: 11 }}>
        <AccessTimeIcon style={{ fontSize: '30px' }} />
        <Typography style={{ marginTop: 2.5, marginLeft: 8 }}>
          {formatTime(props.eventDate)}
        </Typography>
      </div>
      <div style={{ display: 'flex', flexDirection: 'row', marginLeft: 40, marginTop: 11 }}>
        <LocationOnIcon style={{ fontSize: '30px' }} />
        <Typography style={{ marginTop: 3.5, marginLeft: 8 }}>
          {props.venueName}
        </Typography>
      </div>
      <div style={{ display: 'flex', marginTop: 27, flexDirection: 'column', alignItems: 'center' }}>
        <Button
          variant="outlined"
          style={{
            backgroundColor: '#F0F0F0',
            color: '#FF5C35', // Text color
            border: '2px solid #FF5C35', // Add a border
            width: '250px'
          }}
        >
          View Seatmap
        </Button>
        <Button
          variant="outlined"
          style={{
            backgroundColor: '#F0F0F0',
            color: '#FF5C35', // Text color
            border: '2px solid #FF5C35', // Add a border
            width: '250px',
            marginTop: 8
          }}
        >
          View Venue
        </Button>
        <Button
          variant="contained"
          style={{
            backgroundColor: '#FF5C35',
            color: 'white', // Text color
            width: '250px',
            marginTop: 8
          }}
        >
          Indicate Interest
        </Button>
        <Button
          variant="contained"
          style={{
            backgroundColor: '#FF5C35',
            color: 'white', // Text color
            width: '250px',
            marginTop: 8
          }}
        >
          Buy Tickets
        </Button>
      </div>
    </Box>
  )
}