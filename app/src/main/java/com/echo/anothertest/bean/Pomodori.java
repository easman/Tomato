package com.echo.anothertest.bean;

import java.io.Serializable;

/**
 * Created by Echo
 */

public class Pomodori implements Serializable {
    private static final long serialVersionUID = -8806993886819372269L;
    private int workMinutes;
    private int breakMinutes;
    private int totlePomodoriRepeat;
    private String jobDescription;
    private int numberOfFinish;
    private int numberOfUnfinish;
    private boolean existSituation;
    private boolean isSound = true;
    private boolean isVibrate = true;

    public Pomodori(int workMinutes, int breakMinutes, int totlePomodoriRepeat, String jobDescription, boolean isSound, boolean isVibrate) {
        this.breakMinutes = breakMinutes;
        this.workMinutes = workMinutes;
        this.totlePomodoriRepeat = totlePomodoriRepeat;
        this.jobDescription = jobDescription;
        this.numberOfFinish = 0;
        this.numberOfUnfinish = 0;
        this.isSound = isSound;
        this.isVibrate = isVibrate;
        this.existSituation = true;

    }

    public boolean isSound() {
        return isSound;
    }

    public void setSound(boolean sound) {
        isSound = sound;
    }

    public boolean isVibrate() {
        return isVibrate;
    }

    public void setVibrate(boolean vibrate) {
        isVibrate = vibrate;
    }

    public int getWorkMinutes() {
        return workMinutes;
    }

    public void setWorkMinutes(int workMinutes) {
        this.workMinutes = workMinutes;
    }

    public int getBreakMinutes() {
        return breakMinutes;
    }

    public void setBreakMinutes(int breakMinutes) {
        this.breakMinutes = breakMinutes;
    }

    public int getTotlePomodoriRepeat() {
        return totlePomodoriRepeat;
    }

    public void setTotlePomodoriRepeat(int totlePomodoriRepeat) {
        this.totlePomodoriRepeat = totlePomodoriRepeat;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public int getNumberOfFinish() {
        return numberOfFinish;
    }

    public void setNumberOfFinish(int numberOfFinish) {
        this.numberOfFinish = numberOfFinish;
    }

    public int getNumberOfUnfinish() {
        return numberOfUnfinish;
    }

    public void setNumberOfUnfinish(int numberOfUnfinish) {
        this.numberOfUnfinish = numberOfUnfinish;
    }

    public void setExistSituation(boolean existSituation) {
        this.existSituation = existSituation;
    }

    public boolean isExistSituation() {
        return existSituation;
    }
}
