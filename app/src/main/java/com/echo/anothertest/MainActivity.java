package com.echo.anothertest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Echo
 */

public class MainActivity extends Activity {

    private static final int MSG_TIME_IS_UP = 1;
    private static final int MSG_TIME_TICK = 2;
    private static final int MST_PRESS_BACK_BUTTON = 3;
    private static final boolean WORK_TIME_SITUATION = true;
    private static final boolean BREAK_TIME_SITUATION = false;

    //载入数据参数
    private Tomato tomato;
    private int workMinutes;
    private int breakMinutes;
    private int totleTomatoRepeat;
    private int numberOfFinish;
    private int numberOfUnfinish;
    private String jobDescription;


    //运行中调用参数
    private int mTotalProgress;
    private int mCurrentProgress;
    private double currentTomatoNumber;
    private boolean currentSituation;
    private boolean isRunning;
    private boolean txStartHasNotClicked;

    private TasksCompletedView mTasksView;
    private TextView tx1, tx2, txStart, txNumber, txCounter;
    private Timer timer = new Timer();
    private TimerTask timerTask = null;
    private DecimalFormat df = new DecimalFormat();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //提取intent中的tomato对象
        Intent intent = getIntent();
        String tomatoString = intent.getStringExtra("tomato");
        tomato = SerializableHelper.getTomatoFromShare(tomatoString);

        //初始化
        initVariable(tomato);
        initView();

        //数据格式化
        DecimalFormat df = new DecimalFormat();
        df.applyPattern("00");

        //设置txStart监听器
        txStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txStartHasNotClicked) {
                    tx2.setVisibility(View.VISIBLE);
                    txStart.setText(jobDescription);
                    txStartHasNotClicked = false;
                    isRunning = true;
                    startTimer();
                }
            }
        });

        //设置继续按钮监听器
        tx1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tx1.setVisibility(View.INVISIBLE);
                tx2.setVisibility(View.VISIBLE);
                isRunning = true;
                startTimer();
            }
        });

        //设置暂停按钮长按监听器
        tx2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isRunning = false;
                stopTimer();
                tx1.setVisibility(View.VISIBLE);
                tx2.setVisibility(View.INVISIBLE);
                return true;
            }
        });

        //设置暂停按钮长按效果
        tx2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        tx2.setText("啊啊啊啊，你真的不继续了吗");
                        tx2.setTextColor(0xFF0000FF);
                        break;
                    case MotionEvent.ACTION_UP:
                        tx2.setText("长按暂停");
                        tx2.setTextColor(0xFFFFFFFF);
                        break;

                }
                return false;
            }
        });
    }

    /*
    重写onBackPressed，更改返回键功能
     */
    @Override
    public void onBackPressed() {
        handler.sendEmptyMessage(MST_PRESS_BACK_BUTTON);
    }

    /*
    数据初始化方法
     */
    private void initVariable(Tomato tomato) {
        workMinutes = tomato.getWorkMinutes()*1000*60;
        breakMinutes = tomato.getBreakMinutes()*1000*60;
        totleTomatoRepeat = tomato.getTotleTamatoRepeat();
        jobDescription = tomato.getJobDescription();
        numberOfFinish = tomato.getNumberOfFinish();
        numberOfUnfinish = tomato.getNumberOfUnfinish();

        isRunning = false;
        currentTomatoNumber = 0;
        currentSituation = WORK_TIME_SITUATION;
        mTotalProgress = workMinutes;
        mCurrentProgress = 0;
        txStartHasNotClicked = true;
    }

    /*
    布局初始化方法
     */
    private void initView() {
        mTasksView = (TasksCompletedView) findViewById(R.id.tasks_view);
        tx1 = (TextView) findViewById(R.id.tx1);
        tx2 = (TextView) findViewById(R.id.tx2);
        txStart = (TextView) findViewById(R.id.txStart);
        txNumber = (TextView) findViewById(R.id.txNumber);
        txCounter = (TextView) findViewById(R.id.txCounter);

        tx1.setVisibility(View.INVISIBLE);
        tx2.setVisibility(View.INVISIBLE);

        //番茄执行进度
        txNumber.setText((int) (currentTomatoNumber + 1) + "个番茄/" + totleTomatoRepeat + "个番茄");

        //倒计时显示
        df.applyPattern("00");
        txCounter.setText((mTotalProgress - mCurrentProgress) / 1000 / 60 + ":" + df.format((mTotalProgress - mCurrentProgress) / 1000 % 60));
        mTasksView.setmTotalProgress(mTotalProgress);
    }

    /*
    开启计时器方法
     */
    public void startTimer() {
        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    mCurrentProgress++;
                    mTasksView.setProgress(mCurrentProgress);
                    if (mCurrentProgress % 1000 == 0) {
                        handler.sendEmptyMessage(MSG_TIME_TICK);
                    }
                    if (mCurrentProgress >= mTotalProgress) {
                        handler.sendEmptyMessage(MSG_TIME_IS_UP);
                        stopTimer();
//                        endTomato();
                    }

                }
            };
            timer.schedule(timerTask, 0, 1);//延迟一秒执行，以后每隔1ms执行一次

        }
    }

    /*
    停止计时器方法
     */
    private void stopTimer() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    /*
    使用Handler进行UI更新
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_TIME_IS_UP:
//                    new AlertDialog.Builder(getContext()).setTitle("Time is up").setMessage("Time is up").setNegativeButton("Cancle", null).show();   //getContext????
                    timeIsUpEvent();
                    break;
                case MSG_TIME_TICK:
                    int minute = (mTotalProgress - mCurrentProgress) / 1000 / 60;
                    int second = (mTotalProgress - mCurrentProgress) / 1000 % 60;
                    txCounter.setText(minute + ":" + df.format(second));
                    break;
                case MST_PRESS_BACK_BUTTON:
                    stopTimer();
                    new AlertDialog.Builder(MainActivity.this).setCancelable(false).setMessage("你确定要结束这个番茄吗").setPositiveButton("结束番茄", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            endTomato();
                        }
                    }).setNegativeButton("继续工作", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (isRunning) {
                                startTimer();
                            } else {
                                stopTimer();
                            }
                        }
                    }).show();
                    break;
            }
        }
    };

    /*
    工作完成或休息完成时的处理方法
     */
    private void timeIsUpEvent() {
        currentTomatoNumber += 0.5;
        if (currentSituation == WORK_TIME_SITUATION) {   //判断当前执行的是工作还是休息状态
            txNumber.setText((int) (currentTomatoNumber + 1) + "个番茄/" + totleTomatoRepeat + "个番茄");
            if (totleTomatoRepeat - 1 == (int) currentTomatoNumber) {       //判断是否已经完成番茄
                endTomato();
            } else {
                currentSituation = BREAK_TIME_SITUATION;
                mTotalProgress = breakMinutes;
                mTasksView.setmTotalProgress(mTotalProgress);
                mCurrentProgress = 0;
                txStart.setText("休息一下");
                startTimer();
            }
        } else if (currentSituation == BREAK_TIME_SITUATION) {
            txNumber.setText((int) (currentTomatoNumber + 1) + "个番茄/" + totleTomatoRepeat + "个番茄");
            currentSituation = WORK_TIME_SITUATION;
            mTotalProgress = workMinutes;
            mTasksView.setmTotalProgress(mTotalProgress);
            mCurrentProgress = 0;
            txStart.setText("Tic Tok =。=");
            startTimer();
        }
    }

    /*
    番茄终止时的处理方法
     */
    private void endTomato(){
        //对tomato对象标记完成状况
        if (currentTomatoNumber==(double) totleTomatoRepeat-0.5){
            numberOfFinish++;
            tomato.setNumberOfFinish(numberOfFinish);
        }else {
            numberOfUnfinish++;
            tomato.setNumberOfUnfinish(numberOfUnfinish);
        }
        //序列化tomato对象
        String tomatoString = SerializableHelper.setTomatoToShare(tomato);
        Intent intent = new Intent();
        intent.putExtra("tomato_back", tomatoString);
        //通过Intent对象返回结果，调用setResult方法
        setResult(RESULT_OK,intent);
        finish();//结束当前的activity的生命周期
    }

    private Context getContext() {
        return this;
    }
}
