import {
    Box, Button, TextField, Typography, Grid, ImageList, ImageListItem, FormControl, InputLabel, Select, MenuItem, SelectChangeEvent, Snackbar, Alert, Stack
} from '@mui/material';
import React, { useEffect, useState } from 'react';
import { SGStad } from '../../utility/seatMap/SeatMap';
import { loadStripe } from '@stripe/stripe-js/pure';

export function SelectSeats(props: any) {

    const [selectedSection, setSelectedSection] = useState();

    useEffect(() => {
    }, []);

    //set variables
    const colorArray = ['#E5E23D', '#D74A50', '#30A1D3', '#E08D24', '#5BB443'];
    const [quantity, setQuantity] = React.useState('');
    const [maxConsecutiveSeats, setMaxConsecutiveSeats] = React.useState(1);
    const [openSnackbar, setOpenSnackbar] = useState(false);
    const [alertType, setAlertType]: any = useState('info');
    const [alertMsg, setAlertMsg] = useState('');
    const handleSnackbarClose = () => {
        setOpenSnackbar(false);
    };

    const handleChange = (event: SelectChangeEvent) => {
        const newQuantity = event.target.value as string;
        setQuantity(newQuantity);
        props.onQuantityChange(newQuantity);

        // Find the selected section
        const selectedSectionData = props.sectionDetails.find(
            (item: { sectionId: undefined; }) => item.sectionId === selectedSection
        )

        // Check if the selectedSectionData exists
        if (selectedSectionData) {
            const maxConsecutiveSeats = selectedSectionData.maxConsecutiveSeats;
            setMaxConsecutiveSeats(maxConsecutiveSeats);

            // Error message if the ticket order is bigger than the max consecutive seats
            if (parseInt(quantity) > maxConsecutiveSeats) {
                setOpenSnackbar(true);
                setAlertType('warning');
                setAlertMsg(`Maximum consecutive seats in this section is ${maxConsecutiveSeats}`);
            }
        }
    };

    const handleSeats = () => {
        //check if the section is sold out or if maxConsecutiveSeats == 0
        const maxConsecutiveSeats = props.sectionDetails
            ? props.sectionDetails.find((item: { sectionId: string }) => item.sectionId === selectedSection)?.maxConsecutiveSeats
            : 0;

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
        } else if (parseInt(quantity) > maxConsecutiveSeats) {
            //check amount selected is more than maxConsecutiveSeats
            // Show an error message
            setOpenSnackbar(true);
            setAlertType('error');
            setAlertMsg('Quantity selected is more than what is available. Please pick a different quantity or section.');
        }else if (maxConsecutiveSeats == 0) {
            //check if section is sold out
            // Show an error message
            setOpenSnackbar(true);
            setAlertType('error');
            setAlertMsg('Section sold out. Please select a different section.');
        } else {
            // If a section has been selected, proceed to the next step
            props.onSelectedSection(selectedSection);
            props.handleComplete();
        }

    }

    return (
        <div style={{ display: 'flex', justifyContent: 'center', flexDirection: 'column', alignItems: 'center' }}>
            <div>
                <SGStad id={props.eventDetails.venue.venueId} setSelectedSection={setSelectedSection} />
            </div>
            <div>
                <Typography style={{marginLeft:520, marginTop:-450, font:'roboto', fontWeight:500, fontSize:'16px'}}>
                    Price: {(props.sectionDetails.find((item: { sectionId: string }) => item.sectionId === selectedSection) != null ? '$' : '')}
                    {(props.sectionDetails.find((item: { sectionId: string }) => item.sectionId === selectedSection)?.ticketPrice.toFixed(2) || 'Loading...')}
                </Typography>
                {/* <Typography style={{ marginLeft: 520, marginTop: -450, fontFamily: 'Roboto', fontWeight: 500, fontSize: 16 }}>
                    Price: {!props.sectionDetails ? null : '$'} {props.sectionDetails ? (
                        props.sectionDetails.find((item: { sectionId: string }) => item.sectionId === selectedSection)?.ticketPrice || 'Loading...'
                    ) : 'Loading...'}
                </Typography> */}
                <Typography style={{ font: 'roboto', fontWeight: 500, fontSize: '16px', marginLeft: 520, marginTop: 0 }}>
                    Status: {props.sectionDetails ? (props.sectionDetails.find((item: { sectionId: string }) => item.sectionId === selectedSection)?.status || 'Loading...') : 'Loading...'}
                </Typography>
            </div>
            <div style={{ background: '#F8F8F8', height: '110px', width: '300px', borderRadius: '8px', alignContent: 'left', marginLeft: 650, marginTop: -375 }}>
                <Typography style={{ font: 'roboto', fontWeight: 500, fontSize: '18px', marginLeft: 25, marginTop: 18 }}>
                    Ticket Quantity
                </Typography>
                <Box sx={{ minWidth: 120, marginLeft: 2 }}>
                    <FormControl sx={{ m: 1, minWidth: 120 }} size="small">
                        <InputLabel id="demo-select-small-label">Quantity</InputLabel>
                            <Select
                                labelId="demo-select-small-label"
                                id="demo-select-small"
                                value={quantity}
                                label="Quantity"
                                onChange={handleChange}
                                displayEmpty
                                style={{ fontSize: '13px' }}
                                disabled={props.sectionDetails.find((item: { sectionId: string }) => item.sectionId === selectedSection)?.status === "Sold Out" || !selectedSection}
                            >
                                {Array.from({ length: Math.min(maxConsecutiveSeats || 5, 5) }, (_, index) => (
                                    <MenuItem key={index + 1} value={index + 1}>
                                        {index + 1}
                                    </MenuItem>
                                ))}
                            </Select>
                    </FormControl>
                </Box>
            </div>
            <Button variant="outlined" onClick={handleSeats}
                sx={{
                    border: '1px solid #FF5C35',
                    borderRadius: '8px',
                    color: '#FF5C35',
                    height: 39.5,
                    width: 295,
                    marginLeft: 81.5,
                    marginTop: 1,
                    ":hover": {
                        bgcolor: "#FF5C35",
                        color: 'white',
                        BorderColor: '#FF5C35'
                    }
                }}>
                Confirm Seats
            </Button>
            <br />
            <br />
            <br />
            <br />
            <br />
            <br />
            <Grid item xs={8}>
                <Grid container spacing={2}>
                    <Grid item xs={12} >
                        <Typography style={{ font: 'Roboto', fontWeight: 500, fontSize: '18px' }}>
                            Ticket Pricing
                        </Typography>
                    </Grid>
                    {props.categoryDetails.map((cat: any) => (
                        <Grid item key={cat.categoryId} xs={6} display='flex' flexDirection='row'> {/* xs={6} makes each item take up half the row */}
                            <div style={{ background: colorArray[cat.categoryId - 1], height: '20px', width: '20px', borderRadius: '5px' }} />
                            <Typography style={{ color: 'black', marginLeft: 10 }}>
                                {cat.categoryName} - ${cat.price}
                            </Typography>
                        </Grid>
                    ))}

                </Grid>

            </Grid>
                {/* <Snackbar open={props.sectionDetails.find((item: { sectionId: string }) => item.sectionId === selectedSection)?.status === "Sold Out" || !selectedSection} autoHideDuration={4000} onClose={handleSnackbarClose}>
                    <Alert onClose={handleSnackbarClose} severity={'error'} sx={{ width: '100%' }}>
                    {'Section sold out. Please select a different section.'}
                    </Alert>
                </Snackbar> */}
            <Snackbar open={openSnackbar} autoHideDuration={4000} onClose={handleSnackbarClose}>
                <Alert onClose={handleSnackbarClose} severity={alertType} sx={{ width: '100%' }}>
                    {alertMsg}
                </Alert>
            </Snackbar>
        </div>
    )
}

export function EnterDetails(props: any) {

    const [textFieldValues, setTextFieldValues] = useState<string[]>(Array.from({ length: props.quantity }, () => ''));

    //error display variables
    const [openSnackbar, setOpenSnackbar] = useState(false);
    const [alertType, setAlertType]: any = useState('info');
    const [alertMsg, setAlertMsg] = useState('');
    const handleSnackbarClose = () => {
        setOpenSnackbar(false);
    };

    const token = window.localStorage.getItem('accessToken');
    const delay = (ms: number) => new Promise(
        resolve => setTimeout(resolve, ms)
    );

    useEffect(() => {
    }, []);

    const handleTextFieldChange = (event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>, sectionIndex: number) => {

        const updatedTextFieldValues = [...textFieldValues];
        updatedTextFieldValues[sectionIndex] = event.target.value;
        setTextFieldValues(updatedTextFieldValues);
    };

    const handleConfirm = (event: React.FormEvent) => {
        event.preventDefault();

        // Collect images and names
        const names = textFieldValues.filter((name) => name.trim() !== '');

        // Call the parent's function to update the state
        props.updateEnteredData(names);

        // Check if any TextField is empty
        if (textFieldValues.some((value) => value.trim() === '')) {
            // Show an alert message if any TextField is empty
            setOpenSnackbar(true);
            setAlertType('error');
            setAlertMsg('Please fill in all the name fields!');
        } else {
            props.handleComplete();
        }
    };

    return (
        <form onSubmit={handleConfirm}>
            <div style={{ display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center' }}>
                <Typography style={{ font: 'roboto', fontWeight: 500, fontSize: '18px', marginLeft: 0, marginTop: 35, marginRight: 420, marginBottom: 3 }}>
                    Attendee Details
                </Typography>
                <div style={{ display: 'flex', flexDirection: 'column' }}>
                    {Array.from({ length: props.quantity }).map((_, sectionIndex) => (
                        <div key={sectionIndex} style={{ background: '#F8F8F8', width: '600px', borderRadius: '8px', marginBottom: '20px', display: 'flex', alignItems: 'center', flexDirection: 'row', height: '90px' }}>
                            <TextField
                                label="Name"
                                id="outlined-size-small"
                                defaultValue=""
                                size="small"
                                style={{
                                    width: '160px',
                                    height: '20px',
                                    fontSize: '14px',
                                    marginLeft: 45,
                                    marginBottom: 15
                                }}
                                value={textFieldValues[sectionIndex]} // Set the value of the TextField
                                onChange={(e) => handleTextFieldChange(e, sectionIndex)} // Handle changes
                            />
                        </div>
                    ))}
                </div>
            </div>
            <Button variant='outlined' onClick={handleConfirm}
                sx={{
                    border: '1px solid #FF5C35',
                    borderRadius: '8px',
                    color: '#FF5C35',
                    height: 34,
                    width: 200,
                    marginLeft: 107,
                    marginTop: 1,
                    ":hover": {
                        bgcolor: "#FF5C35",
                        color: 'white',
                        BorderColor: '#FF5C35'
                    }
                }}
            >
                Confirm Details
            </Button>

            <Snackbar open={openSnackbar} autoHideDuration={4000} onClose={handleSnackbarClose}>
                <Alert onClose={handleSnackbarClose} severity={alertType} sx={{ width: '100%' }}>
                    {alertMsg}
                </Alert>
            </Snackbar>
        </form>
    )
}


export function EnterDetailsFace(props: any) {
    const [sectionImages, setSectionImages] = useState<Array<Array<File | null>>>([]);
    const [fileUploaded, setFileUploaded] = useState<boolean[]>([]);
    const [textFieldValues, setTextFieldValues] = useState<string[]>(Array.from({ length: props.quantity }, () => ''));

    //error display variables
    const [openSnackbar, setOpenSnackbar] = useState(false);
    const [alertType, setAlertType]: any = useState('info');
    const [alertMsg, setAlertMsg] = useState('');
    const handleSnackbarClose = () => {
        setOpenSnackbar(false);
    };

    const token = window.localStorage.getItem('accessToken');
    const currUserEmail: any = window.localStorage.getItem('email');
    const delay = (ms: number) => new Promise(
        resolve => setTimeout(resolve, ms)
    );

    useEffect(() => {
        // Initialize sectionImages and fileUploaded arrays based on props.quantity
        const initialImages: Array<Array<File | null>> = [];
        const initialFileUploaded: boolean[] = [];

        for (let i = 0; i < props.quantity; i++) {
            initialImages.push([null]);
            initialFileUploaded.push(false);
        }

        setSectionImages(initialImages);
        setFileUploaded(initialFileUploaded);
    }, [props.quantity]);

    const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>, sectionIndex: number) => {
        const files = event.target.files;
        //call method to check if a valid image has been given
        if (files && files.length > 0) {
            const formData = new FormData();
            const file = files[0];
            formData.append('image', file);
            //add in email for facial api to verify the token
            formData.append('email', currUserEmail);

            fetch(`${process.env.REACT_APP_FACIAL_URL}/face/image-verification`, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                },
                method: 'POST',
                body: formData
            })
                .then(async (response) => {
                    if (response.status == 200) {
                        const eventResponse = await response.json();
                        setOpenSnackbar(true);
                        setAlertType('success');
                        setAlertMsg(eventResponse.message);
                        await delay(2000);
                        if (files) {
                            const updatedImages = [...sectionImages];
                            updatedImages[sectionIndex] = Array.from(files);
                            setSectionImages(updatedImages);

                            // Mark the section as having uploaded files
                            const updatedFileUploaded = [...fileUploaded];
                            updatedFileUploaded[sectionIndex] = true;
                            setFileUploaded(updatedFileUploaded);
                        }

                    } else {
                        const eventResponse = await response.json();
                        setOpenSnackbar(true);
                        setAlertType('warning');
                        setAlertMsg(eventResponse.message);
                    }
                })
                .catch((err) => {
                    window.alert(err);
                });
        }
    };

    const handleTextFieldChange = (event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>, sectionIndex: number) => {

        const updatedTextFieldValues = [...textFieldValues];
        updatedTextFieldValues[sectionIndex] = event.target.value;
        setTextFieldValues(updatedTextFieldValues);
    };

    const handleConfirm = (event: React.FormEvent) => {
        event.preventDefault();

        // Collect images and names
        const images = sectionImages.flat().filter((file) => file !== null);
        const names = textFieldValues.filter((name) => name.trim() !== '');

        // Call the parent's function to update the state
        props.updateEnteredData(images, names);

        // Check if any TextField is empty
        if (fileUploaded.some((uploaded) => !uploaded)) {
            // Show alert message if not all sections have uploaded files
            setOpenSnackbar(true);
            setAlertType('error');
            setAlertMsg('Upload an image for each attendee!!!');
        } else if (textFieldValues.some((value) => value.trim() === '')) {
            // Show an alert message if any TextField is empty
            setOpenSnackbar(true);
            setAlertType('error');
            setAlertMsg('Please fill in all the name fields!');
        } else {
            props.handleComplete();
        }
    };

    return (
        <form onSubmit={handleConfirm}>
            <div style={{ display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center' }}>
                <Typography style={{ font: 'roboto', fontWeight: 500, fontSize: '18px', marginLeft: 0, marginTop: 35, marginRight: 420, marginBottom: 3 }}>
                    Attendee Details
                </Typography>
                <div style={{ display: 'flex', flexDirection: 'column' }}>
                    {Array.from({ length: props.quantity }).map((_, sectionIndex) => (
                        <div key={sectionIndex} style={{ background: '#F8F8F8', width: '600px', borderRadius: '8px', marginBottom: '20px', display: 'flex', alignItems: 'center', flexDirection: 'row' }}>
                            <div>
                                {sectionImages[sectionIndex] && sectionImages[sectionIndex].some((file) => file !== null) && (
                                    <Stack sx={{ width: '45px', height: '45px', borderRadius:'100%'}} direction='column' spacing={2}>
                                        {sectionImages[sectionIndex].map((file, index) => (
                                            file !== null && (
                                                <ImageListItem key={index}>
                                                    <img
                                                        src={`${URL.createObjectURL(file)}?w=575&h=250&fit=crop&auto=format`}
                                                        srcSet={`${URL.createObjectURL(file)}`}
                                                        alt={`Selected ${index + 1}`}
                                                        loading="lazy"
                                                        style={{
                                                            borderRadius:'100%',
                                                            height:'55px',
                                                            width:'55px',
                                                            marginLeft:45, 
                                                            marginTop:15, 
                                                            marginBottom:30
                                                        }}
                                                    />
                                                </ImageListItem>
                                            )
                                        ))}
                                    </Stack>
                                    )}
                                <br/>
                                <Button
                                variant="outlined"
                                component="label"
                                sx={{
                                    fontSize: '10px',
                                    border: '1px solid #FF5C35',
                                    borderRadius: '8px',
                                    color: '#FF5C35',
                                    ":hover": {
                                        bgcolor: "#FF5C35",
                                        color: 'white',
                                    },
                                    marginLeft: 3,
                                    marginTop: 2, 
                                    marginBottom:2
                                }}
                                size="small"
                            >
                                Upload Image
                                <input
                                    type="file"
                                    hidden
                                    onChange={(event) => handleFileChange(event, sectionIndex)}
                                    accept="image/*"
                                />
                            </Button>
                            </div>
                            <TextField
                                label="Name"
                                id="outlined-size-small"
                                defaultValue=""
                                size="small"
                                style={{
                                    width: '160px',
                                    height: '20px',
                                    fontSize: '14px',
                                    marginLeft: 45,
                                    marginBottom: 15
                                }}
                                value={textFieldValues[sectionIndex]} // Set the value of the TextField
                                onChange={(e) => handleTextFieldChange(e, sectionIndex)} // Handle changes
                            />
                        </div>
                    ))}
                </div>
            </div>
            <Button variant='outlined' onClick={handleConfirm}
                sx={{
                    border: '1px solid #FF5C35',
                    borderRadius: '8px',
                    color: '#FF5C35',
                    height: 34,
                    width: 200,
                    marginLeft: 107,
                    marginTop: 1,
                    ":hover": {
                        bgcolor: "#FF5C35",
                        color: 'white',
                        BorderColor: '#FF5C35'
                    }
                }}
            >
                Confirm Details
            </Button>

            <Snackbar open={openSnackbar} autoHideDuration={4000} onClose={handleSnackbarClose}>
                <Alert onClose={handleSnackbarClose} severity={alertType} sx={{ width: '100%' }}>
                    {alertMsg}
                </Alert>
            </Snackbar>
        </form>
    )
}



export function Confirmation(props: any) {

    const userId = window.localStorage.getItem('id');
    const token = window.localStorage.getItem('accessToken');

    // Access images and names from the prop
    const { names } = props.enteredData;

    useEffect(() => {
    }, []);

    //for pop up message => error , warning , info , success
    const [openSnackbar, setOpenSnackbar] = useState(false);
    const [alertType, setAlertType]: any = useState('info');
    const [alertMsg, setAlertMsg] = useState('');
    const handleSnackbarClose = () => {
        setOpenSnackbar(false);
    };

    const [isClicked, setIsClicked] = useState(false);

    const catPrice = props.sectionDetails.find((item: { sectionId: string }) => item.sectionId === props.selectedSection)?.ticketPrice;
    const itemSubtotal = catPrice * props.quantity;;
    const orderTotal = itemSubtotal + 5;

    const delay = (ms: number) => new Promise(
        resolve => setTimeout(resolve, ms)
    );

    const makeRequest = async (orderId: any) => {
        // process.env.STRIPE_PUBLISHABLE_KEY
        const stripe = await loadStripe("pk_test_51NvbWcEeFzrUZxTR9mH1Bma9Qlr1jY5j2al13GJ7OooMhKXeBv9qnNOAtBP0OsRrTIbAc7iIuQJudYaywasSzHyO004Iy2P7Aw");
        const body = {
            orderId: orderId, // Add orderId to the body
            products: [
                {
                    id: 1,
                    name: `Section ${props.selectedSection}`,
                    price: catPrice,
                    quantity: props.quantity
                },
                {
                    id: 2,
                    name: "Booking Fee",
                    price: 5,
                    quantity: 1
                },
            ]
        }
        const response = await fetch(`${process.env.REACT_APP_PAYMENT_URL}/payment/create-checkout-session`, {
            method: "Post",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(body)
        })
        const session = await response.json();
        const result = stripe?.redirectToCheckout({
            sessionId: session.id
        })
        console.log("result", result)
        const error = await (await result)?.error
        if (error) {
            console.log("error", error)
        }
    }

    const handleConfirmation = async () => {
        //create order
        if (!isClicked) {
            //Disable further clicks
            setIsClicked(true);

            //call backend to create order
            const formData = new FormData();
            if (userId !== null) {
                formData.append('userId', userId);
                formData.append('eventId', props.eventDetails.eventId);
                formData.append('sectionId', props.selectedSection);
                formData.append('ticketsToPurchase', props.quantity);
                const concatenatedNames = names.filter((name: null | undefined) => name !== null && name !== undefined).join(', ');
                formData.append('ticketHolderString', concatenatedNames);

                try {
                    const response = await fetch(`${process.env.REACT_APP_BACKEND_URL}/order`, {
                        headers: {
                            'Authorization': `Bearer ${token}`,
                        },
                        method: 'POST',
                        body: formData,
                    });

                    if (response.status === 200 || response.status === 201) {
                        // Parse the JSON response
                        const responseData = await response.json();
                        // Access the orderId from the response data
                        const orderId = responseData.data.orderId;

                        setOpenSnackbar(true);
                        setAlertType('success');
                        setAlertMsg('Order successful, your seat has been reserved while you make payment. You have 10 minutes to make payment.');
                        await delay(3000);
                        props.handleComplete();

                        // Call makeRequest after successful order creation
                        await makeRequest(orderId);
                    } else {
                        // const eventResponse = await response.json();
                        setOpenSnackbar(true);
                        setAlertType('warning');
                        setAlertMsg('Order did not go through, please try again.');
                    }
                } catch (err) {
                    console.error(err);
                    // Handle error, if needed
                } finally {
                    // Set isClicked back to false after the order creation process is complete
                    setIsClicked(false);
                }
            }
        }
    }

    return (
        <Grid style={{ display: 'flex', justifyContent: 'center', flexDirection: 'row', marginTop: 50 }}>
            <Grid item style={{ background: '#F8F8F8', height: '265px', width: '450px', borderRadius: '8px', justifyContent: 'left', display: 'flex', alignItems: 'left', marginRight: 5, flexDirection: 'column' }}>
                <Typography style={{ font: 'roboto', fontWeight: 500, fontSize: '18px', marginLeft: 25, marginTop: 24 }}>
                    Attendees
                </Typography>
                {names.map((name: string | null | undefined, index: number) => ( // Use index as a number
                    <Grid item key={index} style={{ background: '#F8F8F8', height: '50px', width: '450px', borderRadius: '8px', display: 'flex', alignItems: 'center', marginRight: 5 }}>
                        {name !== null && name !== undefined && (
                            <Typography style={{ font: 'roboto', fontWeight: 400, fontSize: '15px', marginLeft: 25, marginTop: 0 }}>
                                {name}
                            </Typography>
                        )}
                    </Grid>
                ))}
            </Grid>
            <Grid item style={{ background: '#F8F8F8', height: '210px', width: '300px', borderRadius: '8px', marginLeft: 5 }}>
                <Typography style={{ font: 'roboto', fontWeight: 500, fontSize: '18px', marginLeft: 25, marginTop: 24 }}>
                    Summary
                </Typography>
                <div style={{ display: 'flex', flexDirection: 'row' }}>
                    <Typography style={{ font: 'roboto', fontWeight: 400, fontSize: '15px', marginLeft: 25, marginTop: 0 }}>
                        Items Subtotal:
                    </Typography>
                    <Typography style={{ font: 'roboto', fontWeight: 400, fontSize: '15px', marginLeft: 120, marginTop: 0 }}>
                        ${itemSubtotal}
                    </Typography>
                </div>
                <div style={{ display: 'flex', flexDirection: 'row' }}>
                    <Typography style={{ font: 'roboto', fontWeight: 400, fontSize: '15px', marginLeft: 25, color: '#888888' }}>
                        Section {props.selectedSection}
                    </Typography>
                    <Typography style={{ font: 'roboto', fontWeight: 400, fontSize: '15px', marginLeft: 165, color: '#888888' }}>
                        x{props.quantity}
                    </Typography>
                </div>
                <div style={{ marginTop: 20, display: 'flex', flexDirection: 'row' }}>
                    <Typography style={{ font: 'roboto', fontWeight: 400, fontSize: '15px', marginLeft: 25, marginTop: 0 }}>
                        Booking Fee:
                    </Typography>
                    <Typography style={{ font: 'roboto', fontWeight: 400, fontSize: '15px', marginLeft: 120, marginTop: 0 }}>
                        $5.00
                    </Typography>
                </div>
                <div style={{ display: 'flex', flexDirection: 'row' }}>
                    <Typography style={{ font: 'roboto', fontWeight: 500, fontSize: '18px', marginLeft: 25, marginTop: 18 }}>
                        Order Total:
                    </Typography>
                    <Typography style={{ font: 'roboto', fontWeight: 500, fontSize: '18px', marginLeft: 120, marginTop: 18 }}>
                        ${orderTotal}
                    </Typography>
                </div>
                <Button variant="outlined" onClick={handleConfirmation}
                    sx={{
                        border: '1px solid #FF5C35',
                        borderRadius: '8px',
                        color: '#FF5C35',
                        height: 39.5,
                        width: 300,
                        marginLeft: 0,
                        marginTop: 4.5,
                        ":hover": {
                            bgcolor: "#FF5C35",
                            color: 'white',
                            BorderColor: '#FF5C35'
                        }
                    }}
                >
                    Confirm Order
                </Button>
            </Grid>
            <Snackbar open={openSnackbar} autoHideDuration={4000} onClose={handleSnackbarClose}>
                <Alert onClose={handleSnackbarClose} severity={alertType} sx={{ width: '100%' }}>
                    {alertMsg}
                </Alert>
            </Snackbar>
        </Grid>
    )
}

export function ConfirmationFace(props: any) {

    const userId = window.localStorage.getItem('id');
    const token = window.localStorage.getItem('accessToken');

    useEffect(() => {
    }, []);

    //for pop up message => error , warning , info , success
    const [openSnackbar, setOpenSnackbar] = useState(false);
    const [alertType, setAlertType]: any = useState('info');
    const [alertMsg, setAlertMsg] = useState('');
    const handleSnackbarClose = () => {
        setOpenSnackbar(false);
    };

    // Access images and names from the prop
    const { images, names } = props.enteredData;
    const [isClicked, setIsClicked] = useState(false);

    const makeRequest = async (orderId: any) => {
        // process.env.STRIPE_PUBLISHABLE_KEY
        const stripe = await loadStripe("pk_test_51NvbWcEeFzrUZxTR9mH1Bma9Qlr1jY5j2al13GJ7OooMhKXeBv9qnNOAtBP0OsRrTIbAc7iIuQJudYaywasSzHyO004Iy2P7Aw");
        const body = {
            orderId: orderId, // Add orderId to the body
            enteredData: props.enteredData,
            products: [
                {
                    id: 1,
                    name: `Section ${props.selectedSection}`,
                    price: catPrice,
                    quantity: props.quantity
                },
                {
                    id: 2,
                    name: "Booking Fee",
                    price: 5,
                    quantity: 1
                },
            ]
        }
        const response = await fetch(`${process.env.REACT_APP_PAYMENT_URL}/payment/create-checkout-session`, {
            method: "Post",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(body)
        })
        const session = await response.json();
        const result = stripe?.redirectToCheckout({
            sessionId: session.id
        })
        console.log("result", result)
        const error = await (await result)?.error
        if (error) {
            console.log("error", error)
        }
    }

    const catPrice = props.sectionDetails.find((item: { sectionId: string }) => item.sectionId === props.selectedSection)?.ticketPrice;
    const itemSubtotal = catPrice * props.quantity;
    const orderTotal = itemSubtotal + 5;

    const delay = (ms: number) => new Promise(
        resolve => setTimeout(resolve, ms)
    );

    const handleConfirmation = () => {

        //create order
        if (!isClicked) {
            //Disable further clicks
            setIsClicked(true);

            //call backend to create order
            const formData = new FormData();
            if (userId !== null) {
                formData.append('userId', userId);
                formData.append('eventId', props.eventDetails.eventId);
                formData.append('sectionId', props.selectedSection);
                formData.append('ticketsToPurchase', props.quantity);
                const concatenatedNames = names.filter((name: null | undefined) => name !== null && name !== undefined).join(', ');
                formData.append('ticketHolderString', concatenatedNames);

                fetch(`${process.env.REACT_APP_BACKEND_URL}/order`, {
                    headers: {
                        'Authorization': `Bearer ${token}`,
                    },
                    method: 'POST',
                    body: formData
                })
                    .then(async (response) => {
                        if (response.status == 200 || response.status == 201) {
                            // Parse the JSON response
                            const responseData = await response.json();
                            // Access the orderId from the response data
                            const orderId = responseData.data.orderId;

                            setOpenSnackbar(true);
                            setAlertType('success');
                            setAlertMsg('Order successful, your seat has been reserved while you make payment. You have 10 minutes to make payment.');
                            await delay(3000);
                            props.handleComplete();

                            // Call makeRequest after successful order creation
                            await makeRequest(orderId);

                        } else {
                            const eventResponse = await response.json();
                            setOpenSnackbar(true);
                            setAlertType('warning');
                            setAlertMsg('Order did not go through, please try again.');
                            //if transaction faile, enable clickable
                            setIsClicked(false);
                        }
                    })
                    .catch((err) => {
                        //if transaction faile, enable clickable
                        window.alert(err);
                    })
                    .finally(() => {
                        // Set isClicked back to false after the order creation process is complete
                        setIsClicked(false);
                    })
            }
        }
    }

    return (
        <Grid>
            <Typography style={{ font: 'roboto', fontWeight: 500, fontSize: '18px', marginLeft:380, marginTop:40 }}>
                Attendee Details
            </Typography>
            <Grid style={{ display: 'flex', justifyContent: 'center', flexDirection: 'row', marginTop: 6 }}>
                <Grid item style={{ justifyContent: 'left', display: 'flex', alignItems: 'left', marginRight: 0, flexDirection: 'column' }}>
                    {images.map((image: Blob | MediaSource, index: number) => ( // Use index as a number
                    <Grid item key={index} style={{ background: '#F8F8F8', width: '450px', borderRadius: '8px', justifyContent: 'left', display: 'flex', marginBottom: 10 }}>
                        <div style={{ width: '70px', height: '70px', borderRadius: '100%', overflow: 'hidden', margin: 10 }}>
                        <img
                            src={URL.createObjectURL(image)}
                            alt={`Image ${index}`}
                            style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                        />
                        </div>
                        {names[index] !== null && names[index] !== undefined && (
                        <Typography style={{ font: 'roboto', fontWeight: 500, fontSize: '18px', marginLeft: 10, alignItems: 'center', marginTop: 28 }}>
                            {names[index]}
                        </Typography>
                        )}
                    </Grid>
                    ))}
                </Grid>
            <Grid item style={{ background: '#F8F8F8', height: '210px', width: '300px', borderRadius: '8px', marginLeft: 5 }}>
                <Typography style={{ font: 'roboto', fontWeight: 500, fontSize: '18px', marginLeft: 25, marginTop: 24 }}>
                    Summary
                </Typography>
                <div style={{ display: 'flex', flexDirection: 'row' }}>
                    <Typography style={{ font: 'roboto', fontWeight: 400, fontSize: '15px', marginLeft: 25, marginTop: 0 }}>
                        Items Subtotal:
                    </Typography>
                    <Typography style={{ font: 'roboto', fontWeight: 400, fontSize: '15px', marginLeft: 120, marginTop: 0 }}>
                        ${itemSubtotal}
                    </Typography>
                </div>
                <div style={{ display: 'flex', flexDirection: 'row' }}>
                    <Typography style={{ font: 'roboto', fontWeight: 400, fontSize: '15px', marginLeft: 25, color: '#888888' }}>
                        Section {props.selectedSection}
                    </Typography>
                    <Typography style={{ font: 'roboto', fontWeight: 400, fontSize: '15px', marginLeft: 165, color: '#888888' }}>
                        x{props.quantity}
                    </Typography>
                </div>
                <div style={{ marginTop: 20, display: 'flex', flexDirection: 'row' }}>
                    <Typography style={{ font: 'roboto', fontWeight: 400, fontSize: '15px', marginLeft: 25, marginTop: 0 }}>
                        Booking Fee:
                    </Typography>
                    <Typography style={{ font: 'roboto', fontWeight: 400, fontSize: '15px', marginLeft: 120, marginTop: 0 }}>
                        $5.00
                    </Typography>
                </div>
                <div style={{ display: 'flex', flexDirection: 'row' }}>
                    <Typography style={{ font: 'roboto', fontWeight: 500, fontSize: '18px', marginLeft: 25, marginTop: 18 }}>
                        Order Total:
                    </Typography>
                    <Typography style={{ font: 'roboto', fontWeight: 500, fontSize: '18px', marginLeft: 120, marginTop: 18 }}>
                        ${orderTotal}
                    </Typography>
                </div>
                <Button variant="outlined" onClick={handleConfirmation}
                    sx={{
                        border: '1px solid #FF5C35',
                        borderRadius: '8px',
                        color: '#FF5C35',
                        height: 39.5,
                        width: 300,
                        marginLeft: 0,
                        marginTop: 4.5,
                        ":hover": {
                            bgcolor: "#FF5C35",
                            color: 'white',
                            BorderColor: '#FF5C35'
                        }
                    }}
                >
                    Confirm Order
                </Button>
            </Grid>
            <Snackbar open={openSnackbar} autoHideDuration={4000} onClose={handleSnackbarClose}>
                <Alert onClose={handleSnackbarClose} severity={alertType} sx={{ width: '100%' }}>
                    {alertMsg}
                </Alert>
            </Snackbar>
        </Grid>
        </Grid>
    )
}

export function Payment(props: any) {
    return (
        <div></div>
    )
}