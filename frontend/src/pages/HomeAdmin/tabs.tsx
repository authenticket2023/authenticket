import React, { useEffect, useState } from 'react';
import {
    Box, Typography, LinearProgress, Modal, Avatar, Grid, TextField, FormControlLabel,
    Checkbox, FormControl, InputLabel, MenuItem, Select,
    Switch, FormGroup, Button, ListItemText, OutlinedInput, Snackbar, Alert, IconButton, InputAdornment,
    Dialog, DialogActions, DialogTitle, DialogContentText, DialogContent,
} from '@mui/material';
import MUIDataTable from "mui-datatables";
import ReviewEvent from './reviewAccount';

export function PendingTab() {
    const token = window.localStorage.getItem('accessToken');
    //for reload
    const [reload, setReload] = useState(false);
    useEffect(() => {
        function loadData() {
            setLoadingModal(true);
            loadPendingData();
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
    const columns = ["Organiser ID", "Name", "Email",
        {
            name: "Description",
            options: {
                customBodyRender: (value: string) => <Typography>{value.length < 15 ? value : value.slice(0, 15) + '....'}</Typography>
            }
        },
        {
            name: "Created At",
            options: {
                customBodyRender: (value: any) => <Typography>{new Date(value).toLocaleString('en-us', { year: "numeric", month: "short", day: "numeric", hourCycle: "h24", hour: "numeric", minute: "numeric" })}</Typography>
            }
        },
    ];
    const [pendingData, setPendingData]: any[] = useState([]);
    const [dataLoaded, setDataLoaded] = useState(false);
    const loadPendingData = async () => {
        //calling backend API
        fetch(`${process.env.REACT_APP_BACKEND_DEV_URL}/event-organiser/pending`, {
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
                    data.forEach((organiser: any) => {
                        const row = [organiser.organiserId, organiser.name, organiser.email,
                        organiser.description, organiser.createdAt];
                        fetchData.push(row);
                    });
                    setPendingData(fetchData);
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

    //For review pending organiser page
    const [reviewOpen, setReviewOpen] = React.useState(false);
    const [reviewAccountID, setReviewAccountID] = React.useState('');
    const handleRowClick = (rowData: any, rowMeta: any) => {
        setReviewAccountID(rowData[0]);
        setReviewOpen(true);
    };


    //for deletion
    const [openConfirmDialog, setOpenConfirmDialog] = useState(false);
    const [selectedRows, setSelectedRows] = useState('');

    const onRowsSelect = (curRowSelected: any, allRowsSelected: any) => {
        try {
            let selectedOrganiserID = "";
            allRowsSelected.forEach((element: any) => {
                const dataIndex = element.dataIndex;
                const organiserID = pendingData[dataIndex][0];
                selectedOrganiserID += organiserID + ',';
            });
            setSelectedRows(selectedOrganiserID);
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
    //TODO: change to organiser account deletion
    const handleDelete = (id: any) => {
        const formData = new FormData;
        formData.append('organiserID', selectedRows);
        fetch(`${process.env.REACT_APP_BACKEND_DEV_URL}/event-organiser/delete`, {
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
        downloadOptions: { filename: `AuthenTicket Pending Organiser Account Data(${new Date().toDateString()}).csv` },
        sortOrder: {
            name: 'Created At',
            direction: 'asc'
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
                    title={"Pending Organiser Account Lists"}
                    data={pendingData}
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
                <ReviewEvent open={setReviewOpen} setReload={setReload} setOpenSnackbar={setOpenSnackbar} setAlertType={setAlertType} setAlertMsg={setAlertMsg} accountID={reviewAccountID} />
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

export function AllTab() {
    const token = window.localStorage.getItem('accessToken');
    //for reload
    const [reload, setReload] = useState(false);
    useEffect(() => {
        function loadData() {
            setLoadingModal(true);
            loadAllData();
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
    const columns = [
        {
            name: "Account ID",
            options: {
                customBodyRender: (value: any) => <Typography>{value}</Typography>
            }
        },
        {
            name: "Role",
            options: {
                customBodyRender: (value: any) => <Typography>{value}</Typography>
            }
        },
        {
            name: "Name",
            options: {
                customBodyRender: (value: any) => <Typography>{value}</Typography>
            }
        },
        {
            name: "Email",
            options: {
                customBodyRender: (value: any) => <Typography>{value}</Typography>
            }
        },
        {
            name: "Date of Birth",
            options: {
                customBodyRender: (value: any) => {
                    const dob = new Date(value).toLocaleString('en-us', { year: "numeric", month: "short", day: "numeric" });
                    return (
                        <Typography>
                            {dob === "Invalid Date" ? "NA" : dob}
                        </Typography>
                    )
                }
            }
        },
        {
            name: "Description",
            options: {
                customBodyRender: (value: string) => <Typography>{value.length < 15 ? value : value.slice(0, 15) + '....'}</Typography>
            }
        },
        {
            name: "Created At",
            options: {
                customBodyRender: (value: any) => <Typography>{new Date(value).toLocaleString('en-us', { year: "numeric", month: "short", day: "numeric", hourCycle: "h24", hour: "numeric", minute: "numeric" })}</Typography>
            }
        },
        {
            name: "Review Remarks",
            options: {
                customBodyRender: (value: string) => {
                    return (
                        <Typography>
                            {value === "" ? "NO REMARKS YET" : value.length < 15 ? value : value.slice(0, 15) + '....'}
                        </Typography>
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
        {
            name: "Reviewd By",
            options: {
                customBodyRender: (value: any) => <Typography>{value === "" ? "NOT REVIEW YET" : value}</Typography>
            }
        },
    ];
    const [allData, setAllData]: any[] = useState([]);
    const [dataLoaded, setDataLoaded] = useState(false);

    const loadAllData = async () => {
        // //calling backend API
        fetch(`${process.env.REACT_APP_BACKEND_DEV_URL}/event-organiser`, {
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
                    data.forEach((organiser: any) => {
                        //check if the vaule if null, if yes, set to ""
                        let reviewedByEmail = organiser.reviewedBy !== null ? organiser.reviewedBy.email : "";
                        let reviewRemarks = organiser.reviewRemarks !== null ? organiser.reviewRemarks : "";

                        const row = [organiser.organiserId, organiser.role, organiser.name, organiser.email, '-',
                        organiser.description, organiser.createdAt,
                            reviewRemarks, organiser.reviewStatus,
                            reviewedByEmail]
                        fetchData.push(row)
                    });
                    setAllData((old: any) => [...old, ...fetchData]);
                    loadUserData();
                    loadAdminData();
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

    const loadUserData = async () => {
        // //calling backend API
        fetch(`${process.env.REACT_APP_BACKEND_DEV_URL}/user`, {
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
                    data.forEach((user: any) => {

                        const row = [user.userId, user.role, user.name, user.email, user.dateOfBirth,
                            '-', user.createdAt,
                            '-', '-', '-']
                        fetchData.push(row)
                    });
                    setAllData((old: any) => [...old, ...fetchData]);
                }
            })
            .catch((err) => {
                //close the modal
                setLoadingModal(false);
                window.alert(err);
            });
    }

    const loadAdminData = async () => {
        // //calling backend API
        fetch(`${process.env.REACT_APP_BACKEND_DEV_URL}/admin`, {
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
                    const data = await response.json();
                    const fetchData: any = [];
                    data.forEach((admin: any) => {
                        const row = [admin.adminId, admin.role, admin.name, admin.email, '-',
                            '-', admin.createdAt,
                            '-', '-', '-']
                        fetchData.push(row)
                    });
                    setAllData((old: any) => [...old, ...fetchData]);
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


    //TODO: for deletion
    const [openConfirmDialog, setOpenConfirmDialog] = useState(false);
    const [selectedRows, setSelectedRows] = useState('');

    const onRowsSelect = (curRowSelected: any, allRowsSelected: any) => {
        try {
            console.log(allRowsSelected);
            let selectedEventID = "";
            allRowsSelected.forEach((element: any) => {
                const dataIndex = element.dataIndex;
                const eventID = allData[dataIndex][0];
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
    //TODO: change to account deletion
    const handleDelete = () => {
        const formData = new FormData;
        formData.append('eventId', selectedRows);
        // Implement  delete logic
        fetch(`${process.env.REACT_APP_BACKEND_DEV_URL}/event/delete`, {
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
                window.alert(`Error during deleting :${error}`);
            });
        // After the deletion is successful, close the dialog
        handleCloseConfirmDialog();
    };

    //to customise mui datatable
    const options: any = {
        print: true,
        download: true,
        rowHover: true,
        selectableRows: false,
        onRowsSelect: onRowsSelect,
        //for now disable onclick
        // onRowClick: handleRowClick,
        onRowsDelete: handleClickDeleteIcon,
        downloadOptions: { filename: `AuthenTicket All Account Data(${new Date().toDateString()}).csv` },
        sortOrder: {
            name: 'Role',
            direction: 'asc'
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
                    title={"Account Lists"}
                    data={allData}
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


