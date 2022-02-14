package com.s2dioapps.divinepolytechniccollege.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.s2dioapps.divinepolytechniccollege.MainActivity;
import com.s2dioapps.divinepolytechniccollege.NoInternetActivity;
import com.s2dioapps.divinepolytechniccollege.R;
import com.s2dioapps.divinepolytechniccollege.common.DbQuery;
import com.s2dioapps.divinepolytechniccollege.common.MyCompleteListener;
import com.s2dioapps.divinepolytechniccollege.common.Util;
import com.s2dioapps.divinepolytechniccollege.password.ResetPasswordActivity;
import com.s2dioapps.divinepolytechniccollege.signup.SignupActivity;
import com.s2dioapps.divinepolytechniccollege.ui.test.TestActivity;
import com.s2dioapps.divinepolytechniccollege.ui.test.TestAdapter;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private String email, password;
    private View progressBar;

    FirebaseAuth firebaseAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 104;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword= findViewById(R.id.etPassword);

        progressBar = findViewById(R.id.progressBar);


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        firebaseAuth = FirebaseAuth.getInstance();

    }

    private void firebaseAuthWithGoogle(String idToken) {
        //progressBar.setVisibility(View.VISIBLE);

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Log.d("TAG", "signInWithCredential:success");
                            Toast.makeText(LoginActivity.this, "NIce", Toast.LENGTH_LONG).show();

                        } else {
                           // progressBar.setVisibility(View.INVISIBLE);
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                           // updateUI(null);
                        }
                    }
                });
    }

    public void tvSignupClick(View v)
    {
        startActivity(new Intent(this, SignupActivity.class));
    }
    public void btnLoginClick(View v)
    {
        email = etEmail.getText().toString().trim();
        password= etPassword.getText().toString().trim();

        if(email.equals(""))
        {

            etEmail.setError(getString(R.string.enter_email));

        }
        else if (password.equals(""))
        {

            etPassword.setError(getString(R.string.enter_password));

        } else {
            if(Util.connectionAvailable(this)) {
                progressBar.setVisibility(View.VISIBLE);

                //firebaseAuth = FirebaseAuth.getInstance();

                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        progressBar.setVisibility(View.GONE);
                        if(firebaseAuth.getCurrentUser() != null){
                            firebaseAuth.getCurrentUser().reload();
                        }

                        if(firebaseAuth.getCurrentUser() != null &&
                                firebaseAuth.getCurrentUser().isEmailVerified()) {

                            if (task.isSuccessful()) {

                            DbQuery.loadData(new MyCompleteListener() {
                                @Override
                                public void onSuccess() {

                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();



                                }

                                @Override
                                public void onFailure() {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(LoginActivity.this, "Something went wrong : " +
                                            task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });



                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, "Login Failed : " +
                                    task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }else{

                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "Email is not verified", Toast.LENGTH_SHORT).show();
                    }


                    }
                });
            }
            else
            {
                startActivity(new Intent(LoginActivity.this, NoInternetActivity.class));
            }

        }



    }

    public  void tvResetPasswordClick(View view){
        startActivity(new Intent(LoginActivity.this , ResetPasswordActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

//        if(firebaseUser!=null && Util.connectionAvailable(this))
//        {
//            progressBar.setVisibility(View.VISIBLE);
//
//            DbQuery.loadData(new MyCompleteListener() {
//
//                @Override
//                public void onSuccess() {
//
//                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                    finish();
//
//                }//end of loadData
//
//                @Override
//                public void onFailure() {
//                    progressBar.setVisibility(View.GONE);
//                    Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
//                }
//            });
//
//        }else{
//            startActivity(new Intent(LoginActivity.this, NoInternetActivity.class));
//        }


    }
}