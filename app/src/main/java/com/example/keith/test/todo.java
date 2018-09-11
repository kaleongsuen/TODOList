package com.example.keith.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.icu.util.BuddhistCalendar;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.app.Activity.RESULT_OK;


public class todo extends Fragment {
    ListView listview;
    static ArrayList<Model> modelItems;
    View view;
    static CustomAdapter adapter;
    MyDBHelper helper;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_todo, container, false);

        SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy, EEE");
        Date date=new Date();
        TextView todo_date = (TextView)view.findViewById(R.id.todo_date_label);
        todo_date.setText(sdf.format(date));

        listview = (ListView) view.findViewById(R.id.todo_listView);
        helper = MyDBHelper.getInstance(this.getContext());
        modelItems = helper.listTodoTask(helper.getReadableDatabase());
        adapter = new CustomAdapter(super.getContext(), modelItems);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                ListView listView = (ListView)parent;
                Model models = (Model) listView.getItemAtPosition(position);
//                Toast.makeText(getActivity(),"clicked on row: "+ models.getName(),
//                        Toast.LENGTH_LONG).show();
                Intent editTask = new Intent(getActivity(), com.example.keith.test.editTask.class);
                editTask.putExtra("id",models.getID());
                editTask.putExtra("name",models.getName());
                editTask.putExtra("desc",models.getDesc());
                editTask.putExtra("due_date",models.getDueDate());
                editTask.putExtra("status",models.getStatus());
                editTask.putExtra("cat",models.getCat());

                startActivityForResult(editTask,123);
                Log.e("clicking Items: ","clicked on row: "+ models.getName());
            }
        });
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView)parent;
                Model model = (Model)listView.getItemAtPosition(position);
                Log.e("onItemLongClick","position="+position);
                helper.deleteTask(helper.getWritableDatabase(),model);
                Toast.makeText(getActivity(),"Task '"+ model.getName()+"' is deleted.",
                        Toast.LENGTH_LONG).show();
                modelItems.clear();
                modelItems.addAll(helper.listTodoTask(helper.getReadableDatabase()));
                adapter.notifyDataSetChanged();
                try {
                    deadline.model.clear();
                    deadline.model.addAll(helper.listDeadLine(helper.getReadableDatabase()));
                    deadline.adapter.notifyDataSetChanged();
                }catch (Exception e){
                    Log.e("TODO del task error","Deadline not yet created.");
                }
                return true;
            }
        });

        return view;
    }

/*    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 123) {
            if(resultCode == RESULT_OK){
                Bundle bundle = data.getExtras();
                Log.e("OnActivityResult",bundle.getString("taskName"));
                Log.e("OnActivityResult", String.valueOf(bundle.getInt("status",0)));
                modelItems.get(bundle.getInt("position",0)).name =
                        bundle.getString("taskName");
                modelItems.get(bundle.getInt("position",0)).status =
                        bundle.getInt("status",0);
                adapter.notifyDataSetChanged();

            }
        }
    }*/

}



