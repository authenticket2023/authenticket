import React, { useEffect, useState } from "react";
import { Navigate } from "react-router-dom";
import { NavbarNotLoggedIn, NavbarLoggedIn } from "../../Navbar";
import {
  Alert,
  Avatar,
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Grid,
  IconButton,
  Snackbar,
  TextField,
  Typography,
  Card,
} from "@mui/material";

export const Profile = () => {
  const token = window.localStorage.getItem("accessToken");
  useEffect(() => {}, []);

  let profileImage: any = window.localStorage.getItem("profileImage");
  const profileImageSrc = `${process.env.REACT_APP_S3_URL}/user_profile_images/${profileImage}`;
  
  let email: any = window.localStorage.getItem("email");
  let bday: any = window.localStorage.getItem("dob");
  let name: any = window.localStorage.getItem("username");

  return (
    <div>
      {token != null ? <NavbarLoggedIn /> : <NavbarNotLoggedIn />}

      <Box padding={15} paddingTop={10}>
        <Box>
          <Typography marginBottom={1} sx={{ fontWeight: "bold" }}>
            {" "}
            Profile Page{" "}
          </Typography>
          <Card variant = 'outlined' elevation={0} sx= {{borderColor: 'grey'}}>
            <Box padding={3} paddingTop={5} paddingBottom={5}>
              <Grid container>
                <Grid item>
                  <Avatar
                    alt="Brian Lim"
                    src={profileImageSrc}
                    sx={{ width: 75, height: 75 }}
                  />
                </Grid>
                <Grid item>
                  <Box marginLeft={5}>
                    <Typography variant='h5' sx={{fontWeight: 'bold', textTransform: 'capitalize'}}>
                      {name}
                    </Typography>
                    <Typography color={'grey'}>
                      Email: {email}
                    </Typography>
                    <Typography color={'grey'}>
                      Birthday: {bday}
                    </Typography>
                  </Box>
                </Grid>
                <Grid item>
                  
                </Grid>
              </Grid>
            </Box>
          </Card>
        </Box>
      </Box>
    </div>
  );
};
