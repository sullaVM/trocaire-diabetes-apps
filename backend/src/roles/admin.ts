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
    const inviterID = request.body.inviterID;
    const originUrl = request.headers.host;

    console.log(inviterID);

    const doctorProfileResponse = await db.getDoctorProfile({
      doctorID: inviterID,
    });

    const emailBody = `Hi ${firstName} ${lastName}, \n\nYou have been invited by ${doctorProfileResponse.firstName}  ${doctorProfileResponse.lastName} to sign up to the Trocaire Diabetes Management App. \n\nTo sign up and learn more, please go to http://${originUrl}. \n\nKind regards, \nThe Trocaire Diabetes Management Team`;

    const addDoctorToInvitedDoctorsRequest: requests.IAddDoctorToInvitedDoctors = {
      email,
    };

    await db.addDoctorToInvitedDoctors(addDoctorToInvitedDoctorsRequest);

    sendMail(email, emailBody);

    response.render('callback', {
      helpers: {
        body: 'Success! You have invited a user.',
      },
    });
  } catch (error) {
    console.log('Error sending invite: ', error);

    response.status(500).render('callback', {
      helpers: {
        body: `Failed to invite user: ${error.toString()}`,
      },
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
      email,
    };

    const verifyInvitedDoctorResult = await db.verifyInvitedDoctor(
      verifyEmailInvitedRequest
    );

    if (!verifyInvitedDoctorResult.success) {
      throw new Error('Your email is not authorized to use this app');
    }

    const genHash: string = await new Promise((resolve, reject) => {
      // tslint:disable-next-line: no-shadowed-variable
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

    const user: IFirebaseUser = {
      email,
      password,
      isAdmin: true,
      isDoctor: true,
      displayName: [firstName, lastName].join(' '),
    };

    const firebaseResult = await createNewUser(user);
    if (!firebaseResult) {
      throw new Error('Creating Firebase account for doctor failed');
    }

    response.render('callback', {
      layout: 'simple',
      helpers: {
        message:
          'Welcome to the Korta Diabetes Management App! Sign in to continue.',
      },
    });
  } catch (error) {
    console.log('Error creating doctor account: ', error);
    response.render('callback', {
      layout: 'simple',
      helpers: {
        message: error.toString(),
      },
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
    licenseNo: request.body.licenseNumber,
    email: request.body.email,
    userName: request.body.username,
    password: request.body.password,
  };

  db.updateDoctor(updateDoctorRequest)
    .then(_result => {
      response.render('callback', {
        helpers: {
          body: 'Success! You have updated your profile.',
        },
      });
    })
    .catch(error => {
      response.status(500).render('callback', {
        helpers: {
          body: `Error: ${error.toString()}`,
        },
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
      response.status(500).send({
        success: false,
        message: `Request unsuccessful, Error: ${error.toString()}`,
      });
    });
};

export const createClinic = (request: Request, response: Response) => {
  const clinicName = request.body.clinicName;
  const createClinicRequest: requests.ICreateClinic = {
    clinicName: clinicName,
  };

  db.createClinic(createClinicRequest)
    .then(result => {
      response.render('callback', {
        helpers: {
          body: `Success! You have added a ${clinicName}.`,
        },
      });
    })
    .catch(error => {
      response.status(500).send({
        success: false,
        message: `Request unsuccessful, Error: ${error.toString()}`,
      });
    });
};

export const addDoctorToMultClinics = (
  request: Request,
  response: Response
) => {
  try {
    const doctorID = request.body.doctorID;

    console.log(doctorID);

    if (request.body.clinicIDs) {
      const clinicIDs = [...request.body.clinicIDs];
      clinicIDs.forEach((id: number) => {
        assignClinic(id, doctorID);
      });
    }

    response.render('callback', {
      helpers: {
        body: 'Success!',
      },
    });
  } catch (error) {
    console.log(error);
    response.status(500).render('callback', {
      helpers: {
        body: `Error: ${error.toString()}`,
      },
    });
  }
};

const assignClinic = async (
  clinicID: number,
  doctorID: number
): Promise<string> => {
  try {
    const addDoctorToClinicRequest: requests.IAddDoctorToClinic = {
      clinicID,
      doctorID,
    };
    await db.addDoctorToClinic(addDoctorToClinicRequest);
    return `Successfully added clinic ${doctorID} to ${clinicID}`;
  } catch (error) {
    return `Error assigning clinic ${clinicID} to ${doctorID}: ${error}`;
  }
};
