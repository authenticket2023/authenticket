import React, { useEffect, useState } from 'react';
import {
    Box, Typography, LinearProgress, Modal, Avatar, Grid, TextField, FormControlLabel,
    Checkbox, FormControl, InputLabel, MenuItem, Select,
    Switch, FormGroup, Button, ListItemText, OutlinedInput, Snackbar, Alert, IconButton, InputAdornment,
    Dialog, DialogActions, DialogTitle, DialogContentText, DialogContent,
} from '@mui/material';
import MUIDataTable from "mui-datatables";
import ReviewEvent from './reviewPendingEvent';

export function PendingEventTab() {
    const token = window.localStorage.getItem('accessToken');
    //for reload
    const [reload, setReload] = useState(false);
    useEffect(() => {
        function loadData() {
            setLoadingModal(true);
            loadPendingEventData();
        }
        if (!dataLoaded) {
            loadData();
        }
        //trigger whenever reload value changed
        if (reload) {
            //reload whenever openSnackbar was changed
            loadData()
            //need set this, if not cannot click open again
            setReviewOpen(false);
            setReload(false);
        }
    }, [reload]);
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
        fetch(`${process.env.REACT_APP_BACKEND_URL}/admin/event/review-status/pending`, {
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
                    if (data != null) {
                        data.forEach((event: any) => {
                            const row = [event.eventId, event.eventName, event.eventDescription,
                            event.eventDate, event.ticketSaleDate, event.createdAt]
                            fetchData.push(row)
                        });
                    }
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

    //For review pending event page
    const [reviewOpen, setReviewOpen] = React.useState(false);
    const [reviewEventID, setReviewEventID] = React.useState('');
    const handleRowClick = (rowData: any, rowMeta: any) => {
        setReviewEventID(rowData[0]);
        setReviewOpen(true);
    };


    //for deletion
    const [openConfirmDialog, setOpenConfirmDialog] = useState(false);
    const [selectedRows, setSelectedRows] = useState('');

    const onRowsSelect = (curRowSelected: any, allRowsSelected: any) => {
        try {
            console.log(allRowsSelected);
            let selectedEventID = "";
            allRowsSelected.forEach((element: any) => {
                const dataIndex = element.dataIndex;
                const eventID = pendingEventData[dataIndex][0];
                selectedEventID += eventID + ',';
            });
            setSelectedRows(selectedEventID);
        } catch (error) {
            window.alert(`Error during selecting:${error}`);
        }
    }

    const handleClickDeleteIcon = () => {
        setOpenConfirmDialog(true);
    };

    const handleCloseConfirmDialog = () => {
        setOpenConfirmDialog(false);
    };
    const handleDelete = () => {
        const formData = new FormData;
        formData.append('eventId', selectedRows);
        // Implement  delete logic
        fetch(`${process.env.REACT_APP_BACKEND_URL}/event/delete`, {
            headers: {
                'Authorization': `Bearer ${token}`,
            },
            method: 'PUT',
            body: formData,
        })
            .then(async (response) => {
                if (response.status != 200) {
                    const apiResponse = await response.json();
                    //show alert msg
                    setOpenSnackbar(true);
                    setAlertType('error');
                    setAlertMsg(apiResponse['message']);
                } else {
                    const apiResponse = await response.json();
                    //show alert msg
                    setOpenSnackbar(true);
                    setAlertType('success');
                    setAlertMsg(apiResponse['message']);
                    setReload(true);
                }
            })
            .catch((error) => {
                // Handle any error that occurred during the update process
                window.alert(`Error during deleting pending event:${error}`);
            });
        // After the deletion is successful, close the dialog
        handleCloseConfirmDialog();
    };

    //to customise mui datatable
    const options: any = {
        print: true,
        download: true,
        rowHover: true,
        onRowsSelect: onRowsSelect,
        onRowClick: handleRowClick,
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
                    title={"Pending Event Lists"}
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

            {reviewOpen ?
                <ReviewEvent open={setReviewOpen} setReload={setReload} setOpenSnackbar={setOpenSnackbar} setAlertType={setAlertType} setAlertMsg={setAlertMsg} eventID={reviewEventID} />
                : null}
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
            <Snackbar open={openSnackbar} autoHideDuration={4000} onClose={handleSnackbarClose}>
                <Alert onClose={handleSnackbarClose} severity={alertType} sx={{ width: '100%' }}>
                    {alertMsg}
                </Alert>
            </Snackbar>
        </Box>
    )
}

export function AllEventTab() {
    const token = window.localStorage.getItem('accessToken');
    //for reload
    const [reload, setReload] = useState(false);
    useEffect(() => {
        function loadData() {
            setLoadingModal(true);
            loadAllEventData();
        }
        if (!dataLoaded) {
            loadData();
        }
        //trigger whenever reload value changed
        if (reload) {
            //reload whenever openSnackbar was changed
            loadData()
            //need set this, if not cannot click open again
            setReviewOpen(false);
            setReload(false);
        }
    }, [reload]);
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
        "Organiser Email",
        {
            name: "Review Remarks",
            options: {
                customBodyRender: (value: string) => {
                    return (
                        <span>
                            {value === "" ? "NO REMARKS YET" : value.length < 15 ? value : value.slice(0, 15) + '....'}
                        </span>
                    )
                }
            }
        },
        {
            name: "Review Status",
            options: {
                customBodyRender: (value: string) => {
                    const getColor = (value: string) => {
                        if (value === "rejected") {
                            return 'red';
                        } else if (value === "pending") {
                            return 'orange';
                        } else {
                            return 'green';
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
        "Reviewd By",
        {
            name: "Deleted At",
            options: {
                customBodyRender: (value: any) => {
                    const getColor = (value: string) => {
                        if (value === null) {
                            return 'black';
                        } else {
                            return 'red';
                        }
                    };
                    return (

                        <Typography style={{ color: getColor(value) }}>
                            {
                                value == null ? "-" :
                                    new Date(value).toLocaleString('en-us', { year: "numeric", month: "short", day: "numeric", hourCycle: "h24", hour: "numeric", minute: "numeric" })
                            }
                        </Typography>
                    )
                }
            }
        },
    ];
    const [allEventData, setAllEventData]: any[] = useState([]);
    const [dataLoaded, setDataLoaded] = useState(false);
    const loadAllEventData = async () => {
        // //calling backend API
        fetch(`${process.env.REACT_APP_BACKEND_URL}/event`, {
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
                        let reviewedByEmail = event.reviewedBy !== null ? event.reviewedBy.email : "";
                        let reviewRemarks = event.reviewRemarks !== null ? event.reviewRemarks : "";
                        const row = [event.eventId, event.eventName, event.eventDescription,
                        event.eventDate, event.ticketSaleDate,
                        event.organiserEmail,
                            reviewRemarks, event.reviewStatus,
                            reviewedByEmail, event.deletedAt]
                        fetchData.push(row)
                    });
                    setAllEventData(fetchData);
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

    //For review pending event page
    const [reviewOpen, setReviewOpen] = React.useState(false);
    const [reviewEventID, setReviewEventID] = React.useState('');
    const handleRowClick = (rowData: any, rowMeta: any) => {
        setReviewEventID(rowData[0]);
        setReviewOpen(true);
    };


    //for deletion
    const [openConfirmDialog, setOpenConfirmDialog] = useState(false);
    const [selectedRows, setSelectedRows] = useState('');

    const onRowsSelect = (curRowSelected: any, allRowsSelected: any) => {
        try {
            console.log(allRowsSelected);
            let selectedEventID = "";
            allRowsSelected.forEach((element: any) => {
                const dataIndex = element.dataIndex;
                const eventID = allEventData[dataIndex][0];
                selectedEventID += eventID + ',';
            });
            setSelectedRows(selectedEventID);
        } catch (error) {
            window.alert(`Error during selecting:${error}`);
        }
    }

    const handleClickDeleteIcon = () => {
        setOpenConfirmDialog(true);
    };

    const handleCloseConfirmDialog = () => {
        setOpenConfirmDialog(false);
    };
    const handleDelete = () => {
        const formData = new FormData;
        formData.append('eventId', selectedRows);
        // Implement  delete logic
        fetch(`${process.env.REACT_APP_BACKEND_URL}/event/delete`, {
            headers: {
                'Authorization': `Bearer ${token}`,
            },
            method: 'PUT',
            body: formData,
        })
            .then(async (response) => {
                if (response.status != 200) {
                    const apiResponse = await response.json();
                    //show alert msg
                    setOpenSnackbar(true);
                    setAlertType('error');
                    setAlertMsg(apiResponse['message']);
                } else {
                    const apiResponse = await response.json();
                    //show alert msg
                    setOpenSnackbar(true);
                    setAlertType('success');
                    setAlertMsg(apiResponse['message']);
                    setReload(true);
                }
            })
            .catch((error) => {
                // Handle any error that occurred during the update process
                window.alert(`Error during deleting pending event:${error}`);
            });
        // After the deletion is successful, close the dialog
        handleCloseConfirmDialog();
    };

    //to customise mui datatable
    const options: any = {
        print: true,
        download: true,
        rowHover: true,
        onRowsSelect: onRowsSelect,
        onRowClick: handleRowClick,
        onRowsDelete: handleClickDeleteIcon,
        downloadOptions: { filename: `AuthenTicket All Event Data(${new Date().toDateString()}).csv` },
        sortOrder: {
            name: 'Event ID',
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
                    title={"Event Lists"}
                    data={allEventData}
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

            {reviewOpen ?
                <ReviewEvent open={setReviewOpen} setReload={setReload} setOpenSnackbar={setOpenSnackbar} setAlertType={setAlertType} setAlertMsg={setAlertMsg} eventID={reviewEventID} />
                : null}
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
            <Snackbar open={openSnackbar} autoHideDuration={4000} onClose={handleSnackbarClose}>
                <Alert onClose={handleSnackbarClose} severity={alertType} sx={{ width: '100%' }}>
                    {alertMsg}
                </Alert>
            </Snackbar>
        </Box>
    )
}


