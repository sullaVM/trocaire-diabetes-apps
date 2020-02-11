export interface ICreateUser {
  doctorID: number;
  firstName: string;
  lastName: string;
  mobileNumber: number;
  photoUrl: string;
  password: string;
}

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
