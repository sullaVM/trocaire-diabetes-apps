/**
 * Patient requests
 */

export interface ITakePhoto {
  patientID: number;
}

export interface ICreatePatient {
  doctorID: number;
  firstName: string;
  lastName: string;
  userName: string;
  height: string;
  pregnant: number;
  mobileNumber: number;
  photoDataUrl: string;
  password: string;
  bslUnit: string;
}

export interface IUpdatePatient {
  patientID: number;
  doctorID?: number;
  firstName?: string;
  lastName?: string;
  userName?: string;
  height?: string;
  pregnant?: number;
  mobileNumber?: number;
  photoDataUrl?: string;
  password?: string;
  bslUnit?: string;
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
  value: number;
  unit?: string;
}

export interface IStoreWeight {
  patientID: number;
  time: string;
  weightKG: number;
}

export interface IGetGraphingData {
  patientID: number;
  intervalStart: string;
  intervalEnd: string;
  bslUnit?: string;
}

export interface IGetPatientID {
  userName: number;
}

/**
 * Doctor requests
 */

export interface ICreateDoctor {
  firstName: string;
  lastName: string;
  licenseNumber: number;
  email: string;
  userName: string;
  password: string;
}

export interface IDeleteDoctor {
  doctorID: string;
}

export interface IUpdateDoctor {
  doctorID: number;
  firstName?: string;
  lastName?: string;
  licenseNumber?: number;
  email?: string;
  userName?: string;
  password?: string;
}

export interface IGetDoctorID {
  email: string;
}

export interface IListDoctorsPatients {
  doctorID: number;
}

export interface IGetDoctorProfile {
  doctorID: number;
}

export interface IGetAllDoctorsAtClinic {
  clinicID: number;
}

// Clinic
export interface IAddDoctorToClinic {
  clinicID: number;
  doctorID: number;
}

export interface IGetAllClinics {}

export interface ICreateClinic {
  clinicName: string;
}

export interface IAddDoctorToInvitedDoctors {
  email: string;
}

export interface IDeleteDoctorToInvitedDoctors {
  email: string;
}

export interface IVerifyInvitedDoctor {
  email: string;
}

export interface ISetPatientToken {
  sessionToken: string;
  patientID: number;
}

export interface IClearPatientToken {
  tokenID: string;
}
