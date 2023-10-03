import cors from 'cors';
import express from 'express';
import { services } from './services';
import 'dotenv/config'
const bodyParser = require('body-parser')

//to access the variable in .env file as : process.env.{variableName}
const app = express();

// Middlewares
app.use(express.json());
app.use( bodyParser.json({limit: '50mb'}) );
app.use(bodyParser.urlencoded({
  limit: '50mb',
  extended: true,
  parameterLimit:50000
}));
app.use(cors());

// Mount REST on /api
app.use('/api', services);

const port = process.env.PORT || 4242;

app.listen(port, () => {
	console.log(`Express app listening on localhost:${port}`);
  });