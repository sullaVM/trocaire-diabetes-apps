import mysql from 'mysql';
import fs from 'fs';
import * as requests from './models/requests';
import * as responses from './models/responses';

let db: mysql.Connection;

fs.readFile('dbconfig.json', 'utf8', (error, data) => {
  if (error) {
    console.log('dbconfig.json is missing, put it in root of project');
    throw error;
  }
  const mysqlConfig = JSON.parse(data);
  db = mysql.createConnection(mysqlConfig);
  db.connect();
});

export const createUser = async (
  request: requests.ICreateUser
): Promise<responses.ICreateUser> => {
  const query = `INSERT INTO Patients (DoctorID, FirstName, LastName, MobileNumber, PhotoLink, Password) VALUES (${request.doctorID},${request.firstName},${request.lastName},${request.mobileNumber},${request.photoUrl},${request.password})`;

  const result = await new Promise<responses.ICreateUser>(resolve => {
    db.query(query, (error, results, fields) => {
      if (error) {
        console.error(error);
      }
      console.log({ results, fields });
      resolve({});
    });
  });

  return result;
};
