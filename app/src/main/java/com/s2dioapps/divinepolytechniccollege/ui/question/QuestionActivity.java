package com.s2dioapps.divinepolytechniccollege.ui.question;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.s2dioapps.divinepolytechniccollege.R;
import com.s2dioapps.divinepolytechniccollege.common.DbQuery;
import com.s2dioapps.divinepolytechniccollege.ui.score.ScoreActivity;
import com.s2dioapps.divinepolytechniccollege.ui.test.TestModel;

import java.util.concurrent.TimeUnit;



public class QuestionActivity extends AppCompatActivity {


    public class CustomGridLayoutManager extends LinearLayoutManager {
        private boolean isScrollEnabled = true;

        public CustomGridLayoutManager(Context context) {
            super(context);
        }

        public void setScrollEnabled(boolean flag) {
            this.isScrollEnabled = flag;
        }
        public boolean getScrollEnabled()
        {
            return isScrollEnabled;
        }

        @Override
        public boolean canScrollVertically() {
            //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
            return isScrollEnabled && super.canScrollVertically();
        }
    }

    private RecyclerView questionsView;
    private TextView tvQuesID, timerTV, catNameTV;
    private Button submitB, nextB;
    private int quesID;
    private int countQuestion = 1;
    boolean isScrollEnable;

    CustomGridLayoutManager customGridLayoutManager;
    CountDownTimer timer;

    private long timeLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        init();

        QuestionsAdapter questionsAdapter = new QuestionsAdapter(DbQuery.g_questList);
        questionsView.setAdapter(questionsAdapter);

        customGridLayoutManager = new CustomGridLayoutManager(QuestionActivity.this);
      //  customGridLayoutManager.setScrollEnabled(false);
        questionsView.setLayoutManager(customGridLayoutManager);


        setSnapHelper();
        setClickListeners();



        startTimer();

    }

    private void init()
    {
        questionsView = findViewById(R.id.questions_view);
        tvQuesID = findViewById(R.id.tv_quesID);
        timerTV = findViewById(R.id.tv_timer);
        catNameTV = findViewById(R.id.qa_catName);
        submitB = findViewById(R.id.submitB);
        nextB = findViewById(R.id.question_button_next);



        quesID = 0;
        tvQuesID.setText("1/" + String.valueOf(DbQuery.g_questList.size()));
        catNameTV.setText(DbQuery.g_catList.get(DbQuery.g_selected_cat_index).getName());
    }

    void setSnapHelper()
    {
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(questionsView);

        questionsView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

//                View view = snapHelper.findSnapView(recyclerView.getLayoutManager());
//                quesID = recyclerView.getLayoutManager().getPosition(view);
//
                if(countQuestion <= DbQuery.g_questList.size())
                {
                    tvQuesID.setText(String.valueOf(countQuestion++) + "/"
                            + String.valueOf(DbQuery.g_questList.size()));

                }



            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                customGridLayoutManager.setScrollEnabled(false);

            }
        });
    }

    private void startTimer()
    {
        long totalTime = DbQuery.g_testList.get(DbQuery.g_selected_test_index).getTime()*60*1000;

        timer = new CountDownTimer(totalTime + 1000, 1000) {
            @Override
            public void onTick(long remainingTime) {

                timeLeft = remainingTime;

                @SuppressLint("DefaultLocale") String time = String.format("%02d:%02d min",
                        TimeUnit.MILLISECONDS.toMinutes(remainingTime),
                        TimeUnit.MILLISECONDS.toSeconds(remainingTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainingTime))
                        );

                timerTV.setText(time);
            }

            @Override
            public void onFinish() {

                Intent intent = new Intent(QuestionActivity.this, ScoreActivity.class);

                long totalTime = DbQuery.g_testList.get(DbQuery.g_selected_test_index).getTime()*60*1000;
                intent.putExtra("TIME_TAKEN", totalTime - timeLeft);

                startActivity(intent);

                QuestionActivity.this.finish();

            }
        };
        timer.start();

    }

    private void setClickListeners()
    {
        nextB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(countQuestion <= DbQuery.g_questList.size())
                {
                    customGridLayoutManager.setScrollEnabled(true);
                    questionsView.smoothScrollToPosition(countQuestion);

                }



            }
        });

        submitB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitTest();
            }
        });


    }

    private void submitTest()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(QuestionActivity.this);
        builder.setCancelable(true);

        View view = getLayoutInflater().inflate(R.layout.alert_dialog_layout, null);

        Button cancelB = view.findViewById(R.id.cancelB);
        Button confirmB = view.findViewById(R.id.confirmB);

        builder.setView(view);

        final AlertDialog alertDialog = builder.create();

        cancelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        confirmB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                timer.cancel();
                alertDialog.dismiss();



                Intent intent = new Intent(QuestionActivity.this, ScoreActivity.class);

                long totalTime = (long) DbQuery.g_testList.get(DbQuery.g_selected_test_index).getTime() *60*1000;
                intent.putExtra("TIME_TAKEN", totalTime - timeLeft);

                startActivity(intent);

                QuestionActivity.this.finish();

            }
        });
        alertDialog.show();

    }


}

