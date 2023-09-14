import React, { useState } from 'react';
import CssBaseline from '@mui/material/CssBaseline';
import Link from '@mui/material/Link';
import Paper from '@mui/material/Paper';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import logo from '../../images/logo(orange).png';
import { useNavigate } from 'react-router-dom';
import { TextField, Button, Snackbar, Alert, } from '@mui/material';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { DemoContainer } from '@mui/x-date-pickers/internals/demo';
import moment from 'moment';

//image download
import backgroundImage from '../../images/background.png';
import dayjs from 'dayjs';

function Copyright(props: any) {
  return (
    <Typography variant="body2" color="text.secondary" align="center" {...props}>
      {'Copyright © '}
      <Link color="inherit" href="/signup">
        AuthenTicket
      </Link>{' '}
      {new Date().getFullYear()}
      {'.'}
    </Typography>
  );
}

// TODO remove, this demo shouldn't need to reset the theme.
//const defaultTheme = createTheme();
const myTheme = createTheme({
  palette: {
    background: {
      default: '#FEF9F9'
    }
  }
});

export function OrganiserSignup() {

  let navigate = useNavigate();

  //validation methods
  const validateEmail = (email : any) => {
    // Regular expression to validate email format
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  };

  //variables
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');
  const [dob, setDob] = useState('');
  //validation
  const [emailError, setEmailError] = useState(false);
  const [emailHelperText, setEmailHelperText] = useState('');
  //error , warning , info , success
  const [openSnackbar, setOpenSnackbar] = useState(false);
  const [alertType, setAlertType] : any= useState('info');
  const [alertMsg, setAlertMsg] = useState('');

  //handler method section
  const handleEmail = (e: any) => {
    setEmail(e.target.value);
  }
  const handlePassword = (e: any) => {
    setPassword(e.target.value);
  }
  const handleName = (e: any) => {
    setName(e.target.value);
  }
  const handleDob = (e: any) => {
    moment(e, 'YYYY-MM-DD');
    setDob(e);
  }

  const signupHandler = async (event: any) => {
    event.preventDefault();

    //checking validations, returning error message if conditions not met
    if (!validateEmail(email)) {
      setEmailError(true);
      setEmailHelperText('Please enter a valid email address');
      return;
    } else {
      setEmailError(false);
      setEmailHelperText('');
    }

    // //calling backend API
    fetch(`${process.env.REACT_APP_BACKEND_DEV_URL}/auth/register`, {
      headers: {
        'Content-Type': 'application/json',
      },
      method: 'POST',
      body: JSON.stringify({
        "name": name,
        "email": email,
        "password": password,
        "dateOfBirth": dob,
      })
    })
      .then(async (response) => {
        if (response.status !== 200) {
          const signupResponse = await response.json();
          //show alert msg
          setOpenSnackbar(true);
          setAlertType('error');
          setAlertMsg(signupResponse['message']);
        } else {
          setOpenSnackbar(true);
          setAlertType('success');
          setAlertMsg(`Email ${email} sign up successful! An email will be sent shortly, please verify your account`);
          setTimeout(() => {
            navigate('/login');
          }, 4000);
        }

      })
      .catch((err) => {
        window.alert(err);
      });
  }

  const handleSnackbarClose = () => {
    setOpenSnackbar(false);
  };

  return (

    <ThemeProvider theme={myTheme}>
      <Grid container component="main" sx={{ height: '100vh' }}>
        <CssBaseline />
        <Grid
          item
          xs={12}
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
              <a href='/logIn'>
                <img src={logo} alt="Logo" width={28} height={28} style={{ marginLeft: -230, marginTop: -5, position: 'absolute' }} />
                <span style={{ color: 'black', fontSize: 15, textAlign: 'left', marginTop: -1.5, marginLeft: -195, fontWeight: 500, position: 'absolute' }}>AuthenTicket</span>
              </a>
            </div>
            <Typography component="h1" variant="h5" sx={{ fontWeight: 'bold', fontSize: 45, letterSpacing: -2, marginTop: 11, marginBottom: 1 }}>
              Register your Company
            </Typography>
            <form onSubmit={signupHandler}>
              <TextField
                margin="normal"
                required
                fullWidth
                id="Company Name"
                label="Company Name"
                name="companyName"
                autoComplete="companyName"
                autoFocus
                size="medium"
                onChange={handleName}
              />
              <TextField
                margin="normal"
                required
                fullWidth
                id="Company Email"
                label="Company Email"
                name="companyEmail"
                autoComplete="companyEmail"
                autoFocus
                size="medium"
                error={emailError}
                helperText={emailHelperText}
                onChange={handleEmail}
              />
              <TextField
                margin="normal"
                required
                fullWidth
                name="Company Description"
                label="Description of Company"
                type="companyDescription"
                id="companyDescription"
                autoComplete="companyDescription"
                rows={4}
                multiline
                // helperText={passwordHelperText}
                onChange={handlePassword}
              />
              <Button
                type="submit"
                fullWidth
                variant="contained"
                sx={{ mt: 3, mb: 2, backgroundColor: '#FF5C35' }}
              >
                Register Company
              </Button>
              <Grid container>
                <Grid item>
                  <Typography variant="body2" style={{color:'#858585'}}>
                    Are you a user?{" "}
                    <Link href="/logIn" variant="body2" style={{color:'#2E475D'}}>
                      {"Log In"}
                    </Link>
                  </Typography>
                </Grid>
              </Grid>


              <Copyright sx={{ mt: 5 }} />

              <Snackbar open={openSnackbar} autoHideDuration={2000} onClose={handleSnackbarClose}>
                <Alert onClose={handleSnackbarClose} severity={alertType} sx={{ width: '100%' }}>
                  {alertMsg}
                </Alert>
              </Snackbar>
            </form>
          </Box>
        </Grid>
      </Grid>
    </ThemeProvider>
  );
}