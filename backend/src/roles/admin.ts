import { Request, Response } from 'express';
import { createTransport } from 'nodemailer';
import { hash, compare } from 'bcrypt';
import { createNewUser } from '../firebase/firebase';
import * as db from '../database';
import * as requests from '../models/requests';
import { IFirebaseUser } from '../firebase/types';

export const pwEncryptSaltRounds = 10;

export const inviteDoctor = async (request: Request, response: Response) => {
  try {
    const email = request.body.email;
    const firstName = request.body.firstName;
    const lastName = request.body.lastName;
    const inviterName = request.body.inviterName;
    const originUrl = request.headers.host;

    // TODO(sulla): Clean this up
    const emailBody = `Hi ${firstName}, \nYou have been invited by Dr. ${inviterName} to signup to the Trocaire Diabetes Management App. \n\nTo sign up, please go to http://${originUrl}. \n\nKind regards, \nThe Trocaire Diabetes Management Team`;

    const addDoctorToInvitedDoctorsRequest: requests.IAddDoctorToInvitedDoctors = {
      email: email,
    };

    await db.addDoctorToInvitedDoctors(addDoctorToInvitedDoctorsRequest);

    sendMail(email, emailBody);

    response.sendStatus(200);
  } catch (error) {
    console.log('Error sending invite: ', error);

    response.status(500).send({
      success: false,
      message: error,
    });
  }
};

export const createDoctor = async (request: Request, response: Response) => {
  try {
    const firstName = request.body.firstName;
    const lastName = request.body.lastName;
    const licenseNumber = request.body.licenseNumber;
    const email = request.body.email;
    const username = request.body.username;
    const password = request.body.password;

    const verifyEmailInvitedRequest: requests.IVerifyInvitedDoctor = {
      email: email,
    };

    const verifyInvitedDoctorResult = await db.verifyInvitedDoctor(
      verifyEmailInvitedRequest
    );

    if (!verifyInvitedDoctorResult.success) {
      throw new Error('Your email is not authorized to use this app.');
    }

    const genHash: string = await new Promise((resolve, reject) => {
      hash(password, pwEncryptSaltRounds, (error, hash) => {
        if (error) {
          reject(error);
        }
        resolve(hash);
      });
    });

    const createDoctorRequest: requests.ICreateDoctor = {
      firstName,
      lastName,
      licenseNumber,
      email,
      userName: username,
      password: genHash,
    };

    const dbResult = await db.createDoctor(createDoctorRequest);

    const doctorID = dbResult.doctorID;
    if (!doctorID) {
      throw new Error('DoctorID is invalid or undefined');
    }

    if (request.body.clinicIDs) {
      const clinicIDs = [...request.body.clinicIDs];
      clinicIDs.forEach((id: number) => {
        assignClinic(id, doctorID);
      });
    }

    const user: IFirebaseUser = {
      email,
      password: password,
      isAdmin: true,
      isDoctor: true,
      displayName: [firstName, lastName].join(' '),
    };

    const firebaseResult = await createNewUser(user);
    if (!firebaseResult) {
      throw new Error('Creating Firebase account for doctor failed');
    }

    response.sendStatus(200);
  } catch (error) {
    console.log('Error creating doctor account: ', error);
    response.status(500).send({
      success: false,
      message: error.toString(),
    });
  }
};

export const sendMail = async (email: string, emailBody: string) => {
  const transporter = createTransport({
    host: 'smtp.gmail.com',
    port: 465,
    secure: true,
    auth: {
      type: 'OAuth2',
      user: process.env.GMAIL_ADDRESS,
      refreshToken: process.env.GMAIL_REFRESH_TOKEN,
      clientId: process.env.GOOGLE_CLIENT_ID,
      clientSecret: process.env.GOOGLE_CLIENT_SECRET,
    },
  });

  const mailOptions = {
    from: process.env.GMAIL_ADDRESS,
    to: email,
    subject: 'You have a new account with Trocaire Diabetes App',
    text: emailBody,
  };

  return new Promise((resolve, reject) => {
    transporter.sendMail(mailOptions, (error, info) => {
      if (error) {
        reject(error);
      } else {
        console.log('Email sent: ', info.response);
        resolve();
      }
    });
  });
};

export const updateDoctor = (request: Request, response: Response) => {
  const updateDoctorRequest: requests.IUpdateDoctor = {
    doctorID: request.body.doctorID,
    firstName: request.body.firstName,
    lastName: request.body.lastName,
    licenseNumber: request.body.licenseNumber,
    email: request.body.email,
    userName: request.body.username,
    password: request.body.password,
  };

  db.updateDoctor(updateDoctorRequest)
    .then(result => {
      response.status(200).send(result);
    })
    .catch(error => {
      response.status(200).send({
        success: false,
        message: 'Request unsuccessful, Error:' + error,
      });
    });
};

export const deleteDoctor = (request: Request, response: Response) => {
  const deleteDoctorRequest: requests.IDeleteDoctor = {
    doctorID: request.body.doctorID,
  };

  db.deleteDoctor(deleteDoctorRequest)
    .then(result => {
      response.status(200).send(result);
    })
    .catch(error => {
      response.status(200).send({
        success: false,
        message: 'Request unsuccessful, Error:' + error,
      });
    });
};

export const createClinic = (request: Request, response: Response) => {
  const createClinicRequest: requests.ICreateClinic = {
    clinicName: request.body.clinicName,
  };

  db.createClinic(createClinicRequest)
    .then(result => {
      response.status(200).send(result);
    })
    .catch(error => {
      response.status(200).send({
        success: false,
        message: 'Request unsuccessful, Error: ' + error,
      });
    });
};

export const addDoctorToClinic = (request: Request, response: Response) => {
  const addDoctorToClinicRequest: requests.IAddDoctorToClinic = {
    clinicID: request.body.clinicID,
    doctorID: request.body.doctorID,
  };

  db.addDoctorToClinic(addDoctorToClinicRequest)
    .then(result => {
      response.status(200).send(result);
    })
    .catch(error => {
      response.status(200).send({
        success: false,
        message: 'Request unsuccessful, Error: ' + error,
      });
    });
};

const assignClinic = async (
  clinicID: number,
  doctorID: number
): Promise<string> => {
  try {
    const addDoctorToClinicRequest: requests.IAddDoctorToClinic = {
      clinicID: clinicID,
      doctorID: doctorID,
    };
    await db.addDoctorToClinic(addDoctorToClinicRequest);
    return `Successfully added clinic ${doctorID} to ${clinicID}`;
  } catch (error) {
    return `Error assigning clinic ${clinicID} to ${doctorID}: ${error}`;
  }
};
