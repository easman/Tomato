package com.echo.anothertest.alarm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.echo.anothertest.R;
import com.echo.anothertest.utils.SerializableHelper;
import com.echo.anothertest.bean.Pomodori;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Echo
 */

public class AlarmActivity extends Activity {

    private static final int MSG_TIME_IS_UP = 1;
    private static final int MSG_TIME_TICK_UI = 2;
    private static final int MSG_PRESS_BACK_BUTTON = 3;
    private static final int WORK_TIME_SITUATION = 10;
    private static final int BREAK_TIME_SITUATION = 11;

    //载入数据参数
    private Pomodori pomodori;
    private int workMinutes;
    private int breakMinutes;
    private int totlePomodoriRepeat;
    private int numberOfFinish;
    private int numberOfUnfinish;
    private String pomodoriName;
    private boolean isSound, isVibrate;
    private boolean screenOn;


    //运行中调用参数
    private int mTotalProgress;
    private int mCurrentProgress;
    private double currentPomodoriNumber;
    private int currentSituation;
    private boolean isRunning;
    private boolean isTouchPause;

    private TasksCompletedView mTasksView;
    private TextView tx1, tx2, txStart, txNumber, txCounter;
    private ImageView soundClicker, vibrateClicker;

    private Timer timer = new Timer();
    private TimerTask timerTask = null;

    private DecimalFormat df = new DecimalFormat();
    private IntentFilter screenTurnOnIntentFilter;
    private IntentFilter screenTurnOffIntentFilter;
    private ScreenTurnOnReceiver screenTurnOnReceiver;
    private ScreenTurnOffReceiver screenTurnOffReceiver;
    private PowerManager.WakeLock wakeLock;

    /*
    创建Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //注册广播接收器，监听屏幕状况
        screenTurnOnIntentFilter = new IntentFilter();
        screenTurnOffIntentFilter = new IntentFilter();
        screenTurnOnReceiver = new ScreenTurnOnReceiver();
        screenTurnOffReceiver = new ScreenTurnOffReceiver();
        screenTurnOnIntentFilter.addAction(Intent.ACTION_USER_PRESENT);
        screenTurnOffIntentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenTurnOnReceiver, screenTurnOnIntentFilter);
        registerReceiver(screenTurnOffReceiver, screenTurnOffIntentFilter);

        //提取intent中的Pomodori对象
        Intent intent = getIntent();
        String PomodoriString = intent.getStringExtra("pomodori");
        pomodori = SerializableHelper.getPomodoriFromShare(PomodoriString);

        //初始化
        initVariable(pomodori);
        initView();

        //数据格式化
        DecimalFormat df = new DecimalFormat();
        df.applyPattern("00");

        //载入后直接开始计时初始化
        tx2.setVisibility(View.VISIBLE);
        txStart.setText(pomodoriName);
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

        vibrateClicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVibrate) {
                    vibrateClicker.setImageResource(R.drawable.ic_unvibrate);
                    isVibrate = false;
                } else {
                    vibrateClicker.setImageResource(R.drawable.ic_vibrate);
                    isVibrate = true;
                }
            }
        });
    }

    /*
    重写onDestory，释放广播资源
     */

    @Override
    protected void onDestroy() {
        unregisterReceiver(screenTurnOnReceiver);
        unregisterReceiver(screenTurnOffReceiver);
        super.onDestroy();
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
    private void initVariable(Pomodori pomodori) {
        workMinutes = pomodori.getWorkMinutes() * 1000 * 60;
        breakMinutes = pomodori.getBreakMinutes() * 1000 * 60;
        totlePomodoriRepeat = pomodori.getTotlePomodoriRepeat();
        pomodoriName = pomodori.getPomodoriName();
        numberOfFinish = pomodori.getNumberOfFinish();
        numberOfUnfinish = pomodori.getNumberOfUnfinish();
        isSound = pomodori.isSound();
        isVibrate = pomodori.isVibrate();

        isRunning = false;
        isTouchPause = false;
        screenOn = true;
        currentPomodoriNumber = 0;
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
        vibrateClicker = (ImageView) findViewById(R.id.vibrateClicker);
        if (isSound) {
            soundClicker.setImageResource(R.drawable.ic_sound);
        } else {
            soundClicker.setImageResource(R.drawable.ic_mute);
        }
        vibrateClicker = (ImageView) findViewById(R.id.vibrateClicker);
        if (isVibrate) {
            vibrateClicker.setImageResource(R.drawable.ic_vibrate);
        } else {
            vibrateClicker.setImageResource(R.drawable.ic_unvibrate);
        }

        tx1.setVisibility(View.INVISIBLE);
        tx2.setVisibility(View.INVISIBLE);

        //番茄执行进度
        txNumber.setText((int) (currentPomodoriNumber + 1) + "个番茄/" + totlePomodoriRepeat + "个番茄");

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
                    if (screenOn) {
                        handler.sendEmptyMessage(MSG_TIME_TICK_UI);
                    }
                    if (mCurrentProgress >= mTotalProgress) {
                        handler.sendEmptyMessage(MSG_TIME_IS_UP);
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

        currentPomodoriNumber += 0.5;

        //判断当前执行的是工作还是休息状态
        if (currentSituation == WORK_TIME_SITUATION) {

            //更新番茄个数
            txNumber.setText((int) (currentPomodoriNumber + 1) + "个番茄/" + totlePomodoriRepeat + "个番茄");

            //判断是否已经完成番茄
            if (totlePomodoriRepeat - 1 == (int) currentPomodoriNumber) {

                //震动和铃音
                mediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.break_sound);
                if (isSound) {
                    mediaPlayer.start();
                }
                if (isVibrate) {
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
                        endPomodori();
                    }
                }).show();
            } else {

                //震动和铃音
                mediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.break_sound);
                if (isSound) {
                    mediaPlayer.start();
                }
                if (isVibrate) {
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
            if (isVibrate) {
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
            txNumber.setText((int) (currentPomodoriNumber + 1) + "个番茄/" + totlePomodoriRepeat + "个番茄");
            currentSituation = WORK_TIME_SITUATION;
            mTotalProgress = workMinutes;
            mTasksView.setmTotalProgress(mTotalProgress);
            mCurrentProgress = 0;
            txStart.setText(pomodoriName);
            startTimer();
        }
    }

    /*
    番茄终止时的处理方法
     */
    private void endPomodori() {

        //对Pomodori对象标记完成状况
        if (currentPomodoriNumber == (double) totlePomodoriRepeat - 0.5) {
            numberOfFinish++;
            pomodori.setNumberOfFinish(numberOfFinish);
        } else {
            numberOfUnfinish++;
            pomodori.setNumberOfUnfinish(numberOfUnfinish);
        }

        //记录响铃和震动设置
        pomodori.setSound(isSound);
        pomodori.setVibrate(isVibrate);

        //序列化Pomodori对象
        String pomodoriString = SerializableHelper.setPomodoriToShare(pomodori);
        Intent intent = new Intent();
        intent.putExtra("pomodori_back", pomodoriString);

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
                case MSG_TIME_TICK_UI:
                    if (isTouchPause) {
                        mTasksView.setProgressWithStroke(mTotalProgress - mCurrentProgress);
                    } else {
                        mTasksView.setProgress(mTotalProgress - mCurrentProgress);
                    }
                    int minute = (mTotalProgress - mCurrentProgress) / 1000 / 60;
                    int second = (mTotalProgress - mCurrentProgress) / 1000 % 60;
                    txCounter.setText(minute + ":" + df.format(second));
                    break;
                case MSG_TIME_IS_UP:
                    stopTimer();
                    timeIsUpEvent();
                    break;
                case MSG_PRESS_BACK_BUTTON:
                    stopTimer();
                    new AlertDialog.Builder(AlarmActivity.this).setCancelable(false).setMessage("你确定要结束这个番茄吗").setPositiveButton("结束番茄", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            endPomodori();
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

    class ScreenTurnOffReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            screenOn = false;
        }
    }

    class ScreenTurnOnReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            screenOn = true;
        }
    }
}
