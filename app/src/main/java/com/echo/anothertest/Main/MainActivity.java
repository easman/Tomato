package com.echo.anothertest.Main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.echo.anothertest.R;
import com.echo.anothertest.alarm.AlarmActivity;
import com.echo.anothertest.bean.Pomodori;
import com.echo.anothertest.utils.SerializableHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Echo
 */

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MyAdapter myAdapter;
    private List<Pomodori> pomodoris = new ArrayList<Pomodori>();
    private PopupWindow creatPomodoriWindow;
    private View contentViewPop;
    private TextView backgroundFace;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private boolean isEditMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face);
        isEditMode = false;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("番茄堆");
        setSupportActionBar(toolbar);
        backgroundFace = (TextView) findViewById(R.id.background_face_activity);

        //设置右下角新建按钮功能
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreatWindow(view);
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.pomodori_list);       // 拿到RecyclerView
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));       // 设置LinearLayoutManager
        myAdapter = new MyAdapter(this, pomodoris);                            // 初始化自定义的适配器
        mRecyclerView.setAdapter(myAdapter);                                 // 为mRecyclerView设置适配器
        readSavedPomodoriList();

        //避免添加按钮遮挡卡片内容
        if (pomodoris.size() >= 4) {
            fab.setAlpha(0.5f);
        } else {
            fab.setAlpha(1f);
        }

        //设置编辑番茄卡片，为RecycleView绑定触摸事件
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                //首先回调的方法 返回int表示是否监听该方向
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;//拖拽
                int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;//侧滑删除
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //滑动事件
                Collections.swap(pomodoris, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                myAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                savePomodoriList();
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //侧滑事件
                pomodoris.remove(viewHolder.getAdapterPosition());
                myAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                savePomodoriList();
            }

            @Override
            public boolean isLongPressDragEnabled() {
                //是否可拖拽
                if (isEditMode) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        helper.attachToRecyclerView(mRecyclerView);

        // 设置点击番茄卡片事件
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                if (!isEditMode) {
                    new AlertDialog.Builder(MainActivity.this).setMessage("你要开始这堆番茄吗？").setNegativeButton("是的，我要开始", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String pomodoriString = SerializableHelper.setPomodoriToShare(pomodoris.get(position));
                            Intent intent = new Intent(MainActivity.this, AlarmActivity.class);
                            intent.putExtra("pomodori", pomodoriString);
                            startActivityForResult(intent, position); //使用位置作为结果码，便于结束后更新数据
                        }
                    }).setPositiveButton("不，我要再想想", null).show();
                }
            }

            @Override
            public void onItemLongClick(View view, final int position) {
                if (!isEditMode) {
                    new AlertDialog.Builder(MainActivity.this).setMessage("你想要删除这堆番茄吗").setNegativeButton("是的，我要删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            pomodoris.remove(position);
                            savePomodoriList();
                            mRecyclerView.scrollToPosition(position);
                            myAdapter.notifyDataSetChanged();
                            if (pomodoris.size() >= 4) {
                                fab.setAlpha(0.5f);
                            } else {
                                fab.setAlpha(1f);
                            }
                        }
                    }).setPositiveButton("不，我要再想想", null).show();
                }
            }
        }));


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            Pomodori pomodori = SerializableHelper.getPomodoriFromShare(intent.getStringExtra("pomodori_back")); //反序列化pomodori结果
            pomodoris.set(requestCode, pomodori);
            savePomodoriList();
            myAdapter.notifyDataSetChanged();
        }
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.face, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                if (isEditMode) {
                    item.setIcon(R.drawable.ic_edit);
                    exitEditMode();
                } else {
                    item.setIcon(R.drawable.ic_back);
                    toEditMode();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setMessage("你确定要退出吗？").setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setPositiveButton("取消", null).show();
    }

    //存储数据
    private void savePomodoriList() {
        SharedPreferences.Editor editor = getSharedPreferences(MainActivity.class.getName(), Context.MODE_PRIVATE).edit();
        if (myAdapter.getItemCount() != 0) {
            //使用类名来命名SharedPreferences
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < myAdapter.getItemCount(); i++) {
                sb.append(SerializableHelper.setPomodoriToShare(pomodoris.get(i))).append(",");
            }
            String content = sb.toString().substring(0, sb.length() - 1);
            editor.putString(getString(R.string.card_list), content);
            editor.commit();
            backgroundFace.setText("");
        } else {
            editor.clear();
            editor.commit();
            backgroundFace.setText("点击右下角按钮添加番茄");
        }
        if (pomodoris.size() >= 4) {
            fab.setAlpha(0.5f);
        } else {
            fab.setAlpha(1f);
        }
    }

    //读取数据
    private void readSavedPomodoriList() {
        SharedPreferences sp = getSharedPreferences(MainActivity.class.getName(), Context.MODE_PRIVATE);
        String content = sp.getString(getString(R.string.card_list), null);

        if (content != null) {
            backgroundFace.setText("");
            String[] pomodoriStrings = content.split(",");
            for (String string :
                    pomodoriStrings) {
                pomodoris.add(SerializableHelper.getPomodoriFromShare(string));
            }
        } else {
            backgroundFace.setText("点击右下角按钮添加番茄");
        }
    }

    //自定义新建设置界面
    private void showCreatWindow(View parent) {
        if (creatPomodoriWindow == null) {
            LayoutInflater mLayoutInflater = LayoutInflater.from(this);
            contentViewPop = mLayoutInflater.inflate(R.layout.popup_setting, null);
            creatPomodoriWindow = new PopupWindow(contentViewPop, ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        //初始化item
        final EditText inputJobDescription = (EditText) contentViewPop.findViewById(R.id.input_job_description);
        final EditText inputWorkMinutes = (EditText) contentViewPop.findViewById(R.id.input_work_minutes);
        final EditText inputBreakMinutes = (EditText) contentViewPop.findViewById(R.id.input_break_minutes);
        final SeekBar seekPomodoriRepeat = (SeekBar) contentViewPop.findViewById(R.id.seek_pomodori_repeat);
        final TextView showPomodoriRepeat = (TextView) contentViewPop.findViewById(R.id.show_pomodori_repeat);
        final Switch switchSound = (Switch) contentViewPop.findViewById(R.id.switch_sound);
        final Switch switchWave = (Switch) contentViewPop.findViewById(R.id.switch_wave);
        TextView creatPomodori = (TextView) contentViewPop.findViewById(R.id.click_to_creat_pomodori);
        Switch switchDefaultSetting = (Switch) contentViewPop.findViewById(R.id.switch_default_setting);

        //“创建”按钮的监听事件
        creatPomodori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //检查空值
                StringBuffer sbf = new StringBuffer();
                if (inputJobDescription.getText().toString().length() == 0) {
                    sbf.append("任务名称，");
                }
                if (inputWorkMinutes.getText().toString().length() == 0) {
                    sbf.append("番茄时长，");
                }
                if (inputBreakMinutes.getText().toString().length() == 0) {
                    sbf.append("休息时长，");
                }
                if (sbf.length() > 0) {
                    //显示toast信息
                    String stShow = sbf.toString().substring(0, sbf.length() - 1) + " 不能空呦~";
                    Toast toast = Toast.makeText(getApplicationContext(), stShow, Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    Pomodori currentPomodori = new Pomodori(Integer.parseInt(inputWorkMinutes.getText().toString()),
                            Integer.parseInt(inputBreakMinutes.getText().toString()),
                            seekPomodoriRepeat.getProgress() + 1,
                            inputJobDescription.getText().toString(),
                            switchSound.isChecked(),
                            switchWave.isChecked());
                    pomodoris.add(currentPomodori);
                    savePomodoriList();
                    mRecyclerView.scrollToPosition(myAdapter.getItemCount() - 1);
                    myAdapter.notifyDataSetChanged();
                    creatPomodoriWindow.dismiss();
                    creatPomodoriWindow = null;
                }
            }
        });

        //“启用默认设置”按钮监听器
        switchDefaultSetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    inputWorkMinutes.setEnabled(false);
                    inputBreakMinutes.setEnabled(false);
                    seekPomodoriRepeat.setEnabled(false);
                } else {
                    inputWorkMinutes.setEnabled(true);
                    inputBreakMinutes.setEnabled(true);
                    seekPomodoriRepeat.setEnabled(true);
                }
            }
        });

        //显示当前选择的番茄个数
        seekPomodoriRepeat.setEnabled(false);
        seekPomodoriRepeat.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String st = (progress + 1) + "个";
                showPomodoriRepeat.setText(st);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //设置输入范围
        inputWorkMinutes.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    if (Integer.parseInt(s.toString()) > 120) {
                        inputWorkMinutes.setText("120");
                    } else if (Integer.parseInt(s.toString()) < 1) {
                        inputWorkMinutes.setText("1");
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        inputBreakMinutes.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    if (Integer.parseInt(s.toString()) > 45) {
                        inputBreakMinutes.setText("45");
                    } else if (Integer.parseInt(s.toString()) < 1) {
                        inputBreakMinutes.setText("1");
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //设置是否响铃
        switchSound.setChecked(false);
        switchSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        //设置是否震动
        switchWave.setChecked(false);
        switchWave.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });


        //产生背景变暗效果
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.2f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);

        //设置外部可点击dismiss
        ColorDrawable cd = new ColorDrawable(0x000000);
        creatPomodoriWindow.setBackgroundDrawable(cd);
        creatPomodoriWindow.setOutsideTouchable(true);
        creatPomodoriWindow.setFocusable(true);

        //设置动画
        creatPomodoriWindow.setAnimationStyle(R.style.popwin_anim_style);

        //设置显示位置为屏幕中央
        creatPomodoriWindow.showAtLocation((View) parent.getParent(), Gravity.CENTER, 0, 0);

        //设置dismiss时功能
        creatPomodoriWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            //在dismiss中恢复透明度
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp);
            }
        });

    }

    //打开编辑模式
    private void toEditMode() {
        isEditMode = true;
        toolbar.setTitle("编辑模式");
        toolbar.setAlpha(0.5f);
        backgroundFace.setBackgroundColor(Color.parseColor("#E1E1E1"));
        backgroundFace.setText("");
        AnimationSet animationSet = (AnimationSet) AnimationUtils.loadAnimation(MainActivity.this, R.anim.hide);
        fab.startAnimation(animationSet);
        fab.setVisibility(View.GONE);
    }

    //关闭编辑模式
    private void exitEditMode() {
        isEditMode = false;
        toolbar.setTitle("番茄堆");
        toolbar.setAlpha(1f);
        backgroundFace.setBackgroundColor(Color.parseColor("#FFFFFF"));
        if (pomodoris.size() == 0) {
            backgroundFace.setText("点击右下角按钮添加番茄");
        }
        AnimationSet animationSet = (AnimationSet) AnimationUtils.loadAnimation(MainActivity.this, R.anim.show);
        fab.startAnimation(animationSet);
        fab.setVisibility(View.VISIBLE);
    }
}
