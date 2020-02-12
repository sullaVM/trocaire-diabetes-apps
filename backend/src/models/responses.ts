/**
 * User responses
 */

export interface ICreateUser {
  success: boolean;
  userID?: number;
}

export interface IGetUserProfile {
  success: boolean;
  doctorID?: number;
  firstName?: string;
  lastName?: string;
  mobileNumber?: number;
  photoDataUrl?: string;
}

/**
 * Doctor requests
 */

export interface ICreateDoctor {
  success: boolean;
  doctorID?: number;
}
