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
                    <Navigate to="/HomeOrganiser" /> : <Navigate to="/Forbidden" />
            }
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
                            What is the maximum number of tickets I can purchase per user?
                        </Typography>
                        </AccordionSummary>
                        <AccordionDetails>
                        <Typography>
                            You can buy a maximum of 5 tickets per user.
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
                            What payment methods or forms of payment are available?
                        </Typography>
                        </AccordionSummary>
                        <AccordionDetails>
                        <Typography>
                            You can make a payment via Pay Now, credit/debit cards, GrabPay, or Alipay.
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
                            Can I buy tickets from different ticket categories in a single order?
                        </Typography>
                        </AccordionSummary>
                        <AccordionDetails>
                        <Typography>
                            No, all tickets purchased in a single order must be from the same ticket category.
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
                            What happens if my order is successful?
                        </Typography>
                        </AccordionSummary>
                        <AccordionDetails>
                        <Typography>
                            If your order is successful, an email confirmation will be sent to you. You can check your profile under "My Orders" for details on your tickets.
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
                            What happens if the ticket I intend to buy sells out while I make payment?
                        </Typography>
                        </AccordionSummary>
                        <AccordionDetails>
                        <Typography>
                            Seats you have chosen will be put on hold when you reach the payment stage. So, you will not face the issue of the tickets selling out while you are making payment.
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
                            What happens if the payment stage fails?
                        </Typography>
                        </AccordionSummary>
                        <AccordionDetails>
                        <Typography>
                            If the payment stage fails, the seats held on hold will be released, and you'll need to restart the ticket purchasing process.
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
                            How much time do I have to complete the payment process?
                        </Typography>
                        </AccordionSummary>
                        <AccordionDetails>
                        <Typography>
                            You have 10 minutes to complete the payment. If not, the payment will fail, and the seats will be released.
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
                            Can I transfer my ticket to someone else?
                        </Typography>
                        </AccordionSummary>
                        <AccordionDetails>
                        <Typography>
                            No, tickets are non-transferable.
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
                            What should I do if I need to change the owner of a ticket for special reasons?
                        </Typography>
                        </AccordionSummary>
                        <AccordionDetails>
                        <Typography>
                            To change the owner of a ticket, please contact us (admin@admin.com), and your request will be reviewed on a case-by-case basis.
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
                            Can I choose my own seats when purchasing tickets?
                        </Typography>
                        </AccordionSummary>
                        <AccordionDetails>
                        <Typography>
                            No, best available consecutive seats will be chosen for you by default.
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
                            Where can I indicate interest in a presale event?
                        </Typography>
                        </AccordionSummary>
                        <AccordionDetails>
                        <Typography>
                            The "Indicate Interest" button is on the same page as the "Buy Now" button on the ticket details page.
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
                            How are users chosen for the presale event?
                        </Typography>
                        </AccordionSummary>
                        <AccordionDetails>
                        <Typography>
                            Users are chosen at random, not on a first-come, first-serve basis.
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
                            When does the presale event take place?
                        </Typography>
                        </AccordionSummary>
                        <AccordionDetails>
                        <Typography>
                            The presale event happens one day before the actual public ticket sales.
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
                            How will I know if I'm chosen for the presale?
                        </Typography>
                        </AccordionSummary>
                        <AccordionDetails>
                        <Typography>
                            Users chosen for the presale will receive an email notifying them.
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
                            Do I have to indicate the names of each ticket holder during the purchase process?
                        </Typography>
                        </AccordionSummary>
                        <AccordionDetails>
                        <Typography>
                            Yes, it is compulsory to indicate the names of each ticket holder. This helps prevent reselling and ticket scalping, as the names of the ticket holders are immutable.
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
                            What is the purpose of the facial recognition feature?
                        </Typography>
                        </AccordionSummary>
                        <AccordionDetails>
                        <Typography>
                            The purpose of the facial recognition feature is to prevent ticket scalping and reselling.
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
                            Are images of attendees stored for facial recognition?
                        </Typography>
                        </AccordionSummary>
                        <AccordionDetails>
                        <Typography>
                            No, images of attendees are not stored. Facial recognition is used solely to validate the ticket holder's identity.
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
                            Do all events require attendees to submit an image alongside the name of the ticket holder?
                        </Typography>
                        </AccordionSummary>
                        <AccordionDetails>
                        <Typography>
                            No, not all events require this. Facial recognition is used selectively for specific events. If the event organizer activates this feature, you will need to upload an image of each attendee along with their names during the ticket purchasing process.
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
                            How does the facial recognition process work when entering the venue?
                        </Typography>
                        </AccordionSummary>
                        <AccordionDetails>
                        <Typography>
                            When entering the venue, instead of scanning a QR code, attendees will need to go through a facial recognition camera. The system will identify their tickets based on their facial features to grant entry. This is a security measure designed to prevent ticket fraud and unauthorized access to the event.
                        </Typography>
                        </AccordionDetails>
                    </Accordion>
                </Grid>
            </Grid>
        </div>

    )
}