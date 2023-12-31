import * as React from "react";
import AppBar from "@mui/material/AppBar";
import Box from "@mui/material/Box";
import Toolbar from "@mui/material/Toolbar";
import IconButton from "@mui/material/IconButton";
import Typography from "@mui/material/Typography";
import Menu from "@mui/material/Menu";
import MenuIcon from "@mui/icons-material/Menu";
import Container from "@mui/material/Container";
import Avatar from "@mui/material/Avatar";
import Button from "@mui/material/Button";
import Tooltip from "@mui/material/Tooltip";
import MenuItem from "@mui/material/MenuItem";
import { Link, useLocation, useNavigate } from "react-router-dom";
import logo from "../images/logo(orange).png";
import AccountCircleOutlinedIcon from "@mui/icons-material/AccountCircleOutlined";
import { Popover } from "@mui/material";
import Grid from "@mui/material/Grid";
import LinkMUI from '@mui/material/Link';
import Stack from '@mui/material/Stack';
import QrCodeIcon from '@mui/icons-material/QrCode';
import PersonIcon from '@mui/icons-material/Person';

export const NavbarNotLoggedIn = () => {
  let navigate = useNavigate();

  const { pathname } = useLocation();

  const isTabActive = (path: any) => {
    return pathname === path;
  };

  const [anchorElNav, setAnchorElNav] = React.useState<null | HTMLElement>(
    null
  );
  const [anchorElUser, setAnchorElUser] = React.useState<null | HTMLElement>(
    null
  );

  const handleOpenNavMenu = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorElNav(event.currentTarget);
  };

  const handleCloseNavMenu = () => {
    setAnchorElNav(null);
  };

  const handleHome = () => {
    navigate("/");
  };


  const handleEvents = () => {
    navigate("/Event");
  };


  const handleVenues = () => {
    navigate("/Venue");
  };


  const handleFAQ = () => {
    navigate("/FAQ");
  };


  const handleLogin = () => {
    navigate("/Login");
  };



  return (
    <AppBar position="sticky" style={{ background: "#000000" }}>
      <Container maxWidth="xl">
        <Toolbar disableGutters>
          <Link to="/Home">
            <img
              src={logo}
              alt="Logo"
              width={60}
              height={40}
              style={{ marginLeft: 20, marginRight: -8 }}
            ></img>
          </Link>
          {/* for hamburger bar => dont know why bg color not working */}
          <Box
            sx={{
              flexGrow: 1,
              display: { xs: "flex", md: "none", backgroundColor: "black" },
            }}
          >
            <IconButton
              size="large"
              aria-label="account of current user"
              aria-controls="menu-appbar"
              aria-haspopup="true"
              onClick={handleOpenNavMenu}
              color="inherit"
            >
              <MenuIcon />
            </IconButton>
            <Menu
              id="menu-appbar"
              anchorEl={anchorElNav}
              anchorOrigin={{
                vertical: "bottom",
                horizontal: "left",
              }}
              keepMounted
              transformOrigin={{
                vertical: "top",
                horizontal: "left",
              }}
              open={Boolean(anchorElNav)}
              onClose={handleCloseNavMenu}
              sx={{
                display: { xs: "block", md: "none" },
              }}
            >
              <Button
                key="Home"
                onClick={handleHome}
                sx={{
                  my: 0,
                  color: isTabActive("/Home") ? "#FF5C35" : "black",
                  display: "block",
                }}
              >
                Home
              </Button>
              <Button
                key="Event"
                onClick={handleEvents}
                sx={{
                  my: 0,
                  color: isTabActive("/Event") ? "#FF5C35" : "black",
                  display: "block",
                }}
              >
                Events
              </Button>
              <Button
                key="Venue"
                onClick={handleVenues}
                sx={{
                  my: 0,
                  color: isTabActive("/Venue") ? "#FF5C35" : "black",
                  display: "block",
                }}
              >
                Venues
              </Button>
              <Button
                key="FAQ"
                onClick={handleFAQ}
                sx={{
                  my: 0,
                  color: isTabActive("/FAQ") ? "#FF5C35" : "black",
                  display: "block",
                }}
              >
                FAQ
              </Button>
              <Button
                key="Login"
                onClick={handleLogin}
                sx={{ my: 0, color: "black", display: "block" }}
              >
                Log In
              </Button>
            </Menu>
          </Box>

          <Typography
            variant="h5"
            noWrap
            component="a"
            href=""
            sx={{
              mr: 2,
              display: { xs: 'flex', md: 'none' },
              flexGrow: 1,
              fontFamily: 'Roboto',
              fontWeight: 700,
              letterSpacing: 0,
              color: 'inherit',
              textDecoration: 'none',
            }}
          >
            AuthenTicket
          </Typography>

          <Box
            justifyContent="left"
            alignItems="left"
            sx={{ flexGrow: 1, display: { xs: "none", md: "flex" } }}
          >
            <Button
              key="Home"
              onClick={handleHome}
              sx={{
                my: 2,
                ml: 4,
                color: isTabActive("/Home") ? "#FF5C35" : "white",
                display: "block",
              }}
            >
              Home
            </Button>
            <Button
              key="Event"
              onClick={handleEvents}
              sx={{
                my: 2,
                color: isTabActive("/Event") ? "#FF5C35" : "white",
                display: "block",
              }}
            >
              Events
            </Button>
            <Button
              key="Venue"
              onClick={handleVenues}
              sx={{
                my: 2,
                color: isTabActive("/Venue") ? "#FF5C35" : "white",
                display: "block",
              }}
            >
              Venues
            </Button>
            <Button
              key="FAQ"
              onClick={handleFAQ}
              sx={{
                my: 2,
                color: isTabActive("/FAQ") ? "#FF5C35" : "white",
                display: "block",
              }}
            >
              FAQ
            </Button>
          </Box>

          <Box
            justifyContent="right"
            alignItems="right"
            sx={{
              flexGrow: 1,
              display: { xs: "none", md: "flex" },
              marginRight: 5,
            }}
          >
            <IconButton
              type="button"
              onClick={handleLogin}
              sx={{
                p: "5px",
                color: "white",
                border: "1px solid",
                borderColor: "white",
                borderRadius: 15,
              }}
            >
              <AccountCircleOutlinedIcon sx={{ mr: 1, ml: 1 }} />
              <Typography sx={{ mr: 1 }}>LOGIN</Typography>
            </IconButton>
          </Box>
        </Toolbar>
      </Container>
    </AppBar>
  );
};


export const NavbarLoggedIn = () => {
  let navigate = useNavigate();
  
  const { pathname } = useLocation();
  
  const isTabActive = (path: any) => {
    return pathname === path;
  };
  
  let name: any = window.localStorage.getItem("username");
  let email: any = window.localStorage.getItem("email");
  let profileImage: any = window.localStorage.getItem("profileImage");
  const profileImageSrc = `${process.env.REACT_APP_S3_URL}/user_profile_images/${profileImage}`;

  const [anchorElNav, setAnchorElNav] = React.useState<null | HTMLElement>(
    null
  );
  const [anchorElUser, setAnchorElUser] = React.useState<null | HTMLElement>(
    null
  );

  const handleOpenNavMenu = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorElNav(event.currentTarget);
  };

  const handleCloseNavMenu = () => {
    setAnchorElNav(null);
  };

  const openUser = Boolean(anchorElUser);
  const id = openUser ? "simple-popover" : undefined;

  //for navbar icon
  const handleOpenUserMenu = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorElUser(event.currentTarget);
  };

  const handleCloseUserMenu = () => {
    setAnchorElUser(null);
  };

  const handledLogout = () => {
    setAnchorElUser(null);
    sessionStorage.clear();
    localStorage.clear();
    navigate("/logIn");
    window.location.reload();
  };

  const handleProfile = () => {
    navigate("/Profile");
  };
  const handleHome = () => {
    navigate("/");
  };

  const handleEvents = () => {
    navigate("/Event");
  };

  const handleVenues = () => {
    navigate("/Venue");
  };

  const handleFAQ = () => {
    navigate("/FAQ");
  };

  const handleLogin = () => {
    navigate("/Login");
  };

  return (
    <AppBar position="sticky" style={{ background: "#000000" }}>
      <Container maxWidth="xl">
        <Toolbar disableGutters>
          <Link to="/Home">
            <img
              src={logo}
              alt="Logo"
              width={60}
              height={40}
              style={{ marginLeft: 20, marginRight: -8 }}
            ></img>
          </Link>
          {/* for hamburger bar => dont know why bg color not working */}
          <Box
            sx={{
              flexGrow: 1,
              display: { xs: "flex", md: "none", backgroundColor: "black" },
            }}
          >
            <IconButton
              size="large"
              aria-label="account of current user"
              aria-controls="menu-appbar"
              aria-haspopup="true"
              onClick={handleOpenNavMenu}
              color="inherit"
            >
              <MenuIcon />
            </IconButton>
            <Menu
              id="menu-appbar"
              anchorEl={anchorElNav}
              anchorOrigin={{
                vertical: "bottom",
                horizontal: "left",
              }}
              keepMounted
              transformOrigin={{
                vertical: "top",
                horizontal: "left",
              }}
              open={Boolean(anchorElNav)}
              onClose={handleCloseNavMenu}
              sx={{
                display: { xs: "block", md: "none" },
              }}
            >
              <Button
                key="Home"
                onClick={handleHome}
                sx={{
                  my: 0,
                  color: isTabActive("/Home") ? "#FF5C35" : "black",
                  display: "block",
                }}
              >
                Home
              </Button>
              <Button
                key="Event"
                onClick={handleEvents}
                sx={{
                  my: 0,
                  color: isTabActive("/Event") ? "#FF5C35" : "black",
                  display: "block",
                }}
              >
                Events
              </Button>
              <Button
                key="Venue"
                onClick={handleVenues}
                sx={{
                  my: 0,
                  color: isTabActive("/Venue") ? "#FF5C35" : "black",
                  display: "block",
                }}
              >
                Venues
              </Button>
              <Button
                key="FAQ"
                onClick={handleFAQ}
                sx={{
                  my: 0,
                  color: isTabActive("/FAQ") ? "#FF5C35" : "black",
                  display: "block",
                }}
              >
                FAQ
              </Button>
              <Button
                key="Login"
                onClick={handleLogin}
                sx={{ my: 0, color: "black", display: "block" }}
              >
                Log In
              </Button>
            </Menu>
          </Box>

          <Typography
            variant="h5"
            noWrap
            component="a"
            href=""
            sx={{
              mr: 2,
              display: { xs: "flex", md: "none" },
              flexGrow: 1,
              fontFamily: "Roboto",
              fontWeight: 700,
              letterSpacing: 0,
              color: "inherit",
              textDecoration: "none",
            }}
          >
            AuthenTicket
          </Typography>

          <Box
            justifyContent="left"
            alignItems="left"
            sx={{ flexGrow: 1, display: { xs: "none", md: "flex" } }}
          >
            <Button
              key="Home"
              onClick={handleHome}
              sx={{
                my: 2,
                ml: 4,
                color: isTabActive("/Home") ? "#FF5C35" : "white",
                display: "block",
              }}
            >
              Home
            </Button>
            <Button
              key="Event"
              onClick={handleEvents}
              sx={{
                my: 2,
                color: isTabActive("/Event") ? "#FF5C35" : "white",
                display: "block",
              }}
            >
              Events
            </Button>
            <Button
              key="Venue"
              onClick={handleVenues}
              sx={{
                my: 2,
                color: isTabActive("/Venue") ? "#FF5C35" : "white",
                display: "block",
              }}
            >
              Venues
            </Button>
            <Button
              key="FAQ"
              onClick={handleFAQ}
              sx={{
                my: 2,
                color: isTabActive("/FAQ") ? "#FF5C35" : "white",
                display: "block",
              }}
            >
              FAQ
            </Button>
          </Box>

          <Box sx={{ flexGrow: 0, display:'flex', alignItems:'center' }}>
            <Typography style={{ fontSize:'14px', marginRight:12 }}>
              {name || ''}
            </Typography>
            <Tooltip title="Open Profile">
              <IconButton onClick={handleOpenUserMenu} sx={{ p: 0 }}>
                <Avatar alt={name.toUpperCase() || ''} src={profileImageSrc} />
              </IconButton>
            </Tooltip>
            <Popover
              id={id}
              open={openUser}
              anchorEl={anchorElUser}
              onClose={handleCloseUserMenu}
              anchorOrigin={{
                vertical: 55,
                horizontal: "left",
              }}
              elevation={2}
            >
              <Box onClick={handleProfile} sx={{ border: 1, borderColor: "grey.300", borderRadius: 1 }} paddingBottom={1}>
                <Box height={100} minWidth={300} padding={2} marginRight={4}>
                  <Grid container>
                    <Grid item xs={4}>
                      <Avatar
                        sizes="large"
                        alt={name.toUpperCase() || ''}
                        src={profileImageSrc}
                        sx={{ width: 75, height: 75 }}
                      />
                    </Grid>
                    <Grid item xs={8}>
                      <Box>
                        <Typography
                          variant="h5"
                          sx={{
                            fontWeight: "bold",
                            textTransform: "capitalize",
                          }}
                        >
                        {name}
                        </Typography>
                        <Typography color={"grey"}>{email}</Typography>
                        <Stack direction="row" spacing={2}>
                        <LinkMUI color={'#FF5C35'} onClick={handleProfile} sx={{display:'flex', justifyContent:'flex-end', cursor: 'pointer'}}> View Profile </LinkMUI>
                        <LinkMUI color={'#FF5C35'} onClick={handledLogout} sx={{display:'flex', justifyContent:'flex-end',cursor: 'pointer'}}> Log Out </LinkMUI>
                        </Stack>
                      </Box>
                    </Grid>
                  </Grid>
                </Box>
              </Box>
            </Popover>
          </Box>
        </Toolbar>
      </Container>
    </AppBar>
  );
};

export const NavbarOrganiser = () => {
  let navigate = useNavigate();

  const [anchorElNav, setAnchorElNav] = React.useState<null | HTMLElement>(
    null
  );
  const [anchorElUser, setAnchorElUser] = React.useState<null | HTMLElement>(
    null
  );
  const [anchorElCheckIn, setAnchorElCheckIn] = React.useState<null | HTMLElement>(null);

  const { pathname } = useLocation();

  const isTabActive = (path: any) => {
    return pathname === path;
  };

  let profileImage: any = window.localStorage.getItem('profileImage');
  const profileImageSrc = `${process.env.REACT_APP_S3_URL}/event_organiser_profile/${profileImage}`;

  const handleOpenNavMenu = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorElNav(event.currentTarget);
  };
  const handleOpenUserMenu = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorElUser(event.currentTarget);
  };

  const handleCloseNavMenu = () => {
    setAnchorElNav(null);
  };

  const handleCloseUserMenu = () => {
    setAnchorElUser(null);
  };

  const handledLogout = () => {
    setAnchorElUser(null);
    sessionStorage.clear();
    localStorage.clear();
    navigate('/logIn');
  };

  const handleCheckinOrganiser = () => {
    navigate('/CheckinOrganiser');
    setAnchorElCheckIn(null);

  }

  const handleQRCheckinOrganiser = () => {
    navigate('/QRCheckinOrganiser');
    setAnchorElCheckIn(null);
  }

  const handleCheckIn = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorElCheckIn(event.currentTarget);
  };
  const handleCloseCheckIn = () => {
    setAnchorElCheckIn(null);
  };

  const linkStyle = {
    textDecoration: 'none',
    color: 'black'
  };

  return (
    <AppBar position="sticky" style={{ background: '#000000' }} >
      <Container maxWidth="xl">
        <Toolbar disableGutters>
          <a href="/HomeOrganiser">
            <img src={logo} alt="Logo" width={50} height={50} style={{ marginLeft: 5, marginRight: 8 }}></img>
          </a>

          {/* for hamburger bar */}
          <Box sx={{ flexGrow: 1, display: { xs: 'flex', md: 'none' } }}>
            <IconButton
              size="large"
              aria-label="account of current user"
              aria-controls="menu-appbar"
              aria-haspopup="true"
              onClick={handleOpenNavMenu}
              color="inherit"
            >
              <MenuIcon />
            </IconButton>
            <Menu
              id="menu-appbar"
              anchorEl={anchorElNav}
              anchorOrigin={{
                vertical: 'bottom',
                horizontal: 'left',
              }}
              keepMounted
              transformOrigin={{
                vertical: 'top',
                horizontal: 'left',
              }}
              open={Boolean(anchorElNav)}
              onClose={handleCloseNavMenu}
              sx={{
                display: { xs: 'block', md: 'none' },
              }}
            >
              <Button key='Home' href={'/HomeOrganiser'} sx={{ my: 0, display: 'block', color: isTabActive('/HomeOrganiser') ? '#FF5C35' : 'black' }} >Home</Button>
              <Button key='Event' href={'/EventOrganiser'} sx={{ my: 0, color: isTabActive('/EventOrganiser') ? '#FF5C35' : 'black', display: 'block' }} >Event</Button>
              <Button key='Order' href={'/OrderOrganiser'} sx={{ my: 0, color: isTabActive('/OrderOrganiser') ? '#FF5C35' : 'black', display: 'block' }} >Order</Button>
              <Button key='Checkin' href={'/CheckinOrganiser'} sx={{ my: 0, color: isTabActive('/CheckinOrganiser') ? '#FF5C35' : 'black', display: 'block' }} >Checkin</Button>
              <Button key='QRCheckin' href={'/QRCheckinOrganiser'} sx={{ my: 0, color: isTabActive('/QRCheckinOrganiser') ? '#FF5C35' : 'black', display: 'block' }} >QR</Button>
              <Button key='FAQ' href={'/FAQOrganiser'} sx={{ my: 0, color: isTabActive('/FAQOrganiser') ? '#FF5C35' : 'black', display: 'block' }} >FAQ</Button>
            </Menu>
          </Box>
          <Typography
            variant="h5"
            noWrap
            component="a"
            href=""
            sx={{
              mr: 2,
              display: { xs: 'flex', md: 'none' },
              flexGrow: 1,
              fontFamily: 'Roboto',
              fontWeight: 700,
              letterSpacing: 0,
              color: 'inherit',
              textDecoration: 'none',
            }}
          >
            AuthenTicket
          </Typography>

          <Box justifyContent="left" alignItems="left" sx={{ flexGrow: 1, ml: 4, display: { xs: 'none', md: 'flex' } }}>
            <Button key='Home' href={'/HomeOrganiser'} sx={{ my: 2, color: isTabActive('/HomeOrganiser') ? '#FF5C35' : 'white', display: 'block' }} >Home</Button>
            <Button key='Event' href={'/EventOrganiser'} sx={{ my: 2, color: isTabActive('/EventOrganiser') ? '#FF5C35' : 'white', display: 'block' }} >Event</Button>
            <Button key='Order' href={'/OrderOrganiser'} sx={{ my: 2, color: isTabActive('/OrderOrganiser') ? '#FF5C35' : 'white', display: 'block' }} >Order</Button>
            <Button key='Checkin' onClick={handleCheckIn} sx={{ my: 2, color: isTabActive('/CheckinOrganiser') || isTabActive('/QRCheckinOrganiser') ? '#FF5C35' : 'white', display: 'block' }} >Checkin</Button>
            <Button key='FAQ' href={'/FAQOrganiser'} sx={{ my: 2, color: isTabActive('/FAQOrganiser') ? '#FF5C35' : 'white', display: 'block' }} >FAQ</Button>
          </Box>


          <Box sx={{ flexGrow: 0 }}>
            <Tooltip title="Open settings">
              <IconButton onClick={handleOpenUserMenu} sx={{ p: 0 }}>
                <Avatar alt="Remy Sharp" src={profileImageSrc} />
              </IconButton>
            </Tooltip>
            <Menu
              sx={{ mt: '45px' }}
              id="menu-appbar"
              anchorEl={anchorElUser}
              anchorOrigin={{
                vertical: 'top',
                horizontal: 'right',
              }}
              keepMounted
              transformOrigin={{
                vertical: 'top',
                horizontal: 'right',
              }}
              open={Boolean(anchorElUser)}
              onClose={handleCloseUserMenu}
            >
              <a href='/HomeOrganiser' style={linkStyle}>
                <MenuItem key='Home'>
                  <Typography sx={{ color: isTabActive('/HomeOrganiser') ? '#FF5C35' : 'black' }} textAlign="center">Home</Typography>
                </MenuItem>
              </a>

              <a href='/EventOrganiser' style={linkStyle}>
                <MenuItem key='Event'>
                  <Typography sx={{ color: isTabActive('/EventOrganiser') ? '#FF5C35' : 'black' }} textAlign="center">Event</Typography>
                </MenuItem>
              </a>
              <a href='/OrderOrganiser' style={linkStyle}>
                <MenuItem key='Order'>
                  <Typography sx={{ color: isTabActive('/OrderOrganiser') ? '#FF5C35' : 'black' }} textAlign="center">Order</Typography>

                </MenuItem>
              </a>
              <a href='/CheckinOrganiser' style={linkStyle}>
                <MenuItem key='Checkin'>
                  <Typography sx={{ color: isTabActive('/CheckinOrganiser') ? '#FF5C35' : 'black' }} textAlign="center">Checkin</Typography>
                </MenuItem>
              </a>
              <a href='/QRCheckinOrganiser' style={linkStyle}>
                <MenuItem key='QRCheckin'>
                  <Typography sx={{ color: isTabActive('/QRCheckinOrganiser') ? '#FF5C35' : 'black' }} textAlign="center">QR Code</Typography>
                </MenuItem>
              </a>
              <a href='/FAQOrganiser' style={linkStyle}>
                <MenuItem key='FAQ'>
                  <Typography sx={{ color: isTabActive('/FAQOrganiser') ? '#FF5C35' : 'black' }} textAlign="center">FAQ</Typography>
                </MenuItem>
              </a>
              <MenuItem key='Logout' onClick={handledLogout}>
                <Typography textAlign="center">Log out</Typography>
              </MenuItem>
            </Menu>
          </Box>

          {/* check in dropdown  */}
          <Menu
            anchorEl={anchorElCheckIn}
            open={Boolean(anchorElCheckIn)}
            onClose={handleCloseCheckIn}
            onClick={handleCloseCheckIn}
            PaperProps={{
              elevation: 0,
              sx: {
                overflow: 'visible',
                filter: 'drop-shadow(0px 2px 8px rgba(0,0,0,0.32))',
                mt: 1.5,
                '& .MuiAvatar-root': {
                  width: 32,
                  height: 32,
                  ml: -0.5,
                  mr: 1,
                },

              },
            }}
            transformOrigin={{ horizontal: 'left', vertical: 'top' }}
            anchorOrigin={{ horizontal: 'left', vertical: 'bottom' }}
          >
            <MenuItem onClick={handleCheckinOrganiser}>
              <PersonIcon sx={{ marginRight: 2 }} /> Face
            </MenuItem>

            <MenuItem onClick={handleQRCheckinOrganiser}>
              <QrCodeIcon sx={{ marginRight: 2 }} /> QR
            </MenuItem></Menu>

        </Toolbar>
      </Container>
    </AppBar>
  );
}

export const NavbarAdmin = () => {
  let navigate = useNavigate();
  const { pathname } = useLocation();

  const isTabActive = (path: any) => {
    return pathname === path;
  };

  const [anchorElNav, setAnchorElNav] = React.useState<null | HTMLElement>(
    null
  );
  const [anchorElUser, setAnchorElUser] = React.useState<null | HTMLElement>(
    null
  );

  const profileImageSrc = `https://authenticket.s3.ap-southeast-1.amazonaws.com/logo.png`;

  const handleOpenNavMenu = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorElNav(event.currentTarget);
  };
  const handleOpenUserMenu = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorElUser(event.currentTarget);
  };

  const handleCloseNavMenu = () => {
    setAnchorElNav(null);
  };

  const handleCloseUserMenu = () => {
    setAnchorElUser(null);
  };

  const handledLogout = () => {
    setAnchorElUser(null);
    sessionStorage.clear();
    localStorage.clear();
    navigate("/logIn");
  };

  const handleHomeAdmin = () => {
    navigate("/HomeAdmin");
  };

  const handleArtistAdmin = () => {
    navigate("/ArtistAdmin");
  };

  const handleEventAdmin = () => {
    navigate("/EventAdmin");
  };

  return (
    <AppBar position="sticky" style={{ background: "#000000" }}>
      <Container maxWidth="xl">
        <Toolbar disableGutters>
          <Link to="/HomeAdmin">
            <img
              src={logo}
              alt="Logo"
              width={50}
              height={50}
              style={{ marginLeft: 5, marginRight: 8 }}
            ></img>
          </Link>

          {/* for hamburger bar */}
          <Box sx={{ flexGrow: 1, display: { xs: "flex", md: "none" } }}>
            <IconButton
              size="large"
              aria-label="account of current user"
              aria-controls="menu-appbar"
              aria-haspopup="true"
              onClick={handleOpenNavMenu}
              color="inherit"
            >
              <MenuIcon />
            </IconButton>
            <Menu
              id="menu-appbar"
              anchorEl={anchorElNav}
              anchorOrigin={{
                vertical: "bottom",
                horizontal: "left",
              }}
              keepMounted
              transformOrigin={{
                vertical: "top",
                horizontal: "left",
              }}
              open={Boolean(anchorElNav)}
              onClose={handleCloseNavMenu}
              sx={{
                display: { xs: "block", md: "none" },
              }}
            >
              <Button
                key="Home"
                onClick={handleHomeAdmin}
                sx={{
                  my: 0,
                  color: isTabActive("/HomeAdmin") ? "#FF5C35" : "black",
                  display: "block",
                }}
              >
                Home
              </Button>
              <Button
                key="Artist"
                onClick={handleArtistAdmin}
                sx={{
                  my: 0,
                  color: isTabActive("/ArtistAdmin") ? "#FF5C35" : "black",
                  display: "block",
                }}
              >
                Artist
              </Button>
              <Button
                key="Event"
                onClick={handleEventAdmin}
                sx={{
                  my: 0,
                  color: isTabActive("/EventAdmin") ? "#FF5C35" : "black",
                  display: "block",
                }}
              >
                Event
              </Button>
            </Menu>
          </Box>
          <Typography
            variant="h5"
            noWrap
            component="a"
            href=""
            sx={{
              mr: 2,
              display: { xs: "flex", md: "none" },
              flexGrow: 1,
              fontFamily: "Roboto",
              fontWeight: 700,
              letterSpacing: 0,
              color: "inherit",
              textDecoration: "none",
            }}
          >
            AuthenTicket
          </Typography>

          <Box
            justifyContent="left"
            alignItems="left"
            sx={{ flexGrow: 1, ml: 4, display: { xs: "none", md: "flex" } }}
          >
            <Button
              key="Home"
              onClick={handleHomeAdmin}
              sx={{
                my: 2,
                color: isTabActive("/HomeAdmin") ? "#FF5C35" : "white",
                display: "block",
              }}
            >
              Home
            </Button>
            <Button
              key="Artist"
              onClick={handleArtistAdmin}
              sx={{
                my: 2,
                color: isTabActive("/ArtistAdmin") ? "#FF5C35" : "white",
                display: "block",
              }}
            >
              Artist
            </Button>
            <Button
              key="Event"
              onClick={handleEventAdmin}
              sx={{
                my: 2,
                color: isTabActive("/EventAdmin") ? "#FF5C35" : "white",
                display: "block",
              }}
            >
              Event
            </Button>
          </Box>

          <Box sx={{ flexGrow: 0 }}>
            <Tooltip title="Open settings">
              <IconButton onClick={handleOpenUserMenu} sx={{ p: 0 }}>
                <Avatar alt="Remy Sharp" src={profileImageSrc} />
              </IconButton>
            </Tooltip>
            <Menu
              sx={{ mt: "45px" }}
              id="menu-appbar"
              anchorEl={anchorElUser}
              anchorOrigin={{
                vertical: "top",
                horizontal: "right",
              }}
              keepMounted
              transformOrigin={{
                vertical: "top",
                horizontal: "right",
              }}
              open={Boolean(anchorElUser)}
              onClose={handleCloseUserMenu}
            >
              <MenuItem key="Home" onClick={handleHomeAdmin}>
                <Typography
                  sx={{
                    color: isTabActive("/HomeAdmin") ? "#FF5C35" : "black",
                  }}
                  textAlign="center"
                >
                  Home
                </Typography>
              </MenuItem>
              <MenuItem key="Artist" onClick={handleArtistAdmin}>
                <Typography
                  sx={{
                    color: isTabActive("/ArtistAdmin") ? "#FF5C35" : "black",
                  }}
                  textAlign="center"
                >
                  Artist
                </Typography>
              </MenuItem>
              <MenuItem key="Event" onClick={handleEventAdmin}>
                <Typography
                  sx={{
                    color: isTabActive("/EventAdmin") ? "#FF5C35" : "black",
                  }}
                  textAlign="center"
                >
                  Event
                </Typography>
              </MenuItem>
              <MenuItem key="Logout" onClick={handledLogout}>
                <Typography textAlign="center">Log out</Typography>
              </MenuItem>
            </Menu>
          </Box>
        </Toolbar>
      </Container>
    </AppBar>
  );
};
