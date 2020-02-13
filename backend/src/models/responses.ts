/**
 * User responses
 */

export interface ICreatePatient {
  success: boolean;
  patientID?: number;
}

export interface IGetPatientProfile {
  success: boolean;
  doctorID?: number;
  firstName?: string;
  lastName?: string;
  mobileNumber?: number;
  photoDataUrl?: string;
}

export interface IStoreRBP {
  success: boolean;
}

export interface IStoreBSL {
  success: boolean;
}

/**
 * Doctor responses
 */

export interface ICreateDoctor {
  success: boolean;
  doctorID?: number;
}

export interface IListDoctorsPatients {
  success: boolean;
  patientIDs?: number;
}

export interface IGetDoctorProfile {
  success: boolean;
  doctorID?: number;
  firstName?: string;
  lastName?: string;
  licenseNumber?: number;
  clinicID?: number;
  email?: string;
  userName?: string;
}

export interface IGetAllClinics {
  success: boolean;
  clinics?: string;
}
