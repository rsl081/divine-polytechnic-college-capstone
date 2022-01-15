package com.s2dioapps.divinepolytechniccollege.ui.myprofile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.s2dioapps.divinepolytechniccollege.MainActivity;
import com.s2dioapps.divinepolytechniccollege.R;
import com.s2dioapps.divinepolytechniccollege.common.DbQuery;
import com.s2dioapps.divinepolytechniccollege.common.NodeNames;
import com.s2dioapps.divinepolytechniccollege.login.LoginActivity;
import com.s2dioapps.divinepolytechniccollege.password.ChangePasswordActivity;
import com.s2dioapps.divinepolytechniccollege.signup.SignupActivity;
import com.s2dioapps.divinepolytechniccollege.ui.test.TestActivity;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class MyProfileActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etName;
    private String email, name;

    private ImageView ivProfile;
    private DatabaseReference databaseReference;

    private StorageReference fileStorage;
    private Uri localFileUri, serverFileUri;
    private View progressBar;

    private Dialog progressDialog;
    private TextView dialogText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);


        etEmail = findViewById(R.id.etEmail);
        etName = findViewById(R.id.etName);
        ivProfile = findViewById(R.id.ivProfile);


        progressDialog = new Dialog(MyProfileActivity.this);
        progressDialog.setContentView(R.layout.dialog_layout);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogText = progressDialog.findViewById(R.id.dialog_text);
        dialogText.setText("Loading...");

        progressDialog.show();

        if(!DbQuery.myProfile.getPhoto().equals(""))
        {
            fileStorage = FirebaseStorage.getInstance().getReference(DbQuery.myProfile.getPhoto());
        }else{
            fileStorage = FirebaseStorage.getInstance().getReference("/images/default_profile.png");
        }

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        progressBar = findViewById(R.id.progressBar);

        if(firebaseUser!=null)
        {
            etName.setText(DbQuery.myProfile.getName());
            etEmail.setText(DbQuery.myProfile.getEmail());
            etEmail.setEnabled(false);

            try {

                File localFile = File.createTempFile("tempfile",".jpg");
                fileStorage.getFile(localFile)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());

                                Glide.with(MyProfileActivity.this)
                                    .load(bitmap)
                                    .placeholder(R.drawable.default_profile)
                                    .error(R.drawable.default_profile)
                                    .into(ivProfile);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });




            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

    public void btnLogoutClick(View view)
    {

        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

//        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//        DatabaseReference databaseReference = rootRef.child(NodeNames.TOKENS).child(currentUser.getUid());

//        databaseReference.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if(task.isSuccessful())
//                {
//                    firebaseAuth.signOut();
//                    startActivity(new Intent(MyProfileActivity.this, LoginActivity.class));
//                    finish();
//                }
//                else
//                {
//                    Toast.makeText(MyProfileActivity.this, "Something went wrong",
//                            Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        firebaseAuth.signOut();
        startActivity(new Intent(MyProfileActivity.this, LoginActivity.class));
        finish();

    }//end of btnLogoutClick

    public void btnSaveClick(View view)
    {
        if(etName.getText().toString().trim().equals(""))
        {
            etName.setError(getString(R.string.enter_name));
        }
        else
        {
            if(localFileUri!=null){

                updateNameAndPhoto();

            } else {
                updateOnlyName();
            }
        }

    }

    public void changeImage(View view)
    {
        if(serverFileUri==null)
        {
            pickImage();
        }
        else
        {
            PopupMenu popupMenu  = new PopupMenu(this, view);
            popupMenu.getMenuInflater().inflate(R.menu.menu_picture,popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    int id = menuItem.getItemId();

                    if(id==R.id.mnuChangePic)
                    {
                        pickImage();
                    }
                    else if (id==R.id.mnuRemovePic)
                    {
                        removePhoto();
                    }
                    return false;
                }
            });
            popupMenu.show();
        }

    }

    private void pickImage() {

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

    private  void removePhoto()
    {
        progressBar.setVisibility(View.VISIBLE);
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(etName.getText().toString().trim())
                .setPhotoUri(null)
                .build();

//        firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                progressBar.setVisibility(View.GONE);
//                if (task.isSuccessful()) {
//                    ivProfile.setImageResource(R.drawable.default_profile);
//                    String userID = firebaseUser.getUid();
//                    databaseReference = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);
//
//                    HashMap<String, String> hashMap = new HashMap<>();
//                    hashMap.put(NodeNames.PHOTO, "");
//
//                    databaseReference.child(userID).setValue(hashMap)
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//
//                                    Toast.makeText(MyProfileActivity.this, R.string.photo_removed_successfully, Toast.LENGTH_SHORT).show();
//
//                                }
//                            });
//
//
//                } else {
//                    Toast.makeText(MyProfileActivity.this,
//                            getString(R.string.failed_to_update_profile, task.getException()), Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });

    }//end of removePhoto

    private void updateNameAndPhoto()
    {
        String strFileName= FirebaseAuth.getInstance().getCurrentUser().getUid();
        fileStorage = FirebaseStorage.getInstance().getReference();
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

//                            userHashMap.put(NodeNames.NAME, etName.getText().toString().trim());
//                            userHashMap.put(NodeNames.EMAIL, etEmail.getText().toString().trim());
//                            userHashMap.put(NodeNames.PHOTO, fileRef.getPath());
//                            userHashMap.put(NodeNames.TOTAL_SCORE, 0);

//                            WriteBatch batch0 = DbQuery.g_firestore.batch();
//
//                            DocumentReference userDoc0 = DbQuery.g_firestore.collection("Users")
//                                    .document(FirebaseAuth.getInstance().getUid());
//
//                            batch0.update(userDoc0, "PHOTO", fileRef.getPath());
//                            batch0.commit();



//                            DocumentReference userDoc = DbQuery.g_firestore.collection(NodeNames.USERS)
//                                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid());
//
//                            WriteBatch batch = DbQuery.g_firestore.batch();
//
//                            batch.set(userDoc, userHashMap);
//
//                            DocumentReference countDoc = DbQuery.g_firestore.collection(NodeNames.USERS)
//                                    .document(NodeNames.TOTAL_USERS);
//                            batch.update(countDoc, NodeNames.COUNT, FieldValue.increment(1));
//
//                            batch.update(userDoc, "PHOTO", fileRef.getPath());
//                            DbQuery.myProfile.setName(etName.getText().toString().trim());
//                            DbQuery.myProfile.setPhoto(fileRef.getPath());


                            WriteBatch batch = DbQuery.g_firestore.batch();

                            DocumentReference userDoc = DbQuery.g_firestore.collection("Users")
                                    .document(FirebaseAuth.getInstance().getUid());

                            batch.update(userDoc, "NAME", etName.getText().toString().trim());
                            batch.update(userDoc, "PHOTO", fileRef.getPath());
                            
                            DbQuery.myProfile.setName(etName.getText().toString().trim());
                            DbQuery.myProfile.setPhoto(fileRef.getPath());


                            batch.commit().addOnSuccessListener(new OnSuccessListener<Void>(){
                                @Override
                                public void onSuccess(Void unused) {

                                    Toast.makeText(MyProfileActivity.this, R.string.user_created_successfully, Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(MyProfileActivity.this, MainActivity.class));
                                    finish();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MyProfileActivity.this,
                                            getString(R.string.failed_to_update_profile, e.getMessage()), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });
                }}});
    }//end of updateNameAndPhoto

    private void updateOnlyName() {

        WriteBatch batch = DbQuery.g_firestore.batch();

        DocumentReference userDoc = DbQuery.g_firestore.collection("Users")
                .document(FirebaseAuth.getInstance().getUid());

        batch.update(userDoc, "NAME", etName.getText().toString().trim());
        DbQuery.myProfile.setName(etName.getText().toString().trim());
        //DbQuery.myProfile.setPhoto(String.valueOf(ivProfile.getDrawable()));
        progressBar.setVisibility(View.VISIBLE);
        batch.commit()
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                    startActivity(new Intent(MyProfileActivity.this, MainActivity.class));
                    finish();

                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

    }

    public  void btnChangePasswordClick(View view)
    {
        startActivity(new Intent(MyProfileActivity.this, ChangePasswordActivity.class));
    }





}