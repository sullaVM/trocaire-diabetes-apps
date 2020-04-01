import mysql from 'mysql';
import fs from 'fs';
import * as requests from './models/requests';
import * as responses from './models/responses';

import * as blobUtil from 'blob-util';
import * as sharedBlobService from '@azure/storage-blob';

let db: mysql.Connection;
import {
  BlobService,
  common,
  createBlobService,
  ErrorOrResponse,
  ErrorOrResult,
} from 'azure-storage';

import {
  BlobServiceClient,
  StorageSharedKeyCredential,
  BlobDownloadResponseModel,
} from '@azure/storage-blob';

fs.readFile('dbconfig.json', 'utf8', (error, data) => {
  if (error) {
    console.log('dbconfig.json is missing, put it in root of project');
    throw error;
  }
  const mysqlConfig = JSON.parse(data);
  db = mysql.createConnection(mysqlConfig);
  db.connect();
});

export const updatePhoto = async (
  request: requests.IUpdatePhoto
): Promise<responses.IUpdatePhoto> => {
  const account = process.env.AZURE_ACCOUNT_NAME || '';
  const accountKey = process.env.AZURE_ACCOUNT_KEY || '';
  const sharedKeyCredential = new StorageSharedKeyCredential(
    account,
    accountKey
  );

  const blobServiceClient = new BlobServiceClient(
    // When using AnonymousCredential, following url should include a valid SAS or support public access
    `https://${account}.blob.core.windows.net`,
    sharedKeyCredential
  );

  const containerName = 'images';
  const containerClient = blobServiceClient.getContainerClient(containerName);

  const base64encodedstring = request.base64encodedstring;

  const blobName = request.patientID + 'patient' + new Date().getTime();
  const blockBlobClient = containerClient.getBlockBlobClient(blobName);

  const uploadBlobResponse = await blockBlobClient.upload(
    base64encodedstring,
    base64encodedstring.length
  );

  const urlstart = 'https://';
  const photoDataUrl = urlstart.concat(
    account,
    '.blob.core.windows.net/images/',
    blobName
  );

  const query = `UPDATE Patients SET PhotoDataUrl = '${photoDataUrl}' WHERE PatientID = '${request.patientID}';`;

  const result = await new Promise<responses.IUpdatePhoto>(resolve => {
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

export const createPatient = async (
  request: requests.ICreatePatient
): Promise<responses.ICreatePatient> => {
  const account = process.env.AZURE_ACCOUNT_NAME || '';
  const accountKey = process.env.AZURE_ACCOUNT_KEY || '';
  const sharedKeyCredential = new StorageSharedKeyCredential(
    account,
    accountKey
  );

  const blobServiceClient = new BlobServiceClient(
    // When using AnonymousCredential, following url should include a valid SAS or support public access
    `https://${account}.blob.core.windows.net`,
    sharedKeyCredential
  );

  const containerName = 'images';
  const containerClient = blobServiceClient.getContainerClient(containerName);

  const base64encodedstring = request.base64encodedstring;

  const blobName = request.userName + 'patient' + new Date().getTime();
  const blockBlobClient = containerClient.getBlockBlobClient(blobName);

  if (base64encodedstring) {
    await blockBlobClient.upload(
      base64encodedstring,
      base64encodedstring.length
    );
  }

  const urlstart = 'https://';
  const photoBlobUrl = urlstart.concat(
    account,
    '.blob.core.windows.net/images/',
    blobName
  );

  const userNameExistsQuery = `SELECT UserName FROM Patients WHERE UserName='${request.userName}';`;

  const query = `INSERT INTO Patients (DoctorID, FirstName, LastName, UserName, Height, Pregnant, MobileNumber, PhotoDataUrl, Password, BslUnit)
  VALUES ('${request.doctorID}','${request.firstName}','${request.lastName}','${
    request.userName
  }','${request.height}', '${request.pregnant}', '${
    request.mobileNumber
  }','${photoBlobUrl}','${request.password}','${
    request.bslUnit === 'mgDL' ? 1 : 0
  }');`;

  const result = await new Promise<responses.ICreatePatient>(resolve => {
    db.query(
      userNameExistsQuery,
      (userNameError, userNameResults, userNameFields) => {
        if (userNameError) {
          console.error(userNameError);
          blockBlobClient.delete();
          resolve({ success: false });
        }
        if (!userNameResults[0]) {
          db.query(query, (error, results, fields) => {
            if (error) {
              blockBlobClient.delete();
              console.error(error);
              resolve({ success: false });
            }
            resolve({ patientID: results.insertId, success: true });
          });
        } else {
          blockBlobClient.delete();
          resolve({
            success: false,
            message: 'Username already exists',
          });
        }
      }
    );
  });

  return result;
};

export const getPatientPassword = async (
  request: requests.IGetPatientProfile
): Promise<responses.IGetPatientPassword> => {
  const query = `SELECT Password FROM Patients WHERE PatientID='${request.patientID}';`;

  const result = await new Promise<responses.IGetPatientPassword>(
    (resolve, reject) => {
      db.query(query, (error, results, fields) => {
        if (error) {
          reject(error);
        }
        if (!results[0]) {
          reject(error);
        } else {
          resolve({
            success: true,
            password: results[0].Password,
          });
        }
      });
    }
  );

  return result;
};

export const getPatientToken = async (
  request: requests.IGetPatientProfile
): Promise<responses.IGetPatientToken> => {
  const query = `SELECT SessionToken FROM Patients WHERE PatientID='${request.patientID}';`;

  const result = await new Promise<responses.IGetPatientToken>(
    (resolve, reject) => {
      db.query(query, (error, results, fields) => {
        if (error) {
          reject(error);
        }
        if (!results[0]) {
          reject(error);
        } else {
          resolve({
            success: true,
            sessionToken: results[0].sessionToken,
          });
        }
      });
    }
  );
  return result;
};

export const setPatientToken = async (
  request: requests.ISetPatientToken
): Promise<responses.ISetPatientToken> => {
  const query = `UPDATE Patients SET SessionToken='${request.sessionToken}' WHERE PatientID=${request.patientID};`;

  const result = await new Promise<responses.ISetPatientToken>(
    (resolve, reject) => {
      db.query(query, (error, results, fields) => {
        if (error) {
          reject(error);
        }
        resolve({ success: true });
      });
    }
  );

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
          userName: results[0].UserName,
          height: results[0].Height,
          pregnant: results[0].Pregnant,
          mobileNumber: results[0].MobileNumber,
          photoDataUrl: results[0].PhotoDataUrl,
          bslUnit: results[0].BslUnit === 1 ? 'mgDL' : 'mmolL',
        });
      }
    });
  });

  return result;
};

export const updatePatient = async (
  request: requests.IUpdatePatient
): Promise<responses.IUpdatePatient> => {
  const values: string[] = [];
  Object.entries(request).forEach(([key, value]) => {
    if (value) {
      values.push(`${key}='${value}'`);
    }
  });
  const query = `UPDATE Patients SET ${values.join(',')} WHERE PatientID='${
    request.patientID
  }';`;

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
  const mmolL =
    request.unit && request.unit === 'mgDL'
      ? request.value / 18
      : request.value;

  const query = `INSERT INTO BSL (TimeTaken, PatientID, MmolL)
  VALUES ('${request.time}','${request.patientID}','${mmolL}')`;

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

export const storeWeight = async (
  request: requests.IStoreWeight
): Promise<responses.IStoreWeight> => {
  const query = `INSERT INTO Weight (TimeTaken, PatientID, WeightKG)
  VALUES ('${request.time}','${request.patientID}','${request.weightKG}')`;

  const result = await new Promise<responses.IStoreWeight>(resolve => {
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

export const getGraphingData = async (
  request: requests.IGetGraphingData
): Promise<responses.IGetGraphingData> => {
  const BSLQuery = `SELECT TimeTaken, MmolL FROM BSL WHERE
  PatientID='${request.patientID}' AND
  TimeTaken BETWEEN '${request.intervalStart}' AND '${request.intervalEnd}'
  ORDER BY TimeTaken ASC;`;

  const RBPQuery = `SELECT TimeTaken, Systole, Diastole FROM RBP WHERE
  PatientID='${request.patientID}' AND
  TimeTaken BETWEEN '${request.intervalStart}' AND '${request.intervalEnd}'
  ORDER BY TimeTaken ASC;`;

  const WeightQuery = `SELECT TimeTaken, WeightKG FROM Weight WHERE
  PatientID='${request.patientID}' AND
  TimeTaken BETWEEN '${request.intervalStart}' AND '${request.intervalEnd}'
  ORDER BY TimeTaken ASC;`;

  const result = await new Promise<responses.IGetGraphingData>(resolve => {
    db.query(BSLQuery, (BSLError, BSLResults, BSLfields) => {
      if (BSLError) {
        console.error(BSLError);
        resolve({ success: false });
      }
      db.query(RBPQuery, (RBPError, RBPResults, RBPfields) => {
        if (RBPError) {
          console.error(RBPError);
          resolve({ success: false });
        }
        db.query(WeightQuery, (WeightError, WeightResults, Weightfields) => {
          if (RBPError) {
            console.error(RBPError);
            resolve({ success: false });
          }
          const RBP: { time: string; systole: number; diastole: number }[] = [];
          const BSL: { time: string; value: number }[] = [];
          const Weight: { time: string; value: number }[] = [];

          for (const entry of BSLResults) {
            BSL.push({
              time: entry.TimeTaken,
              value:
                request.bslUnit && request.bslUnit === 'mgDL'
                  ? entry.MmolL * 18
                  : entry.MmolL,
            });
          }

          for (const entry of RBPResults) {
            RBP.push({
              time: entry.TimeTaken,
              systole: entry.Systole,
              diastole: entry.Diastole,
            });
          }

          for (const entry of WeightResults) {
            Weight.push({
              time: entry.TimeTaken,
              value: entry.WeightKG,
            });
          }

          resolve({ success: true, RBP, BSL });
        });
      });
    });
  });

  return result;
};

export const storePatientLog = async (
  request: requests.IStorePatientLog
): Promise<responses.IStorePatientLog> => {
  const query = `INSERT INTO Patient_Logs (TimeTaken, PatientID, Note)
  VALUES ('${request.time}','${request.patientID}','${request.note}');`;

  console.log(query);
  const result = await new Promise<responses.IStorePatientLog>(resolve => {
    if (request.note.length > 255) {
      console.log('Note too long');
      resolve({
        success: false,
        message: 'Note too long, must be less than 255 characters',
      });
    }
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

export const getPatientLogs = async (
  request: requests.IGetPatientLogs
): Promise<responses.IGetPatientLogs> => {
  const query = `SELECT TimeTaken, Note FROM Patient_Logs WHERE
  PatientID='${request.patientID}' AND
  TimeTaken BETWEEN '${request.intervalStart}' AND '${request.intervalEnd}'
  ORDER BY TimeTaken ASC;`;

  const result = await new Promise<responses.IGetPatientLogs>(resolve => {
    db.query(query, (error, results, fields) => {
      if (error) {
        console.error(error);
        resolve({ success: false });
      }
      const logs: { time: string; note: string }[] = [];
      for (const entry of results) {
        logs.push({
          time: entry.TimeTaken,
          note: entry.Note,
        });
      }
      resolve({ success: true, logs });
    });
  });
  return result;
};

export const getPatientID = async (
  request: requests.IGetPatientID
): Promise<responses.IGetPatientID> => {
  const query = `SELECT PatientID FROM Patients WHERE UserName='${request.userName}';`;

  const result = await new Promise<responses.IGetPatientID>(resolve => {
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
          patientID: results[0].PatientID,
        });
      }
    });
  });

  return result;
};

export const createDoctor = async (
  request: requests.ICreateDoctor
): Promise<responses.ICreateDoctor> => {
  const query = `INSERT INTO Doctors (FirstName, LastName, LicenseNo, Email, UserName,Password)
  VALUES ('${request.firstName}','${request.lastName}','${request.licenseNumber}','${request.email}','${request.userName}','${request.password}');`;

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

export const deleteDoctor = async (
  request: requests.IDeleteDoctor
): Promise<responses.IDeleteDoctor> => {
  const query = `DELETE FROM Doctors WHERE DoctorID=
'${request.doctorID}');`;

  const result = await new Promise<responses.IDeleteDoctor>(resolve => {
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

export const getDoctorProfile = async (
  request: requests.IGetDoctorProfile
): Promise<responses.IGetDoctorProfile> => {
  const query = `SELECT * FROM Doctors WHERE DoctorID='${request.doctorID}';`;

  const result = await new Promise<responses.IGetDoctorProfile>(resolve => {
    db.query(query, (error, results, fields) => {
      if (error) {
        console.error(error);
        resolve({ success: false });
      }
      if (results.length < 1) {
        resolve({ success: false });
      } else {
        resolve({
          success: true,
          doctorID: results[0].DoctorID,
          firstName: results[0].FirstName,
          lastName: results[0].LastName,
          licenseNumber: results[0].LicenseNo,
          email: results[0].Email,
          userName: results[0].UserName,
        });
      }
    });
  });

  return result;
};

export const listDoctorsPatients = async (
  request: requests.IListDoctorsPatients
): Promise<responses.IListDoctorsPatients> => {
  const query = `SELECT PatientID FROM Patients WHERE DoctorID='${request.doctorID}';`;

  const result = await new Promise<responses.IListDoctorsPatients>(resolve => {
    db.query(query, (error, results, fields) => {
      if (error) {
        console.error(error);
        resolve({ success: false });
      }
      if (results.length < 1) {
        resolve({ success: false });
      } else {
        const patientIDs = [];
        for (const entry of results) {
          patientIDs.push(entry.PatientID);
        }
        resolve({
          success: true,
          patientIDs,
        });
      }
    });
  });

  return result;
};

export const updateDoctor = async (
  request: requests.IUpdateDoctor
): Promise<responses.IUpdateDoctor> => {
  const values: string[] = [];
  Object.entries(request).forEach(([key, value]) => {
    if (value) {
      values.push(`${key}='${value}'`);
    }
  });
  const query = `UPDATE Doctors SET ${values.join(',')} WHERE DoctorID='${
    request.doctorID
  }';`;

  const result = await new Promise<responses.IUpdateDoctor>(resolve => {
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

export const getAllDoctorsAtClinic = async (
  request: requests.IGetAllDoctorsAtClinic
): Promise<responses.IGetAllDoctorsAtClinic> => {
  const query = `SELECT DoctorID, FROM ClinicsToDoctors WHERE ClinicID='${request.clinicID}';`;
  const result = await new Promise<responses.IGetAllDoctorsAtClinic>(
    resolve => {
      db.query(query, (error, results, fields) => {
        if (error) {
          console.error(error);
          resolve({ success: false });
        }
        if (results.length < 1) {
          resolve({ success: false });
        } else {
          const doctors: {
            doctorID: number;
            firstName: string;
            lastName: string;
          }[] = [];
          for (const doctor of results) {
            doctors.push({
              doctorID: doctor.DoctorID,
              firstName: doctor.FirstName,
              lastName: doctor.LastName,
            });
          }
          resolve({
            success: true,
            doctors,
          });
        }
      });
    }
  );

  return result;
};

export const getAllClinics = async (
  request: requests.IGetAllClinics
): Promise<responses.IGetAllClinics> => {
  const query = `SELECT * FROM Clinics;`;

  const result = await new Promise<responses.IGetAllClinics>(resolve => {
    db.query(query, (error, results, fields) => {
      if (error) {
        console.error(error);
        resolve({ success: false });
      }
      if (results.length < 1) {
        resolve({ success: false });
      } else {
        const clinics: { clinicID: number; clinicName: string }[] = [];
        for (const clinic of results) {
          clinics.push({
            clinicID: clinic.ClinicID,
            clinicName: clinic.ClinicName,
          });
        }
        resolve({
          success: true,
          clinics,
        });
      }
    });
  });

  return result;
};

export const createClinic = async (
  request: requests.ICreateClinic
): Promise<responses.ICreateClinic> => {
  const query = `INSERT INTO Clinics (ClinicName)
  VALUE ('${request.clinicName}');`;

  const result = await new Promise<responses.ICreateClinic>(resolve => {
    db.query(query, (error, results, fields) => {
      if (error) {
        console.error(error);
        resolve({ success: false });
      }
      resolve({ clinicID: results.insertId, success: true });
    });
  });

  return result;
};

export const addDoctorToClinic = async (
  request: requests.IAddDoctorToClinic
): Promise<responses.IAddDoctorToClinic> => {
  const query = `INSERT INTO ClinicsToDoctors (ClinicID, DoctorID)
  VALUE ('${request.clinicID}','${request.doctorID}');`;

  const result = await new Promise<responses.ICreateClinic>(resolve => {
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

export const getDoctorID = async (
  request: requests.IGetDoctorID
): Promise<responses.IGetDoctorID> => {
  const query = `SELECT * FROM Doctors WHERE Email='${request.email}';`;

  const result = await new Promise<responses.IGetDoctorID>(resolve => {
    db.query(query, (error, results, fields) => {
      if (error) {
        resolve({ success: false });
      }
      if (results.length < 1) {
        resolve({ success: false });
      } else {
        resolve({
          success: true,
          doctorID: results[0].DoctorID,
        });
      }
    });
  });

  return result;
};

//  ----------------------------------------------------------------------------------------

// Queries for Invited Doctors
// Call these within a try...catch block to ensure errors are caught.
export const addDoctorToInvitedDoctors = async (
  request: requests.IAddDoctorToInvitedDoctors
): Promise<responses.IAddDoctorToInvitedDoctors> => {
  try {
    const query = `INSERT INTO InvitedDoctors (Email)
  VALUE ('${request.email}');`;

    const result = await new Promise<responses.IAddDoctorToInvitedDoctors>(
      (resolve, reject) => {
        db.query(query, (error, results, fields) => {
          if (error) {
            reject(error);
          }
          resolve({ success: true });
        });
      }
    );

    return result;
  } catch (error) {
    console.log(error);
    return {
      success: false,
    };
  }
};

export const deleteDoctorToInvitedDoctors = async (
  request: requests.IDeleteDoctorToInvitedDoctors
): Promise<responses.IDeleteDoctorToInvitedDoctors> => {
  try {
    const query = `DELETE FROM InvitedDoctors WHERE Email='${request.email}';`;

    const result = await new Promise<responses.IDeleteDoctorToInvitedDoctors>(
      (resolve, reject) => {
        db.query(query, (error, results, fields) => {
          if (error) {
            reject(error);
          }
          resolve({ success: true });
        });
      }
    );

    return result;
  } catch (error) {
    console.log(error);
    return {
      success: false,
    };
  }
};

export const verifyInvitedDoctor = async (
  request: requests.IVerifyInvitedDoctor
): Promise<responses.IVerifyInvitedDoctor> => {
  try {
    const query = `SELECT * FROM InvitedDoctors WHERE Email='${request.email}';`;

    const result = await new Promise<responses.IVerifyInvitedDoctor>(
      (resolve, reject) => {
        db.query(query, (error, results, fields) => {
          if (error) {
            reject(error);
          }
          if (results) {
            if (results.length < 1) {
              resolve({ success: false });
            }
            resolve({ success: true });
          }
          resolve({ success: false });
        });
      }
    );

    return result;
  } catch (error) {
    console.log(error);
    return {
      success: false,
    };
  }
};

//  ----------------------------------------------------------------------------------------
