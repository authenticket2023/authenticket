import React, { useEffect, useRef, useState } from 'react';
import Tabs from '@mui/material/Tabs';
import Tab from '@mui/material/Tab';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';
import { NavbarNotLoggedIn, NavbarLoggedIn } from '../../Navbar';
import { Alert, Grid, IconButton, InputAdornment, Snackbar, TextField } from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import DisplayEvent from './displayEvent';
import TuneIcon from '@mui/icons-material/Tune';
import CircularProgress from '@mui/material/CircularProgress';

interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

function CustomTabPanel(props: TabPanelProps) {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`simple-tabpanel-${index}`}
      aria-labelledby={`simple-tab-${index}`}
      {...other}
    >
      {value === index && (
        <Box sx={{ p: 3 }}>
          <Typography>{children}</Typography>
        </Box>
      )}
    </div>
  );
}

function a11yProps(index: number) {
  return {
    id: `simple-tab-${index}`,
    'aria-controls': `simple-tabpanel-${index}`,
  };
}

export const Event = () => {
  const token = window.localStorage.getItem('accessToken');
  useEffect(() => {
    loadCurrEvents();
    loadPastEvents();
  }, []);

  //for alert
  //error , warning , info , success
  const [openSnackbar, setOpenSnackbar] = useState(false);
  const [alertType, setAlertType]: any = useState('info');
  const [alertMsg, setAlertMsg] = useState('');
  const handleSnackbarClose = () => {
    setOpenSnackbar(false);
  };

  //variables
  const [value, setValue] = useState(0);
  const [currEvents, setCurrEvents]: any = useState([]);
  const [pastEvents, setPastEvents]: any = useState([]);

  const loadCurrEvents = async () => {
    //call backend API
    fetch(`${process.env.REACT_APP_BACKEND_URL}/public/event/current?page=0&size=20`, {
      headers: {
        'Content-Type': 'application/json',
      },
      method: 'GET',
    })
      .then(async (response) => {
        if (response.status == 200) {
          const apiResponse = await response.json();
          const data = apiResponse.data;
          if (data == null) {
            setHasMoreCur(false);
            return;
          }
          const currEventsArr = data.map((event: any) => ({
            eventId: event.eventId,
            eventName: event.eventName,
            eventDescription: event.eventDescription,
            eventImage: event.eventImage,
            eventType: event.eventType,
            eventDate: event.eventDate,
            totalTickets: event.totalTickets,
            eventVenue: event.eventVenue,
          }));
          //check if still have data
          if (currEventsArr.length < 20) {
            setHasMoreCur(false);
          }
          setCurrEvents(currEventsArr);
        } else {
          //display alert, for fetch fail
          setOpenSnackbar(true);
          setAlertType('error');
          setAlertMsg(`Oops something went wrong! Code:${response.status}; Status Text : ${response.statusText}`);
        }
      })
      .catch((err) => {
        setOpenSnackbar(true);
        setAlertType('error');
        setAlertMsg(`Oops something went wrong! Error : ${err}`);
      });
  }

  const loadPastEvents = async () => {
    //call backend API
    fetch(`${process.env.REACT_APP_BACKEND_URL}/public/event/past?page=0&size=20`, {
      headers: {
        'Content-Type': 'application/json',
      },
      method: 'GET',
    })
      .then(async (response) => {
        if (response.status == 200) {
          const apiResponse = await response.json();
          const data = apiResponse.data;
          if (data == null) {
            setHasMorePast(false);
            return;
          }
          const pastEvents = data.map((event: any) => ({
            eventId: event.eventId,
            eventName: event.eventName,
            eventDescription: event.eventDescription,
            eventImage: event.eventImage,
            eventType: event.eventType,
            eventDate: event.eventDate,
            totalTickets: event.totalTickets,
            eventVenue: event.eventVenue,
          }));
          //check if still have data
          if (pastEvents.length < 20) {
            setHasMorePast(false);
          }
          setPastEvents(pastEvents);
        } else {
          //display alert, for fetch fail
          setOpenSnackbar(true);
          setAlertType('error');
          setAlertMsg(`Oops something went wrong! Code:${response.status}; Status Text : ${response.statusText}`);
        }
      })
      .catch((err) => {
        setOpenSnackbar(true);
        setAlertType('error');
        setAlertMsg(`Oops something went wrong! Error : ${err}`);
      });
  }

  const handleChange = (event: React.SyntheticEvent, newValue: number) => {
    setValue(newValue);
  };

  //current search 
  const [currentSearchInput, setCurrentSearchInput] = useState('');
  const handleCurrentSearch = (event: any) => {
    setCurrentSearchInput(event.target.value);
    //reset the hasMore
    if (event.target.value == '') {
      setHasMoreCur(true);
      return;
    }
    //for the loading icon
    if (currEvents.filter((item: any) => item.eventName.toLowerCase().includes(event.target.value)).length < 20) {
      setHasMoreCur(false);
    } else {
      setHasMoreCur(true);
    }
  };
  //past search
  const [pastSearchInput, setPastSearchInput] = useState('');
  const handlePastSearch = (event: any) => {
    setPastSearchInput(event.target.value);

    if (event.target.value == '') {
      setHasMorePast(true);
      return;
    }
    if (pastEvents.filter((item: any) => item.eventName.toLowerCase().includes(event.target.value)).length < 20) {
      setHasMorePast(false);
    } else {
      setHasMorePast(true);
    }
  };

  //lazy load
  const [hasMoreCur, setHasMoreCur] = useState(true);
  const [hasMorePast, setHasMorePast] = useState(true);
  const [currEventPage, setCurrEventPage] = useState(1);
  const [pastEventPage, setPastEventPage] = useState(1);
  const loadMoreData = async (type: any, page: any) => {
    //call backend API
    fetch(`${process.env.REACT_APP_BACKEND_URL}/public/event/${type}?page=${page}&size=20`, {
      headers: {
        'Content-Type': 'application/json',
      },
      method: 'GET',
    })
      .then(async (response) => {
        if (response.status == 200) {
          const apiResponse = await response.json();
          const data = apiResponse.data;
          if (data == null) {
            if (type == 'current') {
              setHasMoreCur(false);
            } else {
              setHasMorePast(false);
            }
            return;
          }
          const EventsArr = data.map((event: any) => ({
            eventId: event.eventId,
            eventName: event.eventName,
            eventDescription: event.eventDescription,
            eventImage: event.eventImage,
            eventType: event.eventType,
            eventDate: event.eventDate,
            totalTickets: event.totalTickets,
            eventVenue: event.eventVenue,
          }));

          //set no more data based on the type : current/past
          if (EventsArr.length < 20) {
            if (type == 'current') {
              setHasMoreCur(false);
            } else if (type == 'past') {
              setHasMorePast(false);
            }
          }
          //append to the data
          if (type == 'current') {
            setCurrEvents((old: any) => [...old, ...EventsArr]);
            setCurrEventPage(currEventPage + 1);
          } else if (type == 'past') {
            setPastEvents((old: any) => [...old, ...EventsArr]);
            setPastEventPage(pastEventPage + 1);
          }
        } else {
          //display alert, for fetch fail
          setOpenSnackbar(true);
          setAlertType('error');
          setAlertMsg(`Oops something went wrong! Code:${response.status}; Status Text : ${response.statusText}`);
        }
      })
      .catch((err) => {
        setOpenSnackbar(true);
        setAlertType('error');
        setAlertMsg(`Oops something went wrong! Error : ${err}`);
      });
  }
  const handleScroll = (e: any) => {
    const { scrollHeight, scrollTop, clientHeight } = e.target;
    //use value to determine whether it is in Event or Past Event
    const bottom = scrollHeight - scrollTop <= clientHeight;

    if (!bottom) {
      return;
    }

    //for current event section
    if (value == 0 && hasMoreCur) {
      console.log('loading current event')
      loadMoreData('current', currEventPage);
    } else if (value == 0 && !hasMoreCur) {
      console.log('no more current event')
    }

    //for past event section
    if (value == 1 && hasMorePast) {
      console.log('loading past event')
      loadMoreData('past', pastEventPage);
    } else if (value == 1 && !hasMorePast) {
      console.log('no more past event')
    }

  }



  return (
    <div >
      {token != null ? <NavbarLoggedIn /> : <NavbarNotLoggedIn />}
      {/* current events */}
      {value == 0 ?
        <Box sx={{ height: '850px', overflow: 'hidden', position: 'relative', display: 'flex', flexDirection: 'column' }}>
          <Box sx={{ width: '100%', position: 'sticky', borderBottom: 1, mt: 5, borderColor: '#CACACA', }}>
            <Tabs value={value} onChange={handleChange} textColor="inherit" TabIndicatorProps={{ style: { display: 'none' } }} sx={{ marginTop: -3, marginLeft: 19 }}>
              <Tab label={(<Typography variant='h3' sx={{ textTransform: 'none', font: 'Roboto', fontSize: '26px', fontWeight: 600 }} >Events</Typography>)} {...a11yProps(0)} />
              <Tab label={(<Typography variant='h3' sx={{ textTransform: 'none', font: 'Roboto', fontSize: '26px', fontWeight: 600 }} >Past Events</Typography>)} {...a11yProps(1)} />
            </Tabs>
            {/* Search Bar */}
            <Box
              component="form"
              sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', paddingLeft: 15, paddingRight: 15, width: '50ch', marginTop: -6, marginBottom: 3, marginLeft: 120 }}
              noValidate
              autoComplete='off'
            >
              <TextField
                id="current search"
                size="small"
                label="Search"
                variant='outlined'
                fullWidth
                onChange={handleCurrentSearch}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <SearchIcon />
                    </InputAdornment>
                  ),
                }}
              />
              <IconButton aria-label="filter"
                // aria-describedby={id}
                // onClick={handleFilterClick} 
                sx={{
                  border: "1px solid #8E8E8E",
                  borderRadius: '5px',
                  marginLeft: 1,
                  height: 39.5,
                  width: 39.5,
                  //   backgroundColor: open ? "#30685e" : "white",
                  //   color: open ? "white" : "#30685e",
                  ":hover": {
                    bgcolor: "#8E8E8E",
                    color: "white"
                  }
                }}>
                <TuneIcon /></IconButton>
            </Box>
          </Box>
          <Box onScroll={handleScroll} sx={{ overflowY: 'auto', height: 'calc(100% + 100)', }}>
            <CustomTabPanel value={value} index={0} >
              <Grid container rowSpacing={2} columnSpacing={7} sx={{ mb: 10 }} alignItems="center" justifyContent="center">
                {currEvents.filter((item: any) => item.eventName.toLowerCase().includes(currentSearchInput)).map((event: any, index: any) => (
                  <React.Fragment key={index}>
                    <Grid item xs={5}>
                      <DisplayEvent event={event} />
                    </Grid>
                  </React.Fragment>
                ))}
                {/* Conditional rendering for "No Match Found" message */}
                {currEvents.length > 0 &&
                  currEvents.filter((item: any) => item.eventName.toLowerCase().includes(currentSearchInput)).length === 0 && (
                    <Grid item xs={12} sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                      <Typography variant="h4" color="textSecondary">
                        No Match Found
                      </Typography>
                    </Grid>
                  )}
              </Grid>
              {/* show if no past events */}
              {currEvents.length == 0 ?
                <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                  <Typography variant='h4'>Stay alert, more exciting events are on the horizon</Typography>
                </Box> :
                null
              }
              {/* show a loading indicator */}
              {hasMoreCur ?
                <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                  <CircularProgress />
                </Box> :
                null
              }
            </CustomTabPanel>
          </Box>
        </Box>
        : null
      }

      {/* Section 2: past events */}
      {value == 1 ?
        <Box sx={{ height: '850px', overflow: 'hidden', position: 'relative', display: 'flex', flexDirection: 'column' }}>
          <Box sx={{ width: '100%', position: 'sticky', borderBottom: 1, mt: 5, borderColor: '#CACACA', }}>
            <Tabs value={value} onChange={handleChange} textColor="inherit" TabIndicatorProps={{ style: { display: 'none' } }} sx={{ marginTop: -3, marginLeft: 19 }}>
              <Tab label={(<Typography variant='h3' sx={{ textTransform: 'none', font: 'Roboto', fontSize: '26px', fontWeight: 600 }} >Events</Typography>)} {...a11yProps(0)} />
              <Tab label={(<Typography variant='h3' sx={{ textTransform: 'none', font: 'Roboto', fontSize: '26px', fontWeight: 600 }} >Past Events</Typography>)} {...a11yProps(1)} />
            </Tabs>
            {/* Search Bar */}
            <Box
              component="form"
              sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', paddingLeft: 15, paddingRight: 15, width: '50ch', marginTop: -6, marginBottom: 3, marginLeft: 120 }}
              noValidate
              autoComplete='off'
            >
              <TextField
                id="past search"
                size="small"
                label="Search"
                variant='outlined'
                fullWidth
                onChange={handlePastSearch}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <SearchIcon />
                    </InputAdornment>
                  ),
                }}
              />
              <IconButton aria-label="filter"
                // aria-describedby={id}
                // onClick={handleFilterClick} 
                sx={{
                  border: "1px solid #8E8E8E",
                  borderRadius: '5px',
                  marginLeft: 1,
                  height: 39.5,
                  width: 39.5,
                  //   backgroundColor: open ? "#30685e" : "white",
                  //   color: open ? "white" : "#30685e",
                  ":hover": {
                    bgcolor: "#8E8E8E",
                    color: "white"
                  }
                }}>
                <TuneIcon /></IconButton>
            </Box>
          </Box>

          <Box onScroll={handleScroll} sx={{ overflowY: 'auto', height: 'calc(100% - 80px)', }}>
            <CustomTabPanel value={value} index={1}>
              <Grid container rowSpacing={2} columnSpacing={7} sx={{ mb: 10, }} alignItems="center" justifyContent="center">
                {pastEvents.filter((item: any) => item.eventName.toLowerCase().includes(pastSearchInput)).map((event: any, index: any) => (
                  <React.Fragment key={index}>
                    <Grid item xs={5}>
                      <DisplayEvent event={event} />
                    </Grid>
                  </React.Fragment>
                ))}
                {/* Conditional rendering for "No Match Found" message */}
                {pastEvents.length > 0 &&
                  pastEvents.filter((item: any) => item.eventName.toLowerCase().includes(pastSearchInput)).length === 0 && (
                    <Grid item xs={12} sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                      <Typography variant="h4" color="textSecondary">
                        No Match Found
                      </Typography>
                    </Grid>
                  )}
              </Grid>
              {/* show if no past events */}
              {pastEvents.length == 0 ?
                <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                  <Typography variant='h4'>There are no events from the past available for viewing.</Typography>
                </Box> :
                null
              }
              {/* show a loading indicator */}
              {hasMorePast ?
                <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                  <CircularProgress />
                </Box> :
                null
              }

            </CustomTabPanel>
          </Box>
        </Box>
        : null
      }

      <Snackbar open={openSnackbar} autoHideDuration={4000} onClose={handleSnackbarClose}>
        <Alert onClose={handleSnackbarClose} severity={alertType} sx={{ width: '100%' }}>
          {alertMsg}
        </Alert>
      </Snackbar>
    </div>

  )
}