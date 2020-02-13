/**
 * User responses
 */

export interface ICreatePatient {
  success: boolean;
  patientID?: number;
}

export interface IUpdatePatient {
  success: boolean;
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
 * Doctor requests
 */

export interface ICreateDoctor {
  success: boolean;
  doctorID?: number;
}
