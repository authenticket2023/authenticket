import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { NavbarOrganiser } from '../../Navbar';
import Accordion from '@mui/material/Accordion';
import AccordionSummary from '@mui/material/AccordionSummary';
import AccordionDetails from '@mui/material/AccordionDetails';
import Typography from '@mui/material/Typography';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import { Grid } from '@mui/material';

export const FAQOrganiser = () => {
    useEffect(() => {
    }, []);
    const token = window.localStorage.getItem('accessToken');
    const role = window.localStorage.getItem('role');

    return (
        <div>
            {
                token != null && role == 'ORGANISER' ?
                    <Navigate to="/FAQOrganiser" /> : <Navigate to="/Forbidden" />
            }
            < NavbarOrganiser />
            <h3 style={{marginLeft:170, marginTop:40}}>
                Frequently Asked Questions
            </h3>

            {/* FAQ section */}
            <Grid sx={{ display:'flex', flexDirection:'column', justifyContent:'center', alignItems:'center', marginTop:3 }}>
                <Grid sx={{ width: '80%' }}>
                    <Accordion>
                        <AccordionSummary
                        expandIcon={<ExpandMoreIcon />}
                        aria-controls="panel1a-content"
                        id="panel1a-header"
                        >
                        <Typography style={{fontWeight:500}}>
                            What is the process for getting my event approved on the ticketing platform?
                        </Typography>
                        </AccordionSummary>
                        <AccordionDetails>
                        <Typography>
                            Events created by organisers must go through a screening process before they are approved. Once approved, you will receive an email notification.
                        </Typography>
                        </AccordionDetails>
                    </Accordion>
                    <Accordion>
                        <AccordionSummary
                        expandIcon={<ExpandMoreIcon />}
                        aria-controls="panel2a-content"
                        id="panel2a-header"
                        >
                        <Typography style={{fontWeight:500}}>
                            Can I choose any venue for my event as an organiser?
                        </Typography>
                        </AccordionSummary>
                        <AccordionDetails>
                        <Typography>
                            Organisers can only select from a list of fixed venues provided by the admin.
                        </Typography>
                        </AccordionDetails>
                    </Accordion>
                    <Accordion>
                        <AccordionSummary
                        expandIcon={<ExpandMoreIcon />}
                        aria-controls="panel2a-content"
                        id="panel2a-header"
                        >
                        <Typography style={{fontWeight:500}}>
                            What if I want a venue that is not on the list of provided venues?
                        </Typography>
                        </AccordionSummary>
                        <AccordionDetails>
                        <Typography>
                            If you need a venue that is not in the provided list, you can send a request via email to the admin (admin@admin.com).
                        </Typography>
                        </AccordionDetails>
                    </Accordion>
                    <Accordion>
                        <AccordionSummary
                        expandIcon={<ExpandMoreIcon />}
                        aria-controls="panel2a-content"
                        id="panel2a-header"
                        >
                        <Typography style={{fontWeight:500}}>
                            How do I choose artists for my event as an organiser?
                        </Typography>
                        </AccordionSummary>
                        <AccordionDetails>
                        <Typography>
                            Organisers can only pick from a list of artists provided by the admin.
                        </Typography>
                        </AccordionDetails>
                    </Accordion>
                    <Accordion>
                        <AccordionSummary
                        expandIcon={<ExpandMoreIcon />}
                        aria-controls="panel2a-content"
                        id="panel2a-header"
                        >
                        <Typography style={{fontWeight:500}}>
                            What if I want to select an artist who is not on the provided list?
                        </Typography>
                        </AccordionSummary>
                        <AccordionDetails>
                        <Typography>
                            If you wish to pick an artist not on the list, you can send a request via email to the admin (admin@admin.com).
                        </Typography>
                        </AccordionDetails>
                    </Accordion>
                    <Accordion>
                        <AccordionSummary
                        expandIcon={<ExpandMoreIcon />}
                        aria-controls="panel2a-content"
                        id="panel2a-header"
                        >
                        <Typography style={{fontWeight:500}}>
                            Can I offer a presale feature for my event?
                        </Typography>
                        </AccordionSummary>
                        <AccordionDetails>
                        <Typography>
                            Yes, as an organiser, you will have the option to choose the presale feature for your event. The presale occurs one day before the actual ticket sales.
                        </Typography>
                        </AccordionDetails>
                    </Accordion>
                    <Accordion>
                        <AccordionSummary
                        expandIcon={<ExpandMoreIcon />}
                        aria-controls="panel2a-content"
                        id="panel2a-header"
                        >
                        <Typography style={{fontWeight:500}}>
                            How can users indicate interest in the presale for my event?
                        </Typography>
                        </AccordionSummary>
                        <AccordionDetails>
                        <Typography>
                            Users can indicate interest in the event at any time before the presale. An "Indicate Interest" feature will be available.
                        </Typography>
                        </AccordionDetails>
                    </Accordion>
                    <Accordion>
                        <AccordionSummary
                        expandIcon={<ExpandMoreIcon />}
                        aria-controls="panel2a-content"
                        id="panel2a-header"
                        >
                        <Typography style={{fontWeight:500}}>
                            How are users selected for the presale, and how many can participate?
                        </Typography>
                        </AccordionSummary>
                        <AccordionDetails>
                        <Typography>
                            A specific number of users will be chosen for the presale at random by the system. The number of people selected for the presale is calculated as the total number of tickets available for the event divided by 5.
                        </Typography>
                        </AccordionDetails>
                    </Accordion>
                    <Accordion>
                        <AccordionSummary
                        expandIcon={<ExpandMoreIcon />}
                        aria-controls="panel2a-content"
                        id="panel2a-header"
                        >
                        <Typography style={{fontWeight:500}}>
                            Can I manually select users for the presale?
                        </Typography>
                        </AccordionSummary>
                        <AccordionDetails>
                        <Typography>
                            No, users are chosen for the presale automatically by the system, not by the organisers.
                        </Typography>
                        </AccordionDetails>
                    </Accordion>
                    <Accordion>
                        <AccordionSummary
                        expandIcon={<ExpandMoreIcon />}
                        aria-controls="panel2a-content"
                        id="panel2a-header"
                        >
                        <Typography style={{fontWeight:500}}>
                            Can I choose the facial recognition feature for my event?
                        </Typography>
                        </AccordionSummary>
                        <AccordionDetails>
                        <Typography>
                            Yes, as an organiser, you will have the option to enable the facial recognition feature for your event.
                        </Typography>
                        </AccordionDetails>
                    </Accordion>
                    <Accordion>
                        <AccordionSummary
                        expandIcon={<ExpandMoreIcon />}
                        aria-controls="panel2a-content"
                        id="panel2a-header"
                        >
                        <Typography style={{fontWeight:500}}>
                            How does the facial recognition feature work for event entry?
                        </Typography>
                        </AccordionSummary>
                        <AccordionDetails>
                        <Typography>
                            The facial recognition feature, when enabled, replaces the need to scan QR codes for entry. On the day of the event, attendees will go through a face scanner to gain access to the venue.
                        </Typography>
                        </AccordionDetails>
                    </Accordion>
                </Grid>
            </Grid>
        </div>

    )
}