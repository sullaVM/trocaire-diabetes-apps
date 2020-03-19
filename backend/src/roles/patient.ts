// Queries accessible by a patients (and their doctors).
import * as db from '../database';
import * as requests from '../models/requests';
import crypto from 'crypto-random-string';
import { compare } from 'bcrypt';
import { Request, Response } from 'express';

export const storeRBP = (request: Request, response: Response) => {
  const storeRBPRequest: requests.IStoreRBP = {
    patientID: request.body.patientID,
    time: request.body.time,
    systole: request.body.systole,
    diastole: request.body.diastole,
  };

  db.storeRBP(storeRBPRequest)
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

export const storeBSL = (request: Request, response: Response) => {
  const storeBSLRequest: requests.IStoreBSL = {
    patientID: request.body.patientID,
    time: request.body.time,
    value: request.body.value,
    unit: request.body.unit,
  };

  db.storeBSL(storeBSLRequest)
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

export const storeWeight = (request: Request, response: Response) => {
  const storeWeightRequest: requests.IStoreWeight = {
    patientID: request.body.patientID,
    time: request.body.time,
    weightKG: request.body.weightKG,
  };

  db.storeWeight(storeWeightRequest)
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

export const getPatientID = (request: Request, response: Response) => {
  const getPatientIDRequest: requests.IGetPatientID = {
    doctorID: request.body.doctorID,
    firstName: request.body.firstName,
    lastName: request.body.lastName,
  };

  db.getPatientID(getPatientIDRequest)
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

export const verifyPassword = async (
  patientID: number,
  password: string
): Promise<boolean> => {
  try {
    const getPasswordRequest: requests.IGetPatientProfile = {
      patientID,
    };

    const getPasswordResponse = await db.getPatientPassword(getPasswordRequest);

    return await compare(password, getPasswordResponse.password);
  } catch (error) {
    console.log('Error verifying password: ', error);
    return false;
  }
};

export const generateToken = (): string => {
  return crypto({ length: 10, type: 'base64' });
};

export const updatePatientToken = async (
  patientID: number,
  token: string
): Promise<boolean> => {
  try {
    const setPatientTokenRequest: requests.ISetPatientToken = {
      sessionToken: token,
      patientID,
    };

    await db.setPatientToken(setPatientTokenRequest);

    return true;
  } catch (error) {
    console.log(error);
    return false;
  }
};

export const clearPatientToken = async (
  patientID: number
): Promise<boolean> => {
  try {
    return await updatePatientToken(patientID, '');
  } catch (error) {
    console.log(error);
    return false;
  }
};

export const verifyPatientToken = async (
  patientID: number,
  token: string
): Promise<boolean> => {
  const correctToken = await db.getPatientToken({
    patientID,
  });

  if (token === correctToken.sessionToken) {
    return true;
  } else {
    return false;
  }
};
