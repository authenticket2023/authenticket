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
import Filter from 'bad-words';

//image download
import backgroundImage from '../../images/background.png';
import dayjs from 'dayjs';
import words from '../../extra-words.json';

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

export function Signup() {
  //profanity filter
  const filter = new Filter();
  filter.addWords(...words);

  let navigate = useNavigate();

  //convert timestamp to date
  const TimestampConverter = (timestamp: number) => {
    // Create a Date object from the timestamp
    const date = new Date(timestamp);

    // Extract components
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0'); // Month is 0-based
    const day = String(date.getDate()).padStart(2, '0');

    const formattedTimestamp = `${year}-${month}-${day}`;

    return formattedTimestamp;
}

  //validation methods
  const validateName = (name: any) => {
    // Regular expression to validate email format
    return filter.isProfane(name);
  };

  const validateEmail = (email: any) => {
    // Regular expression to validate email format
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    
    return emailRegex.test(email);
  };
  
  const validateDob = (dob : any) => {
    //get today's date with format YYYY-MM-DD
    const today = new Date(),
        currentDateTime = today.getFullYear() + '-' + (today.getMonth() + 1) + '-' + today.getDate();

      return dayjs(dob).isBefore(dayjs(currentDateTime));
      
  }

  const validatePassword = (password : any) => {
    // Regular expression to validate password
    const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*?&]{8,}$/;
    
    return passwordRegex.test(password);
  }

  //variables
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');
  const [dob, setDob] = useState('');
  //validation
  const [nameError, setNameError] = useState(false);
  const [nameHelperText, setNameHelperText] = useState('');
  const [emailError, setEmailError] = useState(false);
  const [emailHelperText, setEmailHelperText] = useState('');
  const [dobError, setDobError] = useState(false);
  const [dobHelperText, setDobHelperText] = useState('');
  const [passwordError, setPasswordError] = useState(false);
  const [passwordHelperText, setPasswordHelperText] = useState('');
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
    if (validateName(name)) {
      setNameError(true);
      setNameHelperText('Please enter an appropriate name');
      return;
    } else {
      setNameError(false);
      setNameHelperText('');
    }
    if (!validateEmail(email)) {
      setEmailError(true);
      setEmailHelperText('Please enter a valid email address');
      return;
    } else {
      setEmailError(false);
      setEmailHelperText('');
    }
    if(!validateDob(dob)) {
      setDobError(true);
      setDobHelperText('Please enter a valid date of birth');
      return;
    } else {
      setDobError(false);
      setDobHelperText('');
    }
    if(!validatePassword(password)){
      setPasswordError(true);
      setPasswordHelperText('Please enter a valid password. Password Requirements:\n1.At least one alphabetic character (uppercase or lowercase)\n2.At least one digit\n3.Minimum length of 8 characters');
      return;
    } else {
      setPasswordError(false);
      setPasswordHelperText('');
    }

    const formData = new FormData();
    formData.append('name', name);
    formData.append('email', email);
    formData.append('password', password);
    formData.append('dateOfBirth', TimestampConverter(Number(dob)));

    // //calling backend API
    fetch(`${process.env.REACT_APP_BACKEND_URL}/auth/userRegister`, {
      method: 'POST',
      body: formData
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
              <a href='/Home'>
                <img src={logo} alt="Logo" width={70} height={45} style={{marginLeft:0}} />
              </a>
              <div
                style={{
                  backgroundColor: '#F2F2F2',
                  height: '47px',
                  width: '250px',
                  display: 'flex',
                  borderRadius: '25px',
                  alignItems: 'flex-end',
                  marginLeft:165
                }}
              >
                  <Button sx={{ color: 'black', borderRadius: '18px', marginLeft:1, marginBottom:0.6 }} href='/OrganiserLogin'>
                    Organiser
                  </Button>
                  <Button  sx={{ borderColor: 'black', borderRadius: '25px', color: 'black', marginBottom:0.6, marginLeft:1 }} href='/AdminLogin'>
                    Admin
                  </Button>
                  <Button variant="contained" sx={{ borderColor: 'black', borderRadius: '25px', backgroundColor: 'black', marginRight:1, marginBottom:0.6 }} href='/logIn'>
                    User
                  </Button>
              </div>
            </div>
            <Typography component="h1" variant="h5" sx={{ fontWeight: 'bold', fontSize: 45, letterSpacing: -2, marginTop: 8, marginBottom: 1 }}>
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
                error={nameError}
                helperText={nameHelperText}
                onChange={handleName}
              />
              <LocalizationProvider dateAdapter={AdapterDayjs}>
                <DemoContainer components={['DatePicker']}>
                  <DatePicker 
                  label="Date of Birth"
                  onChange={handleDob}
                  disableFuture
                  format="YYYY-MM-DD"
                  slotProps={{
                    textField: {
                    required: true,
                    fullWidth: true,
                    error: dobError,
                    helperText: dobHelperText,
                    },
                  }}
                  />
                </DemoContainer>
              </LocalizationProvider>
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
                helperText={emailHelperText}
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
                error={passwordError}
                helperText={passwordHelperText}
                onChange={handlePassword}
              />
              <Button
                type="submit"
                fullWidth
                variant="contained"
                sx={{ mt: 3, mb: 2, backgroundColor: 'black' }}
              >
                Create Account
              </Button>
              <Grid container>
                <Grid item>
                  <Typography variant="body2" style={{color:'#858585'}}>
                    Already have an account?{" "}
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