import express from 'express';
import * as controller from './controller';

export const faceRouter = express.Router();

/** POST /api/image-verification */
faceRouter.route('/image-verification').post(controller.checkImage);
/** POST /api/facial-creation */
faceRouter.route('/facial-creation').post(controller.createFacialInfo);
/** POST /api/facial-verification */
faceRouter.route('/facial-verification').post(controller.facialVerification);
/** DELETE /api/facial-deletion */
faceRouter.route('/facial-deletion').delete(controller.deleteFacialInfo);