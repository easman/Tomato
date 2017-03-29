package com.echo.anothertest;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TasksCompletedView mTasksView;
    private TextView tx1, tx2;
    private Button button2, button3;
    private int mTotalProgress;
    private int mCurrentProgress;
    private boolean suspended = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initVariable();
        initView();

        tx2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                suspend();
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
                        tx2.setText("长按取消");
                        tx2.setTextColor(0xFFFFFFFF);
                        break;

                }
                return false;
            }
        });

        tx1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tx1.setVisibility(View.INVISIBLE);
                tx2.setVisibility(View.VISIBLE);
                suspended = false;
                new Thread(new ProgressRunable()).start();
            }
        });

    }

    private void initVariable() {
        mTotalProgress = 10000;
        mCurrentProgress = 0;
    }

    private void initView() {
        mTasksView = (TasksCompletedView) findViewById(R.id.tasks_view);
        tx1 = (TextView) findViewById(R.id.tx1);
        tx2 = (TextView) findViewById(R.id.tx2);
        tx1.setVisibility(View.VISIBLE);
        tx2.setVisibility(View.INVISIBLE);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        mTasksView.setmTotalProgress(mTotalProgress);
    }

    //暂停功能方法
    private void suspend() {
        suspended = true;
    }

    //继续的方法
    private synchronized void resume() {
        suspended = false;
        notify();
    }

    class ProgressRunable implements Runnable {


        @Override
        public void run() {
            while (mCurrentProgress < mTotalProgress) {
                mCurrentProgress += 1;
                mTasksView.setProgress(mCurrentProgress);
                try {
                    Thread.sleep(1);
                    //检查暂停变量
                    synchronized (this) {
                        while (suspended) {
                            wait();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


    }
}
