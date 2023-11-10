import express from 'express';
import * as controller from './controller';

export const paymentRouter = express.Router();

/** POST /api/payment/create-checkout-session */
paymentRouter.route('/create-checkout-session').post(controller.createCheckOutSession);