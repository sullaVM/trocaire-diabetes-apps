// Queries accessible by a patients (and their doctors).
import * as db from '../database';
import * as requests from '../models/requests';

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
