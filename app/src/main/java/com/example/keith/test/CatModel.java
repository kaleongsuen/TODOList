package com.example.keith.test;

/**
 * Created by Keith on 9/11/2017.
 */

public class CatModel {
    String cat_name;
    int id;
    CatModel(int id, String cat_name){
        this.id = id;
        this.cat_name = cat_name;
    }
    public int getID(){return this.id;}
    public String getName(){return this.cat_name;}

}
