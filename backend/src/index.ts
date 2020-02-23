import bodyParser from 'body-parser';
import cookieParser from 'cookie-parser';
import logger from 'morgan';

import * as db from './database';
import * as requests from './models/requests';
import * as responses from './models/responses';

import { join } from 'path';
import { config } from 'dotenv';
import { generate } from 'generate-password';
import { createTransport } from 'nodemailer';
import { hash } from 'bcrypt';

import express, { Express, Request, Response, NextFunction } from 'express';

import {
  initFirebase,
  createNewCookie,
  createNewUser,
  revokeToken,
  isAdmin,
  isDoctor,
} from './firebase/firebase';
import { FirebaseUser } from './firebase/types';

const apiPort = 8081;
const app = express();
const router = express.Router();
const pwEncryptSaltRounds = 10;

config();
initFirebase();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(logger('dev'));
app.use(express.static('src/public'));

const createPatient = (request: Request, response: Response) => {
  const createPatientRequest: requests.ICreatePatient = {
    doctorID: request.body.doctorID,
    firstName: request.body.firstName,
    lastName: request.body.lastName,
    height: request.body.height,
    pregnant: request.body.pregnant,
    mobileNumber: request.body.mobileNumber,
    photoDataUrl: request.body.photoDataUrl,
    password: request.body.password,
    bslUnit: request.body.bslUnit,
  };

  const createPatientResponse: Promise<responses.ICreatePatient> = db.createPatient(
    createPatientRequest
  );

  createPatientResponse
    .then(result => {
      response.status(200).send(result);
    })
    .catch(error => {
      response.status(200).send({
        success: false,
        message: 'Request unsuccessful, Error:' + error,
      });
    });

  router.post('/createPatient', (request, response) => {
    const createPatientRequest: requests.ICreatePatient = {
      doctorID: request.body.doctorID,
      firstName: request.body.firstName,
      lastName: request.body.lastName,
      height: request.body.height,
      pregnant: request.body.pregnant,
      mobileNumber: request.body.mobileNumber,
      photoDataUrl: request.body.photoDataUrl,
      password: request.body.password,
      bslUnit: request.body.bslUnit,
    };

    const createPatientResponse: Promise<responses.ICreatePatient> = db.createPatient(
      createPatientRequest
    );

    createPatientResponse
      .then(result => {
        response.status(200).send(result);
      })
      .catch(error => {
        response.status(200).send({
          success: false,
          message: 'Request unsuccessful, Error:' + error,
        });
      });
  });
};

router.get('/getPatientProfile', (request, response) => {
  const getPatientProfileRequest: requests.IGetPatientProfile = {
    patientID: request.body.patientID,
  };

  const getPatientProfileResponse: Promise<responses.IGetPatientProfile> = db.getPatientProfile(
    getPatientProfileRequest
  );

  getPatientProfileResponse
    .then(result => {
      response.status(200).send(result);
    })
    .catch(error => {
      response.status(200).send({
        success: false,
        message: 'Request unsuccessful, Error:' + error,
      });
    });
});

router.post('/storeRBP', (request, response) => {
  const storeRBPRequest: requests.IStoreRBP = {
    patientID: request.body.patientID,
    time: request.body.time,
    systole: request.body.systole,
    diastole: request.body.diastole,
  };

  const storeRBPResponse: Promise<responses.IStoreRBP> = db.storeRBP(
    storeRBPRequest
  );

  storeRBPResponse
    .then(result => {
      response.status(200).send(result);
    })
    .catch(error => {
      response.status(200).send({
        success: false,
        message: 'Request unsuccessful, Error:' + error,
      });
    });
});

router.post('/storeBSL', (request, response) => {
  const storeBSLRequest: requests.IStoreBSL = {
    patientID: request.body.patientID,
    time: request.body.time,
    value: request.body.value,
    unit: request.body.unit,
  };

  const storeBSLResponse: Promise<responses.IStoreBSL> = db.storeBSL(
    storeBSLRequest
  );

  storeBSLResponse
    .then(result => {
      response.status(200).send(result);
    })
    .catch(error => {
      response.status(200).send({
        success: false,
        message: 'Request unsuccessful, Error:' + error,
      });
    });
});

router.post('/storeWeight', (request, response) => {
  const storeWeightRequest: requests.IStoreWeight = {
    patientID: request.body.patientID,
    time: request.body.time,
    weightKG: request.body.weightKG,
  };

  const storeWeightResponse: Promise<responses.IStoreWeight> = db.storeWeight(
    storeWeightRequest
  );

  storeWeightResponse
    .then(result => {
      response.status(200).send(result);
    })
    .catch(error => {
      response.status(200).send({
        success: false,
        message: 'Request unsuccessful, Error:' + error,
      });
    });
});

router.get('/getGraphingData', (request, response) => {
  const getGraphingDataRequest: requests.IGetGraphingData = {
    patientID: request.body.patientID,
    intervalStart: request.body.intervalStart,
    intervalEnd: request.body.intervalEnd,
    bslUnit: request.body.bslUnit,
  };

  const getGraphingDataResponse: Promise<responses.IGetGraphingData> = db.getGraphingData(
    getGraphingDataRequest
  );

  getGraphingDataResponse
    .then(result => {
      response.status(200).send(result);
    })
    .catch(error => {
      response.status(200).send({
        success: false,
        message: 'Request unsuccessful, Error:' + error,
      });
    });
});

const createDoctor = async (request: Request, response: Response) => {
  const firstName = request.body.firstName;
  const lastName = request.body.lastName;
  const licenseNumber = request.body.licenseNumber;
  const clinicID = request.body.clinicID;
  const email = request.body.email;
  const username = request.body.username;

  // Generate password.
  const generatedPass = generate({
    length: 12,
    numbers: true,
    lowercase: true,
    uppercase: true,
  });

  // Send temporary password to new user (doctor).
  const transporter = createTransport({
    host: 'smtp.gmail.com',
    port: 465,
    secure: true,
    auth: {
      type: 'OAuth2',
      user: process.env.GMAIL_ADDRESS,
      refreshToken: process.env.GMAIL_REFRESH_TOKEN,
      clientId: process.env.GOOGLE_CLIENT_ID,
      clientSecret: process.env.GOOGLE_CLIENT_SECRET,
    },
  });

  const emailBody = [
    `Hi ${firstName}`,
    `You have a new account with Trocaire Diabetes App. Your temporary password is: ${generatedPass}.`,
    `from the Trocaire Team`,
  ].join('\n\n');

  const mailOptions = {
    from: process.env.GMAIL_ADDRESS,
    to: email,
    subject: 'You have a new account with Trocaire Diabetes App',
    text: emailBody,
  };

  // Encrypt generated password and add entry to db.
  hash(generatedPass, pwEncryptSaltRounds, (_, hash) => {
    const createDoctorRequest: requests.ICreateDoctor = {
      firstName: firstName,
      lastName: lastName,
      licenseNumber: licenseNumber,
      clinicID: clinicID,
      email: email,
      userName: username,
      password: hash,
    };

    const user: FirebaseUser = {
      email: email,
      temporaryPassword: generatedPass,
      isAdmin: false,
      isDoctor: true,
      displayName: [firstName, lastName].join(' '),
    };

    const createDoctorResponse: Promise<responses.ICreateDoctor> = db.createDoctor(
      createDoctorRequest
    );

    createDoctorResponse
      .then(async _result => {
        if (await createNewUser(user)) {
          transporter.sendMail(mailOptions, (error, info) => {
            if (error) {
              console.log('Error: ', error);
            } else {
              console.log('Email sent: ' + info.response);
            }
          });
          response.sendStatus(200);
        } else {
          response.sendStatus(500);
        }
      })
      .catch(error => {
        response.status(200).send({
          success: false,
          message: 'Request unsuccessful, Error:' + error,
        });
      });
  });
};

router.post('/updateDoctor', (request, response) => {
  const updateDoctorRequest: requests.IUpdateDoctor = {
    doctorID: request.body.doctorID,
    firstName: request.body.firstName,
    lastName: request.body.lastName,
    licenseNumber: request.body.licenseNumber,
    clinicID: request.body.clinicID,
    email: request.body.email,
    userName: request.body.username,
    password: request.body.password,
  };

  const updateDoctorResponse: Promise<responses.IUpdateDoctor> = db.updateDoctor(
    updateDoctorRequest
  );

  updateDoctorResponse
    .then(result => {
      response.status(200).send(result);
    })
    .catch(error => {
      response.status(200).send({
        success: false,
        message: 'Request unsuccessful, Error:' + error,
      });
    });
});

router.get('/getDoctorsPatients', (request, response) => {
  const getDoctorsPatientsRequest: requests.IListDoctorsPatients = {
    doctorID: request.body.doctorID,
  };

  const getDoctorsPatientsResponse: Promise<responses.IListDoctorsPatients> = db.listDoctorsPatients(
    getDoctorsPatientsRequest
  );

  getDoctorsPatientsResponse
    .then(result => {
      response.status(200).send(result);
    })
    .catch(error => {
      response.status(200).send({
        success: false,
        message: 'Request unsuccessful, Error:' + error,
      });
    });
});

router.get('/getDoctorProfile', (request, response) => {
  const getDoctorProfileRequest: requests.IGetDoctorProfile = {
    doctorID: request.body.doctorID,
  };

  const getDoctorsProfileResponse: Promise<responses.IGetDoctorProfile> = db.getDoctorProfile(
    getDoctorProfileRequest
  );

  getDoctorsProfileResponse
    .then(result => {
      response.status(200).send(result);
    })
    .catch(error => {
      response.status(200).send({
        success: false,
        message: 'Request unsuccessful, Error:' + error,
      });
    });
});

router.get('/getAllDoctorsAtClinic', (request, response) => {
  const getAllDoctorsAtClinicRequest: requests.IGetAllDoctorsAtClinic = {
    clinicID: request.body.clinicID,
  };

  const getAllDoctorsAtClinicResponse: Promise<responses.IGetAllDoctorsAtClinic> = db.getAllDoctorsAtClinic(
    getAllDoctorsAtClinicRequest
  );

  getAllDoctorsAtClinicResponse
    .then(result => {
      response.status(200).send(result);
    })
    .catch(error => {
      response.status(200).send({
        success: false,
        message: 'Request unsuccessful, Error:' + error,
      });
    });
});

router.get('/getAllClinics', (request, response) => {
  const getAllClinicsRequest: requests.IGetAllClinics = {
    doctorID: request.body.doctorID,
  };

  const getAllClinicsResponse: Promise<responses.IGetAllClinics> = db.getAllClinics(
    getAllClinicsRequest
  );

  getAllClinicsResponse
    .then(result => {
      response.status(200).send(result);
    })
    .catch(error => {
      response.status(200).send({
        success: false,
        message: 'Request unsuccessful, Error:' + error,
      });
    });
});

router.post('/createClinic', (request, response) => {
  const createClinicRequest: requests.ICreateClinic = {
    clinicName: request.body.clinicName,
  };

  const createClinicResponse: Promise<responses.ICreateClinic> = db.createClinic(
    createClinicRequest
  );

  createClinicResponse
    .then(result => {
      response.status(200).send(result);
    })
    .catch(error => {
      response.status(200).send({
        success: false,
        message: 'Request unsuccessful, Error:' + error,
      });
    });
});

const login = (_request: Request, response: Response) => {
  response.sendFile(join(__dirname + '/../src/private/login.html'));
};

const dashboard = (_request: Request, response: Response) => {
  response.sendFile(join(__dirname + '/../src/private/dashboard.html'));
};

const doctorSignup = (_request: Request, response: Response) => {
  response.sendFile(join(__dirname + '/../src/private/doctorSignup.html'));
};

const isAdminLoggedIn = async (
  request: Request,
  response: Response,
  next: NextFunction
) => {
  const sessionCookie = request.cookies.session || '';
  if (await isAdmin(sessionCookie)) {
    return next();
  } else {
    return response.redirect('/login');
  }
};

const isDoctorLoggedIn = async (
  request: Request,
  response: Response,
  next: NextFunction
) => {
  const sessionCookie = request.cookies.session || '';

  if (await isDoctor(sessionCookie)) {
    return next();
  } else {
    return response.redirect('/login');
  }
};

app.post('/sessionLogin', async (request: Request, response: Response) => {
  // Set session expiration to 24 hours.
  const expiresIn = 60 * 60 * 24 * 1000;
  const idToken = request.body.idToken.toString();

  const sessionCookie = await createNewCookie(idToken, expiresIn);
  if (!sessionCookie) {
    response.status(401).send('Unauthorized Request');
  }
  const options = {
    maxAge: expiresIn,
    httpOnly: true,
    secure: false /* set to false when testing locally */,
  };
  response.cookie('session', sessionCookie, options);
  response.end();
});

app.get('/sessionLogout', async (request: Request, response: Response) => {
  const sessionCookie = request.cookies.session || '';
  response.clearCookie('session');
  await revokeToken(sessionCookie);
  response.redirect('/login');
});

app.use('/api', router);
app.disable('etag');

const routes = (app: Express) => {
  app.get('/', isAdminLoggedIn, dashboard);
  app.get('/login', login);
  app.get('/doctorSignup', isAdminLoggedIn, doctorSignup);

  router.post('/createDoctor', isAdminLoggedIn, createDoctor);
  router.post('/createPatient', isDoctorLoggedIn, createPatient);
};

routes(app);

app.listen(apiPort, () => console.log(`LISTENING ON PORT ${apiPort}/api`));
