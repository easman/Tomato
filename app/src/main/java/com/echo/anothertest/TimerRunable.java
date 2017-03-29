package com.echo.anothertest;

/**
 * Created by Echo on 2017/3/29.
 */

public class TimerRunable implements Runnable {

    private int mCurrentProgress;
    private int mTotalProgress;
    private boolean suspended;
    private boolean isUp;
    private TasksCompletedView mTasksView;

    //构造一个新的计时器来更新指定UI
    public TimerRunable(int mCurrentProgress,
                        int mTotalProgress,
                        TasksCompletedView mTasksView) {
        this.mCurrentProgress = mCurrentProgress;
        this.mTotalProgress = mTotalProgress;
        this.mTasksView = mTasksView;
        this.suspended = false;
    }

    public void suspend(boolean suspended) {
        this.suspended = suspended;
    }

    public synchronized void resume(){
        suspended = false;
        notify();
    }

    public void setIsUp(boolean isUp) {
        this.isUp = isUp;
    }

    public boolean getIsUp() {
        return isUp;
    }

    public void resetProgress(){
        mCurrentProgress = 0;
        mTasksView.setProgress(mCurrentProgress);
    }

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
        setIsUp(true);

    }


}
