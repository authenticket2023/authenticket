import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import Tabs from '@mui/material/Tabs';
import Tab from '@mui/material/Tab';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';
import { NavbarNotLoggedIn } from '../../Navbar';
import { InputAdornment, TextField } from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import Divider from '@mui/material/Divider';

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
    useEffect(() => {
    }, []);

    //variables
    const [value, setValue] = useState(0);

    const handleChange = (event: React.SyntheticEvent, newValue: number) => {
        setValue(newValue);
      };

    return (
        <div>
            < NavbarNotLoggedIn />
            
            <Box sx={{ width: '100%', position:'sticky' }}>
                <Box sx={{ borderBottom: 0 }}>
                    <Tabs value={value} onChange={handleChange} textColor="inherit" TabIndicatorProps={{ style: {display:'none'}}} sx={{marginTop:5, marginLeft:10}}>
                    <Tab label={(<Typography variant='h3' sx={{textTransform:'none', font:'Roboto', fontSize:'32px', fontWeight:600 }} >Events</Typography>)} {...a11yProps(0)} />
                    <Tab label={(<Typography variant='h3' sx={{textTransform:'none', font:'Roboto', fontSize:'32px', fontWeight:600 }} >Past Events</Typography>)} {...a11yProps(1)} />
                    </Tabs>

                    {/* Search Bar */}
                    <Box 
                        component="form" 
                        sx={{'& > :not(style)': {width:'25ch'}, marginLeft:140, marginBottom:3}} 
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
                    
                    <Divider variant="middle" />

                </Box>
                <CustomTabPanel value={value} index={0}>
                    Item One
                </CustomTabPanel>
                <CustomTabPanel value={value} index={1}>
                    Item Two
                </CustomTabPanel>
            </Box>

        </div>

    )
}