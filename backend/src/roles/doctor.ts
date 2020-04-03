// Queries accessible by a doctor.
import * as db from '../database';
import * as requests from '../models/requests';
import * as responses from '../models/responses';
import { pwEncryptSaltRounds } from '../roles/admin';
import { hash } from 'bcrypt';
import { Request, Response } from 'express';

export const updatePhoto = async (request: Request, response: Response) => {
  const updatePhotoRequest: requests.IUpdatePhoto = {
    patientID: request.body.patientID,
    base64encodedstring: request.body.base64encodedstring,
  };

  db.updatePhoto(updatePhotoRequest)
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
    base64encodedstring: request.body.base64encodedstring,
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
    dbSelection: 'PatientID',
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

export const getDoctorsPatientsProfiles = async (doctorID: number) => {
  try {
    const getDoctorsPatientsRequest: requests.IListDoctorsPatients = {
      doctorID: doctorID,
      dbSelection: '*',
    };

    return db.listDoctorsPatients(getDoctorsPatientsRequest);
  } catch (error) {
    console.log(error);
    return null;
  }
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

export const storePatientLog = async (request: Request, response: Response) => {
  try {
    const storePatientLogRequest: requests.IStorePatientLog = {
      patientID: request.body.patientID,
      time: request.body.time,
      note: request.body.note,
    };

    const result = await db.storePatientLog(storePatientLogRequest);

    const followUpReminderInWeeks = request.body.followUpReminderInWeeks;
    if (followUpReminderInWeeks) {
      const nextVisit = new Date(
        Date.now() + followUpReminderInWeeks * 7 * 24 * 60 * 60 * 1000
      );
      const dateStr = nextVisit.toISOString().slice(0, 10);

      await db.updatePatient({
        patientID: request.body.patientID,
        nextVisit: dateStr,
      });
    }

    response.status(200).send(result);
  } catch (error) {
    response.status(500).send({
      success: false,
      message: 'Request unsuccessful, Error:' + error,
    });
  }
};

// Get a list of patients that haven't been seen as scheduled.
export const getPatientsToSee = async (
  request: Request,
  response: Response
) => {
  try {
    const patients = await db.getPatientsToSee({
      doctorID: request.body.doctorID,
    });
    console.log(patients);
    response.status(200).send(patients);
  } catch (error) {
    console.log(error);
    response.status(500);
  }
};

export const getPatientLogs = (request: Request, response: Response) => {
  const getPatientLogsRequest: requests.IGetPatientLogs = {
    patientID: request.body.patientID,
    intervalStart: request.body.intervalStart,
    intervalEnd: request.body.intervalEnd,
  };

  db.getPatientLogs(getPatientLogsRequest)
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
