package com.s2dioapps.divinepolytechniccollege.ui.leaderboard;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.s2dioapps.divinepolytechniccollege.MainActivity;
import com.s2dioapps.divinepolytechniccollege.R;
import com.s2dioapps.divinepolytechniccollege.common.DbQuery;
import com.s2dioapps.divinepolytechniccollege.common.MyCompleteListener;
import com.s2dioapps.divinepolytechniccollege.ui.test.StartTestActivity;

import java.util.Locale;


public class LeaderboardFragment extends Fragment {

    private TextView totalUsersTV, myImgTextTV, myScoreTV, myRankTV;
    private RecyclerView usersView;
    private RankAdapter adapter;
    private Dialog progressDialog;
    private TextView dialogText;

    public LeaderboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Leaderboard");

        initViews(view);

        progressDialog = new Dialog(getContext());
        progressDialog.setContentView(R.layout.dialog_layout);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogText = progressDialog.findViewById(R.id.dialog_text);
        dialogText.setText("Loading...");

        progressDialog.show();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        usersView.setLayoutManager(layoutManager);

        adapter = new RankAdapter(DbQuery.g_usersList);

        usersView.setAdapter(adapter);

        DbQuery.getTopUsers(new MyCompleteListener() {
            @Override
            public void onSuccess() {

                adapter.notifyDataSetChanged();

                if(DbQuery.myPerformance.getScore() != 0)
                {
                    if(!DbQuery.isMeOnTopList)
                    {
                        calculateRank();
                    }

                    myScoreTV.setText("Score : " + DbQuery.myPerformance.getScore());
                    myRankTV.setText("Rank - " + DbQuery.myPerformance.getRank());

                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure() {


                Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

        totalUsersTV.setText("Total Users: " + DbQuery.g_usersCount);
        myImgTextTV.setText(DbQuery.myPerformance.getName().toUpperCase().substring(0,1));

        return view;
    }

    private void initViews(View view)
    {
        totalUsersTV = view.findViewById(R.id.total_user);
        myImgTextTV = view.findViewById(R.id.img_text);
        myScoreTV = view.findViewById(R.id.total_score);
        myRankTV = view.findViewById(R.id.rank);
        usersView = view.findViewById(R.id.users_view);

    }

    private void calculateRank()
    {
        int lowTopScore = DbQuery.g_usersList.get(DbQuery.g_usersList.size() - 1).getScore();

        int remaining_slots = DbQuery.g_usersCount - 20;

        int mySlot = (DbQuery.myPerformance.getScore() * remaining_slots) / lowTopScore;

        int rank;

        if(lowTopScore != DbQuery.myPerformance.getScore())
        {
            rank = DbQuery.g_usersCount - mySlot;
        }else{
            rank = 21;
        }

        DbQuery.myPerformance.setRank(rank);

    }


}