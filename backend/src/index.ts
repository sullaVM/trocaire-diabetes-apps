import bodyParser from 'body-parser';
import cookieParser from 'cookie-parser';
import logger from 'morgan';
import { join } from 'path';
import { config } from 'dotenv';

import express, {
  Express,
  Request,
  Response,
  NextFunction,
  Router,
} from 'express';

import {
  initFirebase,
  createNewCookie,
  revokeToken,
  isAdmin,
  isDoctor,
} from './firebase/firebase';

import {
  createPatient,
  getPatientProfile,
  getDoctorsPatients,
  getGraphingData,
  getDoctorProfile,
  getAllDoctorsAtClinic,
  getAllClinics,
  getDoctorID,
} from './roles/doctor';

import {
  storeRBP,
  storeBSL,
  storeWeight,
  verifyPatientToken,
} from './roles/patient';

import {
  createClinic,
  createDoctor,
  updateDoctor,
  deleteDoctor,
  addDoctorToClinic,
  inviteDoctor,
} from './roles/admin';

import {
  verifyPassword,
  generateToken,
  updatePatientToken,
} from './roles/patient';

const apiPort = 8081;
const app = express();
const router = express.Router();

config();
initFirebase();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(logger('dev'));
app.use(express.static('src/public'));

const login = (_request: Request, response: Response) => {
  response.sendFile(join(__dirname + '/../src/private/login.html'));
};

const dashboard = (_request: Request, response: Response) => {
  response.sendFile(join(__dirname + '/../src/private/dashboard.html'));
};

const doctorSignup = (_request: Request, response: Response) => {
  response.sendFile(join(__dirname + '/../src/private/doctorSignup.html'));
};

const clinicSignup = (_request: Request, response: Response) => {
  response.sendFile(join(__dirname + '/../src/private/clinicSignup.html'));
};

const inviteDoctorPage = (_request: Request, response: Response) => {
  response.sendFile(join(__dirname + '/../src/private/inviteDoctor.html'));
};

const isAdminLoggedIn = async (
  request: Request,
  response: Response,
  next: NextFunction
) => {
  const sessionCookie = request.cookies.session || ' ';
  if (await isAdmin(sessionCookie)) {
    next();
  } else {
    response.redirect('/login');
  }
};

const isDoctorLoggedIn = async (
  request: Request,
  response: Response,
  next: NextFunction
) => {
  const sessionCookie = request.cookies.session || ' ';
  if (await isDoctor(sessionCookie)) {
    next();
  } else {
    response.sendStatus(403);
  }
};

const isPatientLoggedIn = async (
  request: Request,
  response: Response,
  next: NextFunction
) => {
  const patientID = request.body.patientID;
  const tokenID = request.body.tokenID || ' ';

  if (verifyPatientToken(patientID, tokenID)) {
    next();
  } else {
    response.sendStatus(403);
  }
};

const sessionLogin = async (request: Request, response: Response) => {
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
};

const sessionLogout = async (request: Request, response: Response) => {
  const sessionCookie = request.cookies.session || ' ';
  response.clearCookie('session');
  await revokeToken(sessionCookie);

  response.redirect('/login');
};

const patientLogin = async (request: Request, response: Response) => {
  const password = request.body.password;
  const patientID: number = request.body.patientID;

  const verified = await verifyPassword(patientID, password);
  if (!verified) {
    response.status(403).send({
      message: 'Incorrect password',
    });
  }

  const tokenID = generateToken();

  if (await updatePatientToken(patientID, tokenID)) {
    response.status(200).send({
      success: true,
      tokenID: tokenID,
    });
  } else {
    response.sendStatus(403);
  }
};

app.use('/api', router);
app.disable('etag');

// tslint:disable-next-line: no-shadowed-variable
const initRoutes = (app: Express) => {
  app.get('/', isAdminLoggedIn, dashboard);
  app.get('/doctorSignup', isAdminLoggedIn, doctorSignup);
  app.get('/clinicSignup', isAdminLoggedIn, clinicSignup);
  app.get('/inviteDoctor', isAdminLoggedIn, inviteDoctorPage);

  app.get('/login', login);
  app.get('/sessionLogout', sessionLogout);

  app.post('/sessionLogin', sessionLogin);
  app.post('/patientLogin', patientLogin);
};

// tslint:disable-next-line: no-shadowed-variable
const initApi = (router: Router) => {
  router.post('/createDoctor', isAdminLoggedIn, createDoctor);
  router.post('/updateDoctor', isAdminLoggedIn, updateDoctor);
  router.post('/deleteDoctor', isAdminLoggedIn, deleteDoctor);
  router.post('/createClinic', isAdminLoggedIn, createClinic);
  router.post('/addDoctorToClinic', isAdminLoggedIn, addDoctorToClinic);
  router.post('/inviteUser', isAdminLoggedIn, inviteDoctor);

  router.post('/createPatient', isDoctorLoggedIn, createPatient);
  router.post('/getPatientProfile', getPatientProfile);
  router.post('/getDoctorsPatients', isDoctorLoggedIn, getDoctorsPatients);
  router.post('/getGraphingData', isDoctorLoggedIn, getGraphingData);
  router.post('/getDoctorProfile', isDoctorLoggedIn, getDoctorProfile);
  router.post(
    '/getAllDoctorsAtClinic',
    isDoctorLoggedIn,
    getAllDoctorsAtClinic
  );
  router.post('/getAllClinics', isDoctorLoggedIn, getAllClinics);
  router.get('/admin/getAllClinics', isAdminLoggedIn, getAllClinics);
  router.post('/getDoctorID', isDoctorLoggedIn, getDoctorID);

  // TODO(sulla): Check if patient and doctor are logged in.
  router.post('/storeRBP', isPatientLoggedIn, storeRBP);
  router.post('/storeBSL', isPatientLoggedIn, storeBSL);
  router.post('/storeWeight', isPatientLoggedIn, storeWeight);
};

initRoutes(app);
initApi(router);

app.listen(apiPort, () => console.log(`LISTENING ON PORT ${apiPort}`));
