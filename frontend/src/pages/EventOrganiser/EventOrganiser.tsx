import React, { useEffect, useState } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
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
const delay = (ms: number) => new Promise(
    resolve => setTimeout(resolve, ms)
);

export const EventOrganiser = () => {
    let navigate = useNavigate();
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

    const handleComplete = () => {
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
    const [eventType, setEventType] = useState('');
    const [eventDate, setEventDate] = React.useState<Dayjs>(dayjs(currentDateTime));
    const [saleDate, setSaleDate] = React.useState<Dayjs>(dayjs(currentDateTime));
    const [eventDescription, setEventDescription] = useState('');
    const [otherInfo, setOtherInfo] = useState('');
    const [facialCheckIn, setFacialCheckIn]: any = useState(true);
    const [presale, setPresale]: any = useState(true);

    //to be remove
    const [ticketNumberVIP, setTicketNumberVIP] = useState(100);
    const [ticketNumberCat1, setTicketNumberCat1] = useState(100);
    const [ticketNumberCat2, setTicketNumberCat2] = useState(100);
    const [ticketNumberCat3, setTicketNumberCat3] = useState(100);
    const [ticketNumberCat4, setTicketNumberCat4] = useState(100);


    //for venue & artist
    const [venue, setVenue] = useState('');
    const [artistList, setartistList] = useState<string[]>([]);
    const [otherVenue, setOtherVenue] = useState('');
    const [VIPPrice, setVIPPrice] = useState();
    const [cat1Price, setCat1Price] = useState();
    const [cat2Price, setCat2Price] = useState();
    const [cat3Price, setCat3Price] = useState();
    const [cat4Price, setCat4Price] = useState();

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

        const formattedTimestamp = `${year}-${month}-${day}T${hours}:${minutes}:00`;

        return formattedTimestamp;
    }

    const token = window.localStorage.getItem('accessToken');
    const role = window.localStorage.getItem('role');
    const organiserId: any = window.localStorage.getItem('id');

    const [isClicked, setIsClicked] = useState(false);

    const handleCreateEvent = () => {
        if (!isClicked) {
            //Disable further clicks
            setIsClicked(true);

            //call backend to create events
            const formData = new FormData();
            formData.append('eventName', eventName);
            formData.append('eventDate', TimestampConverter(Number(eventDate)));
            formData.append('eventDescription', eventDescription);
            formData.append('ticketSaleDate', TimestampConverter(Number(saleDate)));
            formData.append('file', selectedFiles[0]);
            formData.append('organiserId', organiserId);
            formData.append('venueId', venue);
            //if the event venue is not in the list, user will enter the venue, add it to other info
            if (venue == '999') {
                formData.append('otherEventInfo', `${otherInfo}; Other venue:${otherVenue}`);
            } else {
                formData.append('otherEventInfo', otherInfo);
            }
            formData.append('artistId', artistList.toString());
            formData.append('typeId', eventType);
            formData.append('ticketPrices', `${VIPPrice},${cat1Price},${cat2Price},${cat3Price},${cat4Price}`);
            formData.append('isEnhanced', facialCheckIn);
            formData.append('hasPresale', presale);
            //TODO: add special requirement into formdata, pending BE
            console.log(`${facialCheckIn} - ${presale}`);
            //calling create event backend API
            fetch(`${process.env.REACT_APP_BACKEND_URL}/event`, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                },
                method: 'POST',
                body: formData
            })
                .then(async (response) => {
                    if (response.status == 200) {
                        setOpenSnackbar(true);
                        setAlertType('success');
                        if (venue == '999') {
                            setAlertMsg(`Your event has been successfully created! Kindly await our administrator's review, and they will get in touch with you shortly to provide further information about the venue.`);
                        } else {
                            setAlertMsg('Event created successfully!!! Please wait for our administrator to review!!!');
                        }
                        await delay(2000);
                        navigate('/HomeOrganiser');
                    } else {
                        const eventResponse = await response.json();
                        setOpenSnackbar(true);
                        setAlertType('warning');
                        setAlertMsg(eventResponse.message);
                        //if transaction faile, enable clickable
                        setIsClicked(false);
                    }
                })
                .catch((err) => {
                    //if transaction faile, enable clickable
                    setIsClicked(false);
                    window.alert(err);
                });

        }
    };

    return (
        <div>
            {
                token != null && role == 'ORGANISER' ?
                    <Navigate to="/EventOrganiser" /> : <Navigate to="/Forbidden" />
            }
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
                                <Button onClick={handleCreateEvent} fullWidth variant="contained" sx={{ p: 1.5, textTransform: "none", fontSize: "16px" }} disabled={isClicked}>
                                    {isClicked ? 'Creating Event' : 'Create Event'}
                                </Button>
                            </Sheet>
                        </React.Fragment>
                    ) : (
                        <React.Fragment>

                            {activeStep == 0 ? <EventDetails
                                currentDateTime={currentDateTime}
                                eventName={eventName}
                                eventType={eventType}
                                eventDate={eventDate}
                                eventDescription={eventDescription}
                                saleDate={saleDate}
                                facialCheckIn={facialCheckIn}
                                presale={presale}
                                otherInfo={otherInfo}
                                setEventName={setEventName}
                                setEventType={setEventType}
                                setEventDescription={setEventDescription}
                                setEventDate={setEventDate}
                                setSaleDate={setSaleDate}
                                setFacialCheckIn={setFacialCheckIn}
                                setPresale={setPresale}
                                setOtherInfo={setOtherInfo}
                                handleComplete={handleComplete}
                                setOpenSnackbar={setOpenSnackbar} setAlertType={setAlertType} setAlertMsg={setAlertMsg} />
                                : null}

                            {activeStep == 1 ? <VenueArtist
                                venue={venue}
                                otherVenue={otherVenue}
                                artistList={artistList}
                                VIPPrice={VIPPrice}
                                cat1Price={cat1Price}
                                cat2Price={cat2Price}
                                cat3Price={cat3Price}
                                cat4Price={cat4Price}
                                setVenue={setVenue}
                                setartistList={setartistList}
                                setOtherVenue={setOtherVenue}
                                setVIPPrice={setVIPPrice}
                                setCat1Price={setCat1Price}
                                setCat2Price={setCat2Price}
                                setCat3Price={setCat3Price}
                                setCat4Price={setCat4Price}
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
                                {/* <Button
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
                                </Button> */}
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
            <Snackbar open={openSnackbar} autoHideDuration={2000} onClose={handleSnackbarClose}>
                <Alert onClose={handleSnackbarClose} severity={alertType} sx={{ width: '100%' }}>
                    {alertMsg}
                </Alert>
            </Snackbar>
        </div>

    );
}