import bodyParser from 'body-parser';
import express from 'express';
import logger from 'morgan';
import * as db from './database';
import * as requests from './models/requests';
import * as responses from './models/responses';

const apiPort = 8080;
const app = express();
const router = express.Router();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(logger('dev'));

router.get('/createUser', (request, response) => {
  const createUserRequest: requests.ICreateUser = {
    doctorID: request.body.doctorID,
    firstName: request.body.firstName,
    lastName: request.body.lastName,
    mobileNumber: request.body.mobileNumber,
    photoDataUrl: request.body.photoDataUrl,
    password: request.body.password,
  };

  const createUserResponse: Promise<responses.ICreateUser> = db.createUser(
    createUserRequest
  );

  createUserResponse
    .then(result => {
      if (result.success) {
        response.status(200).send({
          success: 'true',
          message: 'Request successful',
          result: result.userID,
        });
      } else {
        response.status(200).send({
          success: 'false',
          message: 'Request unsuccessful',
        });
      }
    })
    .catch(error => {
      response.status(200).send({
        success: 'false',
        message: 'Request unsuccessful' + error,
      });
    });
});

router.get('/getUserProfile', (request, response) => {
  const getUserProfileRequest: requests.IGetUserProfile = {
    userID: request.body.userID,
  };

  const getUserProfileResponse: Promise<responses.IGetUserProfile> = db.getUserProfile(
    getUserProfileRequest
  );

  getUserProfileResponse
    .then(result => {
      if (result.success) {
        delete result.success;
        response.status(200).send({
          success: 'true',
          message: 'Request successful',
          result,
        });
      } else {
        response.status(200).send({
          success: 'false',
          message: 'Request unsuccessful',
        });
      }
    })
    .catch(error => {
      response.status(200).send({
        success: 'false',
        message: 'Request unsuccessful' + error,
      });
    });
});

router.get('/createDoctor', (request, response) => {
  const createDoctorRequest: requests.ICreateDoctor = {
    firstName: request.body.firstName,
    lastName: request.body.lastName,
    licenseNumber: request.body.licenseNumber,
    clinicID: request.body.clinicID,
    email: request.body.email,
    username: request.body.username,
    password: request.body.password,
  };

  const createDoctorResponse: Promise<responses.ICreateDoctor> = db.createDoctor(
    createDoctorRequest
  );

  createDoctorResponse
    .then(result => {
      if (result.success) {
        response.status(200).send({
          success: 'true',
          message: 'Request successful',
          result: result.doctorID,
        });
      } else {
        response.status(200).send({
          success: 'false',
          message: 'Request unsuccessful',
        });
      }
    })
    .catch(error => {
      response.status(200).send({
        success: 'false',
        message: 'Request unsuccessful' + error,
      });
    });
});

app.use('/api', router);
app.disable('etag');

app.listen(apiPort, () => console.log(`LISTENING ON PORT ${apiPort}/api`));
