import React, { useEffect, useState } from "react";
import { Navigate } from "react-router-dom";
import { NavbarNotLoggedIn } from "../../Navbar";
import { styled, alpha } from "@mui/material/styles";
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
} from "bear-react-carousel";
import { async } from "q";

export const Home = () => {
  
  const [featured, setFeatured]: any = React.useState();
  const [loaded, setLoaded]: any = React.useState(false);
  
  const loadFeatured = async () => {
    // //calling backend API
    fetch(`${process.env.REACT_APP_BACKEND_DEV_URL}/public/event/featured`, {
      headers: {
        'Content-Type': 'application/json',
      },
      method: 'GET',
    })
      .then(async (response) => {
        if (response.status == 200) {
          const apiResponse = await response.json();
          const data = apiResponse.data;
          setFeatured(data);
          setLoaded(true);
        } else {
          //passing to parent component
          //show alert msg
          // props.setOpenSnackbar(true);
          // props.setAlertType('error');
          // props.setAlertMsg(`Fetch data failed, code: ${response.status}`);
        }
      })
      .catch((err) => {
        window.alert(err);
      });
    }

  const images = [
    {
      id: 1, bg: "https://picsum.photos/400/250",
      subheader: "Featured",
      header: "Lauv: the between albums tour",
      description: "Lorem ipsum dolor sit amet consectetur adipisicing elit. Soluta\
    earum ullam repellat minus, cumque, tempore fugit officia delectus\
    omnis numquam dolor adipisci ut voluptatem dolore odit cum sunt?\
    Lorem ipsum dolor sit amet consectetur adipisicing elit.\
    Architecto maiores reprehenderit veritatis eveniet possimus ex\
    odio? Reiciendis, ducimus. Placeat recusandae nulla nisi vitae est\
    quod libero sint? Laborum, at maiores.",
      link: "#"
    },
    {
      id: 2, bg: "https://picsum.photos/400/250",
      subheader: "Featured",
      header: "Lauv: the between albums tour",
      description: "Lorem ipsum dolor sit amet consectetur adipisicing elit. Soluta\
    earum ullam repellat minus, cumque, tempore fugit officia delectus\
    omnis numquam dolor adipisci ut voluptatem dolore odit cum sunt?\
    Lorem ipsum dolor sit amet consectetur adipisicing elit.\
    Architecto maiores reprehenderit veritatis eveniet possimus ex\
    odio? Reiciendis, ducimus. Placeat recusandae nulla nisi vitae est\
    quod libero sint? Laborum, at maiores.",
      link: "#"
    },
    {
      id: 3, bg: "https://picsum.photos/400/250",
      subheader: "Featured",
      header: "Lauv: the between albums tour",
      description: "Lorem ipsum dolor sit amet consectetur adipisicing elit. Soluta\
    earum ullam repellat minus, cumque, tempore fugit officia delectus\
    omnis numquam dolor adipisci ut voluptatem dolore odit cum sunt?\
    Lorem ipsum dolor sit amet consectetur adipisicing elit.\
    Architecto maiores reprehenderit veritatis eveniet possimus ex\
    odio? Reiciendis, ducimus. Placeat recusandae nulla nisi vitae est\
    quod libero sint? Laborum, at maiores.",
      link: "#"
    },
    {
      id: 4, bg: "https://picsum.photos/400/250",
      subheader: "Featured",
      header: "Lauv: the between albums tour",
      description: "Lorem ipsum dolor sit amet consectetur adipisicing elit. Soluta\
    earum ullam repellat minus, cumque, tempore fugit officia delectus\
    omnis numquam dolor adipisci ut voluptatem dolore odit cum sunt?\
    Lorem ipsum dolor sit amet consectetur adipisicing elit.\
    Architecto maiores reprehenderit veritatis eveniet possimus ex\
    odio? Reiciendis, ducimus. Placeat recusandae nulla nisi vitae est\
    quod libero sint? Laborum, at maiores.",
    link: "#"
    },
    {
      id: 5, bg: "https://picsum.photos/400/250",
      subheader: "Featured",
      header: "Lauv: the between albums tour",
      description: "Lorem ipsum dolor sit amet consectetur adipisicing elit. Soluta\
    earum ullam repellat minus, cumque, tempore fugit officia delectus\
    omnis numquam dolor adipisci ut voluptatem dolore odit cum sunt?\
    Lorem ipsum dolor sit amet consectetur adipisicing elit.\
    Architecto maiores reprehenderit veritatis eveniet possimus ex\
    odio? Reiciendis, ducimus. Placeat recusandae nulla nisi vitae est\
    quod libero sint? Laborum, at maiores.",
      link: "#"
    },
  ];

  const CustomBanner = () => {
    const bearSlideItemData: TBearSlideItemDataList = featured.map((row: any) => {
      return {
        key: row.id,
        children: (
          <BearSlideCard bgUrl={`https://authenticket.s3.ap-southeast-1.amazonaws.com/event_images/${row.event.eventImage}`}>
            <div style={{ height: "100%", backgroundImage: row.bg }} />
          </BearSlideCard>
        ),
      };
    });
    return (
      <BearCarousel
        data={bearSlideItemData}
        height="400px"
        isEnableLoop
        isEnableAutoPlay
      />
    );
  };
  
  const TextAnimationsCarousel = () => {
    const slideItemData: TBearSlideItemDataList = featured.map((row: any) => {
      return {
        key: row.featuredId,
        children: (
          <Box bgcolor="#FF5C35" marginTop={8}>
            <Typography variant="h6" color="white" marginLeft={2}>
              Featured
            </Typography>
            <Typography variant="h4" color="white" sx={{ fontWeight: "bold" }}>
              {row.event.eventName}
            </Typography>
            <Typography variant="subtitle2" justifyItems="center" color="white">
              {row.event.eventDescription}
            </Typography>
            <Box marginTop={2} marginLeft={2}>
              <Button
                variant="outlined"
                href='#'
                sx={{ color: "white", borderColor: "white" }}
                >
                Get tickets
              </Button>
            </Box>
          </Box>
        ),
      };
    });
    return (
      <BearCarousel
        data={slideItemData}
        height="400px"
        isEnableAutoPlay
        isEnableLoop

      />
    );
  };

  const Search = styled("div")(({ theme }) => ({
    position: "relative",
    borderRadius: theme.shape.borderRadius,
    backgroundColor: alpha(theme.palette.common.white, 0.5),
    "&:hover": {
      backgroundColor: alpha(theme.palette.common.white, 0.6),
    },
    marginLeft: 0,
    width: "100%",
    [theme.breakpoints.up("sm")]: {
      marginLeft: theme.spacing(1),
      width: "100%",
    },
  }));

  const SearchIconWrapper = styled("div")(({ theme }) => ({
    padding: theme.spacing(0, 2),
    height: "100%",
    position: "absolute",
    pointerEvents: "none",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
  }));

  const StyledInputBase = styled(InputBase)(({ theme }) => ({
    color: "black",
    "& .MuiInputBase-input": {
      padding: theme.spacing(1, 1, 1, 0),
      // vertical padding + font size from searchIcon
      paddingLeft: `calc(1em + ${theme.spacing(5)})`,
    },
  }));

  function TicketCarousel() {
    return (
      <Carousel
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
        <Box>
          <Grid container>
            <Grid item>
              <img src="https://picsum.photos/400/250" />
            </Grid>
          </Grid>
        </Box>
        <Box>
          <Grid container marginRight={5}>
            <Grid item>
              <img src="https://picsum.photos/400/250" />
            </Grid>
          </Grid>
        </Box>
        <Box>
          <Grid container marginRight={5}>
            <Grid item>
              <img src="https://picsum.photos/400/250" />
            </Grid>
          </Grid>
        </Box>
        <Box>
          <Grid container marginRight={5}>
            <Grid item>
              <img src="https://picsum.photos/400/250" />
            </Grid>
          </Grid>
        </Box>
      </Carousel>
    );
  }

  useEffect(() => {
    if (!loaded) {
        loadFeatured();
    }
}, []);

  return (
    <>
      {loaded ? 
      <Box>
        <div>
          <NavbarNotLoggedIn />
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
              backgroundImage: `url(https://i.imgur.com/UKi8jbp.png)`,
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
                  <br />
                  <br />
                  <Typography
                    component="h1"
                    variant="h3"
                    color="inherit"
                    align="center"
                  >
                    Unlock Unforgettable Experiences
                  </Typography>
                  <Typography
                    component="h1"
                    variant="h6"
                    color="inherit"
                    gutterBottom
                    align="center"
                  >
                    your gateway to premier event adventures
                  </Typography>
                  <br />
                  <br />
                  <br />
                  <Search>
                    <SearchIconWrapper>
                      <SearchIcon sx={{ color: "#3b3b3b" }} />
                    </SearchIconWrapper>
                    <StyledInputBase
                      placeholder="Search…"
                      inputProps={{ "aria-label": "search" }}
                      fullWidth
                    />
                  </Search>
                  <br />
                  <br />
                  <br />
                </Box>
              </Grid>
            </Grid>
          </Paper>
        </div>
        <Typography marginLeft={10} marginTop={8} sx={{ fontWeight: "bold" }}>
          Featured Events
        </Typography>
        <Grid container>
          <Grid item xs={12}>
            <TicketCarousel></TicketCarousel>
          </Grid>
        </Grid>
        <Typography marginLeft={10} marginTop={8} sx={{ fontWeight: "bold" }}>
          New on AuthenTicket
        </Typography>
        <Grid container>
          <Grid item xs={12}>
            <TicketCarousel></TicketCarousel>
          </Grid>
        </Grid>
        <Box bgcolor="#FF5C35" marginTop={12}>
          <Grid container alignItems="center" justifyContent="center">
            <Grid item xs={5} marginTop={4} marginBottom={4}>
              <CustomBanner></CustomBanner>
            </Grid>
            <Grid item xs={5} marginLeft={4}>
              <TextAnimationsCarousel></TextAnimationsCarousel>
            </Grid>
          </Grid>
        </Box>
        <Typography marginLeft={10} marginTop={8} sx={{ fontWeight: "bold" }}>
          Recently Added
        </Typography>
        <Grid container>
          <Grid item xs={12} marginBottom={8}>
            <TicketCarousel></TicketCarousel>
          </Grid>
        </Grid>
      </Box>
        :null}
    </>
  );
};
