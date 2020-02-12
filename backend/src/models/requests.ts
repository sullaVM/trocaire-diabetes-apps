/**
 * User requests
 */

export interface ICreateUser {
  doctorID: number;
  firstName: string;
  lastName: string;
  mobileNumber: number;
  photoDataUrl: string;
  password: string;
}

export interface IGetUserProfile {
  userID: number;
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
