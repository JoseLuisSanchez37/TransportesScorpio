package com.asociadosmonterrubio.admin.utils;

import android.util.Log;

import com.asociadosmonterrubio.admin.models.ChekListCountryside;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

/**
 * Created by Thomas on 17/08/2017.
 */

public class ChekListCountrysideSingleton {
    private  static final ChekListCountrysideSingleton ourInstance = new ChekListCountrysideSingleton();
    private ArrayList<ChekListCountryside> chekListCountryside = new ArrayList<>();

    public  static  ChekListCountrysideSingleton getInstance(){return  ourInstance;}

    private  ChekListCountrysideSingleton(){}

    public ArrayList<ChekListCountryside> getChekListCountryside(){
        return chekListCountryside;
    }
    public  void add(ChekListCountryside chekListCountryside){
        this.chekListCountryside.add(chekListCountryside);
    }

    public  void setChekListCountryside(DataSnapshot dataSnapshot){
       // this.chekListCountryside = dataSnapshot.getValue(chekListCountryside); // .getValue(ChekListCountryside.class);
    }
}
