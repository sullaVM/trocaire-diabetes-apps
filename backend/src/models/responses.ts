/**
 * User responses
 */

export interface ISimpleResponse {
  success: boolean;
}

export interface IDoctorID {
  success: boolean;
  doctorID?: number;
}

export interface ICreatePatient {
  success: boolean;
  message?: string;
  patientID?: number;
}

export interface IGetPatientProfile {
  success: boolean;
  doctorID?: number;
  firstName?: string;
  lastName?: string;
  userName?: string;
  height?: string;
  pregnant?: number;
  mobileNumber?: number;
  photoDataUrl?: string;
  bslUnit?: string;
}

export interface IGetGraphingData {
  success: boolean;
  RBP?: { time: string; systole: number; diastole: number }[];
  BSL?: { time: string; value: number }[];
  Weight?: { time: string; value: number }[];
}

export interface IStorePatientLog {
  success: boolean;
  message?: string;
}

export interface IGetPatientLogs {
  success: boolean;
  logs?: { time: string; note: string }[];
}

export interface IGetPatientID {
  success: boolean;
  patientID?: number;
}

/**
 * Doctor responses
 */

export interface IListDoctorsPatients {
  success: boolean;
  patientIDs?: number[];
}

export interface ISimplePatientProfile {
  patientID: number;
  firstName: string;
  lastName: string;
}

export interface IGetPatientsToSee {
  success: boolean;
  patients?: ISimplePatientProfile[];
}

export interface IGetDoctorProfile {
  success: boolean;
  doctorID?: number;
  firstName?: string;
  lastName?: string;
  licenseNumber?: number;
  email?: string;
  userName?: string;
}

export interface IGetAllDoctorsAtClinic {
  success: boolean;
  doctors?: {
    doctorID: number;
    firstName: string;
    lastName: string;
  }[];
}

export interface IGetAllClinics {
  success: boolean;
  clinics?: { clinicID: number; clinicName: string }[];
}

export interface ICreateClinic {
  success: boolean;
  clinicID?: number;
}

export interface IGetPatientPassword {
  success: boolean;
  password: string;
}

export interface IGetPatientToken {
  success: boolean;
  sessionToken: string;
}
