import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import Tabs from '@mui/material/Tabs';
import Tab from '@mui/material/Tab';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';
import { NavbarNotLoggedIn, NavbarLoggedIn } from '../../Navbar';
import { Grid, InputAdornment, TextField } from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import Divider from '@mui/material/Divider';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import CardMedia from '@mui/material/CardMedia';
import { CardActionArea } from '@mui/material';
import DisplayEvent from './displayEvent';

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
  }, []);

  //variables
  const [value, setValue] = useState(0);
  const [currEvents, setCurrEvents] = useState([]);
  const [pastEvents, setPastEvents] = useState([]);

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
          const currEventsArr = data.map((event: any) => ({
            eventId: event.eventId,
            eventName: event.eventName,
            eventDescription: event.eventDescription,
            eventImage: event.eventImage,
            eventType: event.eventType,
            eventDate: event.eventDate,
            totalTickets: event.totalTickets,
            eventLocation: event.eventLocation,
          }));

          setCurrEvents(currEventsArr);
          currEventsArr.map((item: any) => console.log(item));
        } else {
          //passing to parent component
        }
      })
      .catch((err) => {
        window.alert(err);
      });
  }

  const handleChange = (event: React.SyntheticEvent, newValue: number) => {
    setValue(newValue);
  };

  return (
    <div>
      {token != null ? <NavbarLoggedIn /> : <NavbarNotLoggedIn />}
      <Box sx={{ height: '800px', overflow: 'hidden', position: 'relative' }}>

        {/* Section 1 */}
        <Box sx={{ width: '100%', position: 'sticky', mt: 5, borderBottom: 1 }}>
          <Tabs value={value} onChange={handleChange} textColor="inherit" TabIndicatorProps={{ style: { display: 'none' } }} sx={{ marginTop: 5, marginLeft: 15 }}>
            <Tab label={(<Typography variant='h3' sx={{ textTransform: 'none', font: 'Roboto', fontSize: '32px', fontWeight: 600 }} >Events</Typography>)} {...a11yProps(0)} />
            <Tab label={(<Typography variant='h3' sx={{ textTransform: 'none', font: 'Roboto', fontSize: '32px', fontWeight: 600 }} >Past Events</Typography>)} {...a11yProps(1)} />
          </Tabs>
          {/* Search Bar */}
          <Box
            component="form"
            sx={{ '& > :not(style)': { width: '25ch' }, marginLeft: 140, marginBottom: 3 }}
            noValidate
            autoComplete='off'
          >
            <TextField
              id="input-with-icon-textfield"
              size="small"
              label="Search"
              variant='outlined'
              fullWidth
              // onChange={handleSearch}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <SearchIcon />
                  </InputAdornment>
                )
              }}
            >

            </TextField>
          </Box>
        </Box>

        {/* Section 2: current events */}
        <Box sx={{ overflowY: 'auto', height: '100%', padding: '10px' }}>
          <CustomTabPanel value={value} index={0}>
            {/* <div style={{ overflowX: 'hidden', overflowY: 'scroll', maxHeight: '400px', marginTop: -140 }}> */}
            <Grid container spacing={2}>
              {currEvents.map((event: any, index) => (
                <Grid item key={index} xs={12} sm={6}>
                  <DisplayEvent event={event} />
                </Grid>
              ))}
            </Grid>
            {/* </div> */}
          </CustomTabPanel>
          <CustomTabPanel value={value} index={1}>
            Item Two
          </CustomTabPanel>

        </Box>

      </Box>
    </div>

  )
}