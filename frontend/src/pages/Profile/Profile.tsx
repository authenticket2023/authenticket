import React, { useEffect, useState } from "react";
import { Navigate } from "react-router-dom";
import { NavbarNotLoggedIn, NavbarLoggedIn } from "../../Navbar";
import EditOutlinedIcon from "@mui/icons-material/EditOutlined";
import MoreHorizIcon from '@mui/icons-material/MoreHoriz';
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
import { format } from 'date-fns';

function formatDate(dateString: string): string {
  const date = new Date(dateString);
  return format(date, "dd MMMM yyyy");
}

export const Profile = () => {
  
  const [openSnackbar, setOpenSnackbar] = useState(false);
  const [alertType, setAlertType]: any = useState("info");
  const [alertMsg, setAlertMsg] = useState("");
  const handleSnackbarClose = () => {
    setOpenSnackbar(false);
  };
  
  const token = window.localStorage.getItem("accessToken");
  const id = window.localStorage.getItem("id");
  const [loaded, setLoaded]: any = useState(false);
  const [order, setOrders]: any = useState([]);
  
  const loadOrders = async () => {
    //call backend API
    fetch(`${process.env.REACT_APP_BACKEND_URL}/order/user/${id}`, {
      headers: {
        "Content-Type": "application/json",
        'Authorization': `Bearer ${token}`,
      },
      method: "GET",
    })
    .then(async (response) => {
      if (response.status == 200) {
        const apiResponse = await response.json();
        const data = apiResponse.data;
        const orderArr = data.map((order: any) => ({
          orderId: order.orderId,
          eventId: order.eventId,
          eventName: order.eventName,
          eventDate: order.eventDate,
          venueName: order.venueName,
          orderAmount: order.orderAmount,
          purchaseDate: order.purchaseDate,
          orderStatus: order.orderStatus,
          ticketSet: order.ticketSet.map((ticket: any) => ({
            ticketId: ticket.ticketId,
            eventId: ticket.eventId,
            catId: ticket.catId,
            sectionId: ticket.sectionId,
            rowNo: ticket.rowNo,
            seatNo: ticket.seatNo,
            ticketHolder: ticket.ticketHolder ,
            orderId: ticket.orderId
          }))
        }));
        setOrders(orderArr);
        setLoaded(true);
        console.log(orderArr);
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

      useEffect(() => {
        if (loaded == false){
          loadOrders();
        }
      }, []);
      
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
                    alt={name.toUpperCase() || ''}
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
                    <Typography color={"grey"}>Email: <u>{email}</u></Typography>
                    <Typography color={"grey"}>Birthday: {formatDate(bday)}</Typography>
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
          <Grid container rowSpacing={5} columnSpacing={5} alignContent={'flex-start'}>
            {order.filter((item: any) => {
                  // Check if the event name contains the search input
                  const success = item.orderStatus.toLowerCase().includes('success');
                  return success;
                }).map((orderInfo: any, index: number) => (
              <React.Fragment key={index}>
                <Grid item xs={6}>
                  <DisplayOrder order={orderInfo} />
                </Grid>
              </React.Fragment>
            ))}
            {
              order.filter((item: any) => {
                const success = item.orderStatus.toLowerCase().includes('success');
                return success;
              }).length % 2 == 1 ? 
              <Grid item xs={6}>
                <Box sx = {{display: 'flex', flexDirection: 'column', height: 390, justifyContent:'center', alignItems: 'center'}}>
                  <Typography variant = 'h3' sx = {{fontWeight: 'bold'}}>  </Typography>
                  <MoreHorizIcon  sx={{fontSize: 80, color: 'grey'}}></MoreHorizIcon>
                </Box>
              </Grid>
              : null
            }
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
