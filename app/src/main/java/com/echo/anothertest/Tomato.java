package com.echo.anothertest;

import java.io.Serializable;

/**
 * Created by Echo
 */

public class Tomato implements Serializable {
    private static final long serialVersionUID = -8806993886819372269L;
    private int workMinutes;
    private int breakMinutes;
    private int totleTamatoRepeat;
    private String jobDescription;
    private int numberOfFinish;
    private int numberOfUnfinish;
    private boolean existSituation;
    private boolean isSound = true;
    private boolean isWave = true;

    public Tomato(int workMinutes, int breakMinutes, int totleTamatoRepeat, String jobDescription,boolean isSound,boolean isWave) {
        this.breakMinutes = breakMinutes;
        this.workMinutes = workMinutes;
        this.totleTamatoRepeat = totleTamatoRepeat;
        this.jobDescription = jobDescription;
        this.numberOfFinish = 0;
        this.numberOfUnfinish = 0;
        this.isSound = isSound;
        this.isWave = isWave;
        this.existSituation = true;

    }

    public boolean isSound() {
        return isSound;
    }

    public void setSound(boolean sound) {
        isSound = sound;
    }

    public boolean isWave() {
        return isWave;
    }

    public void setWave(boolean wave) {
        isWave = wave;
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

    public int getTotleTamatoRepeat() {
        return totleTamatoRepeat;
    }

    public void setTotleTamatoRepeat(int totleTamatoRepeat) {
        this.totleTamatoRepeat = totleTamatoRepeat;
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
