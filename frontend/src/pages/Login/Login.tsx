import React, { useState } from 'react';
import Button from '@mui/material/Button';
import CssBaseline from '@mui/material/CssBaseline';
import TextField from '@mui/material/TextField';
import Link from '@mui/material/Link';
import Paper from '@mui/material/Paper';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import { useNavigate } from 'react-router-dom';
import { Alert, IconButton, InputAdornment, Snackbar } from '@mui/material';
import VisibilityIcon from '@mui/icons-material/Visibility';
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';
//image downloads
import logo from '../../images/logo(orange).png';
import backgroundImage from '../../images/background.png';

function Copyright(props: any) {
  return (
    <Typography variant="body2" color="text.secondary" align="center" {...props}>
      {'Copyright © '}
      <Link color="inherit" href="/login">
        Authenticket
      </Link>{' '}
      {new Date().getFullYear()}
      {'.'}
    </Typography>
  );
}

const myTheme = createTheme({
  palette: {
    background: {
      default: '#FEF9F9'
    }
  }
});

export const Login = () => {

  let navigate = useNavigate();
  //validation method
  const validateEmail = (email: any) => {
    // Regular expression to validate email format
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  };
  //variables
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  //validation
  const [emailError, setEmailError] = useState(false);
  const [helperText, setHelperText] = useState('');
  //for pop up message => error , warning , info , success
  const [openSnackbar, setOpenSnackbar] = useState(false);
  const [alertType, setAlertType]: any = useState('info');
  const [alertMsg, setAlertMsg] = useState('');
  const handleSnackbarClose = () => {
    setOpenSnackbar(false);
  };
  //for show password icon
  const [showPassword, setShowPassword] = useState(false);

  const togglePasswordVisibility = () => {
    setShowPassword(!showPassword);
  };


  const handleEmail = (e: any) => {
    setEmail(e.target.value);
  }
  const handlePassword = (e: any) => {
    setPassword(e.target.value);
  }
  const loginHandler = async (event: any) => {
    event.preventDefault();
    if (!validateEmail(email)) {
      setEmailError(true);
      setHelperText('Please enter a valid email address.');
      return;
    } else {
      setEmailError(false);
      setHelperText('');
    }

    const formData = new FormData();
    formData.append('email', email);
    formData.append('password', password);
    // //calling backend API
    fetch(`${process.env.REACT_APP_BACKEND_URL}/auth/userAuthenticate`, {
      method: 'POST',
      body: formData
    })
      .then(async (response) => {
        if (response.status === 200) {
          const loginResponse = await response.json();
          //pass the info to the local storage, so other page can access them
          localStorage.setItem('accessToken', loginResponse.data.token);
          localStorage.setItem('id', loginResponse.data.userDetails.userId);
          localStorage.setItem('role', "USER");
          localStorage.setItem('email', loginResponse.data.userDetails.email);
          localStorage.setItem('username', loginResponse.data.userDetails.name);
          localStorage.setItem('dob', loginResponse.data.userDetails.dateOfBirth);
          localStorage.setItem('profileImage',loginResponse.data.userDetails.profileImage);

          navigate('/Home');
        } else {
          const loginResponse = await response.json();
          setOpenSnackbar(true);
          setAlertType('warning');
          setAlertMsg(loginResponse.message);
        }

      })
      .catch((err) => {
        window.alert(err);
      });

  }

  return (

    <ThemeProvider theme={myTheme}>
      <Grid container component="main" sx={{ height: '100vh' }}>
        <CssBaseline />
        <Grid
          item
          xs={false}
          sm={4}
          md={7}
          sx={{
            backgroundImage: `url(${backgroundImage})`,
            backgroundRepeat: 'no-repeat',
            backgroundColor: (t) =>
              t.palette.mode === 'light' ? t.palette.grey[50] : t.palette.grey[900],
            backgroundSize: 'cover',
            backgroundPosition: 'center',
          }}
        />
        <Grid item xs={12} sm={8} md={5} component={Paper} elevation={6} square>
          <Box
            sx={{
              my: 8,
              mx: 4,
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
            }}
          >
            <div style={{ display: 'flex', alignItems: 'left', flexWrap: 'wrap', justifyContent: 'flex-start' }}>
              <a href='/Home'>
                <img src={logo} alt="Logo" width={70} height={45} style={{ marginLeft: 0 }} />
              </a>
              <Button sx={{ color: 'black', borderRadius: '18px', marginLeft: 25 }} href='/OrganiserLogin'>
                Organiser
              </Button>
              <Button variant="outlined" sx={{ borderColor: 'black', borderRadius: '25px', color: 'black' }} href='/AdminLogin'>
                Admin
              </Button>
            </div>
            <Typography component="h1" variant="h5" sx={{ fontWeight: 'bold', fontSize: 55, letterSpacing: -2, marginTop: 12, marginBottom: -1.5, color: 'black' }}>
              Hi there!
            </Typography>
            <Typography sx={{ fontWeight: 500, marginBottom: 3, color: 'black' }}>
              Welcome to AuthenTicket
            </Typography>
            <form onSubmit={loginHandler}>
              <TextField
                margin="normal"
                required
                fullWidth
                id="email"
                label="Email Address"
                name="email"
                autoComplete="email"
                autoFocus
                error={emailError}
                helperText={helperText}
                onChange={handleEmail}
                inputProps={{ style: { color: '#2E475D' } }}
              />
              <TextField
                margin="normal"
                required
                fullWidth
                name="password"
                label="Password"
                type={showPassword ? 'text' : 'password'} 
                id="password"
                autoComplete="current-password"
                onChange={handlePassword}
                InputProps={{
                  style: { color: '#2E475D' } ,
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton onClick={togglePasswordVisibility} edge="end">
                        {showPassword ? <VisibilityOffIcon /> : <VisibilityIcon />}
                      </IconButton>
                    </InputAdornment>
                  ),
                }}
              />
             
              <Button
                type="submit"
                fullWidth
                variant="contained"
                sx={{ mt: 3, mb: 2, backgroundColor: 'black' }}
              >
                Log In
              </Button>
              <Grid container>
                <Grid item>
                  <Typography variant="body2" style={{ color: '#858585' }}>
                    Don't have an account?{" "}
                    <Link href="/signUp" variant="body2" style={{ color: '#2E475D' }}>
                      {"Sign Up"}
                    </Link>
                  </Typography>
                </Grid>
              </Grid>


              <Copyright sx={{ mt: 5, mb: 5, color: '#858585', marginTop: 14 }} />
            </form>
          </Box>
        </Grid>

        {/* error feedback */}
        <Snackbar open={openSnackbar} autoHideDuration={3000} onClose={handleSnackbarClose}>
          <Alert onClose={handleSnackbarClose} severity={alertType} sx={{ width: '100%' }}>
            {alertMsg}
          </Alert>
        </Snackbar>

      </Grid>
    </ThemeProvider>
  );
}