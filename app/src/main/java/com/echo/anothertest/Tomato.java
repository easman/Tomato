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

    public Tomato(int workMinutes, int breakMinutes, int totleTamatoRepeat, String jobDescription) {
        this.breakMinutes = breakMinutes;
        this.workMinutes = workMinutes;
        this.totleTamatoRepeat = totleTamatoRepeat;
        this.jobDescription = jobDescription;
        this.numberOfFinish = 0;
        this.numberOfUnfinish = 0;
        this.existSituation = true;
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
