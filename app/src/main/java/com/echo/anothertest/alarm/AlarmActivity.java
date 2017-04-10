package com.echo.anothertest.alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.echo.anothertest.R;
import com.echo.anothertest.utils.SerializableHelper;
import com.echo.anothertest.bean.Tomato;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Echo
 */

public class AlarmActivity extends Activity {

    private static final int MSG_TIME_IS_UP = 1;
    private static final int MSG_TIME_TICK = 2;
    private static final int MSG_TIME_TICK_UI = 3;
    private static final int MSG_PRESS_BACK_BUTTON = 4;
    private static final int WORK_TIME_SITUATION = 10;
    private static final int BREAK_TIME_SITUATION = 11;

    //载入数据参数
    private Tomato tomato;
    private int workMinutes;
    private int breakMinutes;
    private int totleTomatoRepeat;
    private int numberOfFinish;
    private int numberOfUnfinish;
    private String jobDescription;
    private boolean isSound, isWave;


    //运行中调用参数
    private int mTotalProgress;
    private int mCurrentProgress;
    private double currentTomatoNumber;
    private int currentSituation;
    private boolean isRunning;
    private boolean isTouchPause;

    private TasksCompletedView mTasksView;
    private TextView tx1, tx2, txStart, txNumber, txCounter;
    private ImageView soundClicker, waveClicker;
    private Timer timer = new Timer();
    private TimerTask timerTask = null;
    private DecimalFormat df = new DecimalFormat();

    private PowerManager.WakeLock wakeLock;

    /*
    创建Activity
     */
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

        //载入后直接开始计时初始化
        tx2.setVisibility(View.VISIBLE);
        txStart.setText(jobDescription);
        isRunning = true;
        startTimer();

//        //设置txStart监听器
//        txStart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (txStartHasNotClicked) {
//
//                }
//            }
//        });

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
                        isTouchPause = true;
                        tx2.setTextColor(0xFF84462C);
                        break;
                    case MotionEvent.ACTION_UP:
                        isTouchPause = false;
                        tx2.setTextColor(0xFFFFFFFF);
                        break;

                }
                return false;
            }
        });

        soundClicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSound) {
                    soundClicker.setImageResource(R.drawable.ic_mute);
                    isSound = false;
                } else {
                    soundClicker.setImageResource(R.drawable.ic_sound);
                    isSound = true;
                }
            }
        });

        waveClicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isWave) {
                    waveClicker.setImageResource(R.drawable.ic_unwave);
                    isWave = false;
                } else {
                    waveClicker.setImageResource(R.drawable.ic_wave);
                    isWave = true;
                }
            }
        });
    }

    /*
    重写onBackPressed，更改返回键功能
     */
    @Override
    public void onBackPressed() {
        handler.sendEmptyMessage(MSG_PRESS_BACK_BUTTON);
    }

    /*
    数据初始化方法
     */
    private void initVariable(Tomato tomato) {
        workMinutes = tomato.getWorkMinutes() * 1000 * 60;
        breakMinutes = tomato.getBreakMinutes() * 1000 * 60;
        totleTomatoRepeat = tomato.getTotleTamatoRepeat();
        jobDescription = tomato.getJobDescription();
        numberOfFinish = tomato.getNumberOfFinish();
        numberOfUnfinish = tomato.getNumberOfUnfinish();
        isSound = tomato.isSound();
        isWave = tomato.isWave();

        isRunning = false;
        isTouchPause = false;
        currentTomatoNumber = 0;
        currentSituation = WORK_TIME_SITUATION;
        mTotalProgress = workMinutes;
        mCurrentProgress = 0;
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
        soundClicker = (ImageView) findViewById(R.id.soundClicker);
        waveClicker = (ImageView) findViewById(R.id.waveClicker);
        if (isSound) {
            soundClicker.setImageResource(R.drawable.ic_sound);
        } else {
            soundClicker.setImageResource(R.drawable.ic_mute);
        }
        waveClicker = (ImageView) findViewById(R.id.waveClicker);
        if (isWave) {
            waveClicker.setImageResource(R.drawable.ic_wave);
        } else {
            waveClicker.setImageResource(R.drawable.ic_unwave);
        }

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

            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                    | PowerManager.ON_AFTER_RELEASE, "DPA");
            wakeLock.acquire();

            timerTask = new TimerTask() {
                @Override
                public void run() {
                    mCurrentProgress += 20;
                    watchHandler.sendEmptyMessage(MSG_TIME_TICK_UI);
                    if (mCurrentProgress % 1000 == 0) {
                        handler.sendEmptyMessage(MSG_TIME_TICK);
                    }
                    if (mCurrentProgress >= mTotalProgress) {
                        stopTimer();
                        handler.sendEmptyMessage(MSG_TIME_IS_UP);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            timer.schedule(timerTask, 0, 20);//每隔20ms执行一次
        }
    }

    /*
    停止计时器方法
     */
    private void stopTimer() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;

            //停止唤醒CPU
            wakeLock.release();

        }
    }

    /*
    工作完成或休息完成时的处理方法
     */
    private void timeIsUpEvent() {
        final MediaPlayer mediaPlayer;
        final Vibrator vib = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);

        currentTomatoNumber += 0.5;

        //判断当前执行的是工作还是休息状态
        if (currentSituation == WORK_TIME_SITUATION) {

            //更新番茄个数
            txNumber.setText((int) (currentTomatoNumber + 1) + "个番茄/" + totleTomatoRepeat + "个番茄");

            //判断是否已经完成番茄
            if (totleTomatoRepeat - 1 == (int) currentTomatoNumber) {

                //震动和铃音
                mediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.break_sound);
                if (isSound) {
                    mediaPlayer.start();
                }
                if (isWave) {
                    long[] lg = new long[]{700, 300, 700, 300, 700, 300, 700, 300, 700, 300, 700, 300};
                    vib.vibrate(lg, -1);
                }
                new AlertDialog.Builder(getContext()).setTitle("恭喜你又完成了一个任务").setMessage("点击OK或空白离开").setNegativeButton("OK", null).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                        }
                        vib.cancel();
                        endTomato();
                    }
                }).show();
            } else {

                //震动和铃音
                mediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.break_sound);
                if (isSound) {
                    mediaPlayer.start();
                }
                if (isWave) {
                    long[] lg = new long[]{700, 300, 700, 300, 700, 300, 700, 300, 700, 300, 700, 300};
                    vib.vibrate(lg, -1);
                }
                new AlertDialog.Builder(getContext()).setMessage("工作了那么久，该休息一下了！").setNegativeButton("OK", null).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                        }
                        vib.cancel();
                    }
                }).show();

                //更新数据
                currentSituation = BREAK_TIME_SITUATION;
                mTotalProgress = breakMinutes;
                mTasksView.setmTotalProgress(mTotalProgress);
                mCurrentProgress = 0;
                txStart.setText("休息一下");
                startTimer();
            }
        } else if (currentSituation == BREAK_TIME_SITUATION) {

            //震动和铃音
            mediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.work_sound);
            if (isSound) {
                mediaPlayer.start();
            }
            if (isWave) {
                long[] lg = new long[]{700, 300, 700, 300, 700, 300, 700, 300, 700, 300, 700, 300, 700, 300, 700, 300};
                vib.vibrate(lg, -1);
            }
            new AlertDialog.Builder(getContext()).setMessage("打起精神，现在是工作时间了！").setNegativeButton("OK", null).setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }
                    vib.cancel();
                }
            }).show();

            // 更新数据
            txNumber.setText((int) (currentTomatoNumber + 1) + "个番茄/" + totleTomatoRepeat + "个番茄");
            currentSituation = WORK_TIME_SITUATION;
            mTotalProgress = workMinutes;
            mTasksView.setmTotalProgress(mTotalProgress);
            mCurrentProgress = 0;
            txStart.setText(jobDescription);
            startTimer();
        }
    }

    /*
    番茄终止时的处理方法
     */
    private void endTomato() {

        //对tomato对象标记完成状况
        if (currentTomatoNumber == (double) totleTomatoRepeat - 0.5) {
            numberOfFinish++;
            tomato.setNumberOfFinish(numberOfFinish);
        } else {
            numberOfUnfinish++;
            tomato.setNumberOfUnfinish(numberOfUnfinish);
        }

        //记录响铃和震动设置
        tomato.setSound(isSound);
        tomato.setWave(isWave);

        //序列化tomato对象
        String tomatoString = SerializableHelper.setTomatoToShare(tomato);
        Intent intent = new Intent();
        intent.putExtra("tomato_back", tomatoString);

        //通过Intent对象返回结果，调用setResult方法
        setResult(RESULT_OK, intent);

        //结束当前的activity的生命周期
        finish();
    }

    /*
    返回context
     */
    private Context getContext() {
        return this;
    }

    /*
   使用Handler进行UI更新
    */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_TIME_IS_UP:
                    timeIsUpEvent();
                    break;
                case MSG_TIME_TICK:
                    int minute = (mTotalProgress - mCurrentProgress) / 1000 / 60;
                    int second = (mTotalProgress - mCurrentProgress) / 1000 % 60;
                    txCounter.setText(minute + ":" + df.format(second));
                    break;
                case MSG_PRESS_BACK_BUTTON:
                    stopTimer();
                    new AlertDialog.Builder(AlarmActivity.this).setCancelable(false).setMessage("你确定要结束这个番茄吗").setPositiveButton("结束番茄", new DialogInterface.OnClickListener() {
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

    //设置更新表盘的Handler
    private Handler watchHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_TIME_TICK_UI:
                    if (isTouchPause) {
                        mTasksView.setProgressWithStroke(mTotalProgress - mCurrentProgress);
                    } else {
                        mTasksView.setProgress(mTotalProgress - mCurrentProgress);
                    }
            }
        }
    };
}
