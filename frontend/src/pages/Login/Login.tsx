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
import { Alert, Snackbar } from '@mui/material';

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

export function Login() {

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
    // //calling backend API
    fetch(`${process.env.REACT_APP_BACKEND_DEV_URL}/auth/authenticate`, {
      headers: {
        'Content-Type': 'application/json',
      },
      method: 'POST',
      body: JSON.stringify({
        "email": email,
        "password": password,
      })
    })
      .then(async (response) => {
        if (response.status == 200) {

          const loginResponse = await response.json();
          //pass the info to the local storage, so other page can access them
          localStorage.setItem('accessToken', loginResponse.data.token);
          localStorage.setItem('email', loginResponse.data.userDetails.email);
          localStorage.setItem('username', loginResponse.data.userDetails.name);
          localStorage.setItem('dob', loginResponse.data.userDetails.date_of_birth);
          localStorage.setItem('profileImage', loginResponse.data.userDetails.profile_image);


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
              <a href='/Login'>
                <img src={logo} alt="Logo" width={70} height={45} style={{ marginLeft: -210, marginTop: 10, position: 'absolute' }} />
              </a>
            </div>
            <Typography component="h1" variant="h5" sx={{ fontWeight: 'bold', fontSize: 55, letterSpacing: -2, marginTop: 12, marginBottom: -1.5, color:'#2E475D' }}>
              Hi there!
            </Typography>
            <Typography sx={{ fontWeight: 500, marginBottom: 3, color:'#2E475D' }}>
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
                inputProps={{style : {color:'#2E475D'}}}
              />
              <TextField
                margin="normal"
                required
                fullWidth
                name="password"
                label="Password"
                type="password"
                id="password"
                autoComplete="current-password"
                onChange={handlePassword}
                inputProps={{style : {color:'#2E475D'}}}
              />
              <Button
                type="submit"
                fullWidth
                variant="contained"
                sx={{ mt: 3, mb: 2, backgroundColor: '#FF5C35' }}
              >
                Log In
              </Button>
              <Grid container alignItems="center" justifyContent="center">
                <Grid item>
                  <Typography variant="body2" style={{color:'#858585'}}>
                    Don't have an account?{" "}
                    <Link href="/signUp" variant="body2" style={{color:'#2E475D'}}>
                      {"Sign Up"}
                    </Link>
                  </Typography>
                </Grid>
              </Grid>

              <Grid container alignItems="center" justifyContent="center" style={{marginTop:8}}>
                <Grid item>
                  <Typography variant="body2" style={{color:'#858585'}}>
                    Are you an organiser?{" "}
                    <Link href="/signUp" variant="body2" style={{color:'#2E475D'}}>
                      {"Register here"}
                    </Link>
                  </Typography>
                </Grid>
              </Grid>
              
              <Copyright sx={{ mt: 5, mb: 5, color:'#858585', marginTop:14 }} />
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