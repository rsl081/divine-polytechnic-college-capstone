package com.s2dioapps.divinepolytechniccollege.ui.score;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.s2dioapps.divinepolytechniccollege.MainActivity;
import com.s2dioapps.divinepolytechniccollege.R;
import com.s2dioapps.divinepolytechniccollege.common.DbQuery;
import com.s2dioapps.divinepolytechniccollege.common.MyCompleteListener;
import com.s2dioapps.divinepolytechniccollege.ui.leaderboard.LeaderboardFragment;
import com.s2dioapps.divinepolytechniccollege.ui.test.TestActivity;

import java.util.concurrent.TimeUnit;

public class ScoreActivity extends AppCompatActivity {

    private TextView scoreTV, timeTV, totalQTV, correctQTV, wrongQTV, unattemptedQTV;
    Button leaderB, reAttemptB, viewAnsB;
    private long timeTaken;
    private Dialog progressDialog;
    private TextView dialogText;
    private int finalScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("RESULT");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        progressDialog = new Dialog(ScoreActivity.this);
        progressDialog.setContentView(R.layout.dialog_layout);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogText = progressDialog.findViewById(R.id.dialog_text);
        dialogText.setText("Loading...");

        progressDialog.show();


        init();

        loadData();

        leaderB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScoreActivity.this, MainActivity.class));
                finish();
            }
        });

        saveResult();
    }

    private void init()
    {
        scoreTV = findViewById(R.id.score);
        timeTV = findViewById(R.id.time);
        totalQTV = findViewById(R.id.totalQ);
        correctQTV = findViewById(R.id.correctQ);
        wrongQTV = findViewById(R.id.wrongQ);
        unattemptedQTV = findViewById(R.id.un_attempted);
        leaderB = findViewById(R.id.leaderB);
    }

    private void loadData()
    {
        int correctQ = 0, wrongQ = 0, unattemptQ = 0;

        for(int i = 0; i < DbQuery.g_questList.size(); i++)
        {
            if (DbQuery.g_questList.get(i).getSelectedAns() == -1) {
                unattemptQ++;
            }else{

                if(DbQuery.g_questList.get(i).getSelectedAns() == DbQuery.g_questList.get(i).getCorrectAns())
                {
                    correctQ++;
                }else{
                    wrongQ++;
                }

            }
        }

        correctQTV.setText(String.valueOf(correctQ));
        wrongQTV.setText(String.valueOf(wrongQ));
        unattemptedQTV.setText(String.valueOf(unattemptQ));

        totalQTV.setText(String.valueOf(DbQuery.g_questList.size()));

        finalScore = (correctQ*100)/DbQuery.g_questList.size();

        scoreTV.setText(String.valueOf(finalScore));

        timeTaken = getIntent().getLongExtra("TIME_TAKEN",0);

        @SuppressLint("DefaultLocale") String time = String.format("%02d:%02d min",
                TimeUnit.MILLISECONDS.toMinutes(timeTaken),
                TimeUnit.MILLISECONDS.toSeconds(timeTaken) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeTaken))
        );

        timeTV.setText(time);

    }//end of loadData

    private void saveResult()
    {


        DbQuery.saveResult(finalScore, new MyCompleteListener() {
            @Override
            public void onSuccess() {

                progressDialog.dismiss();

            }

            @Override
            public void onFailure() {
                Toast.makeText(ScoreActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            startActivity(new Intent(ScoreActivity.this, MainActivity.class));
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }




}