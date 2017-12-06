package com.asociadosmonterrubio.admin.utils;

import android.util.Log;

import com.asociadosmonterrubio.admin.models.BlackListUser;

import java.util.ArrayList;

/**
 * Created by joseluissanchezcruz on 12/5/17.
 */

public class BlackListSingleton {
    private static final BlackListSingleton ourInstance = new BlackListSingleton();
    private ArrayList<BlackListUser> blackListUsers = new ArrayList<>();

    public static BlackListSingleton getInstance() {
        return ourInstance;
    }

    private BlackListSingleton() { }

    public void addBlackListUser(BlackListUser blackListUser){
        blackListUsers.add(blackListUser);
    }

    public BlackListUser getBlackListUser(String curp){
        for(BlackListUser blackListUser : blackListUsers){
            Log.d("BlackListUserCURP", blackListUser.getCURP().trim());
            if (blackListUser.getCURP().equals(curp))
                return blackListUser;
        }
        return null;
    }
}
