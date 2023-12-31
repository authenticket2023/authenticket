import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
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
  let navigate = useNavigate();
  const token = window.localStorage.getItem('accessToken');
  const userID: any = window.localStorage.getItem('id');
  const [isPresaleEvent, setIsPresaleEvent] = useState(true);
  const [preSaleStatus, setPreSaleStatus] = useState(true);
  const [isSelectedForPreSale, setIsSelectedForPreSale] = useState(true);
  const [availableTicket, setAvailableTicket] = useState(true);
  const [pastEvent, setPastEvent] = useState(false);


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

  // dont need logged in
  //check if the current event was presale event, 
  // => True , means will allow user to indicate interest etc
  // => False, only allow buy ticket after sale date start
  const loadIsPresaleEvent = async () => {
    if (token === null || userID === null) {
      setIsSelectedForPreSale(false);
      setPreSaleStatus(false);
    }
    fetch(`${process.env.REACT_APP_BACKEND_URL}/event/presale-event?eventId=${props.eventId}`, {
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
      method: 'GET',
    })
      .then(async (response) => {
        if (response.status == 200) {
          const apiResponse = await response.json();
          setIsPresaleEvent(apiResponse.data);
        }
      })
      .catch((err) => {
        window.alert(err);
      });
  }

  // if user logged in
  // check if current user had sign up for presale
  const loadPreSaleStatus = async () => {
    if (token === null || userID === null) {
      return;
    }
    fetch(`${process.env.REACT_APP_BACKEND_URL}/event/presale-status?eventId=${props.eventId}&userId=${userID}`, {
      headers: {
        'Authorization': `Bearer ${token}`,
      },
      method: 'GET',
    })
      .then(async (response) => {
        if (response.status == 200) {
          const apiResponse = await response.json();
          setPreSaleStatus(apiResponse.data);
        }
      })
      .catch((err) => {
        window.alert(err);
      });
  }

  // if user logged in
  //check if the current user is selcted for the presale 
  const loadIsSelectedForPreSale = async () => {
    if (token === null || userID === null) {
      return;
    }
    fetch(`${process.env.REACT_APP_BACKEND_URL}/event/user-selected?eventId=${props.eventId}&userId=${userID}`, {
      headers: {
        'Authorization': `Bearer ${token}`,
      },
      method: 'GET',
    })
      .then(async (response) => {
        if (response.status == 200) {
          const apiResponse = await response.json();
          setIsSelectedForPreSale(apiResponse.data);
        }
      })
      .catch((err) => {
        window.alert(err);
      });
  }

  // dont need logged in
  //check if the current event still have tickets
  const loadAvailableTicket = async () => {
    fetch(`${process.env.REACT_APP_BACKEND_URL}/event/available?eventId=${props.eventId}`, {
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
      method: 'GET',
    })
      .then(async (response) => {
        if (response.status == 200) {
          const apiResponse = await response.json();
          setAvailableTicket(apiResponse.data);
        }
      })
      .catch((err) => {
        window.alert(err);
      });
  }

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
    fetch(`${process.env.REACT_APP_BACKEND_URL}/event/interest`, {
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

  //*** TODO ***
  const handleBuyPresaleTicket = () => {
    //check if logged in
    if (token === null && userID === null) {
      setOpenSnackbar(true);
      setAlertType('warning');
      setAlertMsg('Please log in before indicate interest!');
      return;
    }
    //TODO
    console.log('redirect to purchasing pre sale ticket page');
    navigate('');
  };


  const handleBuyTicket = () => {
    if (token != null) {
      navigate(`/TicketPurchase/${props.eventId}`);
    } else {
      setOpenSnackbar(true);
      setAlertType('warning');
      setAlertMsg('Please log in before purchasing!');
      return;
    }
  };

  const handleViewVenue = () => {
    navigate(`/VenueDetails/${props.venueId}`);
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
    const eventDate: any = new Date(props.eventDate);
    if (today > eventDate) {
      setPastEvent(true);
    }
    //check if the event is presale event
    loadIsPresaleEvent();

    // Means is after sale date
    if (isTodayAfterSaleDate) {
      loadAvailableTicket();
    }

    //only need load infomation if the event is presale event
    if (token != null && isPresaleEvent) {
      //if isOneDayBeforeSaleDate == false, means current date is before D-1 day, show presale button
      if (!isOneDayBeforeSaleDate) {
        loadPreSaleStatus();
      }
      //this means the current date is btw D-1 to D day
      if (isOneDayBeforeSaleDate && !isTodayAfterSaleDate) {
        loadIsSelectedForPreSale();
      }
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
            color: '#FF5C35',
            border: '2px solid #FF5C35',
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
            color: '#FF5C35',
            border: '2px solid #FF5C35',
            width: '250px',
            marginTop: 8
          }}
          onClick={handleViewVenue}
        >
          View Venue
        </Button>
        {/* check which button to show for presale event only */}
        {(isPresaleEvent && !isOneDayBeforeSaleDate) && (
          <Button
            variant="contained"
            style={{
              backgroundColor: preSaleStatus ? 'green' : '#FF5C35',
              color: 'white',
              width: '250px',
              marginTop: 8
            }}
            onClick={handleIndicateInterest}
            disabled={preSaleStatus}
          >
            {preSaleStatus ? 'Interest Expressed' : 'Indicate Interest'}
          </Button>
        )}
        {(isPresaleEvent && isOneDayBeforeSaleDate && !isTodayAfterSaleDate) && (
          <Button
            variant="contained"
            style={{
              backgroundColor: !isSelectedForPreSale ? 'grey' : '#FF5C35',
              color: 'white',
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
        {(isPresaleEvent && isOneDayBeforeSaleDate && isTodayAfterSaleDate && !pastEvent) && (
          <Button
            variant="contained"
            style={{
              backgroundColor: !availableTicket ? 'grey' : '#FF5C35',
              color: 'white',
              width: '250px',
              marginTop: 8
            }}
            onClick={handleBuyTicket}
            disabled={!availableTicket}
          >
            {availableTicket ? 'Buy Tickets' : 'Sold out'}
          </Button>
        )}

        {/* show button for not presale event*/}
        {(!isPresaleEvent && !isTodayAfterSaleDate) && (
          <Button
            variant="contained"
            style={{
              backgroundColor: 'grey',
              color: 'white',
              width: '250px',
              marginTop: 8
            }}
            disabled={true}
          >
            Stay tuned
          </Button>
        )}
        {(!isPresaleEvent && isTodayAfterSaleDate && !pastEvent) && (
          <Button
            variant="contained"
            style={{
              backgroundColor: !availableTicket ? 'grey' : '#FF5C35',
              color: 'white',
              width: '250px',
              marginTop: 8
            }}
            onClick={handleBuyTicket}
            disabled={!availableTicket}
          >
            {availableTicket ? 'Buy Tickets' : 'Sold out'}
          </Button>
        )}

        {/* for past event*/}
        {(pastEvent) && (
          <Button
            variant="contained"
            style={{
              backgroundColor: 'grey',
              color: 'white',
              width: '250px',
              marginTop: 8
            }}
            disabled={true}
          >
            Event is over
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