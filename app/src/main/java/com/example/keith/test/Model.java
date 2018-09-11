package com.example.keith.test;

/**
 * Created by Keith on 29/10/2017.
 */

public class Model{
    int id;
    String name;
    String desc;
    Long dueDate;
    int status; /* 0 -&gt; checkbox disable, 1 -&gt; checkbox enable */
    int cat;
    Long doneDate;

    Model(int id, String name, String desc, Long dueDate, int status, int cat, Long doneDate){
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.dueDate = dueDate;
        this.status = status;
        this.cat = cat;
        this.doneDate = doneDate;
    }
    public int getID(){return this.id;}
    public String getName(){return this.name;}
    public String getDesc(){return this.desc;}
    public Long getDueDate(){return this.dueDate;}
    public int getStatus(){return this.status;}
    public int getCat(){return this.cat;}
    public Long getDoneDate(){return this.doneDate;}

}