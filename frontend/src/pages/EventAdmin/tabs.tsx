import React, { useEffect, useState } from 'react';
import {
    Box, Typography, LinearProgress, Modal, Avatar, Grid, TextField, FormControlLabel,
    Checkbox, FormControl, InputLabel, MenuItem, Select,
    Switch, FormGroup, Button, ListItemText, OutlinedInput, Snackbar, Alert, IconButton, InputAdornment,
    Dialog, DialogActions, DialogTitle, DialogContentText, DialogContent,
} from '@mui/material';
import dayjs, { Dayjs } from 'dayjs';
import { TextareaAutosize } from '@mui/base/TextareaAutosize';
import { useNavigate } from 'react-router-dom';
import MUIDataTable from "mui-datatables";
import { Sheet } from '@mui/joy';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';

const delay = (ms: number) => new Promise(
    resolve => setTimeout(resolve, ms)
);

export function PendingEventTab() {
    useEffect(() => {
        async function loadData() {
            setLoadingModal(true);
            await delay(250);
            loadPendingEventData();
        }
        if (!dataLoaded) {
            loadData();
        }
    }, []);
    const token = window.localStorage.getItem('accessToken');
    //for reload
    const [reload, setReload] = useState(false);
    //for alert
    //error , warning , info , success
    const [openSnackbar, setOpenSnackbar] = useState(false);
    const [alertType, setAlertType]: any = useState('info');
    const [alertMsg, setAlertMsg] = useState('');
    const handleSnackbarClose = () => {
        setOpenSnackbar(false);
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
    const [openLoadingModal, setLoadingModal] = React.useState(false);

    //for datatable
    const columns = ["Event ID", "Name",
        {
            name: "Description",
            options: {
                customBodyRender: (value: string) => <span>{value.length < 15 ? value : value.slice(0, 15) + '....'}</span>
            }
        },
        {
            name: "Event Date",
            options: {
                customBodyRender: (value: any) => <span>{new Date(value).toLocaleString('en-us', { year: "numeric", month: "short", day: "numeric", hourCycle: "h24", hour: "numeric", minute: "numeric" })}</span>
            }
        },
        {
            name: "Ticket Sale Date",
            options: {
                customBodyRender: (value: any) => <span>{new Date(value).toLocaleString('en-us', { year: "numeric", month: "short", day: "numeric", hourCycle: "h24", hour: "numeric", minute: "numeric" })}</span>
            }
        },
        {
            name: "Submitted At",
            options: {
                customBodyRender: (value: any) => <span>{new Date(value).toLocaleString('en-us', { year: "numeric", month: "short", day: "numeric", hourCycle: "h24", hour: "numeric", minute: "numeric" })}</span>
            }
        },
    ];
    const [pendingEventData, setPendingEventData]: any[] = useState([]);
    const [dataLoaded, setDataLoaded] = useState(false);
    const loadPendingEventData = async () => {
        // //calling backend API
        fetch(`${process.env.REACT_APP_BACKEND_DEV_URL}/event/review-status/pending`, {
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
                    setAlertMsg(`Fetch data faile, code: ${response.status}`);
                } else {
                    const apiResponse = await response.json();
                    const data = apiResponse.data;
                    const fetchData: any = [];
                    data.forEach((event: any) => {
                        const row = [event.eventId, event.eventName,event.eventDescription,
                        event.eventDate, event.ticketSaleDate, event.createdAt]
                        fetchData.push(row)
                    });
                    setPendingEventData(fetchData);
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


    //for deletion
    const [openConfirmDialog, setOpenConfirmDialog] = useState(false);
    const [selectedRows, setSelectedRows] = useState([]);

    const onRowsSelect = (curRowSelected: any, allRowsSelected: any) => {
        try {
            setSelectedRows(allRowsSelected);
        } catch (error) {
            window.alert(`Error during selecting post:${error}`);
        }
    }

    const handleClickDeleteIcon = () => {
        setOpenConfirmDialog(true);
    };

    const handleCloseConfirmDialog = () => {
        setOpenConfirmDialog(false);
    };
    const handleDelete = () => {
        // Implement  delete logic

        // After the deletion is successful, close the dialog
        handleCloseConfirmDialog();
    };

    //to customise mui datatable
    const options: any = {
        print: true,
        download: true,
        rowHover: true,
        onRowsSelect: onRowsSelect,
        onRowsDelete: handleClickDeleteIcon,
        downloadOptions: { filename: `AuthenTicket Pending Event Data(${new Date().toDateString()}).csv` },
        sortOrder: {
            name: 'Submitted At',
            direction: 'desc'
        }
    };

    return (
        <Box
            sx={{
                justifyContent: 'center',
                alignItems: 'center',
                width: "100%",
                overflow: "auto",
                boxShadow: "5",
                marginBottom: 5
            }}>
            {dataLoaded ?

                <MUIDataTable
                    title={"Pening Event Lists"}
                    data={pendingEventData}
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
            <Dialog open={openConfirmDialog} onClose={handleCloseConfirmDialog}>
                <DialogTitle>Confirm Delete</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Are you sure you want to delete?
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseConfirmDialog}>Cancel</Button>
                    <Button onClick={handleDelete} color="secondary" autoFocus>
                        Delete
                    </Button>
                </DialogActions>
            </Dialog>
            <Snackbar open={openSnackbar} autoHideDuration={3000} onClose={handleSnackbarClose}>
                <Alert onClose={handleSnackbarClose} severity={alertType} sx={{ width: '100%' }}>
                    {alertMsg}
                </Alert>
            </Snackbar>
        </Box>
    )
}

export function AllEventTab() {

    return (
        <Box
            sx={{
                justifyContent: 'center',
                alignItems: 'center',
                width: "100%",
                overflow: "auto",
                boxShadow: "5",
                marginBottom: 5
            }}>
            <Typography>Pending</Typography>
        </Box>
    )
}


