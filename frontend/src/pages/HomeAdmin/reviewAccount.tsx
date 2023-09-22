import {
    Box, Typography, Modal,
    Grid, Button, Avatar, TextareaAutosize
} from '@mui/material';
import React, { useEffect } from 'react';
import { Sheet } from '@mui/joy';

const style = {
    position: 'absolute' as 'absolute',
    top: '25%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    width: 400,
    bgcolor: 'background.paper',
    border: '2px solid #000',
    overflow: 'scroll',
    boxShadow: 24,
    p: 4,
};

export default function ReviewAccount(props: any) {
    const token = window.localStorage.getItem('accessToken');
    const adminID : any = window.localStorage.getItem('id');
    const [accountID, setAccountID] = React.useState(props.accountID);
    const [organiserDetail, setOrganiserDetail]: any = React.useState();
    const [loaded, setLoaded]: any = React.useState(false);
    const [reviewOpen, setReviewOpen] = React.useState(true);
    const [remarks, setRemarks]: any = React.useState(null);
    const handleReviewEventModalClose = () => {
        setReviewOpen(false);
        //to update parent element
        props.open(false);
    }

    const loadOrganiserDetailByID = async () => {
        // //calling backend API
        fetch(`${process.env.REACT_APP_BACKEND_DEV_URL}/event-organiser/${accountID}`, {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
            method: 'GET',
        })
            .then(async (response) => {
                if (response.status == 200) {
                    const apiResponse = await response.json();
                    const data = apiResponse.data;
                    setRemarks(data.reviewRemarks);
                    setOrganiserDetail(data);
                    setLoaded(true);
                } else {
                    //passing to parent component
                    //show alert msg
                    props.setOpenSnackbar(true);
                    props.setAlertType('error');
                    props.setAlertMsg(`Fetch data failed, code: ${response.status}`);
                }
            })
            .catch((err) => {
                window.alert(err);
            });
    }

    const updateOrganiserStatus = async (status: string) => {
        const formData = new FormData();
        formData.append('organiserId', accountID);
        formData.append('reviewRemarks', remarks);
        formData.append('reviewStatus', 'approved');
        formData.append('reviewedBy', adminID);
        if (status == 'approved') {
            formData.append('enabled', 'true');
        } else {
            formData.append('enabled', 'false');
        }
        fetch(`${process.env.REACT_APP_BACKEND_DEV_URL}/admin/updateEventOrganiser`, {
            headers: {
                'Authorization': `Bearer ${token}`,
            },
            method: 'PUT',
            body: formData
        })
            .then(async (response) => {
                const apiResponse = await response.json();
                if (response.status == 200) {
                    props.setOpenSnackbar(true);
                    props.setAlertType('success');
                    props.setAlertMsg(`Approved Organiser Account ID :${accountID} successfully!`);
                    setReviewOpen(false);
                    //to update parent element
                    props.open(false);
                    props.setReload(true);
                } else {
                    props.setOpenSnackbar(true);
                    props.setAlertType('error');
                    props.setAlertMsg(`Something wrong`);
                }
            })
            .catch((err) => {
                //close the modal
                window.alert(err);
            });
    }

    const handleRemarks = (event: any) => {
        setRemarks(event.target.value);
    };

    const handleAccept = (event: any) => {
        event.preventDefault();
        updateOrganiserStatus('approved');
    }

    const handleReject = (event: any) => {
        event.preventDefault();
        if (remarks == null || remarks == '') {
            props.setOpenSnackbar(true);
            props.setAlertType('error');
            props.setAlertMsg(`Please enter the reason for rejection!`);
        } else {
            updateOrganiserStatus('rejected');
        }
    }

    useEffect(() => {
        if (!loaded) {
            loadOrganiserDetailByID();
        }
    }, []);


    return (
        <div>
            {loaded ?

                <Modal
                    open={reviewOpen}
                    onClose={handleReviewEventModalClose}

                    sx={{
                        position: 'absolute',
                        overflow: 'scroll',
                        height: '100%',
                        display: 'box',
                    }}
                >
                    <Box sx={{ ...style, width: 800, mt: '15%', mb: '15%', height: 800 }} textAlign='center'>
                        <Sheet>
                            <Avatar
                                src={`https://authenticket.s3.ap-southeast-1.amazonaws.com/event_organiser_profile/${organiserDetail.logoImage}`}
                                style={{
                                    width: "100px",
                                    height: "100px",
                                    margin: "auto",
                                }}
                            />
                            <Grid container spacing={3} sx={{ textAlign: "left", mt:5 }}>

                                <Grid item xs={12} md={6}>
                                    <Typography variant="h6">
                                        Organiser Info
                                    </Typography>
                                    <Typography>Name: {organiserDetail.name}</Typography>
                                    <Typography>Email: {organiserDetail.email}</Typography>
                                </Grid>
                                <Grid item xs={12} sm={6}>
                                    <Typography variant="h6">Description</Typography>
                                    <Typography>{organiserDetail.description}</Typography>
                                </Grid>

                                <Grid item xs={12} sm={12}>
                                    <TextareaAutosize minRows="7" onChange={handleRemarks}
                                        style={{ width: "100%", fontSize: "inherit", font: "inherit", border: "1px solid light-grey", borderRadius: 4 }}
                                        id='remarks' className='StyledTextarea' value={remarks} placeholder="Remarks" />
                                </Grid>

                            </Grid>
                            <Sheet sx={{ alignItems: "center", mb: 5, mt: 5 }}>
                                <Button color="error" variant="contained" onClick={handleReject} >Reject</Button>
                                <Button color="success" variant="contained" sx={{ ml: 10 }} onClick={handleAccept}>Accpet</Button>
                            </Sheet>
                        </Sheet>
                    </Box>
                </Modal>
                : null}

        </div>
    )
}