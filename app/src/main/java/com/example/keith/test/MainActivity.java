package com.example.keith.test;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;

public class MainActivity extends AppCompatActivity {
    MyDBHelper helper;
    static int filter = 0;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helper = MyDBHelper.getInstance(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        //floating button onClick action
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,  com.example.keith.test.createTask.class);
                startActivity(intent);
            }
        });


        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final NavigationView view = (NavigationView) findViewById(R.id.navigation_view);
        view.setItemIconTintList(null);
        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override public boolean onNavigationItemSelected(MenuItem menuItem) {
                //Toast.makeText(MainActivity.this, menuItem.getItemId()+" "+menuItem.getTitle() + " pressed", Toast.LENGTH_LONG).show();
                int id = menuItem.getItemId();
                for(int i=0;i<5;i++){
                    view.getMenu().getItem(i).setChecked(false);
                }
                menuItem.setChecked(true);
                //noinspection SimplifiableIfStatement
                switch (id){
                    case R.id.nav_all:
                        filter = 0;
                        todo.modelItems.clear();
                        todo.modelItems.addAll(helper.listTodoTask(helper.getReadableDatabase()));
                        todo.adapter.notifyDataSetChanged();
                        drawerLayout.closeDrawers();
                        return true;
                    case R.id.nav_work:
                        filter = 1;
                        todo.modelItems.clear();
                        todo.modelItems.addAll( helper.listByCat(helper.getReadableDatabase(),filter));
                        todo.adapter.notifyDataSetChanged();
                        drawerLayout.closeDrawers();
                        return true;
                    case R.id.nav_family:
                        filter = 2;
                        todo.modelItems.clear();
                        todo.modelItems.addAll( helper.listByCat(helper.getReadableDatabase(),filter));
                        todo.adapter.notifyDataSetChanged();
                        drawerLayout.closeDrawers();
                        return true;
                    case R.id.nav_friends:
                        filter = 3;
                        todo.modelItems.clear();
                        todo.modelItems.addAll( helper.listByCat(helper.getReadableDatabase(),filter));
                        todo.adapter.notifyDataSetChanged();
                        drawerLayout.closeDrawers();
                        return true;
                    case R.id.nav_misc:
                        filter = 4;
                        todo.modelItems.clear();
                        todo.modelItems.addAll( helper.listByCat(helper.getReadableDatabase(),filter));
                        todo.adapter.notifyDataSetChanged();
                        drawerLayout.closeDrawers();
                        return true;
                    default:
                        drawerLayout.closeDrawers();
                        return true;
                }
            }
        });
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle( this, drawerLayout, toolbar, R.string.drawer_open , R.string.drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super .onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super .onDrawerOpened(drawerView);
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        //set noti gp, channel
        final String GROUP_KEY_TODO_LIST = "group_key_todo_list";
        CharSequence groupname="TODOList Group";
        NotificationManager notificationManger =
                (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManger.createNotificationChannelGroup(
                new NotificationChannelGroup(GROUP_KEY_TODO_LIST,groupname));

        NotificationManager nm =
                (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        String id ="finna_channel";//channel id
        CharSequence description = "TODOList Reminder 1";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(id, description, importance);
        channel.enableVibration(true);
        channel.enableLights(true);
        channel.setVibrationPattern(new long[]{10,250,250,250,250});
        channel.setLightColor(Color.BLUE);
        channel.setGroup(GROUP_KEY_TODO_LIST);
        nm.createNotificationChannel(channel);//add channel


        // auto clean done task where task_done_date < today-5
        try {
            helper.autoRemoveDoneTask(helper.getWritableDatabase());
        }catch(Exception e){
            Log.e("Auto Remove Done Task","DB not yet created.");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.removeItem(R.id.action_save);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.menu_about:
                Intent intent = new Intent(this, com.example.keith.test.about.class);
                startActivity(intent);
                return true;
/*            case R.id.clear_done_task:
                helper.removeDoneTask(helper.getWritableDatabase());
                try {
                    taskDone.taskDoneModel.clear();
                    taskDone.taskDoneModel.addAll(helper.listDoneTask(helper.getReadableDatabase()));
                    taskDone.adapter.notifyDataSetChanged();
                }catch (Exception e) {
                    Log.e("Remove done task error","TaskDone not yet created.");
                }
                Toast.makeText(this,"All done tasks are deleted.",
                        Toast.LENGTH_LONG).show();
                return true;
            case R.id.filter_none:
                filter = 0;
                todo.modelItems.clear();
                todo.modelItems.addAll(helper.listTodoTask(helper.getReadableDatabase()));
                todo.adapter.notifyDataSetChanged();
                return true;
            case R.id.filter_work:
                filter = 1;
                todo.modelItems.clear();
                todo.modelItems.addAll( helper.listByCat(helper.getReadableDatabase(),filter));
                todo.adapter.notifyDataSetChanged();
                return true;
            case R.id.filter_family:
                filter = 2;
                todo.modelItems.clear();
                todo.modelItems.addAll( helper.listByCat(helper.getReadableDatabase(),filter));
                todo.adapter.notifyDataSetChanged();
                return true;
            case R.id.filter_friends:
                filter = 3;
                todo.modelItems.clear();
                todo.modelItems.addAll( helper.listByCat(helper.getReadableDatabase(),filter));
                todo.adapter.notifyDataSetChanged();
                return true;
            case R.id.filter_misc:
                filter = 4;
                todo.modelItems.clear();
                todo.modelItems.addAll( helper.listByCat(helper.getReadableDatabase(),filter));
                todo.adapter.notifyDataSetChanged();
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
//            // getItem is called to instantiate the fragment for the given page.
//            // Return a PlaceholderFragment (defined as a static inner class below).
//            return PlaceholderFragment.newInstance(position + 1);
            switch (position) {
                case 0:
                    todo tab1 = new todo();
                    return tab1;
                case 1:
                    deadline tab2 = new deadline();
                    return tab2;
                case 2:
                    taskDone tab3 = new taskDone();
                    return tab3;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}
