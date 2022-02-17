package com.s2dioapps.divinepolytechniccollege.signup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.rpc.context.AttributeContext;
import com.s2dioapps.divinepolytechniccollege.R;
import com.s2dioapps.divinepolytechniccollege.common.DbQuery;
import com.s2dioapps.divinepolytechniccollege.common.MyCompleteListener;
import com.s2dioapps.divinepolytechniccollege.common.NodeNames;
import com.s2dioapps.divinepolytechniccollege.login.LoginActivity;
import com.s2dioapps.divinepolytechniccollege.ui.myprofile.MyProfileActivity;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etName, etPassword, etConfirmPassword;
    private String email, name, password, confirmPassword;

    private ImageView ivProfile;
    private FirebaseFirestore firestore;

    private StorageReference fileStorage;
    private Uri localFileUri, serverFileUri;
    private View progressBar;

    Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);

    public String getURLForResource (int resourceId) {
        //use BuildConfig.APPLICATION_ID instead of R.class.getPackage().getName() if both are not same
        //return Uri.parse("android.resource://"+R.class.getPackage().getName()+"/" +resourceId).toString();
        return Uri.parse(String.valueOf(resourceId)).toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etEmail = findViewById(R.id.etEmail);
        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        ivProfile = findViewById(R.id.ivProfile);

        fileStorage = FirebaseStorage.getInstance().getReference();

        progressBar = findViewById(R.id.progressBar);

        firestore = FirebaseFirestore.getInstance();

        //Bitmap bImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.default_profile);
        String str = getURLForResource(R.drawable.default_profile);



    }

    public void pickImage(View v) {

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 101);
        }
        else
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},102);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {

                localFileUri = data.getData();
                ivProfile.setImageURI(localFileUri);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==102)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 101);
            }
            else
            {
                Toast.makeText(this, R.string.permission_required, Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void updateNameAndPhoto()
    {
        String strFileName= FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg";

        final StorageReference fileRef = fileStorage.child("images/"+ strFileName);

        progressBar.setVisibility(View.VISIBLE);
        fileRef.putFile(localFileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if(task.isSuccessful())
                {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            serverFileUri = uri;

                            final HashMap<String, Object> userHashMap = new HashMap<>();

                            userHashMap.put(NodeNames.NAME, etName.getText().toString().trim());
                            userHashMap.put(NodeNames.EMAIL, etEmail.getText().toString().trim());
                            userHashMap.put(NodeNames.PHOTO, fileRef.getPath());
                            userHashMap.put(NodeNames.TOTAL_SCORE, 0);

                            DocumentReference userDoc = firestore.collection(NodeNames.USERS)
                                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid());

                            WriteBatch batch = firestore.batch();

                            batch.set(userDoc, userHashMap);

                            DocumentReference countDoc = firestore.collection(NodeNames.USERS)
                                    .document(NodeNames.TOTAL_USERS);
                            batch.update(countDoc, NodeNames.COUNT, FieldValue.increment(1));
                            batch.commit().addOnSuccessListener(new OnSuccessListener<Void>(){
                                @Override
                                public void onSuccess(Void unused) {

                                    DbQuery.CountLessons(new MyCompleteListener() {
                                        @Override
                                        public void onSuccess() {

                                            //Toast.makeText(SignupActivity.this, R.string.user_created_successfully, Toast.LENGTH_LONG).show();
                                            Toast.makeText(SignupActivity.this, "Verified your email address now", Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                            finish();

                                        }

                                        @Override
                                        public void onFailure() {
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(SignupActivity.this,
                                            getString(R.string.failed_to_update_profile, e.getMessage()), Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                    });
                }}});
    }//end of updateNameAndPhoto

    private void updateOnlyName() {

        progressBar.setVisibility(View.VISIBLE);
        final HashMap<String, Object> userHashMap = new HashMap<>();

        userHashMap.put(NodeNames.NAME, etName.getText().toString().trim());
        userHashMap.put(NodeNames.EMAIL, etEmail.getText().toString().trim());
        userHashMap.put(NodeNames.PHOTO, "");
        userHashMap.put(NodeNames.TOTAL_SCORE, 0);

        DocumentReference userDoc = firestore.collection(NodeNames.USERS)
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        WriteBatch batch = firestore.batch();

        batch.set(userDoc, userHashMap);

        DocumentReference countDoc = firestore.collection(NodeNames.USERS)
                .document(NodeNames.TOTAL_USERS);
        batch.update(countDoc, NodeNames.COUNT, FieldValue.increment(1));
        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>(){
            @Override
            public void onSuccess(Void unused) {

                DbQuery.CountLessons(new MyCompleteListener() {
                    @Override
                    public void onSuccess() {

                        Toast.makeText(SignupActivity.this, "Verified your email address now", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));

                    }

                    @Override
                    public void onFailure() {
                        progressBar.setVisibility(View.GONE);
                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignupActivity.this,
                            getString(R.string.failed_to_update_profile, e.getMessage()), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void btnSignupClick(View v) {
        email = etEmail.getText().toString().trim();
        name = etName.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        confirmPassword = etConfirmPassword.getText().toString().trim();

        Matcher m = p.matcher(password);
        boolean hasSpecialChar = m.find();

        if (email.equals("")) {
            etEmail.setError(getString(R.string.enter_email));
        } else if (name.equals("")) {
            etName.setError(getString(R.string.enter_name));
        } else if (etPassword.equals("")) {
            etPassword.setError(getString(R.string.enter_password));
        } else if (confirmPassword.equals("")) {
            etConfirmPassword.setError(getString(R.string.confirm_password));
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError(getString(R.string.enter_correct_email));
        } else if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError(getString(R.string.password_mismatch));
        } else if(!hasSpecialChar){
            etPassword.setError("Password must contain numbers, letters and special characters.");
            Toast.makeText(this, "Password must contain numbers, letters and special characters.", Toast.LENGTH_SHORT).show();
        } else {

            progressBar.setVisibility(View.VISIBLE);
            final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {

                                firebaseAuth.getCurrentUser().sendEmailVerification();

                                if(localFileUri!=null) {

                                    updateNameAndPhoto();

                                } else {

                                    updateOnlyName();

                                }

                            } else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(SignupActivity.this,
                                        getString(R.string.signup_failed, task.getException()), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }//end of btnSignupClick


}