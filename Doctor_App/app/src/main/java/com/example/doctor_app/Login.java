package com.example.doctor_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import static com.example.doctor_app.data.requests.PatientRequest.API_BASE_URL;

public class Login extends AppCompatActivity {

    private static final String TAG = "EmailPassword";

    private EditText mEmailField;
    private EditText mPasswordField;
    private MaterialButton next;
    private ProgressBar pr;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        // Views
        mEmailField = findViewById(R.id.docEmail);
        mPasswordField = findViewById(R.id.docPassword);
        pr = findViewById(R.id.indeterminateBar);
        pr.setVisibility(View.INVISIBLE);

        // Buttons
        next = findViewById(R.id.buttonNext);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
            }
        });

        // Initialise Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.signOut();
        pr.setVisibility(View.INVISIBLE);
        next.setEnabled(true);
        updateUI(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAuth.signOut();
        pr.setVisibility(View.INVISIBLE);
        next.setEnabled(true);
        updateUI(null);
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        pr.setVisibility(View.VISIBLE);
        next.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            pr.setVisibility(View.INVISIBLE);

                            mAuth.getAccessToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                @Override
                                public void onComplete(@NonNull Task<GetTokenResult> task) {
                                    GetTokenResult tokenResult = task.getResult();
                                    String token = tokenResult.getToken();
                                    CookieManager cookieManager = CookieManager.getInstance();
                                    cookieManager.setCookie(API_BASE_URL, "session=" + token);
                                }
                            });
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            pr.setVisibility(View.INVISIBLE);
                            next.setEnabled(true);
                            updateUI(null);
                        }

                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // Signed-in. Go to dashboard
            Intent intent = new Intent(this, Dashboard.class);
            startActivity(intent);
        }
        // Else, not signed-in. Stay on login page
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }
}
