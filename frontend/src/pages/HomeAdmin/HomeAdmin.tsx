import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { NavbarAdmin } from '../../Navbar';
import { Box, Tab, Tabs } from '@mui/material';
import { TabContext, TabPanel } from '@mui/lab';
import { PendingTab, AllTab } from './tabs';

export const HomeAdmin = () => {
    const token = window.localStorage.getItem('accessToken');
    const role = window.localStorage.getItem('role');

    const [value, setValue] = React.useState('Pending Organiser Account For Review');

    const handleChange = (event: React.SyntheticEvent, newValue: string) => {
        setValue(newValue);
    };

    return (
        <div>
            {
                token != null && role == 'ADMIN' ?
                    <Navigate to="/HomeAdmin" /> : <Navigate to="/Forbidden" />
            }
            < NavbarAdmin />

            <Box sx={{ width: '100%', typography: 'body1' }}>
                <TabContext value={value}>
                    <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
                        <Tabs value={value} onChange={handleChange} textColor="inherit" indicatorColor="primary" centered>
                            <Tab label="Pending Organiser Account For Review" value="Pending Organiser Account For Review" />
                            <Tab label="All Account" value="All Account" />
                        </Tabs>
                    </Box>

                    <TabPanel value="Pending Organiser Account For Review">
                        <br />
                        <br />
                        <PendingTab />
                    </TabPanel>

                    <TabPanel value="All Account">
                        <AllTab />
                    </TabPanel>
                </TabContext>
            </Box>
        </div>

    )
}