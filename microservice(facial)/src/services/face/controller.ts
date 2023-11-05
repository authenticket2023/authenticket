import { NextFunction, Request, Response } from 'express';
import '@tensorflow/tfjs-node';

const canvas = require('canvas');
const path = require("path");
const faceapi = require("@vladmandic/face-api/dist/face-api.node.js");
const faceApiService = require("../../services/utils/faceapiService");
const FaceModel = require('../../models/face');
const baseDir = path.resolve(__dirname, "../../..");
const request = require('request-promise-native');
const FormData = require('form-data');

//Checking the crypto module
const crypto = require('crypto');
const algorithm = 'aes-256-cbc'; //Using AES encryption

// encryption_key and IV
const iv = 'b26f905e15f3ee690c0df4e9dac0e0ca';
const encryption_key = 'f5f79a2729f9b661c6d6bcf2a520ddd1203010ec4d2984f387d32eef0f868f4d';
//need to convert to Buffer for Initialization Vector(IV) and key
const bufferIV = Buffer.from(iv, 'hex');
const bufferKey = Buffer.from(encryption_key, 'hex');

//Encrypting Facial Data
function encrypt(data: any) {
    let cipher = crypto.createCipheriv('aes-256-cbc', bufferKey, bufferIV);
    let encrypted = cipher.update(data);
    encrypted = Buffer.concat([encrypted, cipher.final()]);
    return { encryptedData: encrypted.toString('hex') };
}

// Decrypting Facial Data
function decrypt(data: any) {
    let encryptedText = Buffer.from(data.encryptedData, 'hex');
    let decipher = crypto.createDecipheriv('aes-256-cbc', bufferKey, bufferIV);
    let decrypted = decipher.update(encryptedText);
    decrypted = Buffer.concat([decrypted, decipher.final()]);
    return decrypted.toString();
}

async function verifyToken(token: any, email: any) {
    const apiUrl = `${process.env.MAIN_PRODUCTION_BACKEND_HOST}/api/v2/auth/token-verification`;
    const formData = new FormData();
    formData.append('jwtToken', token);
    formData.append('userEmail', email);
    const options = {
        uri: apiUrl,
        method: 'POST',
        body: formData,
        headers: formData.getHeaders(),
        resolveWithFullResponse: true,
    };
    try {
        const response = await request(options);
        if (response.statusCode === 200) {
            return true;
        } else {
            throw new Error(`${response.message}`);
        }
    } catch (error: any) {
        throw new Error(`${error.message}`);
    }
}

async function uploadLabeledImages(index: any, images: any, eventID: any, label: any, sectionID: any, row: any, seat: any): Promise<boolean> {
    try {
        console.log(`========== uploadLabeledImages for image ${index} ==========`)

        const descriptions = [];
        const img = await canvas.loadImage(images);
        const numOfFaces = await faceapi.detectAllFaces(img).withFaceLandmarks().withFaceDescriptors();
        console.log(`Detected ${numOfFaces.length} faces in the image ${index}.`)
        if (numOfFaces.length != 1) {
            throw new Error(`Detected ${numOfFaces.length} faces in the image. Please choose another image with only one facical in it!`);
        }

        // Read each face and save the face descriptions in the descriptions array
        const detections = await faceapi.detectSingleFace(img)
            .withFaceLandmarks()
            .withFaceDescriptor();

        const floatArrayString = JSON.stringify(Array.from(detections.descriptor))
        var encryptedFacialData = encrypt(floatArrayString);
        descriptions.push(encryptedFacialData);
        // Create a new face document with the given label and save it in DB
        const createFace = new FaceModel({
            eventID: eventID,
            sectionID: sectionID,
            row: row,
            seat: seat,
            label: `${label}(${sectionID}-${row}-${seat})`,
            descriptions: descriptions,
        });
        try {
            await createFace.save();
            console.log('Data saved successfully.');
            return true;
        } catch (error) {
            // mean facial info was existed in DB
            return false;
        }
    } catch (error) {
        throw error;
    }
}

interface Files {
    image1: any;
    image2?: any;
    image3?: any;
    image4?: any;
    image5?: any;
}

export const createFacialInfo = async (req: any, res: any, next: NextFunction) => {
    try {
        console.log('Creating Facial Information');
        const token = req.headers.authorization && req.headers.authorization.split(" ")[1];
        const { image1, image2, image3, image4, image5 }: Files = req.files;
        const { email, eventID, info1, info2, info3, info4, info5 } = req.body;
        //check if token is valid and match with the email
        try {
            const isTokenValid = await verifyToken(token, email);
        } catch (error: any) {
            //to remove all the \\ and \" in the message
            return res.status(403).json({ message: error.message.replace(/\\/g, '').replace(/"/g, ' ') });
        }

        const info1Array = info1.split(',');
        if (info1Array.length != 4) {
            throw new Error(`Invalid info1 field! Please follow the following format : {label,sectionID,row,seat}.`);
        }
        let logMessage = '';

        const result = await uploadLabeledImages('1', image1.data, eventID, info1Array[0], info1Array[1], info1Array[2], info1Array[3]);

        if (!result) {
            logMessage += `Facial data for image 1 already existed in DB. Skipped!\n`;
        }

        if (image2 != null) {
            const info2Array = info2.split(',');
            if (info2Array.length != 4) {
                throw new Error(`Invalid info2 field! Please follow the following format : {label,sectionID,row,seat}.`);
            }
            const result = await uploadLabeledImages('2', image2.data, eventID, info2Array[0], info2Array[1], info2Array[2], info2Array[3]);
            if (!result) {
                logMessage += `Facial data for image 2 already existed in DB. Skipped!\n`;
            }
        }

        if (image3 != null) {
            const info3Array = info3.split(',');
            if (info3Array.length != 4) {
                throw new Error(`Invalid info3 field! Please follow the following format : {label,sectionID,row,seat}.`);
            }
            const result = await uploadLabeledImages('3', image3.data, eventID, info3Array[0], info3Array[1], info3Array[2], info3Array[3]);
            if (!result) {
                logMessage += `Facial data for image 3 already existed in DB. Skipped!\n`;
            }
        }

        if (image4 != null) {
            const info4Array = info4.split(',');
            if (info4Array.length != 4) {
                throw new Error(`Invalid info4 field! Please follow the following format : {label,sectionID,row,seat}.`);
            }
            const result = await uploadLabeledImages('4', image4.data, eventID, info4Array[0], info4Array[1], info4Array[2], info4Array[3]);
            if (!result) {
                logMessage += `Facial data for image 4 already existed in DB. Skipped!\n`;
            }
        }

        if (image5 != null) {
            const info5Array = info5.split(',');
            if (info5Array.length != 4) {
                throw new Error(`Invalid info5 field! Please follow the following format : {label,sectionID,row,seat}.`);
            }
            const result = await uploadLabeledImages('5', image5.data, eventID, info5Array[0], info5Array[1], info5Array[2], info5Array[3]);
            if (!result) {
                logMessage += `Facial data for image 5 already existed in DB. Skipped!\n`;
            }
        }

        return res.status(200).json({
            message: `Facial data stored successfully for event ID: ${eventID}`,
            log: logMessage,
        })

    } catch (error: any) {
        return res.status(400).json({ message: String(error.message) });
    }
}

async function getDescriptorsFromDB(file: any, eventID: any) {
    try {
        console.log('Getting descriptors from DB.');
        // Get all the face data from mongodb based on the eventID
        let faces = await FaceModel.find({ 'eventID': eventID });
        if (faces.length == 0) {
            throw new Error(`No faces found in the database for event ID : ${eventID} !`);
        }

        for (let i = 0; i < faces.length; i++) {
            // Change the face data descriptors from Objects to Float32Array type
            for (let j = 0; j < faces[i].descriptions.length; j++) {
                //decode the facial data
                var decodedString = decrypt(faces[i].descriptions[j]);
                var facialData = new Float32Array(JSON.parse(decodedString));
                faces[i].descriptions[j] = new Float32Array(Object.values(facialData));
            }
            // Turn the DB face docs to
            faces[i] = new faceapi.LabeledFaceDescriptors(faces[i].label, faces[i].descriptions);
        }

        // Load face matcher to find the matching face
        const faceMatcher = new faceapi.FaceMatcher(faces, 0.6);

        // Read the image using canvas or other method
        const img = await canvas.loadImage(file);

        let temp = faceapi.createCanvasFromMedia(img);
        // Process the image for the model
        const displaySize = { width: img.width, height: img.height };
        faceapi.matchDimensions(temp, displaySize);

        // Find matching faces
        const detections = await faceapi.detectAllFaces(img).withFaceLandmarks().withFaceDescriptors();
        console.log(`Detected ${detections.length} faces in the image.`);

        if (detections.length != 1) {
            throw new Error(`Detected ${detections.length} faces in the image. Please make sure the image only contain 1 face!`);
        }

        const resizedDetections = faceapi.resizeResults(detections, displaySize);
        const results = resizedDetections.map((d: any) => faceMatcher.findBestMatch(d.descriptor));
        console.log(`For Event ID : ${eventID}`);
        results.map((item: any) => {
            console.log(`Found : ${item._label}`);
        });

        return results;

    } catch (error) {
        throw error;
    }
}

export const facialVerification = async (req: any, res: any, next: NextFunction) => {
    try {
        const token = req.headers.authorization && req.headers.authorization.split(" ")[1];
        const { image } = req.files;
        const { eventID, email } = req.body;
        //check if token is valid and match with the email
        try {
            const isTokenValid = await verifyToken(token, email);
        } catch (error: any) {
            //to remove all the \\ and \" in the message
            return res.status(403).json({ message: error.message.replace(/\\/g, '').replace(/"/g, ' ') });
        }


        let result = await getDescriptorsFromDB(image.data, eventID);

        return res.status(200).json({
            message: `Facial information found : ${result[0]._label}`
        });

    } catch (error: any) {
        return res.status(400).json({ message: String(error.message) });
    }

};

export const checkImage = async (req: any, res: any, next: NextFunction) => {
    try {
        console.log("========= Calling checkImage =========");
        const token = req.headers.authorization && req.headers.authorization.split(" ")[1];
        const { image } = req.files;
        const { email } = req.body;

        //check if token is valid and match with the email
        try {
            const isTokenValid = await verifyToken(token, email);
        } catch (error: any) {
            //to remove all the \\ and \" in the message
            return res.status(403).json({ message: error.message.replace(/\\/g, '').replace(/"/g, ' ') });
        }

        const img = await canvas.loadImage(image.data);
        const numOfFaces = await faceapi.detectAllFaces(img).withFaceLandmarks().withFaceDescriptors();
        if (numOfFaces.length != 1) {
            throw new Error(`Detected ${numOfFaces.length} faces in the image. Please choose another image with only one facical in it!`);
        }
        return res.status(200).json({
            message: `Detected ${numOfFaces.length} faces in the image. Image valid!`
        });
    } catch (error: any) {
        return res.status(400).json({ message: String(error.message) });
    }

};