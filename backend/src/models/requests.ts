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

export interface IUpdatePatient {
  patientID: number;
  doctorID?: number;
  firstName?: string;
  lastName?: string;
  mobileNumber?: number;
  photoDataUrl?: string;
  password?: string;
}

export interface IGetPatientProfile {
  patientID: number;
}

export interface IStoreRBP {
  patientID: number;
  time: string;
  systole: number;
  diastole: number;
}

export interface IStoreBSL {
  patientID: number;
  time: string;
  BSLmgDL: number;
}

export interface IGetGraphingData {
  patientID: number;
  intervalStart: string;
  intervalEnd: string;
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
  userName: string;
  password: string;
}

export interface IListDoctorsPatients {
  doctorID: number;
}

export interface IGetDoctorProfile {
  doctorID: number;
}

export interface IGetAllClinics {}

// Other
export interface ICreateClinic {
  clinicID: number;
  clinicName: string;
}
