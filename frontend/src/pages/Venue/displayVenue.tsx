import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import Tabs from '@mui/material/Tabs';
import Tab from '@mui/material/Tab';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';
import { Button, Grid, InputAdornment, TextField } from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import CardMedia from '@mui/material/CardMedia';
import { CardActionArea } from '@mui/material';
import { format } from 'date-fns';
import GroupsIcon from '@mui/icons-material/Groups';

interface venueInfo {
    venue: {
        venueId: number;
        venueName: string;
        venueLocation: string;
        venueImage: string
    }
}

function formatDate(dateString: string): string {
    const date = new Date(dateString);
    return format(date, "dd MMMM yyyy, h:mm a");
}


export default function displayVenue(props: venueInfo) {

    return (
        <Card sx={{ minHeight: 200, mt: 3, maxHeight: 200, backgroundColor: '#ffffff', borderRadius: '5px', backgroundImage: `url('${process.env.REACT_APP_S3_URL}/venue_image/${props.venue.venueImage}')`, backgroundSize: '480px', backgroundRepeat: 'no-repeat', backgroundPositionY: 'center' }} >
            <CardActionArea href={`/EventDetails/${props.venue.venueId}`}>
                <Grid container>
                    <Grid item xs={4} height={200}>
                        <Box style={{ backgroundImage: 'linear-gradient(to right, rgba(0,0,0,0), rgba(0,0,0,1)', minHeight: 200 }}>
                        </Box>
                    </Grid>
                    <Grid item xs={8} height={200}>
                        <Box style={{ background: '#000000', minHeight: 200 }} paddingTop={1} paddingLeft={8}>
                            <Typography color='white' variant='h4' fontWeight='500'>
                                {props.venue.venueName}
                            </Typography>
                            <Typography color='white' marginTop={-1} variant='subtitle2'>
                                {props.venue.venueLocation}
                            </Typography>
                            <Typography color='white'
                                sx={{
                                    overflow: "hidden",
                                    textOverflow: "ellipsis",
                                    display: "-webkit-box",
                                    WebkitLineClamp: "5",
                                    WebkitBoxOrient: "vertical",
                                }}>
                                    {/* place holder */}
                                Lorem, ipsum dolor sit amet consectetur adipisicing elit. Quod at quidem et sit unde, asperiores quasi adipisci esse magnam consectetur soluta? Nobis debitis labore distinctio rerum dolor minima recusandae aut! Lorem ipsum dolor sit amet consectetur adipisicing elit. Rerum officia quibusdam magni officiis quasi? Ratione quos possimus quo, praesentium officiis animi inventore! Enim obcaecati fugit nesciunt provident sed quas explicabo.  Lorem ipsum dolor sit amet consectetur adipisicing elit. Dolore amet expedita eum sapiente quam molestiae vero quas in sed laboriosam. A similique neque exercitationem vitae quae accusantium inventore? Eveniet, natus! Lorem ipsum dolor sit amet consectetur adipisicing elit. Aut voluptas nostrum quo optio delectus dolore, cumque rem dignissimos voluptate autem tempora? Sit dolorem minima quas fugiat blanditiis maxime itaque. Pariatur?
                            </Typography>
                        </Box>
                    </Grid>
                </Grid>
            </CardActionArea>
        </Card >
    )
}