package com.example.keith.test;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class createTask extends AppCompatActivity {
    private int mYear, mMonth, mDay;
    private MyDBHelper helper;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = MyDBHelper.getInstance(this);
        setContentView(R.layout.activity_create_task);
        ArrayList<CatModel> catList= helper.getAllCat(helper.getReadableDatabase());
        Spinner spinner = (Spinner)findViewById(R.id.create_task_cat);
        String[] cats = new String[catList.size()];
        for(int i=0;i<catList.size();i++){
            cats[i]= catList.get(i).getName();
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,
                cats);
        spinner.setAdapter(arrayAdapter);
        final ImageButton imageButton = (ImageButton)findViewById(R.id.create_datePicker);
        final EditText dateText = (EditText)findViewById(R.id.create_task_date_picker);
        dateText.setText(sdf.format(new Date()));
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(createTask.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day);
                        String format = sdf.format(calendar.getTime());
                        //String format = sdf.format(view.getCalendarView().getDate());
                        //String format = setDateFormat(year,month,day);
                        dateText.setText(format);
                    }
                }, mYear,mMonth, mDay).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.removeItem(R.id.menu_about);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_save:
                Log.e("ToolBar OnClick", "Save button clicked.");
                EditText task_name = (EditText) findViewById(R.id.create_task_name);
                EditText task_desc = (EditText) findViewById(R.id.create_task_desc);
                //EditText task_due_date =  (EditText)findViewById(R.id.create_task_due_date);


                //DatePicker task_due_date = (DatePicker) findViewById(R.id.create_task_date_picker);

                Spinner spinner = (Spinner) findViewById(R.id.create_task_cat);
//            int taskDay = task_due_date.getDayOfMonth();
//            int taskMon = task_due_date.getMonth()+1;
//            int taskYr = task_due_date.getYear();
                EditText dateText = (EditText)findViewById(R.id.create_task_date_picker);
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                long taskDueDate = 0;
                try {
                    taskDueDate = format.parse(String.valueOf(dateText.getText())).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //task_due_date.getCalendarView().getDate();
                //Date taskDueDate = new Date(pickedDate);
                //String taskDueDate = taskYr+"-"+taskMon+"-"+taskDay+" 00:00:00:0000";

                //EditText task_remark =  (EditText)findViewById(R.id.create_task_remark);

                String taskName = String.valueOf(task_name.getText());
                String taskDesc = String.valueOf(task_desc.getText());

                int taskStatus = 0;  //NEW TASK MUST BE TODO'

                int cat = spinner.getSelectedItemPosition() + 1;

                //String taskRemark = String.valueOf(task_remark.getText());
                ContentValues values = new ContentValues();
                values.put("task_name", taskName);
                values.put("task_desc", taskDesc);
                values.put("task_due_date", taskDueDate);
                values.put("task_status", taskStatus);
                values.put("task_cat", cat);
                long insertId = helper.getWritableDatabase().insert("task", null, values);
                Log.d("DB ADD", insertId + "");
                todo.modelItems.clear();
                todo.modelItems.addAll(helper.listTodoTask(helper.getReadableDatabase()));
                todo.adapter.notifyDataSetChanged();
                try {
                    deadline.model.clear();
                    deadline.model.addAll(helper.listDeadLine(helper.getReadableDatabase()));
                    deadline.adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.e("Create Task error", "Deadline not yet created.");
                    return true;
                }

                ToggleButton toggleButton = (ToggleButton) findViewById(R.id.set_remind);
                if (toggleButton.isChecked()) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date(taskDueDate));
                    //calendar.add(Calendar.DAY_OF_MONTH,-1);
                    calendar.set(Calendar.HOUR, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    int pendingIntentID = 1;
                    int lastRecordId = helper.getLastRecordId(helper.getReadableDatabase());
                    if(lastRecordId != -1 ){
                        pendingIntentID = (int)calendar.getTimeInMillis() + lastRecordId;
                    }
                    Intent intent = new Intent(this, PlayReceiver.class);
                    //intent.putExtra("msg","play_hskay");
                    intent.putExtra("task_name", taskName);
                    PendingIntent pi = PendingIntent.getBroadcast(this,
                            pendingIntentID, intent, PendingIntent.FLAG_ONE_SHOT);

                    AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                    //am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);

                    am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
                    Log.e("SET ALARM", taskName + " -- " + new Date(calendar.getTimeInMillis()));
                }

                Toast.makeText(this,"Task '"+ taskName+"' is created.",
                        Toast.LENGTH_LONG).show();

                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}


