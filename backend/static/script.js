var firebaseConfig = {
    apiKey: "AIzaSyB_S5wuORd-ZfPZmMgC9Nc8YW9xg1fFpgE",
    authDomain: "trocaire-diabete-1581953003031.firebaseapp.com",
    databaseURL: "https://trocaire-diabete-1581953003031.firebaseio.com",
    projectId: "trocaire-diabete-1581953003031",
    storageBucket: "trocaire-diabete-1581953003031.appspot.com",
    messagingSenderId: "630337403399",
    appId: "1:630337403399:web:9cdb0f2f1d3c9a08c1c8a2"
};

// Initialize Firebase
firebase.initializeApp(firebaseConfig);

function toggleSignIn() {
    if (firebase.auth().currentUser) {
        firebase.auth().signOut();
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
        // Sign in with email and pass.
        firebase.auth().signInWithEmailAndPassword(email, password).catch(function (error) {
            // Handle Errors here.
            var errorCode = error.code;
            var errorMessage = error.message;

            if (errorCode === 'auth/wrong-password') {
                alert('Wrong password.');
            } else {
                alert(errorMessage);
            }
            console.log(error);
            document.getElementById('sign-in').disabled = false;
        });

        firebase.auth().onAuthStateChanged(function (user) {
            if (user) {
                window.location = "/doctorSignUp.html";
            }
        });
    }
    document.getElementById('sign-in').disabled = true;
}

function initApp() {
    firebase.auth().onAuthStateChanged(function (user) {
        // document.getElementById('quickstart-verify-email').disabled = true;
        if (user) {
            // User is signed in.
            var displayName = user.displayName;
            var email = user.email;
            var emailVerified = user.emailVerified;
            var photoURL = user.photoURL;
            var isAnonymous = user.isAnonymous;
            var uid = user.uid;
            var providerData = user.providerData;

            const signInStatus = document.getElementById('sign-in-status');
            if (signInStatus) {
                signInStatus.textContent = 'Signed in';
            }

            // Sign out button
            document.getElementById('sign-in').textContent = 'Sign out';

            const accountDetails = document.getElementById('quickstart-account-details');
            if (accountDetails) {
                accountDetails.textContent = JSON.stringify(user, null, '  ');
            }

        } else {
            // User is signed out.
            const signInStatus = document.getElementById('sign-in-status');
            if (signInStatus) {
                signInStatus.textContent = 'Signed out';
            }

            // Sign in button
            document.getElementById('sign-in').textContent = 'Sign in';

            const accountDetails = document.getElementById('quickstart-account-details');
            if (accountDetails) {
                accountDetails.textContent = "null";
            }

        }
        document.getElementById('sign-in').disabled = false;
    });

    document.getElementById('sign-in').addEventListener('click', toggleSignIn, false);
}

window.onload = function () {
    initApp();
};