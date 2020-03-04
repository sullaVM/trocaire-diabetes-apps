const firebaseConfig = {
  apiKey: 'AIzaSyB_S5wuORd-ZfPZmMgC9Nc8YW9xg1fFpgE',
  authDomain: 'trocaire-diabete-1581953003031.firebaseapp.com',
  databaseURL: 'https://trocaire-diabete-1581953003031.firebaseio.com',
  projectId: 'trocaire-diabete-1581953003031',
  storageBucket: 'trocaire-diabete-1581953003031.appspot.com',
  messagingSenderId: '630337403399',
  appId: '1:630337403399:web:9cdb0f2f1d3c9a08c1c8a2',
};

// Initialize Firebase
firebase.initializeApp(firebaseConfig);
firebase.auth().setPersistence(firebase.auth.Auth.Persistence.SESSION);

async function signIn() {
  if (firebase.auth().currentUser) {
    firebase.auth().signOut();
    await axios.get('/sessionLogout');
  } else {
    var email = document.getElementById('email').value;
    var password = document.getElementById('password').value;
    if (email.length < 4) {
      alert('Please enter an email address.');
      return;
    }
    if (password.length < 4) {
      alert('Please enter a password.');
      return;
    }

    // Sign in with email and password.
    try {
      await firebase.auth().signInWithEmailAndPassword(email, password);
      const startSession = async user => {
        if (user) {
          const idToken = await user.getIdToken();
          await axios.post('/sessionLogin', {
            idToken: idToken,
          });
          window.location.assign('/');
        }
      };
      firebase.auth().onAuthStateChanged(startSession);
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
}

async function signOut() {
  firebase.auth().signOut();
  await axios.get('/sessionLogout');
  window.location.assign('/login');
}

function initApp() {
  firebase.auth().onAuthStateChanged(function(user) {
    if (user) {
      // User is signed in.
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

      const doctorSignUpForm = document.getElementById('doctor-sign-up');
      if (doctorSignUpForm) {
        initDoctorSignup();
      }
    } else {
      // User is signed out.
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
  });

  const signin = document.getElementById('sign-in');
  if (signin) {
    signin.addEventListener('click', signIn, false);
  }

  const signout = document.getElementById('sign-out');
  if (signout) {
    signout.addEventListener('click', signOut, false);
  }
}

async function initDoctorSignup() {
  const clinicsRes = await axios.get('/api/admin/getAllClinics');
  const clinicList = document.getElementById('clinic-list');

  clinicsRes.data.clinics.forEach(clinic => {
    const option = document.createElement('option');
    option.innerHTML = clinic.clinicName;
    option.value = clinic.clinicID;
    clinicList.options.add(option);
  });
}

function addToChosen() {
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

  // Removing the item
  item.addEventListener('click', elem => {
    const target = elem.target;
    target.parentNode.removeChild(target);
  });
}

window.onload = function() {
  initApp();
};
