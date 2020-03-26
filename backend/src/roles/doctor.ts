// Queries accessible by a doctor.
import * as db from '../database';
import * as requests from '../models/requests';
import { pwEncryptSaltRounds } from '../roles/admin';
import { hash } from 'bcrypt';
import { Request, Response } from 'express';

export const takePhoto = async (request: Request, response: Response) => {
  const takePhotoRequest: requests.ITakePhoto = {
    patientID: request.body.patientID,
    photo: request.body.photo,
  };

  db.takePhoto(takePhotoRequest)
    .then(result => {
      response.status(200).send(result);
    })
    .catch(error => {
      response.status(500).send({
        success: false,
        message: 'Request unsuccessful, Error: ' + error,
      });
    });
};

export const createPatient = async (request: Request, response: Response) => {
  const genHash: string = await new Promise((resolve, reject) => {
    // tslint:disable-next-line: no-shadowed-variable
    hash(request.body.password, pwEncryptSaltRounds, (error, hash) => {
      if (error) {
        reject(error);
      }
      resolve(hash);
    });
  });

  const createPatientRequest: requests.ICreatePatient = {
    doctorID: request.body.doctorID,
    firstName: request.body.firstName,
    lastName: request.body.lastName,
    userName: request.body.userName,
    height: request.body.height,
    pregnant: request.body.pregnant,
    mobileNumber: request.body.mobileNumber,
    photoDataUrl: request.body.photoDataUrl,
    password: genHash,
    bslUnit: request.body.bslUnit,
  };

  db.createPatient(createPatientRequest)
    .then(result => {
      response.status(200).send(result);
    })
    .catch(error => {
      response.status(500).send({
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
      response.status(500).send({
        success: false,
        message: 'Request unsuccessful, Error: ' + error,
      });
    });
};

export const updatePatient = (request: Request, response: Response) => {
  const updatePatientRequest: requests.IUpdatePatient = {
    patientID: request.body.patientID,
    firstName: request.body.firstName,
    lastName: request.body.lastName,
    userName: request.body.userName,
    height: request.body.height,
    pregnant: request.body.pregnant,
    mobileNumber: request.body.mobileNumber,
    photoDataUrl: request.body.photoDataUrl,
    bslUnit: request.body.bslUnit,
  };

  db.updatePatient(updatePatientRequest)
    .then(result => {
      response.status(200).send(result);
    })
    .catch(error => {
      response.status(500).send({
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
      response.status(500).send({
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
      response.status(500).send({
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
      response.status(500).send({
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
      response.status(500).send({
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
      response.status(500).send({
        success: false,
        message: 'Request unsuccessful, Error: ' + error,
      });
    });
};

export const getDoctorID = (request: Request, response: Response) => {
  const getDoctorIDRequest: requests.IGetDoctorID = {
    email: request.body.email,
  };

  db.getDoctorID(getDoctorIDRequest)
    .then(result => {
      response.status(200).send(result);
    })
    .catch(error => {
      response.status(500).send({
        success: false,
        message: 'Request unsuccessful, Error: ' + error,
      });
    });
};

export const getDoctorIDFromLogin = async (email: string) => {
  try {
    const getDoctorIDRequest: requests.IGetDoctorID = {
      email,
    };

    const doctorID = await db.getDoctorID(getDoctorIDRequest);

    return doctorID.doctorID;
  } catch (error) {
    return ' ';
  }
};
