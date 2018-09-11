package com.example.keith.test;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ComponentCallbacks;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class editTask extends AppCompatActivity {
    Intent parentIntent = null;
    private int mYear, mMonth, mDay;
    private MyDBHelper helper;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    int ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        helper = MyDBHelper.getInstance(this);

        parentIntent = getIntent();
        ID = parentIntent.getIntExtra("id",0);
        String name = parentIntent.getStringExtra("name");
        String desc = parentIntent.getStringExtra("desc");
        Long dueDate = parentIntent.getLongExtra("due_date",0);
        int status = parentIntent.getIntExtra("status",0);
        int cat  = parentIntent.getIntExtra("cat",0);

        EditText taskName = (EditText) findViewById(R.id.edit_name);
        EditText taskDesc = (EditText) findViewById(R.id.edit_desc);
        //DatePicker taskDueDate = (DatePicker) findViewById(R.id.edit_task_date_picker);
        Spinner spinner = (Spinner)findViewById(R.id.edit_task_cat);

        taskName.setText(name);
        taskDesc.setText(desc);

        ArrayList<CatModel> catList= helper.getAllCat(helper.getReadableDatabase());
        String[] cats = new String[catList.size()];
        for(int i=0;i<catList.size();i++){
            cats[i]= catList.get(i).getName();
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,
                cats);
        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(cat-1);

        Date iniDate = new Date(dueDate);
/*        Calendar calendar = Calendar.getInstance();
        calendar.setTime(iniDate);
        int y = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH);
        int d = calendar.get(Calendar.DAY_OF_MONTH);
        taskDueDate.init(y,m,d,null);*/

        final ImageButton imageButton = (ImageButton)findViewById(R.id.edit_datePicker);
        final EditText dateText = (EditText)findViewById(R.id.edit_task_date_picker);
        dateText.setText(sdf.format(iniDate));
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(editTask.this, new DatePickerDialog.OnDateSetListener() {
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

        RadioGroup radioGroup = (RadioGroup)findViewById(R.id.edit_radio_group);
        if(status == 1){
            radioGroup.check(R.id.edit_inactive);
        }
        else{
            radioGroup.check(R.id.edit_active);
        }

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
        if (id == R.id.action_save) {
            Log.e("ToolBar OnClick","Save button clicked.");
            EditText nameField = (EditText) findViewById(R.id.edit_name);
            EditText descField = (EditText) findViewById(R.id.edit_desc);
            //DatePicker due_dateField = (DatePicker)findViewById(R.id.edit_task_date_picker);
            RadioGroup radioGroup = (RadioGroup)findViewById(R.id.edit_radio_group);
            Spinner spinner = (Spinner)findViewById(R.id.edit_task_cat);

            String name = String.valueOf(nameField.getText());
            String desc = String.valueOf(descField.getText());

            //long dueDate = due_dateField.getCalendarView().getDate();

            EditText dateText = (EditText)findViewById(R.id.edit_task_date_picker);
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            long dueDate = 0;
            try {
                dueDate = format.parse(String.valueOf(dateText.getText())).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            long doneDate=-1;
            int status = 0;
            if(radioGroup.getCheckedRadioButtonId()==R.id.edit_active){
                status = 0; //MARK TODO
            }
            else if(radioGroup.getCheckedRadioButtonId()==R.id.edit_inactive){
                status = 1; //MARK DONE
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.set(Calendar.HOUR,0);
                calendar.set(Calendar.MINUTE,0);
                calendar.set(Calendar.SECOND,0);
                doneDate = calendar.getTimeInMillis();
            }

            int cat = spinner.getSelectedItemPosition()+1;

            helper.updateTask(helper.getWritableDatabase(),new Model(ID,name,desc,dueDate,status,cat,doneDate));
            Log.d("DB UPDATE",ID +": "+name);
            todo.modelItems.clear();
            todo.modelItems.addAll(helper.listTodoTask(helper.getReadableDatabase()));
            todo.adapter.notifyDataSetChanged();
            try {
                taskDone.taskDoneModel.clear();
                taskDone.taskDoneModel.addAll(helper.listDoneTask(helper.getReadableDatabase()));
                taskDone.adapter.notifyDataSetChanged();
            }catch (Exception e) {
                Log.e("Edit Task error","TaskDone not yet created.");
            }
            try{
                deadline.model.clear();
                deadline.model.addAll(helper.listDeadLine(helper.getReadableDatabase()));
                deadline.adapter.notifyDataSetChanged();
            }catch(Exception e){
                Log.e("Edit Task error","Deadline not yet created.");
            }

            Toast.makeText(this,"Task '"+ name+"' is updated.",
                    Toast.LENGTH_LONG).show();

            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
