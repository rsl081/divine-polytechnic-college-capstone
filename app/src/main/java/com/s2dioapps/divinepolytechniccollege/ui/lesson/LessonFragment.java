package com.s2dioapps.divinepolytechniccollege.ui.lesson;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.s2dioapps.divinepolytechniccollege.R;
import com.s2dioapps.divinepolytechniccollege.common.DbQuery;


public class LessonFragment extends Fragment {

    private GridView catViewLesson;

    public LessonFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lesson, container, false);

        catViewLesson = view.findViewById(R.id.cat_grid_lesson);

        LessonAdapter lessonAdapter = new LessonAdapter(DbQuery.g_leList);
        catViewLesson.setAdapter(lessonAdapter);

        return view;
    }
}