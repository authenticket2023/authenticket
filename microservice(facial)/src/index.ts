import cors from 'cors';
import express from 'express';
import { services } from './services';
import 'dotenv/config';

const faceapi = require("@vladmandic/face-api/dist/face-api.node.js");
const fileupload = require("express-fileupload");
const bodyParser = require('body-parser')
const mongoose = require("mongoose");
const { Canvas, Image } = require("canvas");
const canvas = require("canvas");

//to access the variable in .env file as : process.env.{variableName}
const app = express();

// Middlewares
app.use(express.json());
app.use(fileupload());
app.use( bodyParser.json({limit: '50mb'}) );
app.use(bodyParser.urlencoded({
  limit: '50mb',
  extended: true,
  parameterLimit:50000
}));
app.use(cors());

// Mount REST on /api
app.use('/api', services);

const port = process.env.PORT || 8000;

//connect to mongoDB
mongoose.connect(
	`mongodb+srv://authenticket:Qwerty123@authenticket.yibufxm.mongodb.net/AuthenTicket?retryWrites=true&w=majority`,
	{
		useNewUrlParser: true,
		useUnifiedTopology: true,
	}
).then(() => {
	app.listen(port, () => {
		console.log(`Express app listening on localhost:${port}`)
		console.log("DB connected and server is running.");
	});
}).catch((err: any) => {
	console.log(err);
});

//load the face api model
const faceDetectionNet = faceapi.nets.ssdMobilenetv1;
const minConfidence = 0.5;
const faceDetectionOptions = new faceapi.SsdMobilenetv1Options({ minConfidence });

async function LoadModels() {
	// Load the models
	// __dirname gives the root directory of the server
	await faceDetectionNet.loadFromDisk(__dirname + "/faceAPIModel");
	await faceapi.nets.faceRecognitionNet.loadFromDisk(__dirname + "/faceAPIModel");
	await faceapi.nets.faceLandmark68Net.loadFromDisk(__dirname + "/faceAPIModel");
	await faceapi.nets.ssdMobilenetv1.loadFromDisk(__dirname + "/faceAPIModel");
}
LoadModels();
