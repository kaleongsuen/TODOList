package com.example.keith.test;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class taskDone extends Fragment {
    View view;
    static ArrayList<Model> taskDoneModel;
    static CustomAdapter adapter;
    ListView listview;
    MyDBHelper helper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_task_done, container, false);

        SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy, EEE");
        Date date=new Date();
        TextView todo_date = (TextView)view.findViewById(R.id.task_done_date_label);
        todo_date.setText(sdf.format(date));
        listview = (ListView) view.findViewById(R.id.task_done_listView);
        helper = MyDBHelper.getInstance(this.getContext());
        taskDoneModel = helper.listDoneTask(helper.getReadableDatabase());
        adapter = new CustomAdapter(super.getContext(), taskDoneModel);
        listview.setAdapter(adapter);

        return view;
    }

}
