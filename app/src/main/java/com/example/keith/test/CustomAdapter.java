package com.example.keith.test;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import junit.framework.Test;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Keith on 29/10/2017.
 */

public class CustomAdapter extends ArrayAdapter<Model> {
    ArrayList<Model> modelItems = null;
    Context context;
    MyDBHelper helper;

    public  CustomAdapter(Context context, ArrayList<Model> resource){
        super(context,R.layout.row,resource);
        this.context = context;
        this.modelItems = resource;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        // TODO Auto-generated method stub

        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.row, parent, false);
        final TextView name = (TextView) convertView.findViewById(R.id.textView1);
        final TextView date = (TextView) convertView.findViewById(R.id.textView2);
        final TextView remain = (TextView) convertView.findViewById(R.id.textView4);
        final TextView remainTag = (TextView) convertView.findViewById(R.id.remain_day_tag);
        final View colorTag = (View)convertView.findViewById(R.id.color_tag);
        CheckBox cb = (CheckBox) convertView.findViewById(R.id.checkBox1);
        name.setText(modelItems.get(position).getName());
        switch (modelItems.get(position).getCat()){
            case 1:
                colorTag.setBackgroundColor(Color.parseColor("#70d6ef"));
                break;
            case 2:
                colorTag.setBackgroundColor(Color.parseColor("#76ec67"));
                break;
            case 3:
                colorTag.setBackgroundColor(Color.parseColor("#f6f67a"));
                break;
            case 4:
                colorTag.setBackgroundColor(Color.parseColor("#ed9b68"));
                break;
            default:
                colorTag.setBackgroundColor(0xdddddddd);
        }
        //DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
       // Date tempDate = new Date(modelItems.get(position).getDueDate());
        //date.setText(dateFormat.format(tempDate));
        LocalDate tempDate = new LocalDate(modelItems.get(position).getDueDate());
        date.setText(tempDate.toString("dd-MM-yyyy"));
        LocalDate today = new LocalDate();
        final int diffdays = Days.daysBetween(today,tempDate).getDays();
        //Date today = new Date();

        //long diffdays = TimeUnit.DAYS.convert(tempDate.getTime() - today.getTime(),
        //        TimeUnit.MILLISECONDS);
        remain.setText(String.valueOf(diffdays)+" day");

        if(modelItems!=null) {
            if (modelItems.get(position).getStatus() == 1) { //TASK DONE
                cb.setChecked(true);
                name.setTypeface(null, Typeface.NORMAL);
                name.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                remain.setVisibility(View.GONE);
                TextView textView = (TextView)convertView.findViewById(R.id.remain_day_tag);
                textView.setVisibility(View.GONE);
                TextView textView1 = (TextView)convertView.findViewById(R.id.due_date_tag);
                textView1.setText("Task Done Date: ");
                date.setText((new LocalDate(modelItems.get(position).getDoneDate())).toString());
            } else { //TASK NOT DONE
                cb.setChecked(false);
                name.setTypeface(null, Typeface.BOLD);
                remainTag.setTextColor(0xffff4444);
                remain.setTextColor(0xffff4444);
            }

            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {  // ------------------ MARK TASK DONE --------------------------------
                        //name.setTypeface(null, Typeface.BOLD_ITALIC);
                        helper = MyDBHelper.getInstance(context);
                        helper.markTaskDone(helper.getWritableDatabase(),modelItems.get(position));
                        /*todo.modelItems.clear();
                        todo.modelItems.addAll(helper.listTodoTask(helper.getReadableDatabase()));
                        todo.adapter.notifyDataSetChanged();*/
                        if (MainActivity.filter == 0) {  // show all task
                            todo.modelItems.clear();
                            todo.modelItems.addAll(helper.listTodoTask(helper.getReadableDatabase()));
                            todo.adapter.notifyDataSetChanged();
                        }else {  // show task with cat
                            todo.modelItems.clear();
                            todo.modelItems.addAll( helper.listByCat(helper.getReadableDatabase(),MainActivity.filter));
                            todo.adapter.notifyDataSetChanged();
                        }
                        try {
                            taskDone.taskDoneModel.clear();
                            taskDone.taskDoneModel.addAll(helper.listDoneTask(helper.getReadableDatabase()));
                            taskDone.adapter.notifyDataSetChanged();
                        }catch(Exception e){
                            Log.e("Mark Task Done error","TaskDone not yet created.");
                        }
                        if(diffdays <= 1){
                            try {
                                deadline.model.clear();
                                deadline.model.addAll(helper.listDeadLine(helper.getReadableDatabase()));
                                deadline.adapter.notifyDataSetChanged();
                            }catch (Exception e){
                                Log.e("Mark Task Done error","Deadline not yet created.");
                            }
                        }
                    } else {   // ------------------ MARK TASK TODO -----------------------------------
                        //name.setTypeface(null, Typeface.NORMAL);
                        helper = MyDBHelper.getInstance(context);
                        helper.markTaskTodo(helper.getWritableDatabase(),modelItems.get(position));
                        taskDone.taskDoneModel.clear();
                        taskDone.taskDoneModel.addAll(helper.listDoneTask(helper.getReadableDatabase()));
                        taskDone.adapter.notifyDataSetChanged();
                        if(diffdays <= 1){
                            deadline.model.clear();
                            deadline.model.addAll(helper.listDeadLine(helper.getReadableDatabase()));
                            deadline.adapter.notifyDataSetChanged();
                        }
                    }
                    //Log.e("checkbox change", modelItems.get(position).getName() + " change.");
                }
            });
        }
        return convertView;
    }
}

