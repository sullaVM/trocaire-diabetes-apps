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
} from './roles/doctor';

import { storeRBP, storeBSL, storeWeight } from './roles/patient';

import { createClinic, createDoctor, updateDoctor } from './roles/admin';

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

const isAdminLoggedIn = async (
  request: Request,
  response: Response,
  next: NextFunction
) => {
  const sessionCookie = request.cookies.session || '';
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
  const sessionCookie = request.cookies.session || '';
  if (await isDoctor(sessionCookie)) {
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
  const sessionCookie = request.cookies.session || '';
  response.clearCookie('session');
  await revokeToken(sessionCookie);
  response.redirect('/login');
};

app.use('/api', router);
app.disable('etag');

const initRoutes = (app: Express) => {
  app.get('/login', login);
  app.post('/sessionLogin', sessionLogin);
  app.get('/sessionLogout', sessionLogout);

  app.get('/', isAdminLoggedIn, dashboard);
  app.get('/doctorSignup', isAdminLoggedIn, doctorSignup);
  app.get('/createClinic', isAdminLoggedIn, createClinic);
};

const initApi = (router: Router) => {
  router.post('/createDoctor', isAdminLoggedIn, createDoctor);
  router.post('/updateDoctor', isAdminLoggedIn, updateDoctor);

  router.post('/createPatient', isDoctorLoggedIn, createPatient);
  router.get('/getPatientProfile', isDoctorLoggedIn, getPatientProfile);
  router.get('/getDoctorsPatients', isDoctorLoggedIn, getDoctorsPatients);
  router.get('/getGraphingData', isDoctorLoggedIn, getGraphingData);
  router.get('/getDoctorProfile', isDoctorLoggedIn, getDoctorProfile);
  router.get('/getAllDoctorsAtClinic', isDoctorLoggedIn, getAllDoctorsAtClinic);
  router.get('/getAllClinics', isDoctorLoggedIn, getAllClinics);

  // TODO(sulla): Check if patient and doctore are logged in.
  router.post('/storeRBP', storeRBP);
  router.post('/storeBSL', storeBSL);
  router.post('/storeWeight', storeWeight);
};

initRoutes(app);
initApi(router);

app.listen(apiPort, () => console.log(`LISTENING ON PORT ${apiPort}`));
