package com.echo.anothertest;

import java.io.Serializable;

/**
 * Created by Echo
 */

public class Tomato implements Serializable {
    private int workMinutes;
    private int breakMinutes;
    private int totleTamatoTime;
    private String jobDescription;
    private int finishTime;
    private int unFinishTime;

    public Tomato(int workMinutes,int breakMinutes,int totleTamatoTime,String jobDescription){
        this.breakMinutes = breakMinutes;
        this.workMinutes = workMinutes;
        this.totleTamatoTime = totleTamatoTime;
        this.jobDescription = jobDescription;
        this.finishTime = 0;
        this.unFinishTime = 0;
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

    public int getTotleTamatoTime() {
        return totleTamatoTime;
    }

    public void setTotleTamatoTime(int totleTamatoTime) {
        this.totleTamatoTime = totleTamatoTime;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public int getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    public int getUnFinishTime() {
        return unFinishTime;
    }

    public void setUnFinishTime(int unFinishTime) {
        this.unFinishTime = unFinishTime;
    }
}
