import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { NavbarAdmin } from '../../Navbar';
import { Box, Tab, Tabs} from '@mui/material';
import { TabContext, TabPanel } from '@mui/lab';
import {PendingEventTab, AllEventTab} from './tabs';

export const EventAdmin = () => {
    const token = window.localStorage.getItem('accessToken');
    const accRole = window.localStorage.getItem('accRole');

    const [value, setValue] = React.useState('Pending Event');

    const handleChange = (event: React.SyntheticEvent, newValue: string) => {
        setValue(newValue);
    };

    return (
        <div>
            < NavbarAdmin />

            <Box sx={{ width: '100%', typography: 'body1' }}>
                <TabContext value={value}>
                    <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
                        <Tabs value={value} onChange={handleChange} textColor="inherit" indicatorColor="primary" centered>
                            <Tab label="Pending Event" value="Pending Event" />
                            <Tab label="All Event" value="All Event" />
                        </Tabs>
                    </Box>

                    <TabPanel value="Pending Event">
                        <br />
                        <br />
                        <PendingEventTab />
                    </TabPanel>

                    <TabPanel value="All Event">
                        <AllEventTab />
                    </TabPanel>
                </TabContext>
            </Box>
        </div>

    )
}