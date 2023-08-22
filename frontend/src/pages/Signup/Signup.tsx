import React, { useState } from 'react';
import CssBaseline from '@mui/material/CssBaseline';
import Link from '@mui/material/Link';
import Paper from '@mui/material/Paper';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import { useNavigate } from 'react-router-dom';
import { TextField, Button, Snackbar, Alert, } from '@mui/material';

//image download
import backgroundImage from '../../images/background.png';
import logo from '../../images/logo(orange).png';

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

// const { palette } = createTheme();
// const { augmentColor } = palette;
// const createColor = (mainColor) => augmentColor({color: { main: mainColor }});
// const theme = createTheme({
//     palette: {
//         darkGreen: createColor('#588061'),
//         lightPink :createColor('#FEF9F9'),
//         pink: createColor('#E7B5AC'),
//     },
// });

export function Signup() {

  let navigate = useNavigate();
  //validation method
  const validateEmail = (email : any) => {
    // Regular expression to validate email format
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  };


  //variables
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');
  //validation
  const [emailError, setEmailError] = useState(false);
  const [helperText, setHelperText] = useState('');
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

  const signupHandler = async (event: any) => {
    event.preventDefault();
    if (!validateEmail(email)) {
      setEmailError(true);
      setHelperText('Please enter a valid email address.');
      return;
    }else {
      setEmailError(false);
      setHelperText('');
    }
    // //calling backend API
    fetch(`${process.env.REACT_APP_BACKEND_PRODUCTION_URL}/User/signup`, {
      headers: {
        'Content-Type': 'application/json',
      },
      method: 'POST',
      body: JSON.stringify({
        "email": email,
        "password": password,
        "name": name,
      })
    })
      .then(async (response) => {
        if (response.status != 200) {
          const signupResponse = await response.json();
          //show alert msg
          setOpenSnackbar(true);
          setAlertType('error');
          setAlertMsg(signupResponse['message']);
        } else {
          setOpenSnackbar(true);
          setAlertType('success');
          setAlertMsg(`Email ${email} sign up successfully! Redirecting to login page.`);
          setTimeout(() => {
            navigate('/login');
          }, 2000);
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
              <img src={logo} alt="Logo" width={70} height={45} style={{ marginLeft: -210, marginTop: 10, position: 'absolute' }} />
              </a>
              {/* <Button
                type="submit"
                variant="outlined"
                color='inherit'
                sx={{ mt: 3, mb: 2, fontWeight: 500, borderRadius: 8, position: 'absolute', top: 30, right: 80 }}
              >
                Admin
              </Button> */}
            </div>
            <Typography component="h1" variant="h5" sx={{ fontWeight: 'bold', fontSize: 45, letterSpacing: -2, marginTop: 11, marginBottom: 1, color: '#2E475D' }}>
              Create your account
            </Typography>
            <form onSubmit={signupHandler}>
              <TextField
                margin="normal"
                required
                fullWidth
                id="Name"
                label="Name"
                name="name"
                autoComplete="name"
                autoFocus
                size="medium"
                onChange={handleName}
              />
              <TextField
                margin="normal"
                required
                fullWidth
                id="Email"
                label="Email"
                name="Email"
                autoComplete="email"
                autoFocus
                size="medium"
                error={emailError}
                helperText={helperText}
                onChange={handleEmail}
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
              />
              <Button
                type="submit"
                fullWidth
                variant="contained"
                sx={{ mt: 3, mb: 2, backgroundColor: '#FF5C35' }}
              >
                Create Account
              </Button>
              <Grid container>
                <Grid item>
                  <Link href="/logIn" variant="body2">
                    {"Already have an account? Log In"}
                  </Link>
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