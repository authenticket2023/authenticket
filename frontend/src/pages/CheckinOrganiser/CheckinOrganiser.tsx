import Webcam from 'react-webcam';
import { CameraOptions, useFaceDetection } from 'react-use-face-detection';
import FaceDetection from '@mediapipe/face_detection';
import { Camera } from '@mediapipe/camera_utils';
import { NavbarOrganiser } from '../../Navbar';
import { Navigate } from 'react-router-dom';
import { Box, Button, Grid, ImageList, ImageListItem } from '@mui/material';
import { useCallback, useEffect, useState } from 'react';

const width = 700;
const height = 700;

export const CheckinOrganiser = (): JSX.Element => {
    const { webcamRef, boundingBox, isLoading, detected, facesDetected }: any = useFaceDetection({
        faceDetectionOptions: {
            model: 'short',
        },
        faceDetection: new FaceDetection.FaceDetection({
            locateFile: (file) => `https://cdn.jsdelivr.net/npm/@mediapipe/face_detection/${file}`,
        }),
        camera: ({ mediaSrc, onFrame }: CameraOptions) =>
            new Camera(mediaSrc, {
                onFrame,
                width,
                height,
            }),
    });
    const token = window.localStorage.getItem('accessToken');
    const role = window.localStorage.getItem('role');
    const organiserId: any = window.localStorage.getItem('id');

    const [image, setImage]: any = useState(null);
    const capture = useCallback(() => {
        const imageSrc = webcamRef.current.getScreenshot();
        setImage(imageSrc);
    }, [webcamRef]);

    const handleCam = (event: any) => {
        console.log(webcamRef)
        capture();
        console.log(image)

    };

    return (
        <Box>
            {
                token != null && role == 'ORGANISER' ?
                    <Navigate to="/CheckinOrganiser" /> : <Navigate to="/Forbidden" />
            }
            < NavbarOrganiser />
            {/* <p>{`Loading: ${isLoading}`}</p>
            <p>{`Face Detected: ${detected}`}</p>
            <p>{`Number of faces detected: ${facesDetected}`}</p> */}
            <Button color="success" variant="contained" sx={{ mt: 5 }} onClick={handleCam}> Capture </Button>
            <Grid container spacing={1} sx={{ alignContent: "center", display: 'flex', justifyContent: 'center' }}>
                <Grid item xs={12} sm={5}>
                    <Box style={{ width, height, position: 'relative' }}>
                        {boundingBox.map((box: any, index: any) => (
                            <div
                                key={`${index + 1}`}
                                style={{
                                    border: '2px solid yellow',
                                    position: 'absolute',
                                    top: `${box.yCenter * 100}%`,
                                    left: `${box.xCenter * 100}%`,
                                    width: `${box.width * 100}%`,
                                    height: `${box.height * 100}%`,
                                    zIndex: 1,
                                }}
                            />
                        ))}
                        <Webcam
                            ref={webcamRef}
                            audio={false}
                            forceScreenshotSourceSize
                            style={{
                                height,
                                width,
                            }}
                        />
                    </Box>
                </Grid>
                {image ? (
                    <img src={image} alt="webcam" />
                ) : null}
            </Grid>
        </Box>
    );
};