const firebaseConfig = {
  apiKey: 'AIzaSyB_S5wuORd-ZfPZmMgC9Nc8YW9xg1fFpgE',
  authDomain: 'trocaire-diabete-1581953003031.firebaseapp.com',
  databaseURL: 'https://trocaire-diabete-1581953003031.firebaseio.com',
  projectId: 'trocaire-diabete-1581953003031',
  storageBucket: 'trocaire-diabete-1581953003031.appspot.com',
  messagingSenderId: '630337403399',
  appId: '1:630337403399:web:9cdb0f2f1d3c9a08c1c8a2',
};

firebase.initializeApp(firebaseConfig);
firebase.auth().setPersistence(firebase.auth.Auth.Persistence.LOCAL);

async function signIn() {
  try {
    if (firebase.auth().currentUser) {
      window.localStorage.clear();
      firebase.auth().signOut();
      await axios.get('/sessionLogout');
    } else {
      const email = document.getElementById('email').value;
      const password = document.getElementById('password').value;

      if (email.length < 4) {
        alert('Please enter an email address.');
        return;
      }
      if (password.length < 4) {
        alert('Please enter a password longer than 6 characters.');
        return;
      }

      await firebase.auth().signInWithEmailAndPassword(email, password);

      const user = await new Promise((resolve, reject) => {
        firebase.auth().onAuthStateChanged((user, error) => {
          if (error) {
            reject(error);
          }
          resolve(user);
        });
      });

      if (user) {
        const idToken = await user.getIdToken();
        const response = await axios.post('/sessionLogin', {
          email: email,
          idToken: idToken,
        });

        const doctorID = response.data.doctorID;
        if (!doctorID) {
          window.location.assign('/');
        } else {
          localStorage.setItem('doctorID', doctorID);
          window.location.assign(`/?doctorID=${doctorID}`);
        }
      }

      // TODO(sulla): Redirect instead of alert
      if (!user.emailVerified) {
        await user.sendEmailVerification();
        alert(
          'A verification email has been sent to you. Please check your email.'
        );
      }
    }
  } catch (error) {
    var errorCode = error.code;
    var errorMessage = error.message;

    if (errorCode === 'auth/wrong-password') {
      alert('Wrong password.');
    } else {
      alert(errorMessage);
    }
  }
}

async function signOut() {
  window.localStorage.clear();
  firebase.auth().signOut();
  await axios.get('/sessionLogout');
  window.location.assign('/');
}

function validatePassword() {
  const password = document.getElementById('password');
  const confirmPassword = document.getElementById('confirmPassword');

  if (password.value != confirmPassword.value) {
    confirmPassword.setCustomValidity("Passwords don't match.");
  } else {
    confirmPassword.setCustomValidity('');
  }
}

async function initClinics() {
  const clinicsRes = await axios.get('/api/admin/getAllClinics');
  const clinicList = document.getElementById('clinic-list');

  clinicsRes.data.clinics.forEach(clinic => {
    const option = document.createElement('option');
    option.innerHTML = clinic.clinicName;
    option.value = clinic.clinicID;
    clinicList.options.add(option);
  });
}

function addToChosenClinics() {
  const clinicList = document.getElementById('clinic-list');
  const clinicValue = clinicList.options[clinicList.selectedIndex].value;
  const clinicName = clinicList.options[clinicList.selectedIndex].textContent;

  const chosenClinics = document.getElementById('chosen-clinics');

  const item = document.createElement('li');
  const input = document.createElement('input');
  const textNode = document.createTextNode(clinicName);

  input.name = 'clinicIDs';
  input.type = 'hidden';
  input.value = clinicValue;

  item.value = clinicValue;
  item.appendChild(input);
  item.appendChild(textNode);

  chosenClinics.appendChild(item);

  item.addEventListener('click', elem => {
    const target = elem.target;
    target.parentNode.removeChild(target);
  });
}

async function initEditProfile() {
  const response = await axios.post('/api/getDoctorProfile', {
    doctorID: localStorage.getItem('doctorID'),
  });
  const userProfile = response.data;

  document.getElementById('first-name').value = userProfile.firstName;
  document.getElementById('last-name').value = userProfile.lastName;
  document.getElementById('license-number').value = userProfile.licenseNumber;
  document.getElementById('email').value = userProfile.email;
  document.getElementById('username').value = userProfile.userName;
}

async function initApp() {
  try {
    const signin = document.getElementById('sign-in');
    if (signin) {
      signin.addEventListener('click', signIn, false);
    }

    const passwordInput = document.getElementById('password');
    if (passwordInput) {
      passwordInput.addEventListener('keydown', event => {
        if (event.keyCode == 13) {
          signIn();
        }
      });
    }

    const signout = document.getElementById('sign-out');
    if (signout) {
      signout.addEventListener('click', signOut, false);
    }

    const doctorIDInput = document.getElementById('doctor-id');
    if (doctorIDInput) {
      doctorIDInput.value = localStorage.getItem('doctorID');
    }

    const getDashboard = document.getElementById('get-dashboard');
    if (getDashboard) {
      getDashboard.href = `/?doctorID=${localStorage.getItem('doctorID')}`;
    }

    const user = await new Promise((resolve, reject) => {
      firebase.auth().onAuthStateChanged((user, error) => {
        if (error) {
          reject(error);
        }
        resolve(user);
      });
    });

    if (user) {
      const signInStatus = document.getElementById('sign-in-status');
      if (signInStatus) {
        signInStatus.textContent = 'Signed in';
      }

      const accountDetails = document.getElementById(
        'quickstart-account-details'
      );
      if (accountDetails) {
        accountDetails.textContent = JSON.stringify(user, null, '  ');
      }

      const addDoctorToClinicForm = document.getElementById(
        'add-doctor-clinic'
      );
      if (addDoctorToClinicForm) {
        initClinics();
      }

      const editProfileForm = document.getElementById('edit-profile');
      if (editProfileForm) {
        initEditProfile();
      }
    } else {
      const signInStatus = document.getElementById('sign-in-status');
      if (signInStatus) {
        signInStatus.textContent = 'Signed out';
      }

      const accountDetails = document.getElementById(
        'quickstart-account-details'
      );
      if (accountDetails) {
        accountDetails.textContent = 'null';
      }
    }
  } catch (error) {
    console.log('Error initialising app: ', error);
  }
}

window.onload = function() {
  initApp();
};
