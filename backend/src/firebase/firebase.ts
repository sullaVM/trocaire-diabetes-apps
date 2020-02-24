import { initializeApp, credential, auth } from 'firebase-admin';
import { FirebaseUser } from './types';

export const initFirebase = () => {
  initializeApp({
    credential: credential.applicationDefault(),
  });
};

export const createNewUser = async (user: FirebaseUser): Promise<boolean> => {
  try {
    const userRecord = await auth().createUser({
      email: user.email,
      password: user.temporaryPassword,
      displayName: user.displayName,
    });

    if (user.isAdmin) {
      await auth().setCustomUserClaims(userRecord.uid, { admin: true });
    }
    if (user.isDoctor) {
      await auth().setCustomUserClaims(userRecord.uid, { doctor: true });
    }

    return true;
  } catch (error) {
    console.log('Create Firebase user error: ', error);
    return false;
  }
};

export const createNewCookie = async (
  idToken: string,
  expiresIn: number
): Promise<string | null> => {
  try {
    const decodedIdToken = await auth().verifyIdToken(idToken);
    const now = new Date().getTime();
    const age = now / 1000 - decodedIdToken.auth_time;

    if (age < 5 * 60) {
      return await auth().createSessionCookie(idToken, { expiresIn });
    }
    return null;
  } catch (error) {
    console.log('Create cookie error: ', error);
    return null;
  }
};

export const revokeToken = async (cookie: string): Promise<void> => {
  try {
    const decodedClaims = await auth().verifySessionCookie(cookie);
    await auth().revokeRefreshTokens(decodedClaims.sub);
  } catch (error) {
    console.log('Revoke token error: ', error);
  }
};

export const isAdmin = async (cookie: string): Promise<boolean> => {
  try {
    const decodedClaims = await auth().verifySessionCookie(cookie, true);
    const userRecord = await auth().getUser(decodedClaims.uid);
    const claims = userRecord.customClaims;

    if (!claims) {
      return false;
    }
    if (claims.hasOwnProperty('admin')) {
      return true;
    }
    return false;
  } catch (error) {
    console.log('Admin check error: ', error);
    return false;
  }
};

export const isDoctor = async (cookie: string): Promise<boolean> => {
  try {
    const decodedClaims = await auth().verifySessionCookie(cookie, true);
    const userRecord = await auth().getUser(decodedClaims.uid);
    const claims = userRecord.customClaims;

    if (!claims) {
      return false;
    }
    if (claims.hasOwnProperty('doctor')) {
      return true;
    }
    return false;
  } catch (error) {
    console.log('Doctor check error: ', error);
    return false;
  }
};
