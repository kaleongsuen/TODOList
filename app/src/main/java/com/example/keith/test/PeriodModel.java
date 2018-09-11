package com.example.keith.test;

/**
 * Created by Keith on 1/8/2018.
 */

public class PeriodModel {
    long lastPeriodDate;
    int period;
    int id;
    PeriodModel(int id, long lastPeriodDate, int period){
        this.id = id;
        this.lastPeriodDate = lastPeriodDate;
        this.period = period;
    }
    public int getID(){return this.id;}
    public long getLastPeriodDate(){return this.lastPeriodDate;}
    public int getPeriod(){return this.period;}

}
