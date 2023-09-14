import * as React from 'react';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import IconButton from '@mui/material/IconButton';
import Typography from '@mui/material/Typography';
import Menu from '@mui/material/Menu';
import MenuIcon from '@mui/icons-material/Menu';
import Container from '@mui/material/Container';
import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import Tooltip from '@mui/material/Tooltip';
import MenuItem from '@mui/material/MenuItem';
import { Link, useNavigate } from 'react-router-dom';
import logo from '../images/logo(orange).png';
import AccountCircleOutlinedIcon from '@mui/icons-material/AccountCircleOutlined';

export const NavbarNotLoggedIn = () => {
    let navigate = useNavigate();

    const [anchorElNav, setAnchorElNav] = React.useState<null | HTMLElement>(null);
    const [anchorElUser, setAnchorElUser] = React.useState<null | HTMLElement>(null);

    const handleOpenNavMenu = (event: React.MouseEvent<HTMLElement>) => {
        setAnchorElNav(event.currentTarget);
    };

    const handleCloseNavMenu = () => {
        setAnchorElNav(null);
    };

    const handleHome = () => {
        navigate('/');
    }

    const handleEvents = () => {
        navigate('/Event');
    }

    const handleVenues = () => {
        navigate('/Venue');
    }

    const handleFAQ = () => {
        navigate('/FAQ');
    }

    const handleLogin = () => {
        navigate('/Login');
    }

    const handleSupport = () => {
        navigate('/Support');
    }

    const handleAbout = () => {
        navigate('/About');
    }

    return (
        <AppBar position="sticky" style={{ background: '#000000' }} >
            <Container maxWidth="xl">
                <Toolbar disableGutters>
                    <Link to="/Home">
                        <img src={logo} alt="Logo" width={60} height={40} style={{ marginLeft: 20, marginRight: -8 }}></img>
                    </Link>
                    {/* for hamburger bar => dont know why bg color not working */}
                    <Box sx={{ flexGrow: 1, display: { xs: 'flex', md: 'none', backgroundColor: 'black'}}}>
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
                                display: { xs: 'block', md: 'none'},
                            }}
                        >
                            <Button key='Home' onClick={handleHome} sx={{ my: 0, color: 'black', display: 'block' }} >Home</Button>
                            <Button key='Events' onClick={handleEvents} sx={{ my: 0, color: 'black', display: 'block' }} >Events</Button>
                            <Button key='Venues' onClick={handleEvents} sx={{ my: 0, color: 'black', display: 'block' }} >Venues</Button>
                            <Button key='FAQ' onClick={handleFAQ} sx={{ my: 0, color: 'black', display: 'block' }} >FAQ</Button>
                            {/* <Button key='Support' onClick={handleSupport} sx={{ my: 0, color: 'white', display: 'block' }} >Support</Button>
                            <Button key='About' onClick={handleAbout} sx={{ my: 0, color: 'white', display: 'block' }} >About</Button> */}
                            <Button key='Login' onClick={handleLogin} sx={{ my: 0, color: 'black', display: 'block' }} >Log In</Button>
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

                    <Box justifyContent="left" alignItems="left" sx={{ flexGrow: 1, display: { xs: 'none', md: 'flex' } }}>
                        <Button key='Home' onClick={handleHome} sx={{ my: 2, ml: 4, color: 'white', display: 'block' }} >Home</Button>
                        <Button key='Events' onClick={handleEvents} sx={{ my: 2, color: 'white', display: 'block' }} >Events</Button>
                        <Button key='Venues' onClick={handleVenues} sx={{ my: 2, color: 'white', display: 'block' }} >Venues</Button>
                        <Button key='FAQ' onClick={handleFAQ} sx={{ my: 2, color: 'white', display: 'block' }} >FAQ</Button>
                    </Box>

                    <Box justifyContent="right" alignItems="right" sx={{ flexGrow: 1, display: { xs: 'none', md: 'flex' }, marginRight:5 }}>
                        <IconButton type="button" onClick={handleLogin} sx={{
                            p: '5px', color: 'white', border: '1px solid',
                            borderColor: 'white',
                            borderRadius: 15,
                        }}>
                            <AccountCircleOutlinedIcon sx={{ mr: 1, ml: 1 }} />
                            <Typography sx={{ mr: 1 }}>
                                LOGIN
                            </Typography>
                        </IconButton>
                    </Box>

                </Toolbar>
            </Container>
        </AppBar>
    );
}

export const NavbarOrganiser = () => {
    let navigate = useNavigate();

    const [anchorElNav, setAnchorElNav] = React.useState<null | HTMLElement>(null);
    const [anchorElUser, setAnchorElUser] = React.useState<null | HTMLElement>(null);


    let profileImage: any = window.localStorage.getItem('profileImage');
    const profileImageSrc = `${process.env.REACT_APP_BACKEND_IMAGES_URL}/user_profile/${profileImage}`;


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
        localStorage.removeItem('accessToken');
        localStorage.removeItem('userName');
        localStorage.removeItem('accRole');
        localStorage.removeItem('profileImage');
        navigate('/Login');
    };

    const handleHomeOrganiser = () => {
        navigate('/HomeOrganiser');
    }

    const handleEventOrganiser = () => {
        navigate('/EventOrganiser');
    }

    return (
        <AppBar position="sticky" style={{ background: '#000000' }} >
            <Container maxWidth="xl">
                <Toolbar disableGutters>
                    <Link to="/HomeOrganiser">
                        <img src={logo} alt="Logo" width={50} height={50} style={{ marginLeft: 5, marginRight: 8 }}></img>
                    </Link>

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
                            <Button key='Home' onClick={handleHomeOrganiser} sx={{ my: 0, color: 'black', display: 'block' }} >Home</Button>
                            <Button key='Event' onClick={handleEventOrganiser} sx={{ my: 0, color: 'black', display: 'block' }} >Event</Button>
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

                    <Box justifyContent="left" alignItems="left" sx={{ flexGrow: 1, ml:4, display: { xs: 'none', md: 'flex' } }}>
                        <Button key='Home' onClick={handleHomeOrganiser} sx={{ my: 2, color: 'white', display: 'block' }} >Home</Button>
                        <Button key='Event' onClick={handleEventOrganiser} sx={{ my: 2, color: 'white', display: 'block' }} >Event</Button>
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
                            <MenuItem key='Event' onClick={handleEventOrganiser}>
                                <Typography textAlign="center">Event</Typography>
                            </MenuItem>
                            <MenuItem key='Logout' onClick={handledLogout}>
                                <Typography textAlign="center">Log out</Typography>
                            </MenuItem>
                        </Menu>
                    </Box>
                </Toolbar>
            </Container>
        </AppBar>
    );
}

export const NavbarAdmin = () => {
    let navigate = useNavigate();

    const [anchorElNav, setAnchorElNav] = React.useState<null | HTMLElement>(null);
    const [anchorElUser, setAnchorElUser] = React.useState<null | HTMLElement>(null);


    let profileImage: any = window.localStorage.getItem('profileImage');
    const profileImageSrc = `${process.env.REACT_APP_BACKEND_IMAGES_URL}/user_profile/${profileImage}`;


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
        localStorage.removeItem('accessToken');
        localStorage.removeItem('userName');
        localStorage.removeItem('accRole');
        localStorage.removeItem('profileImage');
        navigate('/login');
    };

    const handleHomeAdmin = () => {
        navigate('/HomeAdmin');
    }

    const handleArtistAdmin = () => {
        navigate('/ArtistAdmin');
    }

    const handleEventAdmin = () => {
        navigate('/EventAdmin');
    }

    const handleVenueAdmin = () => {
        navigate('/VenueAdmin');
    }

    return (
        <AppBar position="sticky" style={{ background: '#000000' }} >
            <Container maxWidth="xl">
                <Toolbar disableGutters>
                    <Link to="/HomeAdmin">
                        <img src={logo} alt="Logo" width={50} height={50} style={{ marginLeft: 5, marginRight: 8 }}></img>
                    </Link>

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
                            <Button key='Home' onClick={handleHomeAdmin} sx={{ my: 0, color: 'black', display: 'block' }} >Home</Button>
                            <Button key='Artist' onClick={handleArtistAdmin} sx={{ my: 0, color: 'black', display: 'block' }} >Artist</Button>
                            <Button key='Event' onClick={handleEventAdmin} sx={{ my: 0, color: 'black', display: 'block' }} >Event</Button>
                            <Button key='Venue' onClick={handleVenueAdmin} sx={{ my: 0, color: 'black', display: 'block' }} >Venue</Button>
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

                    <Box justifyContent="left" alignItems="left" sx={{ flexGrow: 1, ml:4, display: { xs: 'none', md: 'flex' } }}>
                        <Button key='Home' onClick={handleHomeAdmin} sx={{ my: 2, color: 'white', display: 'block' }} >Home</Button>
                        <Button key='Artist' onClick={handleArtistAdmin} sx={{ my: 2, color: 'white', display: 'block' }} >Artist</Button>
                        <Button key='Event' onClick={handleEventAdmin} sx={{ my: 2, color: 'white', display: 'block' }} >Event</Button>
                        <Button key='Venue' onClick={handleVenueAdmin} sx={{ my: 0, color: 'white', display: 'block' }} >Venue</Button>
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
                            <MenuItem key='Artist' onClick={handleArtistAdmin}>
                                <Typography textAlign="center">Artist</Typography>
                            </MenuItem>
                            <MenuItem key='Event' onClick={handleEventAdmin}>
                                <Typography textAlign="center">Event</Typography>
                            </MenuItem>
                            <MenuItem key='Event' onClick={handleVenueAdmin}>
                                <Typography textAlign="center">Venue</Typography>
                            </MenuItem>
                            <MenuItem key='Logout' onClick={handledLogout}>
                                <Typography textAlign="center">Log out</Typography>
                            </MenuItem>
                        </Menu>
                    </Box>
                </Toolbar>
            </Container>
        </AppBar>
    );
}