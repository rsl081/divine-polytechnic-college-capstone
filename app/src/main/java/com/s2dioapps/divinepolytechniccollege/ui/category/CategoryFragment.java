package com.s2dioapps.divinepolytechniccollege.ui.category;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.s2dioapps.divinepolytechniccollege.R;

import java.util.ArrayList;
import java.util.List;


public class CategoryFragment extends Fragment {

    private GridView catView;
    private List<CategoryModel> catList = new ArrayList<>();

    public CategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_category, container, false);

        catView = view.findViewById(R.id.cat_grid);

        loadCategories();

        CategoryAdapter adapter = new CategoryAdapter(catList);
        catView.setAdapter(adapter);

        return view;
    }

    private void loadCategories()
    {
        catList.clear();

        catList.add(new CategoryModel("1", "MATH", 20));
        catList.add(new CategoryModel("2", "SCIENCE", 8));
        catList.add(new CategoryModel("3", "TECH", 3));
        catList.add(new CategoryModel("4", "EPP", 6));
        catList.add(new CategoryModel("5", "MAPEH", 1));
        catList.add(new CategoryModel("5", "MAPEH", 3));
        catList.add(new CategoryModel("5", "MAPEH", 3));
        catList.add(new CategoryModel("5", "MAPEH", 3));
        catList.add(new CategoryModel("5", "MAPEH", 3));
    }

}