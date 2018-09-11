package com.example.keith.test;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class deadline extends Fragment {
    View view;
    static ArrayList<Model> model;
    static CustomAdapter adapter;
    ListView listview;
    MyDBHelper helper;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_deadline, container, false);

        SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy, EEE");
        Date date=new Date();
        TextView todo_date = (TextView)view.findViewById(R.id.task_deadline_date_label);
        todo_date.setText(sdf.format(date));
        listview = (ListView) view.findViewById(R.id.task_deadline_listView);
        helper = MyDBHelper.getInstance(this.getContext());
        model = helper.listDeadLine(helper.getReadableDatabase());
        adapter = new CustomAdapter(super.getContext(), model);
        listview.setAdapter(adapter);

        return view;
    }

}
