import { Request, Response } from 'express';
import { generate } from 'generate-password';
import { createTransport } from 'nodemailer';
import { hash } from 'bcrypt';
import { createNewUser } from '../firebase/firebase';

import * as db from '../database';
import * as requests from '../models/requests';
import { IFirebaseUser } from '../firebase/types';

const pwEncryptSaltRounds = 10;

export const createDoctor = async (request: Request, response: Response) => {
  const firstName = request.body.firstName;
  const lastName = request.body.lastName;
  const licenseNumber = request.body.licenseNumber;
  const email = request.body.email;
  const username = request.body.username;
  const clinicIDs: number[] = request.body.clinicIDs;

  // Generate password.
  const generatedPass = generate({
    length: 12,
    numbers: true,
    lowercase: true,
    uppercase: true,
  });

  // Send temporary password to new user (doctor).
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

  const emailBody = [
    `Hi ${firstName}`,
    `You have a new account with Trocaire Diabetes App. Your temporary password is: ${generatedPass}.`,
    `from the Trocaire Team`,
  ].join('\n\n');

  const mailOptions = {
    from: process.env.GMAIL_ADDRESS,
    to: email,
    subject: 'You have a new account with Trocaire Diabetes App',
    text: emailBody,
  };

  // Encrypt generated password and add entry to db.
  hash(generatedPass, pwEncryptSaltRounds, (_, hash) => {
    const createDoctorRequest: requests.ICreateDoctor = {
      firstName,
      lastName,
      licenseNumber,
      email,
      userName: username,
      password: hash,
    };

    const user: IFirebaseUser = {
      email,
      temporaryPassword: generatedPass,
      isAdmin: true,
      isDoctor: true,
      displayName: [firstName, lastName].join(' '),
    };

    db.createDoctor(createDoctorRequest)
      .then(async result => {
        const doctorID = result.doctorID;
        if (doctorID) {
          clinicIDs.forEach((id: number) => {
            assignClinic(id, doctorID);
          });
        }

        if (await createNewUser(user)) {
          transporter.sendMail(mailOptions, (error, info) => {
            if (error) {
              console.log('Error: ', error);
            } else {
              console.log('Email sent: ' + info.response);
            }
          });
          response.sendStatus(200);
        } else {
          response.sendStatus(500);
        }
      })
      .catch(error => {
        response.status(200).send({
          success: false,
          message: 'Request unsuccessful, Error:' + error,
        });
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
