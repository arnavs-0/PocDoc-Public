package com.arnav.pocdoc.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.arnav.pocdoc.Onboarding.Introduction;
import com.arnav.pocdoc.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import es.dmoral.toasty.Toasty;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

import java.util.Random;

public class Registration extends AppCompatActivity {
    private static final String TAG = "EmailPassword";
    Button login, register;
    MaterialProgressBar bar;
    EditText email, password, firstName, lastName, confirmPassword;
    SignInButton google;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        login = findViewById(R.id.haveaccount);
        register = findViewById(R.id.register);

        bar = findViewById(R.id.regbar);

        email = findViewById(R.id.email_reg);
        password = findViewById(R.id.password_reg);
        firstName = findViewById(R.id.firstname);
        lastName = findViewById(R.id.lastname);
        confirmPassword = findViewById(R.id.confirm);
        google = findViewById(R.id.sign_up_button);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(view -> createAccount());

        google.setOnClickListener(view -> {
            bar.setVisibility(View.VISIBLE);
            signUpWithGoogle();
        });

        login.setOnClickListener(view -> {
            bar.setVisibility(View.VISIBLE);
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            bar.setVisibility(View.GONE);
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                initializeMedicalID(account.getEmail());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toasty.success(getApplicationContext(), "You Registered Successfully", Toast.LENGTH_SHORT, true).show();
                        Intent intent = new Intent(getApplicationContext(), Introduction.class);
                        intent.putExtra("user", user);
                        startActivity(intent);
                        bar.setVisibility(View.GONE);
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        bar.setVisibility(View.GONE);
                        Toasty.error(getApplicationContext(), "Sign Up Failed.", Toast.LENGTH_SHORT, true).show();
                    }
                });
    }

    private void initializeMedicalID(String email) {
        // Initialize values for Medical ID
        String empty_text = getString(R.string.empty_text);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("medical_id").child(FirebaseAuth.getInstance().getUid());

        Random generator = new Random();
        StringBuilder id = new StringBuilder();
        for (int i=0; i < 8; i++) {
            id.append(generator.nextInt(10));
        }

        reference.child("patientID").setValue(id.toString());
        reference.child("location").setValue(empty_text);
        reference.child("email").setValue(email);
        reference.child("insurance").setValue(empty_text);

        reference.child("age").setValue("20");
        reference.child("gender").setValue("male");
        reference.child("height").setValue(empty_text);
        reference.child("weight").setValue(empty_text);
        reference.child("bloodType").setValue(empty_text);
        reference.child("race").setValue(empty_text);

        reference.child("conditions").setValue(empty_text);
        reference.child("familyHistory").setValue(empty_text);
        reference.child("bloodPressure").setValue(empty_text);
        reference.child("sugar").setValue(empty_text);
        reference.child("allergies").setValue(empty_text);
        reference.child("habits").setValue(empty_text);
        reference.child("otc").setValue(empty_text);
        reference.child("rx").setValue(empty_text);
    }

    private void createAccount(){
        String emailInput = email.getText().toString();
        String passwordInput = password.getText().toString();
        Log.d(TAG, "createAccount:" + emailInput);
        if (!validateForm()) {
            return;
        }

        bar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(emailInput, passwordInput)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();

                        initializeMedicalID(emailInput);

                        Intent intent = new Intent(getApplicationContext(), Introduction.class);
                        intent.putExtra("user", user);
                        startActivity(intent);
                        bar.setVisibility(View.GONE);
                        Toasty.success(getApplicationContext(), "Signed Up Successfully.", Toast.LENGTH_SHORT, true).show();
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        bar.setVisibility(View.GONE);
                        Toasty.error(Registration.this, "Authentication failed.", Toast.LENGTH_SHORT, true).show();
                    }
                    bar.setVisibility(View.GONE);
                });
    }

    private void signUpWithGoogle(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private boolean validateForm() {
        boolean valid = true;

        String emailInput = email.getText().toString();
        if (TextUtils.isEmpty(emailInput)) {
            email.setError("Required.");
            valid = false;
        } else {
            email.setError(null);
        }

        String passwordInput = password.getText().toString();
        if (passwordInput.isEmpty()) {
            password.setError("Required.");
            valid = false;
        } else {
            password.setError(null);
        }

        String confirmInput = confirmPassword.getText().toString();
        if (confirmInput.isEmpty()){
            confirmPassword.setError("Required.");
            valid = false;
        } else {
            confirmPassword.setError(null);
        }

        if (!passwordInput.equals(confirmInput)){
            confirmPassword.setError("Password Does Not Match");
            valid = false;
        } else {
            confirmPassword.setError(null);
        }


        return valid;
    }
}