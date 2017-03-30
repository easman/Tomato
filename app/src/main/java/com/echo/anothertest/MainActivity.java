package com.echo.anothertest;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TasksCompletedView mTasksView;
    private TextView tx1, tx2, txStart;
    private Button resetButton;
    private int mTotalProgress;
    private int mCurrentProgress;
//    private boolean suspended;
    private boolean txStartHasNotClicked;
    private boolean firstTimeRunning;
//    private boolean isTomato;
//    private int TomatoTime ;
//    private int mTotalRelaxProgress;
//    private int getmCurrentRelaxProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initVariable();
        initView();
        final TimerRunable timerRunable =new TimerRunable(mCurrentProgress,mTotalProgress,mTasksView);

        txStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txStartHasNotClicked){
                    tx2.setVisibility(View.VISIBLE);
                    txStart.setText("Tic Tok =。=");
                    txStartHasNotClicked = false;
                    if(firstTimeRunning){
                        firstTimeRunning = false;
                        new Thread(timerRunable).start();
                    }else {
                        timerRunable.resume();
                    }
                }
            }
        });

        tx1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tx1.setVisibility(View.INVISIBLE);
                tx2.setVisibility(View.VISIBLE);
                timerRunable.resume();

            }
        });

        tx2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                timerRunable.suspend(true);
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

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerRunable.suspend(true);
                timerRunable.resetProgress();
                txStartHasNotClicked = true;
                txStart.setText("开始番茄");
                tx1.setVisibility(View.INVISIBLE);
                tx2.setVisibility(View.INVISIBLE);

            }
        });
    }

    private void initVariable() {
        mTotalProgress = 10000;
        mCurrentProgress = 0;
        txStartHasNotClicked = true;
        firstTimeRunning= true;
    }

    private void initView() {
        resetButton = (Button) findViewById(R.id.reset_button);
        mTasksView = (TasksCompletedView) findViewById(R.id.tasks_view);
        tx1 = (TextView) findViewById(R.id.tx1);
        tx2 = (TextView) findViewById(R.id.tx2);
        txStart = (TextView) findViewById(R.id.txStart);
        tx1.setVisibility(View.INVISIBLE);
        tx2.setVisibility(View.INVISIBLE);
        mTasksView.setmTotalProgress(mTotalProgress);
    }

}
