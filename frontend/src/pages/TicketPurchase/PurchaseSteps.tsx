import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { NavbarNotLoggedIn, NavbarLoggedIn } from '../../Navbar';
import { useParams } from 'react-router-dom';
import { Box } from '@mui/system';
import { Grid, List, ListItem, StepButton, stepClasses, Typography } from '@mui/material';
import { Button } from 'react-bootstrap';
import { styled } from '@mui/material/styles';
import Stack from '@mui/material/Stack';
import Stepper from '@mui/material/Stepper';
import Step from '@mui/material/Step';
import StepLabel from '@mui/material/StepLabel';
import Check from '@mui/icons-material/Check';
import StepConnector, { stepConnectorClasses } from '@mui/material/StepConnector';
import { StepIconProps } from '@mui/material/StepIcon';
import { Countertops } from '@mui/icons-material';
import { Sheet } from '@mui/joy';
import { SelectSeats, EnterDetailsFace, EnterDetails, Confirmation, ConfirmationFace, Payment } from './steps';

const QontoConnector = styled(StepConnector)(({ theme }) => ({
    [`&.${stepConnectorClasses.alternativeLabel}`]: {
      top: 10,
      left: 'calc(-50% + 16px)',
      right: 'calc(50% + 16px)',
    },
    [`&.${stepConnectorClasses.active}`]: {
      [`& .${stepConnectorClasses.line}`]: {
        borderColor: '#FF5C35',
      },
    },
    [`&.${stepConnectorClasses.completed}`]: {
      [`& .${stepConnectorClasses.line}`]: {
        borderColor: '#FF5C35',
      },
    },
    [`& .${stepConnectorClasses.line}`]: {
      borderColor: theme.palette.mode === 'dark' ? theme.palette.grey[800] : '#eaeaf0',
      borderTopWidth: 3,
      borderRadius: 1,
    },
  }));
  
  const QontoStepIconRoot = styled('div')<{ ownerState: { active?: boolean } }>(
    ({ theme, ownerState }) => ({
      color: theme.palette.mode === 'dark' ? theme.palette.grey[700] : '#eaeaf0',
      display: 'flex',
      height: 22,
      alignItems: 'center',
      ...(ownerState.active && {
        color: '#FF5C35',
      }),
      '& .QontoStepIcon-completedIcon': {
        color: '#784af4',
        zIndex: 1,
        fontSize: 18,
      },
      '& .QontoStepIcon-circle': {
        width: 8,
        height: 8,
        borderRadius: '50%',
        backgroundColor: 'currentColor',
      },
    }),
  );
  
  function QontoStepIcon(props: StepIconProps) {
    const { active, completed, className } = props;
  
    return (
      <QontoStepIconRoot ownerState={{ active }} className={className}>
        {completed ? (
          <Check className="QontoStepIcon-completedIcon" style={{color:'#FF5C35'}}/>
        ) : (
          <div className="QontoStepIcon-circle" />
        )}
      </QontoStepIconRoot>
    );
  }

//   steps for normal process
  const steps = ['Select Seats', 'Enter Details', 'Confirmation', 'Payment'];
//   steps for process with facial recognition
  const stepsFace = ['Select Seats', 'Enter Details', 'Confirmation', 'Payment'];


// Process without facial recognition
export const PurchaseSteps = (props: any) => {

    useEffect(() => {
        loadSectionDetails();
    }, []);

    const [activeStep, setActiveStep] = React.useState(0);
    const [completed, setCompleted] = React.useState<{
        [k: number]: boolean;
    }>({});
    const [isClicked, setIsClicked] = useState(false);

    //first step
    const [quantity, setQuantity] = useState(''); 
    const [selectedSection, setSelectedSection] = useState('');
    const [sectionDetails, setSectionDetails] = React.useState<any[]>([]);

    //second step
    const [selectedFiles, setSelectedFiles]: any = useState(null);
    const [enteredData, setEnteredData] = useState<{ names: string[] }>({
        names: [],  // Store entered names here
    });

    //third step

    //fetch section details
    const loadSectionDetails = async () => {
        // //calling backend API
        fetch(`${process.env.REACT_APP_BACKEND_URL}/public/event/section-ticket-details/${props.eventDetails.eventId}`, {
            headers: {
              'Content-Type': 'application/json',
            },
            method: 'GET',
          })
            .then(async (response) => {
              if (response.status == 200) {
                const apiResponse = await response.json();
                const data = apiResponse.data;
                setSectionDetails(data);
  
              } else {
                //passing to parent component
              }
            })
            .catch((err) => {
              window.alert(err);
            });
    }

    const handleQuantityChange = (newQuantity: string) => {
        setQuantity(newQuantity);
    };

    const handleSelectedSection = (section: string) => {
        setSelectedSection(section);
    };

    const totalSteps = () => {
        return steps.length;
    };

    const completedSteps = () => {
        return Object.keys(completed).length;
    };

    const isLastStep = () => {
        return activeStep === totalSteps() - 1;
    };

    const allStepsCompleted = () => {
        return completedSteps() === totalSteps();
    };

    const handleNext = () => {
        const newActiveStep =
            isLastStep() && !allStepsCompleted()
                ? // It's the last step, but not all steps have been completed,
                // find the first step that has been completed
                steps.findIndex((step, i) => !(i in completed))
                : activeStep + 1;
        setActiveStep(newActiveStep);
    };

    const handleBack = () => {
        setActiveStep((prevActiveStep) => prevActiveStep - 1);
    };

    const handleStep = (step: number) => () => {
        setActiveStep(step);
    };

    const handleComplete = () => {
        const newCompleted = completed;
        newCompleted[activeStep] = true;
        setCompleted(newCompleted);
        handleNext();
    };

    const handleReset = () => {
        setActiveStep(0);
        setCompleted({});
    };

    //for pop up message => error , warning , info , success
    const [openSnackbar, setOpenSnackbar] = useState(false);
    const [alertType, setAlertType]: any = useState('info');
    const [alertMsg, setAlertMsg] = useState('');
    const handleSnackbarClose = () => {
        setOpenSnackbar(false);
    };

    //handle purchase
    const handlePurchase = () => {

    }

    const handleSectionDetails = (details: string[]) => {
        setSectionDetails(details);
    }

    // Function to update the enteredData state
    const updateEnteredData = ( names: string[]) => {
        setEnteredData({ names });
    };

    return (
        <div>
            <Stepper alternativeLabel activeStep={activeStep} connector={<QontoConnector />}>
                {steps.map((label, index) => (
                <Step key={label} completed={completed[index]}
                sx={{
                    '& .MuiStepLabel-root .Mui-active': {
                        color: '#FF5C35', // circle color (ACTIVE)
                      },
                      '& .MuiStepLabel-root .Mui-completed': {
                        color: '#FF5C35', // circle color (COMPLETED)
                      },
                }}>
                    <StepButton onClick={handleStep(index)} >
                        {label}
                    </StepButton>
                    {/* <StepLabel StepIconComponent={QontoStepIcon}>{label}</StepLabel> */}
                </Step>
                ))}
            </Stepper>

            <div>
                {allStepsCompleted() ? (
                        <React.Fragment>
                            <Typography sx={{ mt: 2, mb: 1 }}>
                                All steps completed - you&apos;re finished
                            </Typography>
                            <Sheet sx={{ alignItems: "center", mt: 2, mb: 2 }}>
                                <Button onClick={handlePurchase} variant="contained" style={{textTransform: "none", fontSize: "16px" }} disabled={isClicked}>
                                    {isClicked ? 'Processing Purchase' : 'Purchase Successful'}
                                </Button>
                            </Sheet>
                        </React.Fragment>
                    ) : (
                        <React.Fragment>

                            {activeStep == 0 ? <SelectSeats
                                categoryDetails={props.categoryDetails}
                                eventDetails={props.eventDetails}
                                sectionDetails={sectionDetails}
                                onQuantityChange={handleQuantityChange}
                                selectedSection={selectedSection}
                                onSelectedSection={handleSelectedSection}
                                onSectionDetails={handleSectionDetails}
                                handleComplete={handleComplete}
                                setOpenSnackbar={setOpenSnackbar} setAlertType={setAlertType} setAlertMsg={setAlertMsg} />
                                : null}

                            {activeStep == 1 ? <EnterDetails
                                categoryDetails={props.categoryDetails}
                                eventDetails={props.eventDetails}
                                quantity={quantity}
                                selectedSection={selectedSection}
                                selectedFiles={selectedFiles}
                                setSelectedFiles={setSelectedFiles}
                                updateEnteredData={updateEnteredData} // Pass the function to EnterDetails
                                handleComplete={handleComplete}
                                setOpenSnackbar={setOpenSnackbar} setAlertType={setAlertType} setAlertMsg={setAlertMsg}
                            /> : null}

                            {activeStep == 2 ? <Confirmation
                                categoryDetails={props.categoryDetails}
                                eventDetails={props.eventDetails}
                                quantity={quantity}
                                selectedSection={selectedSection}
                                enteredData={enteredData} // Pass the names to Confirmation
                                sectionDetails={sectionDetails}
                                handleComplete={handleComplete}
                                setOpenSnackbar={setOpenSnackbar} setAlertType={setAlertType} setAlertMsg={setAlertMsg}
                            /> : null}

                            {activeStep == 3 ? <Payment
                                
                                handleComplete={handleComplete}
                                setOpenSnackbar={setOpenSnackbar} setAlertType={setAlertType} setAlertMsg={setAlertMsg}
                            /> : null}

                            <Box sx={{ display: 'flex', flexDirection: 'row', pt: 2 }}>
                                {/* <Button
                                    color="inherit"
                                    disabled={activeStep === 0}
                                    onClick={handleBack}
                                    sx={{ mr: 1 }}
                                >
                                    Back
                                </Button>
                                <Box sx={{ flex: '1 1 auto' }} />
                                <Button onClick={handleNext} sx={{ mr: 1 }}>
                                    Next
                                </Button> */}
                                {activeStep !== steps.length &&
                                    (completed[activeStep] ? (
                                        <Typography variant="caption" sx={{ display: 'inline-block' }}>
                                            Step {activeStep + 1} already completed
                                        </Typography>
                                    ) : (
                                        null
                                    ))}
                            </Box>
                        </React.Fragment>
                    )}
            </div>
        </div>
    )
}

// process with facial recognition
export const PurchaseStepsFace = (props: any) => {

    useEffect(() => {
        loadSectionDetails();
    }, []);

    const [activeStep, setActiveStep] = React.useState(0);
    const [completed, setCompleted] = React.useState<{
        [k: number]: boolean;
    }>({});
    const [isClicked, setIsClicked] = useState(false);

    //first step
    const [quantity, setQuantity] = useState(''); // State variable for quantity
    const [selectedSection, setSelectedSection] = useState('');
    const [sectionDetails, setSectionDetails] = React.useState<any[]>([]);

    //second step
    const [selectedFiles, setSelectedFiles]: any = useState(null);
    const [enteredData, setEnteredData] = useState<{ images: File[], names: string[] }>({
        images: [], // Store uploaded images here
        names: [],  // Store entered names here
    });

    //fetch section details
    const loadSectionDetails = async () => {
        // //calling backend API
        fetch(`${process.env.REACT_APP_BACKEND_URL}/public/event/section-ticket-details/${props.eventDetails.eventId}`, {
            headers: {
              'Content-Type': 'application/json',
            },
            method: 'GET',
          })
            .then(async (response) => {
              if (response.status == 200) {
                const apiResponse = await response.json();
                const data = apiResponse.data;
                setSectionDetails(data);
              }
            })
            .catch((err) => {
              window.alert(err);
            });
    }

    const handleSectionDetails = (details: string[]) => {
        setSectionDetails(details);
    }

    const handleQuantityChange = (newQuantity: string) => {
        setQuantity(newQuantity);
    };

    const handleSelectedSection = (section: string) => {
        setSelectedSection(section);
    };

    const totalSteps = () => {
        return stepsFace.length;
    };

    const completedSteps = () => {
        return Object.keys(completed).length;
    };

    const isLastStep = () => {
        return activeStep === totalSteps() - 1;
    };

    const allStepsCompleted = () => {
        return completedSteps() === totalSteps();
    };

    const handleNext = () => {
        const newActiveStep =
            isLastStep() && !allStepsCompleted()
                ? // It's the last step, but not all steps have been completed,
                // find the first step that has been completed
                stepsFace.findIndex((step, i) => !(i in completed))
                : activeStep + 1;
        setActiveStep(newActiveStep);
    };

    const handleBack = () => {
        setActiveStep((prevActiveStep) => prevActiveStep - 1);
    };

    const handleStep = (step: number) => () => {
        setActiveStep(step);
    };

    const handleComplete = () => {
        const newCompleted = completed;
        newCompleted[activeStep] = true;
        setCompleted(newCompleted);
        handleNext();
    };

    const handleReset = () => {
        setActiveStep(0);
        setCompleted({});
    };

    //for pop up message => error , warning , info , success
    const [openSnackbar, setOpenSnackbar] = useState(false);
    const [alertType, setAlertType]: any = useState('info');
    const [alertMsg, setAlertMsg] = useState('');
    const handleSnackbarClose = () => {
        setOpenSnackbar(false);
    };

    //handle purchase
    const handlePurchase = () => {

    }

    // Function to update the enteredData state
    const updateEnteredData = (images: File[], names: string[]) => {
        setEnteredData({ images, names });
    };

    return (
        <div>
            <Stepper alternativeLabel activeStep={activeStep} connector={<QontoConnector />}>
                {stepsFace.map((label, index) => (
                <Step key={label} completed={completed[index]}
                sx={{
                    '& .MuiStepLabel-root .Mui-active': {
                        color: '#FF5C35', // circle color (ACTIVE)
                      },
                      '& .MuiStepLabel-root .Mui-completed': {
                        color: '#FF5C35', // circle color (COMPLETED)
                      },
                }}>
                    <StepButton onClick={handleStep(index)} >
                        {label}
                    </StepButton>
                    {/* <StepLabel StepIconComponent={QontoStepIcon}>{label}</StepLabel> */}
                </Step>
                ))}
            </Stepper>

            <div>
                {allStepsCompleted() ? (
                        <React.Fragment>
                            <Typography sx={{ mt: 2, mb: 1 }}>
                                All steps completed - you&apos;re finished
                            </Typography>
                            <Sheet sx={{ alignItems: "center", mt: 2, mb: 2 }}>
                                <Button onClick={handlePurchase} variant="contained" style={{textTransform: "none", fontSize: "16px" }} disabled={isClicked}>
                                    {isClicked ? 'Processing Purchase' : 'Purchase Successful'}
                                </Button>
                            </Sheet>
                        </React.Fragment>
                    ) : (
                        <React.Fragment>

                            {activeStep == 0 ? <SelectSeats
                                categoryDetails={props.categoryDetails}
                                eventDetails={props.eventDetails}
                                onQuantityChange={handleQuantityChange}
                                selectedSection={selectedSection}
                                onSelectedSection={handleSelectedSection}
                                onSectionDetails={handleSectionDetails}
                                sectionDetails={sectionDetails}
                                handleComplete={handleComplete}
                                setOpenSnackbar={setOpenSnackbar} setAlertType={setAlertType} setAlertMsg={setAlertMsg} />
                                : null}

                            {activeStep == 1 ? <EnterDetailsFace
                                categoryDetails={props.categoryDetails}
                                eventDetails={props.eventDetails}
                                quantity={quantity}
                                selectedSection={selectedSection}
                                selectedFiles={selectedFiles}
                                setSelectedFiles={setSelectedFiles}
                                updateEnteredData={updateEnteredData} // Pass the function to EnterDetails
                                handleComplete={handleComplete}
                                setOpenSnackbar={setOpenSnackbar} setAlertType={setAlertType} setAlertMsg={setAlertMsg}
                            /> : null}
                            
                            {activeStep == 2 ? <ConfirmationFace
                                categoryDetails={props.categoryDetails}
                                eventDetails={props.eventDetails}
                                quantity={quantity}
                                selectedSection={selectedSection}
                                sectionDetails={sectionDetails}
                                enteredData={enteredData} // Pass the images and names to ConfirmationFace

                                handleComplete={handleComplete}
                                setOpenSnackbar={setOpenSnackbar} setAlertType={setAlertType} setAlertMsg={setAlertMsg}
                            /> : null}

                            {activeStep == 3 ? <Payment
                                
                                handleComplete={handleComplete}
                                setOpenSnackbar={setOpenSnackbar} setAlertType={setAlertType} setAlertMsg={setAlertMsg}
                            /> : null}

                            <Box sx={{ display: 'flex', flexDirection: 'row', pt: 2 }}>
                                {/* <Button
                                    color="inherit"
                                    disabled={activeStep === 0}
                                    onClick={handleBack}
                                    sx={{ mr: 1 }}
                                >
                                    Back
                                </Button>
                                <Box sx={{ flex: '1 1 auto' }} />
                                <Button onClick={handleNext} sx={{ mr: 1 }}>
                                    Next
                                </Button> */}
                                {activeStep !== steps.length &&
                                    (completed[activeStep] ? (
                                        <Typography variant="caption" sx={{ display: 'inline-block' }}>
                                            Step {activeStep + 1} already completed
                                        </Typography>
                                    ) : (
                                        null
                                    ))}
                            </Box>
                        </React.Fragment>
                    )}
            </div>
        </div>
    )
}