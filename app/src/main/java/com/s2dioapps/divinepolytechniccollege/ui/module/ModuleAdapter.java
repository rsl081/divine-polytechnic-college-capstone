package com.s2dioapps.divinepolytechniccollege.ui.module;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.s2dioapps.divinepolytechniccollege.R;
import com.s2dioapps.divinepolytechniccollege.common.DbQuery;
import com.s2dioapps.divinepolytechniccollege.common.MyCompleteListener;
import com.s2dioapps.divinepolytechniccollege.common.NodeNames;
import com.s2dioapps.divinepolytechniccollege.login.LoginActivity;
import com.s2dioapps.divinepolytechniccollege.signup.SignupActivity;
import com.s2dioapps.divinepolytechniccollege.ui.myprofile.MyProfileActivity;
import com.s2dioapps.divinepolytechniccollege.ui.test.StartTestActivity;
import com.s2dioapps.divinepolytechniccollege.ui.test.TestAdapter;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ViewHolder> {

    private List<ModuleModel> moduleList;
    private List<UserLesson> userLessonList;
//    private int ctr = 1;

    private MyInterface mInterface;

    interface MyInterface {
        void someEvent();
    }


    public ModuleAdapter(List<ModuleModel> moduleList, MyInterface i) {

        this.moduleList = moduleList;
        mInterface = i;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.test_item_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String pdf = moduleList.get(position).getModulePDF();
        String name = moduleList.get(position).getModuleID();
        //int countModule = moduleList.get(position).getCount();

        //Maling code to repition nangyayare sa pag get ng number, bale brute force style to
        int countModule = DbQuery.g_userlesson.get(DbQuery
                .g_selected_lesson_index).getLessonNameCount();


        holder.setData(position, pdf, name, mInterface, countModule);
    }

    @Override
    public int getItemCount() {
        return moduleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView moduleNo;

        private TextView topScore;
        private ProgressBar progressBar;
        private View bg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            moduleNo = itemView.findViewById(R.id.testNo);

            topScore = itemView.findViewById(R.id.scoretext);
            progressBar = itemView.findViewById(R.id.testProgressbar);
            bg = itemView.findViewById(R.id.bg_ll);

        }

        private void setData(int pos, String pdf, String name, ModuleAdapter.MyInterface myInterface, int count)  {
            moduleNo.setText("Module No : " + String.valueOf(pos + 1));
            topScore.setText(name);
            progressBar.setVisibility(View.GONE);

            if((pos+(moduleList.size()-count)) < moduleList.size())
            {

                itemView.setEnabled(true);
                bg.setBackgroundColor(Color.parseColor("#FFFFFFFF"));

            } else {

                itemView.setEnabled(false);
                bg.setBackgroundColor(Color.parseColor("#E0E0E0"));

            }


            // Create a reference from an HTTPS URL
            // Note that in the URL, characters are URL escaped!
            StorageReference httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(pdf);


            itemView.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("IntentReset")
                @Override
                public void onClick(View v) {

                    myInterface.someEvent();

                    //DbQuery.g_selected_lesson_index = pos;

                    if((count-1) == pos)
                    {
                        //Toast.makeText(itemView.getContext(), "Goods", Toast.LENGTH_SHORT).show();

                        DbQuery.saveModuleCount(count);
                    }


                    try {

                        File localFile = File.createTempFile("tempfile",".pdf");
                        httpsReference.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        //progressDialog.dismiss();



                                        Log.e("HAPPY", String.valueOf(uri));

                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setType("application/pdf");
                                        intent.setData(uri);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        itemView.getContext().startActivity(intent);


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
            });




        }


    }
}
