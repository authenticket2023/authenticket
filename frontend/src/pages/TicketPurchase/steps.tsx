import { BorderColor } from '@mui/icons-material';
import {
    Box, Modal, Button, TextField, Avatar, Typography, Grid, TextareaAutosize, ImageList, ImageListItem, FormControl, InputLabel, Select, MenuItem, OutlinedInput, Checkbox, ListItemText, InputAdornment, FormGroup, Switch, FormControlLabel, SelectChangeEvent, Snackbar, Alert
} from '@mui/material';
import React, { useEffect, useState } from 'react';
import { SGStad } from '../utility/SeatMap';

export function SelectSeats(props: any) {

    const [selectedSection, setSelectedSection] = useState();

    useEffect(() => {
        loadStatus();
        console.log(selectedSection);
    }, [selectedSection]);

    //set variables
    const [quantity, setQuantity] = React.useState('');
    const [sectionDetails, setSectionDetails] = React.useState<any[]>([]);
    const [openSnackbar, setOpenSnackbar] = useState(false);
    const [alertType, setAlertType]: any = useState('info');
    const [alertMsg, setAlertMsg] = useState('');
    const handleSnackbarClose = () => {
        setOpenSnackbar(false);
    };

    const loadStatus = async () => {
        // //calling backend API
        fetch(`${process.env.REACT_APP_BACKEND_URL}/public/event/section-ticket-details/${props.eventDetails.eventId}`, {
            headers: {
              'Content-Type': 'application/json',
            },
            method: 'GET',
          })
            .then(async (response) => {
              if (response.status == 200) {
                const apiResponse = await response.json();
                const data = apiResponse.data;
                console.log(data);
                setSectionDetails(data);
  
              } else {
                //passing to parent component
              }
            })
            .catch((err) => {
              window.alert(err);
            });
    }

    const handleChange = (event: SelectChangeEvent) => {
        const newQuantity = event.target.value as string;
        setQuantity(newQuantity);
        props.onQuantityChange(newQuantity);
        // props.onSelectedSection(selectedSection);

        // Find the selected section
        const selectedSectionData = sectionDetails.find(
            (item) => item.sectionId === selectedSection
        );

        // Check if the selectedSectionData exists
        if (selectedSectionData) {
            const maxConsecutiveSeats = selectedSectionData.maxConsecutiveSeats;

            // Error message if the ticket order is bigger than the max consecutive seats
            if (parseInt(quantity) > maxConsecutiveSeats) {
            setOpenSnackbar(true);
            setAlertType('warning');
            setAlertMsg(`Maximum consecutive seats in this section is ${maxConsecutiveSeats}`);
            }
        }
    };

    const handleSeats = () => {

        // // Find the selected section
        // const selectedSectionData = sectionDetails.find(
        //     (item) => item.sectionId === selectedSection
        // );

        // // Check if the selectedSectionData exists
        // if (selectedSectionData) {
        //     const maxConsecutiveSeats = selectedSectionData.maxConsecutiveSeats;

        //     // Error message if the ticket order is bigger than the max consecutive seats
        //     if (parseInt(quantity) > maxConsecutiveSeats) {
        //     setOpenSnackbar(true);
        //     setAlertType('warning');
        //     setAlertMsg(`Maximum consecutive seats in this section is ${maxConsecutiveSeats}`);
        //     } else {
        //         props.handleComplete();
        //     }
        // }

        //check if a section is chosen before proceedign to the next page
        if (!selectedSection) {
            // Show an error message
            setOpenSnackbar(true);
            setAlertType('error');
            setAlertMsg('Please select a section before proceeding.');
        } else if (!quantity) {
            //check if quantity is chosen
            // Show an error message
            setOpenSnackbar(true);
            setAlertType('error');
            setAlertMsg('Please select a quantity before proceeding.');
        } else {
            // If a section has been selected, proceed to the next step
            props.onSelectedSection(selectedSection);
            props.handleComplete();
        }
    }

    // console.log(props.eventDetails.venue.venueId)
    // console.log(quantity);
    // console.log(props.selectedSection);
    
    return (
        <div style={{display:'flex', justifyContent:'center', flexDirection:'column', alignItems:'center'}}>
            <div>
                <SGStad id={props.eventDetails.venue.venueId} setSelectedSection={setSelectedSection}/>
                <Typography style={{font:'roboto', fontWeight:500, fontSize:'16px', marginLeft:675, marginTop:-457}}>
                Status: {sectionDetails ? (sectionDetails.find((item: { sectionId: string }) => item.sectionId === selectedSection)?.status || 'Unknown') : 'Loading...'}
                </Typography>
            </div>
            <div style={{background:'#F8F8F8', height:'110px', width:'300px', borderRadius:'8px', alignContent:'left', marginLeft:650, marginTop:25}}>
                <Typography style={{font:'roboto', fontWeight:500, fontSize:'18px', marginLeft:25, marginTop:18}}>
                    Ticket Quantity
                </Typography>
                <Box sx={{ minWidth: 120, marginLeft:2 }}>
                    <FormControl sx={{ m: 1, minWidth: 120 }} size="small">
                        <InputLabel id="demo-select-small-label">Quantity</InputLabel>
                        <Select
                        labelId="demo-select-small-label"
                        id="demo-select-small"
                        value={quantity}
                        label="Quantity"
                        displayEmpty
                        onChange={handleChange}
                        style={{fontSize:'13px'}}
                        >
                            <MenuItem value={1}>1</MenuItem>
                            <MenuItem value={2}>2</MenuItem>
                            <MenuItem value={3}>3</MenuItem>
                            <MenuItem value={4}>4</MenuItem>
                            <MenuItem value={5}>5</MenuItem>
                        </Select>
                    </FormControl>
                </Box>
            </div>
            <Button variant="outlined" onClick={handleSeats}
                sx={{
                    border:'1px solid #FF5C35', 
                    borderRadius:'8px',
                    color:'#FF5C35',
                    height: 39.5,
                    width: 295,
                    marginLeft:81.5,
                    marginTop:1,
                    ":hover": {
                        bgcolor: "#FF5C35",
                        color:'white',
                        BorderColor:'#FF5C35'
                    }
                }}>
                Confirm Seats
            </Button>

            <Snackbar open={openSnackbar} autoHideDuration={4000} onClose={handleSnackbarClose}>
                <Alert onClose={handleSnackbarClose} severity={alertType} sx={{ width:'100%' }}>
                    {alertMsg}
                </Alert>
            </Snackbar>
        </div>
    )
}

export function EnterDetails(props: any) {
    return (
        <div></div>
    )
}

export function Confirmation(props: any) {
    // console.log(props.quantity);
    // console.log(props.eventDetails);

    const handleConfirmation = () => {
        
        props.handleComplete();
    }

    return (
        <Grid style={{display:'flex', justifyContent:'center', flexDirection:'row', marginTop:50}}>
            <Grid item style={{background:'#F8F8F8', height:'265px', width:'450px', borderRadius:'8px', justifyContent:'center', display:'flex', alignItems:'center', marginRight:5}}>
                <img
                    src={`https://authenticket.s3.ap-southeast-1.amazonaws.com/event_images/${props.eventDetails.eventImage}`}
                    style={{
                        maxHeight: '150px',
                        borderRadius:'8px'
                    }}
                    alt="Event Image"
                />
            </Grid>
            <Grid item style={{background:'#F8F8F8', height:'210px', width:'300px', borderRadius:'8px', marginLeft:5}}>
                <Typography style={{font:'roboto', fontWeight:500, fontSize:'18px', marginLeft:25, marginTop:24}}>
                    Summary
                </Typography>
                <div>
                    <Typography style={{font:'roboto', fontWeight:400, fontSize:'15px', marginLeft:25, marginTop:0}}>
                        Items Subtotal: 
                    </Typography>
                </div>
                <div style={{display:'flex', flexDirection:'row'}}>
                    <Typography style={{font:'roboto', fontWeight:400, fontSize:'15px', marginLeft:25, color:'#888888'}}>
                        Section {props.selectedSection}
                    </Typography>
                    <Typography style={{font:'roboto', fontWeight:400, fontSize:'15px', marginLeft:150, color:'#888888'}}>
                        x{props.quantity}
                    </Typography>
                </div>
                <div style={{marginTop:20, display:'flex', flexDirection:'row'}}>
                    <Typography style={{font:'roboto', fontWeight:400, fontSize:'15px', marginLeft:25, marginTop:0}}>
                        Booking Fee: 
                    </Typography>
                    <Typography style={{font:'roboto', fontWeight:400, fontSize:'15px', marginLeft:120, marginTop:0}}>
                        $5.00
                    </Typography>
                </div>
                <div style={{display:'flex', flexDirection:'row'}}>
                    <Typography style={{font:'roboto', fontWeight:500, fontSize:'18px', marginLeft:25, marginTop:18}}>
                        Order Total:
                    </Typography>
                </div>
                <Button variant="outlined" onClick={handleConfirmation}
                    sx={{
                        border:'1px solid #FF5C35', 
                        borderRadius:'8px',
                        color:'#FF5C35',
                        height: 39.5,
                        width: 300,
                        marginLeft:0,
                        marginTop:4.5,
                        ":hover": {
                            bgcolor: "#FF5C35",
                            color:'white',
                            BorderColor:'#FF5C35'
                        }
                    }}
                >
                    Confirm Order
                </Button>
            </Grid>
        </Grid>
    )
}

export function ConfirmationFace(props: any) {
    return (
        <div></div>
    )
}

export function Payment(props: any) {
    return (
        <div></div>
    )
}