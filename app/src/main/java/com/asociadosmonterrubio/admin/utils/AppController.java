package com.asociadosmonterrubio.admin.utils;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.asociadosmonterrubio.admin.firebase.FireBaseQuery;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by joseluissanchezcruz on 5/18/17.
 */

public class AppController extends Application {

    public static AppController instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseDatabase.getInstance().getReference(FireBaseQuery.ASISTENCIAS).keepSynced(true);
        FirebaseDatabase.getInstance().getReference(FireBaseQuery.EMPLEADOS).keepSynced(true);
        FirebaseDatabase.getInstance().getReference(FireBaseQuery.SALIDAS).keepSynced(true);
        FirebaseDatabase.getInstance().getReference(FireBaseQuery.INDEX).keepSynced(true);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
