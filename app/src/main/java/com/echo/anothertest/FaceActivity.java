package com.echo.anothertest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Echo
 */

public class FaceActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mRecyclerView;
    private MyAdapter myAdapter;
    private List<Tomato> tomatos = new ArrayList<Tomato>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //设置右下角按钮功能
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FaceActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //设置滑出菜单
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        mRecyclerView = (RecyclerView) findViewById(R.id.tomato_list);       // 拿到RecyclerView
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));       // 设置LinearLayoutManager
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());            // 设置ItemAnimator
        mRecyclerView.setHasFixedSize(true);                                 // 设置固定大小
        myAdapter = new MyAdapter(this, tomatos);                            // 初始化自定义的适配器
        mRecyclerView.setAdapter(myAdapter);                                 // 为mRecyclerView设置适配器
        readSavedTomatoList();

        //设置点击番茄卡片事件
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemLongClick(View view, final int position) {
                new AlertDialog.Builder(FaceActivity.this).setTitle("操作选项").setItems(new CharSequence[]{"删除"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                tomatos.remove(position);
                                saveTomatoList();
                                mRecyclerView.scrollToPosition(position);
                                myAdapter.notifyDataSetChanged();
                                break;
                            default:
                                break;
                        }

                    }
                }).setNegativeButton("取消",null).show();
            }
        }));

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.face, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String[] someTestItem = new String[]{"跑步", "学钢琴", "看电视", "玩游戏", "学python", "练字", "上自习", "读英语", "练习街舞", "聊天"};
        int[] someTestWorkTime = new int[]{1,2,3,4,5,6,7,8,9,10};
        int[] someTestBreakTime = new int[]{1,2,3,4,1,2,3,4,1,2};
        int[] sometotleTamato = new int[]{1,1,1,2,2,2,3,3,3,4};

        switch (item.getItemId()) {
            case R.id.action_settings:
                if (myAdapter.getItemCount() != 0) {
                    tomatos.remove(myAdapter.getItemCount() - 1);
                    saveTomatoList();
                    mRecyclerView.scrollToPosition(myAdapter.getItemCount() - 1);
                    myAdapter.notifyDataSetChanged();
                }
                return true;
            case R.id.action_settings2:
                Random random = new Random();
                int a = random.nextInt(10);
                Tomato currentTomato = new Tomato(someTestWorkTime[a], someTestBreakTime[a], sometotleTamato[a], someTestItem[a]);
                tomatos.add(currentTomato);
                saveTomatoList();
                mRecyclerView.scrollToPosition(myAdapter.getItemCount() - 1);
                myAdapter.notifyDataSetChanged();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //存储数据
    private void saveTomatoList() {
        SharedPreferences.Editor editor = getSharedPreferences(FaceActivity.class.getName(), Context.MODE_PRIVATE).edit();
        if (myAdapter.getItemCount() != 0) {
            //使用类名来命名SharedPreferences
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < myAdapter.getItemCount(); i++) {
                sb.append(SerializableHelper.setTomatoToShare(tomatos.get(i))).append(",");
            }
            String content = sb.toString().substring(0, sb.length() - 1);
            editor.putString(getString(R.string.alarm_list), content);
            editor.commit();
        }else {
            editor.clear();
            editor.commit();
        }
    }

    //读取数据
    private void readSavedTomatoList(){
        SharedPreferences sp = getSharedPreferences(FaceActivity.class.getName(),Context.MODE_PRIVATE);
        String content = sp.getString(getString(R.string.alarm_list),null);

        if (content!= null){
            String[] tomatoStrings = content.split(",");
            for (String string :
                    tomatoStrings) {

                tomatos.add(SerializableHelper.getTomatoFromShare(string));
            }
        }
    }
}
