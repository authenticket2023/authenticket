import express from 'express';
import * as controller from './controller';

export const faceRouter = express.Router();

/** POST /api/checkImage */
faceRouter.route('/checkImage').post(controller.checkImage);
/** POST /api/createFacialInfo */
faceRouter.route('/createFacialInfo').post(controller.createFacialInfo);
/** POST /api/facialVerification */
faceRouter.route('/facialVerification').post(controller.facialVerification);