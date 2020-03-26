import bodyParser from 'body-parser';
import cookieParser from 'cookie-parser';
import logger from 'morgan';
import { join } from 'path';
import { config } from 'dotenv';
import exphbs from 'express-handlebars';

import express, {
  Express,
  Request,
  Response,
  NextFunction,
  Router,
  response,
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
  getDoctorIDFromLogin,
  updatePatient,
} from './roles/doctor';

import {
  storeRBP,
  storeBSL,
  storeWeight,
  verifyPatientToken,
  getPatientID,
  clearPatientToken,
} from './roles/patient';

import {
  createClinic,
  createDoctor,
  updateDoctor,
  deleteDoctor,
  addDoctorToMultClinics,
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

app.engine('handlebars', exphbs());
app.set('view engine', 'handlebars');
app.set('views', join(__dirname, '/../src/views'));

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(logger('dev'));
app.use(express.static('src/public'));

const login = (_request: Request, response: Response) => {
  response.sendFile(join(__dirname + '/../src/private/login.html'));
};

const doctorSignupPage = (_request: Request, response: Response) => {
  response.sendFile(join(__dirname + '/../src/private/signup.html'));
};

const dashboard = (_request: Request, response: Response) => {
  response.render('dashboard', {
    layout: 'main',
    helpers: {
      title: 'Dashboard',
    },
  });
};

const clinicSignupPage = (_request: Request, response: Response) => {
  response.render('registerClinic', {
    layout: 'main',
    helpers: {
      title: 'Register a Clinic',
    },
  });
};

const inviteDoctorPage = (_request: Request, response: Response) => {
  response.render('inviteDoctor', {
    layout: 'main',
    helpers: {
      title: 'Invite a Doctor',
    },
  });
};

const addDoctorToClinicsPage = (_request: Request, response: Response) => {
  response.render('addDoctorToClinics', {
    layout: 'main',
    helpers: {
      title: 'Add a Doctor to Clinics',
    },
  });
};

const registerPatientPage = (_request: Request, response: Response) => {
  response.render('registerPatient', {
    layout: 'main',
    helpers: {
      title: 'Register a Patient',
    },
  });
};

const editProfilePage = (_request: Request, response: Response) => {
  response.render('editProfile', {
    layout: 'main',
    helpers: {
      title: 'Edit Profile',
    },
  });
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
  const email = request.body.email;

  const sessionCookie = await createNewCookie(idToken, expiresIn);
  if (!sessionCookie) {
    response.status(401).send('Unauthorized Request');
  }
  const options = {
    maxAge: expiresIn,
    httpOnly: true,
    secure: false /* set to false when testing locally */,
  };

  const doctorID = await getDoctorIDFromLogin(email);

  response.cookie('session', sessionCookie, options);
  response.send({
    doctorID,
  });
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
  } else {
    const tokenID = generateToken();
    if (await updatePatientToken(patientID, tokenID)) {
      response.status(200).send({
        success: true,
        tokenID,
      });
    } else {
      response.sendStatus(403);
    }
  }
};

const patientLogout = async (request: Request, respose: Response) => {
  const patientID = request.body.patientID;
  if (clearPatientToken(patientID)) {
    response.status(200).send({
      message: 'logged out',
    });
  } else {
    response.status(403);
  }
};

app.use('/api', router);
app.disable('etag');

// tslint:disable-next-line: no-shadowed-variable
const initRoutes = (app: Express) => {
  app.get('/', isAdminLoggedIn, dashboard);

  // Public Pages
  app.get('/login', login);
  app.get('/signup', doctorSignupPage);

  // Admin and Doctor Login/Logout
  app.post('/sessionLogin', sessionLogin);
  app.get('/sessionLogout', sessionLogout);

  // Patient Login/Logout
  app.post('/patientLogin', patientLogin);
  app.post('/patientLogout', patientLogout);

  // Admin/Doctor Actions
  app.get('/inviteDoctor', isAdminLoggedIn, inviteDoctorPage);
  app.get('/registerPatient', isDoctorLoggedIn, registerPatientPage);

  // Clinic Pages
  app.get('/clinicSignup', isAdminLoggedIn, clinicSignupPage);
  app.get('/addDoctorToClinics', isDoctorLoggedIn, addDoctorToClinicsPage);

  // Doctor Profile Edit Request
  app.get('/editProfile', isDoctorLoggedIn, editProfilePage);
};

// tslint:disable-next-line: no-shadowed-variable
const initApi = (router: Router) => {
  router.post('/createDoctor', createDoctor);

  // Doctor Account Requests
  router.post('/inviteUser', isAdminLoggedIn, inviteDoctor);
  router.post('/updateDoctor', isAdminLoggedIn, updateDoctor);
  router.post('/deleteDoctor', isAdminLoggedIn, deleteDoctor);
  router.post('/getDoctorProfile', isDoctorLoggedIn, getDoctorProfile);
  router.post('/getDoctorID', isDoctorLoggedIn, getDoctorID);

  // Clinic Creation and Assignment
  router.post('/createClinic', isAdminLoggedIn, createClinic);
  router.post('/addDoctorToClinics', isAdminLoggedIn, addDoctorToMultClinics);

  // Doctor's Patient Requests
  router.post('/createPatient', isDoctorLoggedIn, createPatient);
  router.post('/getPatientProfile', isDoctorLoggedIn, getPatientProfile);
  router.post('/getDoctorsPatients', isDoctorLoggedIn, getDoctorsPatients);
  router.post('/getGraphingData', isDoctorLoggedIn, getGraphingData);
  router.post('/updatePatient', isDoctorLoggedIn, updatePatient);
  router.post('/getPatientLogs', isDoctorLoggedIn, getPatientProfile);
  router.post('/storePatientLog', isDoctorLoggedIn, getPatientProfile);

  // Clinic Requests
  router.post('/getAllClinics', isDoctorLoggedIn, getAllClinics);
  router.post(
    '/getAllDoctorsAtClinic',
    isDoctorLoggedIn,
    getAllDoctorsAtClinic
  );
  router.get('/admin/getAllClinics', isAdminLoggedIn, getAllClinics);

  // Patient Records Requests
  router.post('/storeRBP', isPatientLoggedIn, storeRBP);
  router.post('/storeBSL', isPatientLoggedIn, storeBSL);
  router.post('/storeWeight', isPatientLoggedIn, storeWeight);

  // Request does not need to be authenticated
  router.post('/getPatientID', getPatientID);
};

initRoutes(app);
initApi(router);

app.listen(apiPort, () => console.log(`LISTENING ON PORT ${apiPort}`));
