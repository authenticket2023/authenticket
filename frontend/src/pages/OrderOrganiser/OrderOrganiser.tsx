import { Navigate } from 'react-router-dom';
import { NavbarOrganiser } from '../../Navbar';
import { useEffect, useState } from 'react';
import MUIDataTable from "mui-datatables";
import { Alert, Box, FormControl, Grid, InputLabel, LinearProgress, MenuItem, Modal, Select, Snackbar, Typography } from '@mui/material';

export const OrderOrganiser = () => {
    //To login information
    const role: any = window.localStorage.getItem('role');
    const token = window.localStorage.getItem('accessToken');
    const organiserId: any = window.localStorage.getItem('id')
    const organiserName = window.localStorage.getItem('username');

    //for pop up message => error , warning , info , success
    const [openSnackbar, setOpenSnackbar] = useState(false);
    const [alertType, setAlertType]: any = useState('info');
    const [alertMsg, setAlertMsg] = useState('');
    const handleSnackbarClose = () => {
        setOpenSnackbar(false);
    };

    //To fetch data from DB for the drop down list
    const [fetched, setFetched]: any = useState(false);
    const [eventList, setEventList]: any = useState([]);
    const [eventID, setEventID]: any = useState(null);
    const eventFetcher = async () => {
        try {
            const response = await fetch(`${process.env.REACT_APP_BACKEND_URL}/event-organiser/events/${organiserId}`, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
                method: 'GET'
            });
            if (response.status !== 200) {
                //show alert msg
                setOpenSnackbar(true);
                setAlertType('error');
                setAlertMsg("error fetching data!!!");
            } else {
                const data = await response.json();
                const sortedArray = data['data'].sort((a: any, b: any) => b.eventId - a.eventId);

                setEventList(sortedArray);
                setEventID(sortedArray[0].eventId);
                setFetched(true);
                loadOrdersByEventID(sortedArray[0].eventId);
            }
        } catch (err) {
            window.alert(err);
        }
    };

    const handleEvent = (event: any) => {
        setEventID(event.target.value);
    };

    //for modal
    const style = {
        position: 'absolute' as 'absolute',
        top: '50%',
        left: '50%',
        transform: 'translate(-50%, -50%)',
        width: 400,
        bgcolor: 'background.paper',
        border: '2px solid #000',
        boxShadow: 24,
        p: 4,
    };

    const [openLoadingModal, setLoadingModal] = useState(false);

    //for datatable
    const columns = [
        {
            name: "Order ID",
            options: {
                customBodyRender: (value: string) => <Typography>{value}</Typography>
            }
        },
        {
            name: "Amount",
            options: {
                customBodyRender: (value: string) => <Typography>${value}</Typography>
            }
        },
        {
            name: "Purchase Date",
            options: {
                customBodyRender: (value: any) => <Typography>{new Date(value).toLocaleString('en-us', { year: "numeric", month: "short", day: "numeric",})}</Typography>
            }
        },
        {
            name: "Number Of Tickets",
            options: {
                customBodyRender: (value: any) => <Typography>{value == 0 ? '-': value}</Typography>
            }
        },
        {
            name: "Buyer Email",
            options: {
                customBodyRender: (value: any) => <Typography>{value}</Typography>
            }
        },
        {
            name: "Order Status",
            options: {
                customBodyRender: (value: string) => {
                    const getColor = (value: string) => {
                        if (value.toLowerCase() === "cancelled") {
                            return 'red';
                        } else if (value.toLowerCase() === "success") {
                            return 'green';
                        } else {
                            return 'orange';
                        }
                    };
                    return (
                        <Typography style={{ color: getColor(value) }}>
                            {value.toUpperCase()}
                        </Typography>
                    )
                }
            }
        },
    ];
    const [orderDataByEventID, setOrderDataByEventID]: any[] = useState([]);
    const [dataLoaded, setDataLoaded] = useState(false);

    const loadOrdersByEventID = async (eventID: any) => {
        // //calling backend API
        fetch(`${process.env.REACT_APP_BACKEND_URL}/order/event/${eventID}?page=0&size=100`, {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
            method: 'GET',
        })
            .then(async (response) => {
                if (response.status != 200) {
                    //close the modal
                    setLoadingModal(false);
                    //show alert msg
                    setOpenSnackbar(true);
                    setAlertType('error');
                    setAlertMsg(`Fetch data failed, code: ${response.status}`);
                } else {
                    const apiResponse = await response.json();
                    const data = apiResponse.data;
                    const fetchData: any = [];
                    data.forEach((order: any) => {
                        const row = [order.orderId, order.orderAmount, order.purchaseDate, order.ticketSet.length, order.purchaser.email, order.orderStatus]
                        fetchData.push(row)
                    });
                    setOrderDataByEventID(fetchData);
                    setDataLoaded(true);
                    //close the modal
                    setLoadingModal(false);
                }
            })
            .catch((err) => {
                //close the modal
                setLoadingModal(false);
                window.alert(err);
            });
    }

    //to customise mui datatable
    const options: any = {
        print: true,
        download: true,
        rowHover: true,
        selectableRows: false,
        downloadOptions: { filename: `${organiserName} - Event ID ${eventID} Ticket Order Data(${new Date().toDateString()}).csv` },
        sortOrder: {
            name: 'Order Status',
            direction: 'desc'
        }
    };

    useEffect(() => {
        //fetch event for the organiser
        if (!fetched) {
            eventFetcher();
        } else {
            loadOrdersByEventID(eventID);
        }
    }, [eventID]);

    return (
        <>
            {
                token != null && role == 'ORGANISER' ?
                    <Navigate to="/OrderOrganiser" /> : <Navigate to="/Forbidden" />
            }
            < NavbarOrganiser />
            <Grid container spacing={3} sx={{ mt: 3, ml: 3, }}>
                <Grid item xs={12}>
                    <Box sx={{ minWidth: 120, maxWidth: 400 }}>
                        <FormControl fullWidth>
                            <InputLabel shrink={Boolean(eventID)}>Event</InputLabel>
                            <Select
                                value={eventID}
                                label="Event"
                                onChange={handleEvent}
                                required
                            >
                                {eventList.map((event: any) => (
                                    <MenuItem key={event.eventId} value={event.eventId}>{event.eventName} ({event.eventId})</MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                    </Box>
                </Grid>
            </Grid>
            <Box sx={{ mt: 3, ml: 6, mr: 6 }}>
                {dataLoaded ?
                    <MUIDataTable
                        title={`${organiserName} - Orders`}
                        data={orderDataByEventID}
                        columns={columns}
                        options={options}
                    /> :

                    <Modal
                        keepMounted
                        open={openLoadingModal}
                    >
                        <Box sx={style}>
                            <Typography id="loading" variant="h6" component="h2">
                                Loading data, please wait.
                            </Typography>
                            <LinearProgress />
                        </Box>
                    </Modal>
                }
            </Box>
            {/* success / error feedback */}
            <Snackbar open={openSnackbar} autoHideDuration={2000} onClose={handleSnackbarClose}>
                <Alert onClose={handleSnackbarClose} severity={alertType} sx={{ width: '100%' }}>
                    {alertMsg}
                </Alert>
            </Snackbar>

        </>

    );
}

