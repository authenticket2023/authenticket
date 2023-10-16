import React, { useEffect, useState } from 'react';
import {
    Box, Typography, LinearProgress, Modal, Avatar, Grid, TextField, FormControlLabel,
    Checkbox, FormControl, InputLabel, MenuItem, Select,
    Switch, FormGroup, Button, ListItemText, OutlinedInput, Snackbar, Alert, IconButton, InputAdornment,
    Dialog, DialogActions, DialogTitle, DialogContentText, DialogContent,
} from '@mui/material';
import MUIDataTable from "mui-datatables";
import UpdateArtist from './updateArtist';
import CreateArtist from './createArtist';

export function AllArtist() {

    const token = window.localStorage.getItem('accessToken');

    //for reload
    const [reload, setReload] = useState(false);
    useEffect(() => {
        function loadData() {
            setLoadingModal(true);
            loadAllArtistData();
        }
        if (!dataLoaded) {
            loadData();
        }
        //trigger whenever reload value changed
        if (reload) {
            //reload whenever openSnackbar was changed
            loadData()
            //need set this, if not cannot click open again
            setUpdateOpen(false);
            setCreateOpen(false);
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
    const columns = ["Artist ID", "Name", "Image",
        // {
        //     name: "Event Date",
        //     options: {
        //         customBodyRender: (value: any) => <span>{new Date(value).toLocaleString('en-us', { year: "numeric", month: "short", day: "numeric", hourCycle: "h24", hour: "numeric", minute: "numeric" })}</span>
        //     }
        // },
        // {
        //     name: "Ticket Sale Date",
        //     options: {
        //         customBodyRender: (value: any) => <span>{new Date(value).toLocaleString('en-us', { year: "numeric", month: "short", day: "numeric", hourCycle: "h24", hour: "numeric", minute: "numeric" })}</span>
        //     }
        // },
        // {
        //     name: "Deleted At",
        //     options: {
        //         customBodyRender: (value: any) => {
        //             const getColor = (value: string) => {
        //                 if (value === null) {
        //                     return 'black';
        //                 } else {
        //                     return 'red';
        //                 }
        //             };
        //             return (

        //                 <Typography style={{ color: getColor(value) }}>
        //                     {
        //                         value == null ? "-" :
        //                             new Date(value).toLocaleString('en-us', { year: "numeric", month: "short", day: "numeric", hourCycle: "h24", hour: "numeric", minute: "numeric" })
        //                     }
        //                 </Typography>
        //             )
        //         }
        //     }
        // },
    ];
    const [allArtistData, setAllArtistData]: any[] = useState([]);
    const [dataLoaded, setDataLoaded] = useState(false);

    const loadAllArtistData = async () => {
        // //calling backend API
        fetch(`${process.env.REACT_APP_BACKEND_URL}/artist`, {
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
                    data.forEach((artist: any) => {
                        const row = [artist.artistId, artist.artistName, artist.artistImage]
                        fetchData.push(row)
                    });
                    setAllArtistData(fetchData);
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

    //For update  artist page
    const [updateOpen, setUpdateOpen] = React.useState(false);
    const [updateArtistID, setUpdateArtistID] = React.useState('');
    const handleRowClick = (rowData: any, rowMeta: any) => {
        setUpdateArtistID(rowData[0]);
        setUpdateOpen(true);
    };

    //For create artist page
    const [createOpen, setCreateOpen] = React.useState(false);
    const handleCreateButton = () => {
        setCreateOpen(true);
    }

    // for deletion
    const [openConfirmDialog, setOpenConfirmDialog] = useState(false);
    const [selectedRows, setSelectedRows] = useState<string[]>([]);

    const onRowsSelect = (currentRowsSelected: number[], allRowsSelected: any[]) => {
        try {
        const selectedArtistIDs: string[] = allRowsSelected.map((selectedRow) => {
            const dataIndex: number = selectedRow.dataIndex;
            return allArtistData[dataIndex][0];
        });

        setSelectedRows(selectedArtistIDs);
        } catch (error) {
        window.alert(`Error during selecting: ${error}`);
        }
    }

    const handleClickDeleteIcon = () => {
        setOpenConfirmDialog(true);
    };

    const handleCloseConfirmDialog = () => {
        setOpenConfirmDialog(false);
    };

    const handleDelete = () => {
        if (selectedRows.length === 0) {
        // No rows selected, handle this case as needed
        return;
        }

        selectedRows.forEach((artistId) => {
        fetch(`${process.env.REACT_APP_BACKEND_URL}/artist/${artistId}`, {
            headers: {
            'Authorization': `Bearer ${token}`,
            },
            method: 'PUT',
        })
            .then(async (response) => {
            if (response.status !== 200) {
                const apiResponse = await response.json();
                // Handle error and show alert message
            } else {
                const apiResponse = await response.json();
                // Handle success and show alert message
            }
            })
            .catch((error) => {
            // Handle any error that occurred during the deletion process
            window.alert(`Error during deleting artist: ${error}`);
            });
        });

        handleCloseConfirmDialog();
    };

    //to customise mui datatable
    const options: any = {
        print: true,
        download: true,
        rowHover: true,
        selectableRows: true,
        onRowsSelect: onRowsSelect,
        onRowClick: handleRowClick,
        onRowsDelete: handleClickDeleteIcon,
        downloadOptions: { filename: `Artist Data(${new Date().toDateString()}).csv` },
        sortOrder: {
            name: 'Artist ID',
            direction: 'Image'
        }
    };

    return (
        <Grid>
            <Grid sx={{display:'flex', justifyContent:'right', marginBottom:2, marginRight:5}}>
            <Button variant="contained" sx={{backgroundColor:'#FF5C35'}} onClick={handleCreateButton}>
                Add Artist
            </Button>
            </Grid>

            {/* createArtist data popup */}
            {createOpen ?
                <CreateArtist open={setCreateOpen} setReload={setReload} setOpenSnackbar={setOpenSnackbar} setAlertType={setAlertType} setAlertMsg={setAlertMsg} />
                : null}

        <Box 
            sx={{
            justifyContent: 'center',
            alignItems: 'center',
            width: "100%",
            overflow: "auto",
            boxShadow: "5",
        }}>

            {/* display datatable */}
            {dataLoaded ?

            <MUIDataTable
                title={`All Artists`}
                data={allArtistData}
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

            {/* update data popup */}
            {updateOpen ?
                <UpdateArtist open={setUpdateOpen} setReload={setReload} setOpenSnackbar={setOpenSnackbar} setAlertType={setAlertType} setAlertMsg={setAlertMsg} artistID={updateArtistID} />
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
        </Grid>
    )
}