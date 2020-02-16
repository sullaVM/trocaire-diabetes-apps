import bodyParser from 'body-parser';
import express from 'express';
import logger from 'morgan';
import * as db from './database';
import * as requests from './models/requests';
import * as responses from './models/responses';

const apiPort = 8081;
const app = express();
const router = express.Router();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(logger('dev'));
app.use(express.static('static'));

router.post('/createPatient', (request, response) => {
  const createPatientRequest: requests.ICreatePatient = {
    doctorID: request.body.doctorID,
    firstName: request.body.firstName,
    lastName: request.body.lastName,
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

router.post('/updatePatient', (request, response) => {
  const updatePatientRequest: requests.IUpdatePatient = {
    patientID: request.body.patientID,
    doctorID: request.body.doctorID,
    firstName: request.body.firstName,
    lastName: request.body.lastName,
    mobileNumber: request.body.mobileNumber,
    photoDataUrl: request.body.photoDataUrl,
    password: request.body.password,
    bslUnit: request.body.bslUnit,
  };

  const updatePatientResponse: Promise<responses.IUpdatePatient> = db.updatePatient(
    updatePatientRequest
  );

  updatePatientResponse
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

router.post('/createDoctor', (request, response) => {
  const createDoctorRequest: requests.ICreateDoctor = {
    firstName: request.body.firstName,
    lastName: request.body.lastName,
    licenseNumber: request.body.licenseNumber,
    clinicID: request.body.clinicID,
    email: request.body.email,
    userName: request.body.username,
    password: request.body.password,
  };

  const createDoctorResponse: Promise<responses.ICreateDoctor> = db.createDoctor(
    createDoctorRequest
  );

  createDoctorResponse
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

app.use('/api', router);
app.disable('etag');

app.listen(apiPort, () => console.log(`LISTENING ON PORT ${apiPort}/api`));
