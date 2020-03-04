// Queries accessible by a doctor.
import * as db from '../database';
import * as requests from '../models/requests';

import { Request, Response } from 'express';

export const createPatient = (request: Request, response: Response) => {
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

  db.createPatient(createPatientRequest)
    .then(result => {
      response.status(200).send(result);
    })
    .catch(error => {
      response.status(200).send({
        success: false,
        message: 'Request unsuccessful, Error: ' + error,
      });
    });
};

export const getPatientProfile = (request: Request, response: Response) => {
  const getPatientProfileRequest: requests.IGetPatientProfile = {
    patientID: request.body.patientID,
  };

  db.getPatientProfile(getPatientProfileRequest)
    .then(result => {
      response.status(200).send(result);
    })
    .catch(error => {
      response.status(200).send({
        success: false,
        message: 'Request unsuccessful, Error: ' + error,
      });
    });
};

export const getDoctorsPatients = (request: Request, response: Response) => {
  const getDoctorsPatientsRequest: requests.IListDoctorsPatients = {
    doctorID: request.body.doctorID,
  };

  db.listDoctorsPatients(getDoctorsPatientsRequest)
    .then(result => {
      response.status(200).send(result);
    })
    .catch(error => {
      response.status(200).send({
        success: false,
        message: 'Request unsuccessful, Error:' + error,
      });
    });
};

export const getGraphingData = (request: Request, response: Response) => {
  const getGraphingDataRequest: requests.IGetGraphingData = {
    patientID: request.body.patientID,
    intervalStart: request.body.intervalStart,
    intervalEnd: request.body.intervalEnd,
    bslUnit: request.body.bslUnit,
  };

  db.getGraphingData(getGraphingDataRequest)
    .then(result => {
      response.status(200).send(result);
    })
    .catch(error => {
      response.status(200).send({
        success: false,
        message: 'Request unsuccessful, Error:' + error,
      });
    });
};

export const getDoctorProfile = (request: Request, response: Response) => {
  const getDoctorProfileRequest: requests.IGetDoctorProfile = {
    doctorID: request.body.doctorID,
  };

  db.getDoctorProfile(getDoctorProfileRequest)
    .then(result => {
      response.status(200).send(result);
    })
    .catch(error => {
      response.status(200).send({
        success: false,
        message: 'Request unsuccessful, Error:' + error,
      });
    });
};

export const getAllDoctorsAtClinic = (request: Request, response: Response) => {
  const getAllDoctorsAtClinicRequest: requests.IGetAllDoctorsAtClinic = {
    clinicID: request.body.clinicID,
  };

  db.getAllDoctorsAtClinic(getAllDoctorsAtClinicRequest)
    .then(result => {
      response.status(200).send(result);
    })
    .catch(error => {
      response.status(200).send({
        success: false,
        message: 'Request unsuccessful, Error: ' + error,
      });
    });
};

export const getAllClinics = (request: Request, response: Response) => {
  const getAllClinicsRequest: requests.IGetAllClinics = {
    doctorID: request.body.doctorID,
  };

  db.getAllClinics(getAllClinicsRequest)
    .then(result => {
      response.status(200).send(result);
    })
    .catch(error => {
      response.status(200).send({
        success: false,
        message: 'Request unsuccessful, Error: ' + error,
      });
    });
};

export const getDoctorID = (request: Request, response: Response) => {
  const getDoctorIDRequest: requests.IGetDoctorID = {
    email: request.query.email,
  };

  db.getDoctorID(getDoctorIDRequest)
    .then(result => {
      response.status(200).send(result);
    })
    .catch(error => {
      response.status(200).send({
        success: false,
        message: 'Request unsuccessful, Error: ' + error,
      });
    });
};
