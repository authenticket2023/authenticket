import {
    Box, Modal, Button, TextField, Avatar, Typography, Grid, TextareaAutosize, ImageList, ImageListItem, FormControl, InputLabel, Select, MenuItem, OutlinedInput, Checkbox, ListItemText, InputAdornment, FormGroup, Switch, FormControlLabel, SelectChangeEvent
} from '@mui/material';
import React, { useEffect, useState } from 'react';
import { SGStad } from '../utility/SeatMap';

export function SelectSeats(props: any) {

    const [quantity, setQuantity] = React.useState('');

    const handleChange = (event: SelectChangeEvent) => {
        setQuantity(event.target.value as string);
    };

    // console.log(props.eventDetails.venue.venueId)
    
    return (
        <div style={{display:'flex', justifyContent:'center', flexDirection:'column', alignItems:'center'}}>
            <SGStad id={props.eventDetails.venue.venueId} />
            <div style={{background:'#F8F8F8', height:'90px', width:'300px', borderRadius:'8px', alignContent:'left', marginLeft:650, marginTop:-460}}>
                <Typography style={{font:'roboto', fontWeight:500, fontSize:'18px', marginLeft:25, marginTop:18}}>
                    Ticket Quantity
                </Typography>
                <Box sx={{ minWidth: 120 }}>
                    <FormControl fullWidth>
                        <InputLabel id="demo-simple-select-label">Quantity</InputLabel>
                        <Select
                        labelId="demo-simple-select-label"
                        id="demo-simple-select"
                        value={quantity}
                        label="Quantity"
                        onChange={handleChange}
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
        </div>
    )
}

export function EnterDetails(props: any) {
    return (
        <div></div>
    )
}

export function Confirmation(props: any) {
    return (
        <div></div>
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