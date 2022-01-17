package com.s2dioapps.divinepolytechniccollege.ui.lesson;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.s2dioapps.divinepolytechniccollege.R;
import com.s2dioapps.divinepolytechniccollege.common.DbQuery;
import com.s2dioapps.divinepolytechniccollege.ui.module.ModuleActivity;
import com.s2dioapps.divinepolytechniccollege.ui.test.TestActivity;

import java.util.List;

public class LessonAdapter extends BaseAdapter {

    private List<LessonModel> le_list;

    public LessonAdapter(List<LessonModel> le_list) {
        this.le_list = le_list;
    }

    @Override
    public int getCount() {
        return le_list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View myView;

        if(convertView == null){
            myView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cat_item_layout,
                    parent, false);
        }else{
            myView = convertView;
        }

        TextView catName = myView.findViewById(R.id.cat_name);
        TextView noOfTests = myView.findViewById(R.id.no_of_tests);

        catName.setText(le_list.get(position).getName());
        if(le_list.get(position).getOnOfModules() <= 1)
        {
            noOfTests.setText(String.valueOf(le_list.get(position).getOnOfModules()) + " Module");

        }else{

            noOfTests.setText(String.valueOf(le_list.get(position).getOnOfModules()) + " Modules");

        }


        myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbQuery.g_selected_lesson_index = position;

                Intent intent = new Intent(v.getContext(), ModuleActivity.class);

                v.getContext().startActivity(intent);

            }
        });




        return myView;
    }
}
