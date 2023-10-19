import React, { useEffect, useState } from "react";
import { Navigate } from "react-router-dom";
import { NavbarNotLoggedIn, NavbarLoggedIn } from "../../Navbar";
import EditOutlinedIcon from "@mui/icons-material/EditOutlined";
import {
  Avatar,
  Box,
  Grid,
  Typography,
  Card,
  Alert,
  Snackbar,
} from "@mui/material";
import DisplayOrder from "./displayOrders";

export const Profile = () => {
  useEffect(() => {
    if (loaded == false){
      loadOrders();
    }
  }, []);

  const [openSnackbar, setOpenSnackbar] = useState(false);
  const [alertType, setAlertType]: any = useState("info");
  const [alertMsg, setAlertMsg] = useState("");
  const handleSnackbarClose = () => {
    setOpenSnackbar(false);
  };

  const token = window.localStorage.getItem("accessToken");
  const id = window.localStorage.getItem("id");
  const [loaded, setLoaded]: any = useState(false);
  const [order, setOrders]: any = useState();

  const loadOrders = async () => {
    //call backend API
    fetch(`${process.env.REACT_APP_BACKEND_URL}/order/user/${id}`, {
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      method: "GET",
    })
      .then(async (response) => {
        if (response.status == 200) {
          const apiResponse = await response.json();
          const data = apiResponse.data;
          const orderArr = data.map((order: any) => ({
            orderId: order.orderId,
            orderAmount: order.orderAmount,
            purchaseDate: order.purchaseDate,
            orderStatus: order.orderStatus,
            ticketSet: order.ticketSet,
          }));
          console.log(orderArr);
          setOrders(orderArr);
          setLoaded(true);
        } else {
          //display alert, for fetch fail
          setOpenSnackbar(true);
          setAlertType("error");
          setAlertMsg(
            `Oops something went wrong! Code:${response.status}; Status Text : ${response.statusText}`
          );
        }
      })
      .catch((err) => {
        setOpenSnackbar(true);
        setAlertType("error");
        setAlertMsg(`Oops something went wrong! Error : ${err}`);
      });
  };

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
          <Typography
            marginLeft={4}
            marginBottom={1}
            sx={{ fontWeight: "bold" }}
          >
            {" "}
            Profile Page{" "}
          </Typography>
          <Card
            variant="outlined"
            elevation={0}
            sx={{ borderColor: "grey", borderRadius: 3 }}
          >
            <Box
              display="flex"
              justifyContent="flex-end"
              paddingRight={1}
              paddingTop={1}
            >
              <EditOutlinedIcon fontSize="small"></EditOutlinedIcon>
            </Box>
            <Box padding={3} paddingTop={2} paddingBottom={5}>
              <Grid container>
                <Grid paddingLeft={2} item>
                  <Avatar
                    alt="Brian Lim"
                    src={profileImageSrc}
                    sx={{ width: 75, height: 75 }}
                  />
                </Grid>
                <Grid item>
                  <Box marginLeft={5}>
                    <Typography
                      variant="h5"
                      sx={{ fontWeight: "bold", textTransform: "capitalize" }}
                    >
                      {name}
                    </Typography>
                    <Typography color={"grey"}>Email: {email}</Typography>
                    <Typography color={"grey"}>Birthday: {bday}</Typography>
                  </Box>
                </Grid>
              </Grid>
            </Box>
          </Card>
          <Box marginTop={5}>
            <Typography
              marginLeft={4}
              marginBottom={1}
              sx={{ fontWeight: "bold" }}
            >
              {" "}
              Your Tickets{" "}
            </Typography>
          </Box>
        </Box>
        <Box>
          <Grid container>
            {order.map((orderInfo: any, index: any) => (
              <React.Fragment key={index}>
                <Grid item xs={6}>
                  <DisplayOrder order={orderInfo} />
                </Grid>
              </React.Fragment>
            ))}
          </Grid>
        </Box>
        <Snackbar
          open={openSnackbar}
          autoHideDuration={4000}
          onClose={handleSnackbarClose}
        >
          <Alert
            onClose={handleSnackbarClose}
            severity={alertType}
            sx={{ width: "100%" }}
          >
            {alertMsg}
          </Alert>
        </Snackbar>
      </Box>
    </div>
  );
};
