import express from 'express';
import jwt from 'express-jwt';
import * as controller from './controller';

export const faceRouter = express.Router();

/** GET /api/getInfo */

faceRouter.route('/createFacialInfo').post(controller.createFacialInfo);

faceRouter.route('/facialVerification').post(controller.facialVerification);