/**
 * Patient requests
 */

export interface ICreatePatient {
  doctorID: number;
  firstName: string;
  lastName: string;
  mobileNumber: number;
  photoDataUrl: string;
  password: string;
}

export interface IGetPatientProfile {
  patientID: number;
}

export interface IStoreRBP {
  time: string;
  patientID: number;
  systole: number;
  diastole: number;
}

export interface IStoreBSL {
  time: string;
  patientID: number;
  BSLmgDL: number;
}

/**
 * Doctor requests
 */

export interface ICreateDoctor {
  firstName: string;
  lastName: string;
  licenseNumber: number;
  clinicID: number;
  email: string;
  username: string;
  password: string;
}

export interface ICreateClinic {
  clinicID: number;
  clinicName: string;
}
