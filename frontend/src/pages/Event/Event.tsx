import React, { useEffect, useRef, useState } from 'react';
import Tabs from '@mui/material/Tabs';
import Tab from '@mui/material/Tab';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';
import { NavbarNotLoggedIn, NavbarLoggedIn } from '../../Navbar';
import { Alert, Checkbox, Grid, IconButton, InputAdornment, Popover, Snackbar, TextField } from '@mui/material';
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
    "aria-controls": `simple-tabpanel-${index}`,
  };
}

export const Event = () => {
  const token = window.localStorage.getItem('accessToken');
  const [dataLoaded, setDataLoaded] = useState(false);
  //for alert
  //error , warning , info , success
  const [openSnackbar, setOpenSnackbar] = useState(false);
  const [alertType, setAlertType]: any = useState("info");
  const [alertMsg, setAlertMsg] = useState("");
  const handleSnackbarClose = () => {
    setOpenSnackbar(false);
  };

  //variables
  const [value, setValue] = useState(0);
  const [currEvents, setCurrEvents]: any = useState([]);
  const [pastEvents, setPastEvents]: any = useState([]);

  const loadCurrEvents = async () => {
    //call backend API
    fetch(
      `${process.env.REACT_APP_BACKEND_URL}/public/event/current?page=0&size=20`,
      {
        headers: {
          "Content-Type": "application/json",
        },
        method: "GET",
      }
    )
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
          setDataLoaded(true);
        } else {
          //display alert, for fetch fail
          setOpenSnackbar(true);
          setAlertType("error");
          setAlertMsg(
            `Oops something went wrong! Code:${response.status}; Status Text : ${response.statusText}`
          );
        }
      })
      .catch((err) => {
        setOpenSnackbar(true);
        setAlertType("error");
        setAlertMsg(`Oops something went wrong! Error : ${err}`);
      });
  };

  const loadPastEvents = async () => {
    //call backend API
    fetch(
      `${process.env.REACT_APP_BACKEND_URL}/public/event/past?page=0&size=20`,
      {
        headers: {
          "Content-Type": "application/json",
        },
        method: "GET",
      }
    )
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
          setDataLoaded(true);
        } else {
          //display alert, for fetch fail
          setOpenSnackbar(true);
          setAlertType("error");
          setAlertMsg(
            `Oops something went wrong! Code:${response.status}; Status Text : ${response.statusText}`
          );
        }
      })
      .catch((err) => {
        setOpenSnackbar(true);
        setAlertType("error");
        setAlertMsg(`Oops something went wrong! Error : ${err}`);
      });
  };

  const handleChange = (event: React.SyntheticEvent, newValue: number) => {
    setValue(newValue);
  };

  //current search 

  const [currentSearchInput, setCurrentSearchInput] = useState('');
  const handleCurrentSearch = (event: any) => {
    setCurrentSearchInput(event.target.value);
  };
  //current filter
  const [anchorElCurrent, setAnchorElCurrent] = useState(null);
  const handleCurrentFilterButtonClick = (event: any) => {
    setAnchorElCurrent(event.currentTarget);
  };

  //TODO: enhencement to get from DB
  const [checkedVenues, setCheckedVenues]: any = useState(['The Star Theatre', 'Capitol Theatre', 'Singapore National Stadium', 'Other']); // State to store checked venues
  const venues = ['The Star Theatre', 'Capitol Theatre', 'Singapore National Stadium', 'Other'];

  // Function to handle checkbox change
  const handleVenueCheckboxChange = (item: any) => {
    if (checkedVenues.includes(item)) {
      // If the item is already checked, uncheck it
      setCheckedVenues(checkedVenues.filter((checkedItem: any) => checkedItem !== item));
    } else {
      // If the item is not checked, check it
      setCheckedVenues([...checkedVenues, item]);
    }
  };

  const [checkedEventTypes, setCheckedEventTypes]: any = useState(['Musical', 'Concert', 'Sports', 'Others']); // State to store checked venues
  const eventTypes = ['Musical', 'Concert', 'Sports', 'Others'];
  // Function to handle checkbox change
  const handleEventTypeCheckboxChange = (item: any) => {
    if (checkedEventTypes.includes(item)) {
      // If the item is already checked, uncheck it
      setCheckedEventTypes(checkedEventTypes.filter((checkedItem: any) => checkedItem !== item));
    } else {
      // If the item is not checked, check it
      setCheckedEventTypes([...checkedEventTypes, item]);
    }

  };
  const filteredEvents = currEvents.filter((item: any) => {
    const nameMatch = item.eventName.toLowerCase().includes(currentSearchInput.toLowerCase());
    const venueMatch = checkedVenues.includes(item.eventVenue);
    const eventTypeMatch = checkedEventTypes.includes(item.eventType);
    return nameMatch && venueMatch && eventTypeMatch;
  });



  //past search
  const [pastSearchInput, setPastSearchInput] = useState('');
  const handlePastSearch = (event: any) => {
    setPastSearchInput(event.target.value);
  };

  //current filter
  const [anchorElPast, setAnchorElPast] = useState(null);
  const handlePastFilterButtonClick = (event: any) => {
    setAnchorElPast(event.currentTarget);
  };

  //TODO: enhencement to get from DB
  const [checkedVenuesPast, setCheckedVenuesPast]: any = useState(['The Star Theatre', 'Capitol Theatre', 'Singapore National Stadium', 'Other']); // State to store checked venues
  // const venues = ['The Star Theatre', 'Capitol Theatre', 'Singapore National Stadium', 'Other'];
  // Function to handle checkbox change
  const handleVenueCheckboxChangePast = (item: any) => {
    if (checkedVenuesPast.includes(item)) {
      // If the item is already checked, uncheck it
      setCheckedVenuesPast(checkedVenuesPast.filter((checkedItem: any) => checkedItem !== item));
    } else {
      // If the item is not checked, check it
      setCheckedVenuesPast([...checkedVenuesPast, item]);
    }
  };

  const [checkedEventTypesPast, setCheckedEventTypesPast]: any = useState(['Musical', 'Concert', 'Sports', 'Others']); // State to store checked venues
  // const eventTypes = ['Musical', 'Concert', 'Sports', 'Others'];
  // Function to handle checkbox change
  const handleEventTypeCheckboxChangePast = (item: any) => {
    if (checkedEventTypesPast.includes(item)) {
      // If the item is already checked, uncheck it
      setCheckedEventTypesPast(checkedEventTypesPast.filter((checkedItem: any) => checkedItem !== item));
    } else {
      // If the item is not checked, check it
      setCheckedEventTypesPast([...checkedEventTypesPast, item]);
    }

  };
  const filteredEventsPast = pastEvents.filter((item: any) => {
    const nameMatch = item.eventName.toLowerCase().includes(pastSearchInput.toLowerCase());
    const venueMatch = checkedVenuesPast.includes(item.eventVenue);
    const eventTypeMatch = checkedEventTypesPast.includes(item.eventType);
    return nameMatch && venueMatch && eventTypeMatch;
  });

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

  useEffect(() => {
    if (!dataLoaded) {
      loadCurrEvents();
      loadPastEvents();
    } else {
      //current event
      if (filteredEvents.length < 20) {
        setHasMoreCur(false);
      } else {
        setHasMoreCur(true);
      }
      //past event
      if (filteredEventsPast.length < 20) {
        setHasMorePast(false);
      } else {
        setHasMorePast(true);
      }
    }
  }, [currentSearchInput, checkedEventTypes, checkedVenues, pastSearchInput, checkedEventTypesPast, checkedVenuesPast]);

  return (
    <div >
      {token != null ? <NavbarLoggedIn /> : <NavbarNotLoggedIn />}
      {/* current events */}
      {value == 0 ?
        <Box sx={{ height: '850px', overflow: 'hidden', position: 'relative', display: 'flex', flexDirection: 'column' }}>
          <Grid container sx={{ borderBottom: 1, borderColor: '#CACACA', mt: 4, }}>
            <Grid item xs={12} sm={6}>
              <Tabs value={value} onChange={handleChange} textColor="inherit" TabIndicatorProps={{ style: { display: 'none' } }} sx={{ marginTop: -3, marginLeft: 19 }}>
                <Tab label={(<Typography variant='h3' sx={{ textTransform: 'none', font: 'Roboto', fontSize: '26px', fontWeight: 600 }} >Events</Typography>)} {...a11yProps(0)} />
                <Tab label={(<Typography variant='h3' sx={{ textTransform: 'none', font: 'Roboto', fontSize: '26px', fontWeight: 600 }} >Past Events</Typography>)} {...a11yProps(1)} />
              </Tabs>
            </Grid>
            {/* Search Bar */}

            <Grid item xs={12} sm={1} sx={{ mr: 7 }} />
            <Grid item xs={12} sm={3} sx={{ mb: 1 }}>
              <TextField
                id="current-search"
                size="small"
                label="Search"
                variant="outlined"
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
            </Grid>
            <Grid item xs={12} sm={1} sx={{ mb: 1 }}>
              <IconButton
                aria-label="filter"
                onClick={handleCurrentFilterButtonClick}
                sx={{
                  border: '1px solid #8E8E8E',
                  borderRadius: '5px',
                  marginLeft: 1,
                  height: 39.5,
                  width: 39.5,
                  ":hover": {
                    bgcolor: "#8E8E8E",
                    color: "white"
                  }
                }}
              >
                <TuneIcon />
              </IconButton>
              <Popover
                open={Boolean(anchorElCurrent)}
                anchorEl={anchorElCurrent}
                onClose={() => setAnchorElCurrent(null)}
                anchorOrigin={{
                  vertical: 'bottom',
                  horizontal: 'left',
                }}
              >
                <Box sx={{ minWidth: 150, mr: 1, ml: 1 }}>
                  <Typography
                    sx={{ borderBottom: '2px solid #000', display: 'inline-block', mb: 1, mt: 1 }}
                    variant="body1"
                    color="textSecondary"
                  >
                    Venue
                  </Typography>
                  {venues.map((item, index) => (
                    <Box key={index}>
                      <Checkbox
                        checked={checkedVenues.includes(item)}
                        onChange={() => handleVenueCheckboxChange(item)}
                        inputProps={{ 'aria-label': 'controlled' }}
                      />
                      {item}
                    </Box>
                  ))}
                  <Typography
                    sx={{ borderBottom: '2px solid #000', display: 'inline-block', mb: 1, mt: 1 }}
                    variant="body1"
                    color="textSecondary"
                  >
                    Type
                  </Typography>
                  {eventTypes.map((item, index) => (
                    <Box key={index}>
                      <Checkbox
                        checked={checkedEventTypes.includes(item)}
                        onChange={() => handleEventTypeCheckboxChange(item)}
                        inputProps={{ 'aria-label': 'controlled' }}
                      />
                      {item}
                    </Box>
                  ))}
                </Box>
              </Popover>
            </Grid>
          </Grid>

          <Box onScroll={handleScroll} sx={{ overflowY: 'auto', height: 'calc(100% + 100)', }}>
            <CustomTabPanel value={value} index={0} >
              <Grid container rowSpacing={2} columnSpacing={7} sx={{ mb: 10 }} alignItems="center" justifyContent="center">
                {currEvents.filter((item: any) => {
                  // Check if the event name contains the search input
                  const nameMatch = item.eventName.toLowerCase().includes(currentSearchInput.toLowerCase());
                  // Check if the venue is included in the checkedVenues array
                  const venueMatch = checkedVenues.includes(item.eventVenue);
                  // Check if the venue is included in the checkedVenues array
                  const eventTypeMatch = checkedEventTypes.includes(item.eventType);
                  // Return true if both conditions are met
                  return nameMatch && venueMatch && eventTypeMatch;
                })
                  .map((event: any, index: any) => (
                    <React.Fragment key={index}>
                      <Grid item xs={5}>
                        <DisplayEvent event={event} />
                      </Grid>
                    </React.Fragment>
                  ))}
                {/* Conditional rendering for "No Match Found" message */}
                {currEvents.length > 0 &&
                  currEvents.filter((item: any) => {
                    // Check if the event name contains the search input
                    const nameMatch = item.eventName.toLowerCase().includes(currentSearchInput.toLowerCase());
                    // Check if the venue is included in the checkedVenues array
                    const venueMatch = checkedVenues.includes(item.eventVenue);
                    // Check if the venue is included in the checkedVenues array
                    const eventTypeMatch = checkedEventTypes.includes(item.eventType);
                    // Return true if both conditions are met
                    return nameMatch && venueMatch && eventTypeMatch;
                  }).length === 0 && (
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
          <Grid container sx={{ borderBottom: 1, borderColor: '#CACACA', mt: 4, }}>

            <Grid item xs={12} sm={6}>
              <Tabs value={value} onChange={handleChange} textColor="inherit" TabIndicatorProps={{ style: { display: 'none' } }} sx={{ marginTop: -3, marginLeft: 19 }}>
                <Tab label={(<Typography variant='h3' sx={{ textTransform: 'none', font: 'Roboto', fontSize: '26px', fontWeight: 600 }} >Events</Typography>)} {...a11yProps(0)} />
                <Tab label={(<Typography variant='h3' sx={{ textTransform: 'none', font: 'Roboto', fontSize: '26px', fontWeight: 600 }} >Past Events</Typography>)} {...a11yProps(1)} />
              </Tabs>
            </Grid>
            {/* Search Bar */}

            <Grid item xs={12} sm={1} sx={{ mr: 7 }} />
            <Grid item xs={12} sm={3} sx={{ mb: 1 }}>
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
            </Grid>
            <Grid item xs={12} sm={1} sx={{ mb: 1 }}>
              <IconButton aria-label="filter"
                onClick={handlePastFilterButtonClick}
                sx={{
                  border: "1px solid #8E8E8E",
                  borderRadius: '5px',
                  marginLeft: 1,
                  height: 39.5,
                  width: 39.5,
                  ":hover": {
                    bgcolor: "#8E8E8E",
                    color: "white"
                  }
                }}>
                <TuneIcon />
              </IconButton>
              <Popover
                open={Boolean(anchorElPast)}
                anchorEl={anchorElPast}
                onClose={() => setAnchorElPast(null)}
                anchorOrigin={{
                  vertical: 'bottom',
                  horizontal: 'left',
                }}
              >
                <Box sx={{ minWidth: 150, mr: 1, ml: 1 }}>
                  <Typography sx={{ borderBottom: '2px solid #000', display: 'inline-block', mb: 1, mt: 1 }} variant="body1" color="textSecondary">Venue</Typography>
                  {venues.map((item, index) => (
                    <Box key={index}>
                      <Checkbox
                        checked={checkedVenuesPast.includes(item)}
                        onChange={() => handleVenueCheckboxChangePast(item)}
                        inputProps={{ 'aria-label': 'controlled' }}
                      />
                      {item}
                    </Box>
                  ))}
                  <Typography sx={{ borderBottom: '2px solid #000', display: 'inline-block', mb: 1, mt: 1 }} variant="body1" color="textSecondary">Type</Typography>
                  {eventTypes.map((item, index) => (
                    <Box key={index}>
                      <Checkbox
                        checked={checkedEventTypesPast.includes(item)}
                        onChange={() => handleEventTypeCheckboxChangePast(item)}
                        inputProps={{ 'aria-label': 'controlled' }}
                      />
                      {item}
                    </Box>
                  ))}

                </Box>
              </Popover>
            </Grid>
          </Grid>

          <Box onScroll={handleScroll} sx={{ overflowY: 'auto', height: 'calc(100% - 80px)', }}>
            <CustomTabPanel value={value} index={1}>
              <Grid container rowSpacing={2} columnSpacing={7} sx={{ mb: 10, }} alignItems="center" justifyContent="center">
                {pastEvents.filter((item: any) => {
                  // Check if the event name contains the search input
                  const nameMatch = item.eventName.toLowerCase().includes(pastSearchInput.toLowerCase());
                  // Check if the venue is included in the checkedVenues array
                  const venueMatch = checkedVenuesPast.includes(item.eventVenue);
                  // Check if the venue is included in the checkedVenues array
                  const eventTypeMatch = checkedEventTypesPast.includes(item.eventType);
                  // Return true if both conditions are met
                  return nameMatch && venueMatch && eventTypeMatch;
                })
                  .map((event: any, index: any) => (
                    <React.Fragment key={index}>
                      <Grid item xs={5}>
                        <DisplayEvent event={event} />
                      </Grid>
                    </React.Fragment>
                  ))}
                {/* Conditional rendering for "No Match Found" message */}
                {pastEvents.length > 0 &&
                  pastEvents.filter((item: any) => {
                    // Check if the event name contains the search input
                    const nameMatch = item.eventName.toLowerCase().includes(pastSearchInput.toLowerCase());
                    // Check if the venue is included in the checkedVenues array
                    const venueMatch = checkedVenuesPast.includes(item.eventVenue);
                    // Check if the venue is included in the checkedVenues array
                    const eventTypeMatch = checkedEventTypesPast.includes(item.eventType);
                    // Return true if both conditions are met
                    return nameMatch && venueMatch && eventTypeMatch;
                  }).length === 0 && (
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

      <Snackbar
        open={openSnackbar}
        autoHideDuration={4000}
        onClose={handleSnackbarClose}
      >
        <Alert
          onClose={handleSnackbarClose}
          severity={alertType}
          sx={{ width: "100%" }}
        >
          {alertMsg}
        </Alert>
      </Snackbar>
    </div>
  );
};
