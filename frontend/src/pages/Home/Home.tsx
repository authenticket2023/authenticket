import React, { useEffect, useState } from "react";
import { Navigate } from "react-router-dom";
import { NavbarNotLoggedIn, NavbarLoggedIn } from "../../Navbar";
import { styled, alpha } from "@mui/material/styles";
import CardMedia from '@mui/material/CardMedia'
import Card from '@mui/material/Card';
import Paper from "@mui/material/Paper";
import Typography from "@mui/material/Typography";
import Grid from "@mui/material/Grid";
import Box from "@mui/material/Box";
import SearchIcon from "@mui/icons-material/Search";
import InputBase from "@mui/material/InputBase";
import Button from "@mui/material/Button";
import Carousel from "react-multi-carousel";
import "react-multi-carousel/lib/styles.css";
import "bear-react-carousel/dist/index.css";
import BearCarousel, {
  TBearSlideItemDataList,
  BearSlideCard,
  BearSlideImage,
} from "bear-react-carousel";
import { async } from "q";
import { CardActionArea } from "@mui/material";
import { Link } from 'react-router-dom';

import backgroundImage from '../../images/backgroundImage-2.png';
import { Footer } from "../../Footer/Footer";


export const Home = () => {

  const token = window.localStorage.getItem('accessToken');
  const role = window.localStorage.getItem('role');
  const [featured, setFeatured]: any = React.useState([]);
  const [bestSellers, setBestSellers]: any = React.useState([]);
  const [recents, setRecents]: any = React.useState([]);
  const [upcoming, setUpcoming]: any = React.useState([]);
  const [loaded, setLoaded]: any = React.useState(false);
  const [bsLoaded, setBSLoaded]: any = React.useState(false);
  const [recLoaded, setRecLoaded]: any = React.useState(false);
  const [upcLoaded, setUpcLoaded]: any = React.useState(false);

  useEffect(() => {
    if (!loaded || !bsLoaded || !recLoaded) {
      loadFeatured();
    }
  }, []);

  const loadFeatured = async () => {
    // //calling backend API
    fetch(`${process.env.REACT_APP_BACKEND_URL}/public/event/featured?page=0&size=3`, {
      headers: {
        'Content-Type': 'application/json',
      },
      method: 'GET',
    })
      .then(async (response) => {
        if (response.status == 200) {
          const apiResponse = await response.json();
          const data = apiResponse.data;
          const featuredArr = data.map((featured: any) => ({
            featuredId: featured.featuredId,
            eventId: featured.event.eventId,
            eventName: featured.event.eventName,
            eventDescription: featured.event.eventDescription,
            eventImage: featured.event.eventImage,
            eventType: featured.event.eventType,
            eventDate: featured.event.eventDate,
            totalTickets: featured.event.totalTickets,
            eventLocation: featured.event.eventLocation,
            eventStartDate: featured.startDate,
            eventEndDate: featured.endDate,
          }));
          setFeatured(featuredArr);
          loadBestSellers();
          loadRecents();
          loadUpcoming();
          setLoaded(true);
          //console.log(featuredArr);
        } else {
          //passing to parent component
        }
      })
      .catch((err) => {
        window.alert(err);
      });
  }

  const loadBestSellers = async () => {
    // //calling backend API
    fetch(`${process.env.REACT_APP_BACKEND_URL}/public/event/bestseller?page=0&size=7`, {
      headers: {
        'Content-Type': 'application/json',
      },
      method: 'GET',
    })
      .then(async (response) => {
        if (response.status == 200) {
          const apiResponse = await response.json();
          const data = apiResponse.data;
          // console.log(data);
          const bsArr = data.map((bestseller: any) => ({
            eventId: bestseller.eventId,
            eventName: bestseller.eventName,
            eventDescription: bestseller.eventDescription,
            eventImage: bestseller.eventImage,
            eventType: bestseller.eventType,
            eventDate: bestseller.eventDate,
            totalTickets: bestseller.totalTickets,
            eventLocation: bestseller.eventLocation,
          }))

          setBestSellers(bsArr);
          setBSLoaded(true);
          //console.log(data);
          // bsArr.map((item: any) => console.log(item));
        } else {
          //passing to parent component
        }
      })
      .catch((err) => {
        window.alert(err);
      });
  }

  const loadRecents = async () => {
    // //calling backend API
    fetch(`${process.env.REACT_APP_BACKEND_URL}/public/event/recently-added`, {
      headers: {
        'Content-Type': 'application/json',
      },
      method: 'GET',
    })
      .then(async (response) => {
        if (response.status == 200) {
          const apiResponse = await response.json();
          const data = apiResponse.data;
          const bsArr = data.map((recent: any) => ({
            eventId: recent.eventId,
            eventName: recent.eventName,
            eventDescription: recent.eventDescription,
            eventImage: recent.eventImage,
            eventType: recent.eventType,
            eventDate: recent.eventDate,
            totalTickets: recent.totalTickets,
            eventLocation: recent.eventLocation,
          }))

          setRecents(bsArr);
          setRecLoaded(true);
          //console.log(data);
          //bsArr.map((item: any) => console.log(item));
        } else {
          //passing to parent component
        }
      })
      .catch((err) => {
        window.alert(err);
      });
  }

  const loadUpcoming = async () => {
    // //calling backend API
    fetch(`${process.env.REACT_APP_BACKEND_URL}/public/event/upcoming?page=0&size=10`, {
      headers: {
        'Content-Type': 'application/json',
      },
      method: 'GET',
    })
      .then(async (response) => {
        if (response.status == 200) {
          const apiResponse = await response.json();
          const data = apiResponse.data;
          const bsArr = data.map((upcm: any) => ({
            eventId: upcm.eventId,
            eventName: upcm.eventName,
            eventDescription: upcm.eventDescription,
            eventImage: upcm.eventImage,
            eventType: upcm.eventType,
            eventDate: upcm.eventDate,
            totalTickets: upcm.totalTickets,
            eventLocation: upcm.eventLocation,
          }))

          setUpcoming(bsArr);
          setUpcLoaded(true);
          //console.log(data);
          //bsArr.map((item: any) => console.log(item));
        } else {
          //passing to parent component
        }
      })
      .catch((err) => {
        window.alert(err);
      });
  }

  const CustomBanner = () => {
    const bearSlideItemData: TBearSlideItemDataList = featured.map(
      (row: any) => {
        return {
          key: row.id,
          children: (
            <Box bgcolor="#FF5C35">
              <Grid container justifyContent={"center"}>
                <Grid item xs={6} bgcolor="#FF5C35">
                  <BearSlideCard>
                    <div
                      style={{
                        position: "relative",
                        width: "100%",
                        height: "400px",
                      }}
                    >
                      <img
                        src={`https://authenticket.s3.ap-southeast-1.amazonaws.com/event_images/${row.eventImage}`}
                        style={{
                          position: "absolute",
                          top: "50%",
                          left: "50%",
                          transform: "translate(-50%, -50%)",
                          maxWidth: "100%",
                          maxHeight: "100%",
                        }}
                      />
                    </div>
                  </BearSlideCard>
                </Grid>
                <Grid item xs={5} marginLeft={4}>
                  <Box bgcolor="#FF5C35" marginTop={12}>
                    <Typography variant="h6" color="white" marginLeft={2}>
                      Featured
                    </Typography>
                    <Typography
                      variant="h4"
                      color="white"
                      sx={{ fontWeight: "bold" }}
                    >
                      {row.eventName}
                    </Typography>
                    <Typography
                      variant="subtitle2"
                      justifyItems="center"
                      color="white"
                    >
                      {row.eventDescription}
                    </Typography>
                    <Box marginTop={2} marginLeft={2}>
                      <Button
                        variant="outlined"
                         href={`/EventDetails/${row.eventId}`}
                        sx={{ color: "white", borderColor: "white" }}
                      >
                        Get tickets
                      </Button>
                    </Box>
                  </Box>
                </Grid>
              </Grid>
            </Box>
          ),
        };
      });
    return (
      <BearCarousel
        data={bearSlideItemData}
        height="400px"
        isEnableAutoPlay
        isEnableLoop

      />
    );
  };

  const StyledInputBase = styled(InputBase)(({ theme }) => ({
    color: "black",
    "& .MuiInputBase-input": {
      padding: theme.spacing(1, 1, 1, 0),
      // vertical padding + font size from searchIcon
      paddingLeft: `calc(1em + ${theme.spacing(5)})`,
    },
  }));

  function TicketItem(props: any) {
    return (
      <Box marginTop={2} marginRight={1}>
      <Link to={`/EventDetails/${props.eventId}`} style={{ textDecoration: 'none' }}>
        <Card sx={{
          minHeight: 175,
          minWidth: 400,
          maxHeight: 175,
          maxWidth: 400,
          backgroundImage: `url(https://authenticket.s3.ap-southeast-1.amazonaws.com/event_images/${props.eventImage})`,
          backgroundSize: 'contain',
        }}>
        </Card>
      </Link>
        <Typography>
          {props.eventName}
        </Typography>
      </Box>
    )
  }

  function BestSellersCarousell() {
    return <Carousel
      additionalTransfrom={0}
      arrows
      autoPlaySpeed={3000}
      centerMode={false}
      className=""
      containerClass="container"
      dotListClass=""
      draggable
      focusOnSelect={false}
      infinite
      itemClass=""
      keyBoardControl
      minimumTouchDrag={80}
      partialVisible
      pauseOnHover
      renderArrowsWhenDisabled={false}
      renderButtonGroupOutside={false}
      renderDotsOutside={false}
      responsive={{
        desktop: {
          breakpoint: {
            max: 3000,
            min: 1024,
          },
          items: 3,
          partialVisibilityGutter: 20,
        },
        mobile: {
          breakpoint: {
            max: 464,
            min: 0,
          },
          items: 1,
          partialVisibilityGutter: 30,
        },
        tablet: {
          breakpoint: {
            max: 1024,
            min: 464,
          },
          items: 2,
          partialVisibilityGutter: 30,
        },
      }}
      rewind={false}
      rewindWithAnimation={false}
      rtl={false}
      shouldResetAutoplay
      showDots={false}
      sliderClass=""
      slidesToSlide={1}
      swipeable
    >
      {bestSellers.map((bs: { eventName: any; eventImage: any; eventId: any; }) => (
        <TicketItem
          eventName={bs.eventName}
          eventImage={bs.eventImage}
          eventId={bs.eventId}
        />
      ))}
    </Carousel>;
  }

  function RecentCarousell() {
    return <Carousel
      additionalTransfrom={0}
      arrows
      autoPlaySpeed={3000}
      centerMode={false}
      className=""
      containerClass="container"
      dotListClass=""
      draggable
      focusOnSelect={false}
      infinite
      itemClass=""
      keyBoardControl
      minimumTouchDrag={80}
      partialVisible
      pauseOnHover
      renderArrowsWhenDisabled={false}
      renderButtonGroupOutside={false}
      renderDotsOutside={false}
      responsive={{
        desktop: {
          breakpoint: {
            max: 3000,
            min: 1024,
          },
          items: 3,
          partialVisibilityGutter: 20,
        },
        mobile: {
          breakpoint: {
            max: 464,
            min: 0,
          },
          items: 1,
          partialVisibilityGutter: 30,
        },
        tablet: {
          breakpoint: {
            max: 1024,
            min: 464,
          },
          items: 2,
          partialVisibilityGutter: 30,
        },
      }}
      rewind={false}
      rewindWithAnimation={false}
      rtl={false}
      shouldResetAutoplay
      showDots={false}
      sliderClass=""
      slidesToSlide={1}
      swipeable
    >
      {recents.map((bs: { eventName: any; eventImage: any; eventId: any;}) => (
        <TicketItem
          eventName={bs.eventName}
          eventImage={bs.eventImage}
          eventId={bs.eventId}
        />
      ))}
    </Carousel>;
  }

  function UpcomingCarousell() {
    return <Carousel
      additionalTransfrom={0}
      arrows
      autoPlaySpeed={3000}
      centerMode={false}
      className=""
      containerClass="container"
      dotListClass=""
      draggable
      focusOnSelect={false}
      infinite
      itemClass=""
      keyBoardControl
      minimumTouchDrag={80}
      partialVisible
      pauseOnHover
      renderArrowsWhenDisabled={false}
      renderButtonGroupOutside={false}
      renderDotsOutside={false}
      responsive={{
        desktop: {
          breakpoint: {
            max: 3000,
            min: 1024,
          },
          items: 3,
          partialVisibilityGutter: 20,
        },
        mobile: {
          breakpoint: {
            max: 464,
            min: 0,
          },
          items: 1,
          partialVisibilityGutter: 30,
        },
        tablet: {
          breakpoint: {
            max: 1024,
            min: 464,
          },
          items: 2,
          partialVisibilityGutter: 30,
        },
      }}
      rewind={false}
      rewindWithAnimation={false}
      rtl={false}
      shouldResetAutoplay
      showDots={false}
      sliderClass=""
      slidesToSlide={1}
      swipeable
    >
      {upcoming.map((bs: { eventName: any; eventImage: any; eventId: any;}) => (
        <TicketItem
          eventName={bs.eventName}
          eventImage={bs.eventImage}
          eventId={bs.eventId}
        />
      ))}
    </Carousel>;
  }




  return (
    <>
            {token != null ? <NavbarLoggedIn /> : <NavbarNotLoggedIn />}
      {loaded ?
        <Box>
          <div>
            {
                token != null && role == 'ADMIN' ?
                    <Navigate to="/HomeAdmin" /> : null
            }
            {
                token != null && role == 'ORGANISER' ?
                    <Navigate to="/HomeOrganiser" /> :  null
            }
            <Paper
              elevation={5}
              sx={{
                position: "relative",
                backgroundColor: "grey.800",
                color: "#fff",
                mb: 4,
                backgroundSize: "cover",
                backgroundRepeat: "no-repeat",
                backgroundPosition: "center",
                backgroundImage: `url(${backgroundImage})`,
                height: '250px'
              }}
            >
              <Box
                sx={{
                  position: "relative",
                  top: 0,
                  bottom: 0,
                  right: 0,
                  left: 0,
                  backgroundColor: "rgba(0,0,0,.3)",
                }}
              />
              <Grid
                container
                spacing={4}
                alignItems="center"
                justifyContent="center"
              >

                <Grid item md={6}>
                  <Box
                    sx={{
                      position: "relative",
                      p: { xs: 3, md: 6 },
                      pr: { md: 0 },
                    }}
                  >
                    <Typography
                      component="h1"
                      variant="h3"
                      color="inherit"
                      align="center"
                      style={{
                        whiteSpace:'nowrap',
                        overflow:'hidden',
                        width:'100%',
                        fontSize:'45px',
                        fontWeight:'500'
                      }}
                    >
                      Unlock Unforgettable Experiences
                    </Typography>
                    <Typography
                      component="h1"
                      variant="h6"
                      color="inherit"
                      gutterBottom
                      align="center"
                      style={{
                        fontWeight:'400'
                      }}
                    >
                      your gateway to premier event adventures
                    </Typography>
                    <br />
                    <br />
                    <br />
                  </Box>
                </Grid>
              </Grid>
            </Paper>
          </div>
          <Typography marginLeft={15} marginTop={8} sx={{ fontWeight: "bold" }}>
            Best Sellers
          </Typography>
          <Grid container>
            <Grid item xs={12}>
              <BestSellersCarousell />
            </Grid>
          </Grid>
          <Typography marginLeft={15} marginTop={8} sx={{ fontWeight: "bold" }}>
            New on AuthenTicket
          </Typography>
          <Grid container>
            <Grid item xs={12}>
              <RecentCarousell />
            </Grid>
          </Grid>
          <Box bgcolor="#FF5C35" marginTop={12} >
             <CustomBanner></CustomBanner>
          </Box>
          <Typography marginLeft={15} marginTop={8} sx={{ fontWeight: "bold" }}>
            Recently Added
          </Typography>
          <Grid container>
            <Grid item xs={12} marginBottom={8}>
              <UpcomingCarousell />
            </Grid>
          </Grid>
          <Footer/>
        </Box>
        : null}
    </>
  );
};
