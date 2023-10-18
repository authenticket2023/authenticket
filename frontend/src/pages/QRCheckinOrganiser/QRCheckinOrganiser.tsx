import { NavbarOrganiser } from '../../Navbar';
import { Navigate } from 'react-router-dom';
import { Alert, Box, Button, FormControl, Grid, InputLabel, MenuItem, Select, Snackbar, Typography, CircularProgress } from '@mui/material';
import { useState } from 'react';
import QrReader from "react-qr-reader";
import './index.css';

export const QRCheckinOrganiser = (): JSX.Element => {
    const token = window.localStorage.getItem('accessToken');
    const role = window.localStorage.getItem('role');

    //for pop up message => error , warning , info , success
    const [openSnackbar, setOpenSnackbar] = useState(false);
    const [alertType, setAlertType]: any = useState('info');
    const [alertMsg, setAlertMsg] = useState('');
    const handleSnackbarClose = () => {
        setOpenSnackbar(false);
    };

    const [selected, setSelected]: any = useState("environment");
    const [startScan, setStartScan] = useState(false);
    const [QRData, setQRData] = useState("");

    const verifyQR = async (QRData : any) => {
        // //calling backend API
        fetch(`${process.env.REACT_APP_BACKEND_URL}/event/valid-qr?token=${QRData}`, {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
            method: 'GET',
        })
            .then(async (response) => {
                if (response.status != 200) {
                    //show alert msg
                    setOpenSnackbar(true);
                    setAlertType('error');
                    setAlertMsg(`Fetch data failed, code: ${response.status}`);
                } else {
                    const apiResponse = await response.json();
                    const data = apiResponse.data;
                    setOpenSnackbar(true);
                    setAlertType('success');
                    setAlertMsg(`Welcome! QR Code Verified.`);
                }
            })
            .catch((err) => {
                window.alert(err);
            });
    }

    const handleScan = async (scanData: any) => {
        if (scanData && scanData !== "") {
            setQRData(scanData);
            //call BE to verify the QR code
            verifyQR(scanData);
        }
    };

    const handleError = (err: any) => {
        console.error(err);
    };
    return (
        <Box>
            {
                token != null && role == 'ORGANISER' ?
                    <Navigate to="/QRCheckinOrganiser" /> : <Navigate to="/Forbidden" />
            }
            < NavbarOrganiser />

            <Grid container spacing={2}>

                <Grid item xs={12} sx={{ mt: 5 }} >
                    <Typography variant="h4" textAlign="center">
                        Last Scan: {QRData}
                    </Typography>
                </Grid>

                <Grid item xs={12} sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                    <Button
                        variant="contained"
                        onClick={() => {
                            setStartScan(!startScan);
                        }}
                        sx={{ height: 55, mr: 2 }}
                    >
                        {startScan ? "Stop Scan" : "Start Scan"}
                    </Button>
                    <Select
                        value={selected}
                        onChange={(e) => {
                            setSelected(e.target.value);
                        }}
                        variant="outlined"
                        autoWidth
                    >
                        <MenuItem value={"environment"}>Back Camera</MenuItem>
                        <MenuItem value={"user"}>Front Camera</MenuItem>
                    </Select>
                </Grid>

                {startScan && (
                    <div className="ocrloader">
                        <p>Scanning</p>
                        <em></em>
                        <span></span>
                        <Grid item xs={12} sx={{ mt: 2, display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                            <QrReader
                                facingMode={selected}
                                delay={1500}
                                onError={handleError}
                                onScan={handleScan}
                                style={{ width: '500px' }}
                            />
                        </Grid>
                    </div>
                )}
            </Grid>

            {/* success / error feedback */}
            <Snackbar open={openSnackbar} autoHideDuration={2000} onClose={handleSnackbarClose}>
                <Alert onClose={handleSnackbarClose} severity={alertType} sx={{ width: '100%' }}>
                    {alertMsg}
                </Alert>
            </Snackbar>
        </Box>

    )
};