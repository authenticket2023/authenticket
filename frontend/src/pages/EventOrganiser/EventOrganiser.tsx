import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { NavbarOrganiser } from '../../Navbar';
import Box from '@mui/material/Box';
import Stepper from '@mui/material/Stepper';
import Step from '@mui/material/Step';
import StepButton from '@mui/material/StepButton';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import { EventDetails, VenueArtist, EventPoster } from './steps';
import dayjs, { Dayjs } from 'dayjs';
import { Alert, Snackbar } from '@mui/material';
import { Sheet } from '@mui/joy';

const steps = ['Event Details', 'Venue & Artist', 'Event Poster'];

export const EventOrganiser = () => {

    const [activeStep, setActiveStep] = React.useState(0);
    const [completed, setCompleted] = React.useState<{
        [k: number]: boolean;
    }>({});

    const totalSteps = () => {
        return steps.length;
    };

    const completedSteps = () => {
        return Object.keys(completed).length;
    };

    const isLastStep = () => {
        return activeStep === totalSteps() - 1;
    };

    const allStepsCompleted = () => {
        return completedSteps() === totalSteps();
    };

    const handleNext = () => {
        const newActiveStep =
            isLastStep() && !allStepsCompleted()
                ? // It's the last step, but not all steps have been completed,
                // find the first step that has been completed
                steps.findIndex((step, i) => !(i in completed))
                : activeStep + 1;
        setActiveStep(newActiveStep);
    };

    const handleBack = () => {
        setActiveStep((prevActiveStep) => prevActiveStep - 1);
    };

    const handleStep = (step: number) => () => {
        setActiveStep(step);
    };

    //TODO: need add validation
    const handleComplete = () => {
        console.log(activeStep);
        const newCompleted = completed;
        newCompleted[activeStep] = true;
        setCompleted(newCompleted);
        handleNext();
    };

    const handleReset = () => {
        setActiveStep(0);
        setCompleted({});
    };

    //for EventDetails
    //get today's date with format YYYY-MM-DD HH:mm:ss
    const today = new Date(),
        currentDateTime = today.getFullYear() + '-' + (today.getMonth() + 1) + '-' + today.getDate() + ' ' + today.getHours() + ':' + today.getMinutes() + ':' + today.getSeconds();

    const [eventName, setEventName] = useState('');
    const [eventDate, setEventDate] = React.useState<Dayjs>(dayjs(currentDateTime));
    const [saleDate, setSaleDate] = React.useState<Dayjs>(dayjs(currentDateTime));
    const [eventDescription, setEventDescription] = useState('');
    const [otherInfo, setOtherInfo] = useState('');
    const [ticketNumberVIP, setTicketNumberVIP] = useState(0);
    const [ticketNumberCat1, setTicketNumberCat1] = useState(0);
    const [ticketNumberCat2, setTicketNumberCat2] = useState(0);
    const [ticketNumberCat3, setTicketNumberCat3] = useState(0);
    const [ticketNumberCat4, setTicketNumberCat4] = useState(0);
    const [VIPPrice, setVIPPrice] = useState(0);
    const [cat1Price, setCat1Price] = useState(0);
    const [cat2Price, setCat2Price] = useState(0);
    const [cat3Price, setCat3Price] = useState(0);
    const [cat4Price, setCat4Price] = useState(0);
    //for venue & artist
    const [venue, setVenue] = useState('');
    const [artistList, setartistList] = useState<string[]>([]);
    const [otherVenue, setOtherVenue] = useState('');

    //for EventPoster
    const [selectedFiles, setSelectedFiles]: any = useState(null);

    //for testing
    useEffect(() => {
    }, []);

    //for pop up message => error , warning , info , success
    const [openSnackbar, setOpenSnackbar] = useState(false);
    const [alertType, setAlertType]: any = useState('info');
    const [alertMsg, setAlertMsg] = useState('');
    const handleSnackbarClose = () => {
        setOpenSnackbar(false);
    };

    const TimestampConverter = (timestamp: number) => {
        // Create a Date object from the timestamp
        const date = new Date(timestamp);

        // Extract components
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0'); // Month is 0-based
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        const seconds = String(date.getSeconds()).padStart(2, '0');

        const formattedTimestamp = `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;

        return formattedTimestamp;
    }

    const handleCreateEvent = () => {
        //for testing
        console.log('---Step 1-----')
        console.log('Event Name:' + eventName + ' |Event date:' + eventDate + ' |Sale date:' + saleDate);
        console.log('VIP:' + ticketNumberVIP + ' |Price:' + VIPPrice);
        console.log('cat1:' + ticketNumberCat1 + ' |Price:' + cat1Price);
        console.log('cat2:' + ticketNumberCat2 + ' |Price:' + cat2Price);
        console.log('cat3:' + ticketNumberCat3 + ' |Price:' + cat3Price);
        console.log('cat4:' + ticketNumberCat4 + ' |Price:' + cat4Price);
        console.log('---Step 2-----')
        console.log('Venue:' + venue + ' |Artist list:' + artistList + '|other venue:' + otherVenue);
        console.log('---Step 3-----')
        console.log(selectedFiles);

        //retrieve venue from DB
        const addTicketCategory = async (eventId: any) => {
            try {
                const token = window.localStorage.getItem('accessToken');
                const response = await fetch(`${process.env.REACT_APP_BACKEND_DEV_URL}/event/addTicketCategory`, {
                    headers: {
                        // 'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json',
                    },
                    method: 'PUT',
                    body: JSON.stringify({
                        "eventId": eventId,
                        "data": [
                            {
                                "catId": "1",
                                "price": VIPPrice,
                                "availableTickets": ticketNumberVIP,
                                "totalTicketsPerCat": ticketNumberVIP
                            },
                            {
                                "catId": "2",
                                "price": cat1Price,
                                "availableTickets": ticketNumberCat1,
                                "totalTicketsPerCat": ticketNumberCat1
                            },
                            {
                                "catId": "3",
                                "price": cat2Price,
                                "availableTickets": ticketNumberCat2,
                                "totalTicketsPerCat": ticketNumberCat2
                            },
                            {
                                "catId": "4",
                                "price": cat3Price,
                                "availableTickets": ticketNumberCat3,
                                "totalTicketsPerCat": ticketNumberCat3
                            },
                            {
                                "catId": "5",
                                "price": cat4Price,
                                "availableTickets": ticketNumberCat4,
                                "totalTicketsPerCat": ticketNumberCat4
                            },
                        ]
                    })
                });
                if (response.status !== 200) {
                    //show alert msg
                    setOpenSnackbar(true);
                    setAlertType('error');
                    setAlertMsg("error on adding ticket category data!!!");
                } else {
                    setOpenSnackbar(true);
                    setAlertType('success');
                    setAlertMsg('Event created successfully!!!');
                }
            } catch (err) {
                window.alert(err);
            }
        };

        //TODO: call backend to create events
        const formData = new FormData();
        formData.append('eventName', eventName);
        //need format
        formData.append('eventDate', TimestampConverter(Number(eventDate)));
        formData.append('eventLocation', 'dont need this field');
        formData.append('eventDescription', eventDescription);
        formData.append('otherEventInfo', otherInfo);
        formData.append('ticketSaleDate', TimestampConverter(Number(saleDate)));
        formData.append('file', selectedFiles[0]);
        //this get from login?
        formData.append('organiserId', '1');
        //get from drop down
        formData.append('venueId', '1');
        formData.append('artistId', artistList.toString());
        formData.append('typeId', '3');

        //calling create event backend API
        fetch(`${process.env.REACT_APP_BACKEND_DEV_URL}/event`, {
            method: 'POST',
            body: formData
        })
            .then(async (response) => {
                if (response.status == 200) {
                    const eventResponse = await response.json();
                    //if ok, call addTicketCategory
                    addTicketCategory('35');
                } else {
                    const eventResponse = await response.json();
                    setOpenSnackbar(true);
                    setAlertType('warning');
                    setAlertMsg(eventResponse.message);
                    addTicketCategory('38');
                }
            })
            .catch((err) => {
                window.alert(err);
            });


    };

    return (
        <div>
            < NavbarOrganiser />
            <Box sx={{ mt: '5%', ml: '15%', mr: '15%', mb: '5%', width: '70%' }}>
                <Stepper nonLinear activeStep={activeStep} alternativeLabel>
                    {steps.map((label, index) => (
                        <Step key={label} completed={completed[index]}>
                            <StepButton color="inherit" onClick={handleStep(index)}>
                                {label}
                            </StepButton>
                        </Step>
                    ))}
                </Stepper>
                <div>
                    {allStepsCompleted() ? (
                        <React.Fragment>
                            <Typography sx={{ mt: 2, mb: 1 }}>
                                All steps completed - you&apos;re finished
                            </Typography>
                            <Sheet sx={{ alignItems: "center", mt: 2, mb: 2 }}>
                                <Button onClick={handleCreateEvent} fullWidth variant="contained" sx={{ p: 1.5, textTransform: "none", fontSize: "16px" }}>Create Event</Button>
                            </Sheet>
                        </React.Fragment>
                    ) : (
                        <React.Fragment>

                            {activeStep == 0 ? <EventDetails
                                currentDateTime={currentDateTime}
                                eventName={eventName}
                                eventDate={eventDate}
                                eventDescription={eventDescription}
                                ticketNumberVIP={ticketNumberVIP}
                                ticketNumberCat1={ticketNumberCat1}
                                ticketNumberCat2={ticketNumberCat2}
                                ticketNumberCat3={ticketNumberCat3}
                                ticketNumberCat4={ticketNumberCat4}
                                VIPPrice={VIPPrice}
                                cat1Price={cat1Price}
                                cat2Price={cat2Price}
                                cat3Price={cat3Price}
                                cat4Price={cat4Price}
                                saleDate={saleDate}
                                otherInfo={otherInfo}
                                setEventName={setEventName}
                                setEventDescription={setEventDescription}
                                setEventDate={setEventDate}
                                setTicketNumberVIP={setTicketNumberVIP}
                                setTicketNumberCat1={setTicketNumberCat1}
                                setTicketNumberCat2={setTicketNumberCat2}
                                setTicketNumberCat3={setTicketNumberCat3}
                                setTicketNumberCat4={setTicketNumberCat4}
                                setVIPPrice={setVIPPrice}
                                setCat1Price={setCat1Price}
                                setCat2Price={setCat2Price}
                                setCat3Price={setCat3Price}
                                setCat4Price={setCat4Price}
                                setSaleDate={setSaleDate}
                                setOtherInfo={setOtherInfo}
                                handleComplete={handleComplete}
                                setOpenSnackbar={setOpenSnackbar} setAlertType={setAlertType} setAlertMsg={setAlertMsg} />
                                : null}

                            {activeStep == 1 ? <VenueArtist
                                venue={venue}
                                otherVenue={otherVenue}
                                artistList={artistList}
                                setVenue={setVenue}
                                setartistList={setartistList}
                                setOtherVenue={setOtherVenue}
                                handleComplete={handleComplete}
                                setOpenSnackbar={setOpenSnackbar} setAlertType={setAlertType} setAlertMsg={setAlertMsg}
                            /> : null}

                            {activeStep == 2 ? <EventPoster
                                selectedFiles={selectedFiles}
                                setSelectedFiles={setSelectedFiles}
                                handleComplete={handleComplete}
                                setOpenSnackbar={setOpenSnackbar} setAlertType={setAlertType} setAlertMsg={setAlertMsg}
                            /> : null}

                            <Box sx={{ display: 'flex', flexDirection: 'row', pt: 2 }}>
                                <Button
                                    color="inherit"
                                    disabled={activeStep === 0}
                                    onClick={handleBack}
                                    sx={{ mr: 1 }}
                                >
                                    Back
                                </Button>
                                <Box sx={{ flex: '1 1 auto' }} />
                                <Button onClick={handleNext} sx={{ mr: 1 }}>
                                    Next
                                </Button>
                                {activeStep !== steps.length &&
                                    (completed[activeStep] ? (
                                        <Typography variant="caption" sx={{ display: 'inline-block' }}>
                                            Step {activeStep + 1} already completed
                                        </Typography>
                                    ) : (
                                        null
                                    ))}
                            </Box>
                        </React.Fragment>
                    )}
                </div>
            </Box>

            {/* success / error feedback */}
            <Snackbar open={openSnackbar} autoHideDuration={3000} onClose={handleSnackbarClose}>
                <Alert onClose={handleSnackbarClose} severity={alertType} sx={{ width: '100%' }}>
                    {alertMsg}
                </Alert>
            </Snackbar>
        </div>

    );
}