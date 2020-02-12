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

export const createUser = async (
  request: requests.ICreateUser
): Promise<responses.ICreateUser> => {
  const query = `INSERT INTO Patients (DoctorID, FirstName, LastName, MobileNumber, PhotoLink, Password)
  VALUES ('${request.doctorID}','${request.firstName}','${request.lastName}','${request.mobileNumber}','${request.photoDataUrl}','${request.password}');`;

  const result = await new Promise<responses.ICreateUser>(resolve => {
    db.query(query, (error, results, fields) => {
      if (error) {
        console.error(error);
        resolve({ success: false });
      }
      resolve({ userID: results.insertId, success: true });
    });
  });

  return result;
};

export const getUserProfile = async (
  request: requests.IGetUserProfile
): Promise<responses.IGetUserProfile> => {
  const query = `SELECT * FROM Patients WHERE PatientID='${request.userID}';`;

  const result = await new Promise<responses.IGetUserProfile>(resolve => {
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
