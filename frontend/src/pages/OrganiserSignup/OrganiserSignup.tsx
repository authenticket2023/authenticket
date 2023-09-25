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

//image download
import backgroundImage from '../../images/background.png';

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
  const validateEmail = (email: any) => {
    // Regular expression to validate email format
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  };

  //variables
  const [companyEmail, setCompanyEmail] = useState('');
  const [companyName, setCompanyName] = useState('');
  const [companyDescription, setCompanyDescription] = useState('');
  //validation
  const [emailError, setEmailError] = useState(false);
  const [emailHelperText, setEmailHelperText] = useState('');
  //error , warning , info , success
  const [openSnackbar, setOpenSnackbar] = useState(false);
  const [alertType, setAlertType] : any= useState('info');
  const [alertMsg, setAlertMsg] = useState('');

  //handler method section
  const handleCompanyEmail = (e: any) => {
    setCompanyEmail(e.target.value);
  }
  const handleCompanyName = (e: any) => {
    setCompanyName(e.target.value);
  }
  const handleCompanyDescription = (e: any) => {
    setCompanyDescription(e.target.value);
  }

  const signupHandler = async (event: any) => {
    event.preventDefault();

    //checking validations, returning error message if conditions not met
    if (!validateEmail(companyEmail)) {
      setEmailError(true);
      setEmailHelperText('Please enter a valid email address');
      return;
    } else {
      setEmailError(false);
      setEmailHelperText('');
    }

    const formData = new FormData();
    formData.append('name', companyName);
    formData.append('email', companyEmail);
    formData.append('description', companyDescription);

    // //calling backend API
    fetch(`${process.env.REACT_APP_BACKEND_URL}/auth/eventOrgRegister`, {
      headers: {
      },
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
          setAlertMsg(`Email ${companyEmail} sign up successful! An email will be sent shortly`);
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
              <a href='/Home'>
                <img src={logo} alt="Logo" width={70} height={45} style={{marginLeft:0}} />
              </a>
              <Button sx={{color:'black', borderRadius:'18px', marginLeft:28}} href='/Login'>
                User
              </Button>
              <Button variant="outlined" sx={{borderColor:'black', borderRadius:'25px', color:'black'}} href='/AdminLogin'>
                Admin
              </Button>
            </div>

            <Typography component="h1" variant="h5" sx={{ fontWeight: 'bold', fontSize: 45, letterSpacing: -2, marginTop: 8, marginBottom: 1 }}>
              Register your Company
            </Typography>
            <form onSubmit={signupHandler}>
              <TextField
                margin="normal"
                required
                fullWidth
                id="CompanyName"
                label="Company Name"
                name="companyName"
                autoComplete="companyName"
                autoFocus
                size="medium"
                onChange={handleCompanyName}
              />
              <TextField
                margin="normal"
                required
                fullWidth
                id="CompanyEmail"
                label="Company Email"
                name="companyEmail"
                autoComplete="companyEmail"
                autoFocus
                size="medium"
                error={emailError}
                helperText={emailHelperText}
                onChange={handleCompanyEmail}
              />
              <TextField
                margin="normal"
                required
                fullWidth
                name="CompanyDescription"
                label="Company Description"
                type="companyDescription"
                id="companyDescription"
                autoComplete="companyDescription"
                rows={4}
                multiline
                // helperText={passwordHelperText}
                onChange={handleCompanyDescription}
              />
              <Button
                type="submit"
                fullWidth
                variant="contained"
                sx={{ mt: 3, mb: 2, backgroundColor: 'black' }}
              >
                Register Company
              </Button>
              <Grid container>
                <Grid item>
                  <Typography variant="body2" style={{color:'#858585'}}>
                    Already have a organiser account?{" "}
                    <Link href="/OrganiserLogin" variant="body2" style={{color:'#2E475D'}}>
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