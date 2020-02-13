import mysql from 'mysql';
import fs from 'fs';
import * as requests from './models/requests';
import * as responses from './models/responses';

let db: mysql.Connection;

fs.readFile('dbconfig.json', 'utf8', (error, data) => {
  if (error) {
    console.log('dbconfig.json is missing, put it in root of project');
    throw error;
  }
  const mysqlConfig = JSON.parse(data);
  db = mysql.createConnection(mysqlConfig);
  db.connect();
});

export const createPatient = async (
  request: requests.ICreatePatient
): Promise<responses.ICreatePatient> => {
  const query = `INSERT INTO Patients (DoctorID, FirstName, LastName, MobileNumber, PhotoLink, Password)
  VALUES ('${request.doctorID}','${request.firstName}','${request.lastName}','${request.mobileNumber}','${request.photoDataUrl}','${request.password}');`;

  const result = await new Promise<responses.ICreatePatient>(resolve => {
    db.query(query, (error, results, fields) => {
      if (error) {
        console.error(error);
        resolve({ success: false });
      }
      resolve({ patientID: results.insertId, success: true });
    });
  });

  return result;
};

export const getPatientProfile = async (
  request: requests.IGetPatientProfile
): Promise<responses.IGetPatientProfile> => {
  const query = `SELECT * FROM Patients WHERE PatientID='${request.patientID}';`;

  const result = await new Promise<responses.IGetPatientProfile>(resolve => {
    db.query(query, (error, results, fields) => {
      if (error) {
        console.error(error);
        resolve({ success: false });
      }
      if (!results[0]) {
        resolve({ success: false });
      } else {
        resolve({
          success: true,
          doctorID: results[0].DoctorID,
          firstName: results[0].FirstName,
          lastName: results[0].LastName,
          mobileNumber: results[0].MobileNumber,
          photoDataUrl: results[0].PhotoLink,
        });
      }
    });
  });

  return result;
};

export const updatePatient = async (
  request: requests.IUpdatePatient
): Promise<responses.IUpdatePatient> => {
  let updateCount = 0;

  const query = `UPDATE Patients 
  SET
  ${
    request.doctorID
      ? `${updateCount++ ? ',' : ''}DoctorID='${request.doctorID}'`
      : ''
  }
  ${
    request.firstName
      ? `${updateCount++ ? ',' : ''}FirstName='${request.firstName}'`
      : ''
  }
  ${
    request.lastName
      ? `${updateCount++ ? ',' : ''}LastName='${request.lastName}'`
      : ''
  }
  ${
    request.mobileNumber
      ? `${updateCount++ ? ',' : ''}MobileNumber='${request.mobileNumber}'`
      : ''
  }
  ${
    request.photoDataUrl
      ? `${updateCount++ ? ',' : ''}PhotoLink='${request.photoDataUrl}'`
      : ''
  }
  WHERE PatientID='${request.patientID}';`;

  const result = await new Promise<responses.IUpdatePatient>(resolve => {
    db.query(query, (error, results, fields) => {
      if (error) {
        console.error(error);
        resolve({ success: false });
      }
      resolve({ success: true });
    });
  });

  return result;
};

export const storeRBP = async (
  request: requests.IStoreRBP
): Promise<responses.IStoreRBP> => {
  const query = `INSERT INTO RBP (TimeTaken, PatientID, Systole, Diastole)
  VALUES ('${request.time}','${request.patientID}','${request.systole}','${request.diastole}');`;

  const result = await new Promise<responses.IStoreRBP>(resolve => {
    db.query(query, (error, results, fields) => {
      if (error) {
        console.error(error);
        resolve({ success: false });
      }
      resolve({ success: true });
    });
  });

  return result;
};

export const storeBSL = async (
  request: requests.IStoreBSL
): Promise<responses.IStoreBSL> => {
  const query = `INSERT INTO BSL (TimeTaken, PatientID, BSLmgDL)
  VALUES ('${request.time}','${request.patientID}','${request.BSLmgDL}')`;

  const result = await new Promise<responses.IStoreBSL>(resolve => {
    db.query(query, (error, results, fields) => {
      if (error) {
        console.error(error);
        resolve({ success: false });
      }
      resolve({ success: true });
    });
  });

  return result;
};

export const createDoctor = async (
  request: requests.ICreateDoctor
): Promise<responses.ICreateDoctor> => {
  const query = `INSERT INTO Doctors (FirstName, LastName, LicenseNo, ClinicID, Email, UserName,Password)
  VALUES ('${request.firstName}','${request.lastName}','${request.licenseNumber}','${request.clinicID}','${request.email}','${request.username}','${request.password}');`;

  const result = await new Promise<responses.ICreateDoctor>(resolve => {
    db.query(query, (error, results, fields) => {
      if (error) {
        console.error(error);
        resolve({ success: false });
      }
      resolve({ doctorID: results.insertId, success: true });
    });
  });

  return result;
};
