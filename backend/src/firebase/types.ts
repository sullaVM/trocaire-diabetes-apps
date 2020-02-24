export interface FirebaseUser {
  email: Required<string>;
  temporaryPassword: Required<string>;
  isAdmin: Required<boolean>;
  isDoctor: Required<boolean>;
  displayName: Required<string>;
}
