export interface IFirebaseUser {
  email: Required<string>;
  password: Required<string>;
  isAdmin: Required<boolean>;
  isDoctor: Required<boolean>;
  displayName: Required<string>;
}

export interface ICustomClaims {
  admin: boolean;
  doctor: boolean;
}
