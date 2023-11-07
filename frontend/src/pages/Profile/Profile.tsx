import React, { useEffect, useState } from "react";
import { Navigate } from "react-router-dom";
import { NavbarNotLoggedIn, NavbarLoggedIn } from "../../Navbar";
import EditOutlinedIcon from "@mui/icons-material/EditOutlined";
import MoreHorizIcon from "@mui/icons-material/MoreHoriz";
import {
  Avatar,
  Box,
  Grid,
  Typography,
  Card,
  Alert,
  Snackbar,
  CircularProgress,
} from "@mui/material";
import DisplayOrder from "./displayOrders";
import { format } from "date-fns";
import { Footer } from "../../Footer";

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

  const [loading, setLoading]: any = useState(true)

  const loadOrders = async () => {
    //call backend API
    fetch(
      `${process.env.REACT_APP_BACKEND_URL}/order/user/${id}?page=0&size=6`,
      {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        method: "GET",
      }
    )
      .then(async (response) => {
        if (response.status == 200) {
          const apiResponse = await response.json();
          const data = apiResponse.data;
          setOrders(data);
          setLoaded(true);
          if (data == null) {
            setHasMoreOrders(false);
            return;
          }
          if (data.length < 6) {
            setHasMoreOrders(false);
          }
          setLoading(false);
        } else {
          //display alert, for fetch fail
          setOpenSnackbar(true);
          setAlertType("error");
          setAlertMsg(
            `Oops something went wrong! Code:${response.status}; Status Text : ${response.statusText}`
          );
          setLoading(false);
        }
      })
      .catch((err) => {
        setOpenSnackbar(true);
        setAlertType("error");
        setAlertMsg(`Oops something went wrong! Error : ${err}`);
        setLoading(false);
      });
  };

  const [hasMoreOrders, setHasMoreOrders] = useState(true);
  const [currOrderPage, setCurrOrderPage] = useState(1);

  const loadMoreData = async (page: any) => {
    //call backend
    fetch(
      `${process.env.REACT_APP_BACKEND_URL}/order/user/${id}?page=${currOrderPage}&size=6`,
      {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        method: "GET",
      }
    )
      .then(async (response) => {
        if (response.status == 200) {
          const apiResponse = await response.json();
          const data = apiResponse.data;

          if (data == null) {
            setHasMoreOrders(false)
            return;
          }
          if (data.length < 6) {
            setHasMoreOrders(false);
          }
          setOrders((old: any) => [...old, ...data]);
          setCurrOrderPage(currOrderPage + 1);
        }
        setLoading(false);
      })
      .catch((err) => {
        setOpenSnackbar(true);
        setAlertType("error");
        setAlertMsg(`Oops something went wrong! Error : ${err}`);
        setLoading(false);
      });
  };

  const handleLoadMore = (e: any) => {
    setLoading(true);
    loadMoreData(currOrderPage);
  };

  let profileImage: any = window.localStorage.getItem("profileImage");
  const profileImageSrc = `${process.env.REACT_APP_S3_URL}/user_profile_images/${profileImage}`;

  let email: any = window.localStorage.getItem("email");
  let bday: any = window.localStorage.getItem("dob");
  let name: any = window.localStorage.getItem("username");

  useEffect(() => {
    if (loaded == false) {
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
                    alt={name.toUpperCase() || ""}
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
                    <Typography color={"grey"}>
                      Email: <u>{email}</u>
                    </Typography>
                    <Typography color={"grey"}>
                      Birthday: {formatDate(bday)}
                    </Typography>
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
          <Grid
            container
            rowSpacing={5}
            columnSpacing={5}
            alignContent={"flex-start"}
          >
            {order
              .map((orderInfo: any, index: number) => (
                <React.Fragment key={index}>
                  <Grid item xs={6}>
                    <DisplayOrder order={orderInfo} />
                  </Grid>
                </React.Fragment>
              ))}
          </Grid>
                {hasMoreOrders && !loading ?
          <Box
            onClick={handleLoadMore}
            sx={{
              display: "flex",
              flexDirection: "column",
              height: 200,
              justifyContent: "center",
              alignItems: "center",
              color: "grey"
            }}
          >
            <Typography variant="h5" sx={{ fontWeight: "bold"}}>
              {"Click to load more"}
            </Typography>
            <MoreHorizIcon sx={{ fontSize: 40, color: "grey" }}></MoreHorizIcon>
          </Box>
          : null}
          {loading ? 
            <Box
            sx={{
              display: "flex",
              flexDirection: "column",
              height: 200,
              justifyContent: "center",
              alignItems: "center",
              color: "grey"
            }}
          >
            {/* <Typography variant="h5" marginBottom={3} sx={{ fontWeight: "bold"}}>
              {"Loading..."}
            </Typography> */}
            <CircularProgress sx={{color: 'orange'}} />
          </Box>
          : null}
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
      <Footer/>
    </div>
  );
};
