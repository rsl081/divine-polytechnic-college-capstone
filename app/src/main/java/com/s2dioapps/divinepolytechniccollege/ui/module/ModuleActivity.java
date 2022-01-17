package com.s2dioapps.divinepolytechniccollege.ui.module;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.s2dioapps.divinepolytechniccollege.R;
import com.s2dioapps.divinepolytechniccollege.common.DbQuery;
import com.s2dioapps.divinepolytechniccollege.common.MyCompleteListener;
import com.s2dioapps.divinepolytechniccollege.ui.test.TestActivity;
import com.s2dioapps.divinepolytechniccollege.ui.test.TestAdapter;

public class ModuleActivity extends AppCompatActivity {

    private RecyclerView testView;
    private Toolbar toolbar;
    private ModuleAdapter adapter;
    private Dialog progressDialog;
    private TextView dialogText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module);

        toolbar = findViewById(R.id.toolbar_module);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(DbQuery.g_leList.get(DbQuery.g_selected_lesson_index).getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        testView = findViewById(R.id.module_recycler_view);

        progressDialog = new Dialog(ModuleActivity.this);
        progressDialog.setContentView(R.layout.dialog_layout);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogText = progressDialog.findViewById(R.id.dialog_text);
        dialogText.setText("Loading...");

        progressDialog.show();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        testView.setLayoutManager(layoutManager);


        DbQuery.loadModuleData(new MyCompleteListener() {
            @Override
            public void onSuccess() {

                adapter = new ModuleAdapter(DbQuery.g_moduleList,new ModuleAdapter.MyInterface() {

                    @Override
                    public void someEvent() {

                        adapter.notifyDataSetChanged();

                    }


                });

                testView.setAdapter(adapter);

                progressDialog.dismiss();

            }

            @Override
            public void onFailure() {
                progressDialog.dismiss();
                Toast.makeText(ModuleActivity.this, "Something went wrong! Please try again",
                        Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void AdapterNotify()
    {
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            ModuleActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}