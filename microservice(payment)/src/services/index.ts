import express from 'express';
import { paymentRouter } from './payment';

export const services = express.Router();

services.use('/payment', paymentRouter);
