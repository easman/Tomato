package com.echo.anothertest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    private static final int MSG_TIME_IS_UP = 1;
    private static final int MSG_TIME_TICK = 2;
    private static final int MST_PRESS_BACK_BUTTON = 3;
    private static final boolean WORK_TIME_SITUATION = true;
    private static final boolean BREAK_TIME_SITUATION = false;
    private boolean currentSituation;
    private boolean isRunning;
    private boolean txStartHasNotClicked;
    private int mTotalProgress;
    private int mCurrentProgress;
    private int workMinutes;
    private int breakMinutes;
    private int totleTamatoTime;
    private double currentTomatoNumber;
    private TasksCompletedView mTasksView;
    private TextView tx1, tx2, txStart;
    private Timer timer = new Timer();
    private TimerTask timerTask = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initVariable();
        initView();
        txStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txStartHasNotClicked) {
                    tx2.setVisibility(View.VISIBLE);
                    txStart.setText("Tic Tok =。=");
                    txStartHasNotClicked = false;
                    if (currentSituation == WORK_TIME_SITUATION) {
                        currentTomatoNumber += 0.5;
                    }
                    isRunning = true;
                    startTimer();
                }
            }
        });

        tx1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tx1.setVisibility(View.INVISIBLE);
                tx2.setVisibility(View.VISIBLE);
                isRunning = true;
                startTimer();
            }
        });

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

    @Override
    public void onBackPressed() {
        handler.sendEmptyMessage(MST_PRESS_BACK_BUTTON);
    }

    private void initVariable() {
        workMinutes = 15000;
        breakMinutes = 8000;
        totleTamatoTime = 4;
        isRunning = false;
        currentTomatoNumber = 0;
        currentSituation = WORK_TIME_SITUATION;
        mTotalProgress = workMinutes;
        mCurrentProgress = 0;
        txStartHasNotClicked = true;
    }

    private void initView() {
        mTasksView = (TasksCompletedView) findViewById(R.id.tasks_view);
        tx1 = (TextView) findViewById(R.id.tx1);
        tx2 = (TextView) findViewById(R.id.tx2);
        txStart = (TextView) findViewById(R.id.txStart);
        tx1.setVisibility(View.INVISIBLE);
        tx2.setVisibility(View.INVISIBLE);
        mTasksView.setmTotalProgress(mTotalProgress);
    }

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
                    }

                }
            };
            timer.schedule(timerTask, 0, 1);//延迟一秒执行，以后每隔1ms执行一次

        }
    }

    private void stopTimer() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_TIME_IS_UP:
                    new AlertDialog.Builder(getContext()).setTitle("Time is up").setMessage("Time is up").setNegativeButton("Cancle", null).show();   //getContext????
                    timeIsUpEvent();
                    break;
                case MSG_TIME_TICK:
                    int minute = mCurrentProgress / 1000 / 60;
                    int second = mCurrentProgress / 1000 % 60;
                    System.out.println(minute + ":" + second);
                    //TODO 设置TextView的时间字符串
                    break;
                case MST_PRESS_BACK_BUTTON:
                    stopTimer();
                    new AlertDialog.Builder(getContext()).setCancelable(false).setMessage("你确定要结束这个番茄吗").setPositiveButton("结束番茄", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.super.onBackPressed();
                            //TODO 结束番茄
                        }
                    }).setNegativeButton("继续工作", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(isRunning){
                                startTimer();
                            }else {
                                stopTimer();
                            }
                        }
                    }).show();
                    break;
            }
        }
    };

    private void timeIsUpEvent() {
        if (currentSituation == WORK_TIME_SITUATION) {   //判断当前执行的是工作还是休息状态
            currentTomatoNumber += 0.5;
            System.out.println(currentTomatoNumber);
            if (currentTomatoNumber == totleTamatoTime) {       //判断是否已经完成番茄

                //TODO 结束番茄
            } else {
                currentSituation = BREAK_TIME_SITUATION;
                mTotalProgress = breakMinutes;
                mTasksView.setmTotalProgress(mTotalProgress);
                mCurrentProgress = 0;
                txStart.setText("休息一下");
                startTimer();
            }
        } else if (currentSituation == BREAK_TIME_SITUATION) {
            currentSituation = WORK_TIME_SITUATION;
            mTotalProgress = workMinutes;
            mTasksView.setmTotalProgress(mTotalProgress);
            mCurrentProgress = 0;
            txStart.setText("Tic Tok =。=");
            startTimer();

        }
        //TODO 完成切换或者其他结束时应该完成的项目
    }

    private Context getContext() {
        return this;
    }
}
