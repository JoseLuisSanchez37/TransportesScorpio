package com.asociadosmonterrubiotest.admin.utils;

import android.text.TextUtils;

import java.util.Calendar;

/**
 * Created by joseluissanchezcruz on 8/26/17.
 */

public class Session {

    public static boolean isSessionValid(){
        String last_date_stored = UserPreferences.getPreference(UserPreferences.LAST_SESSION_DATE);
        Calendar c = Calendar.getInstance();

        if (!TextUtils.isEmpty(last_date_stored)){

            Calendar calendarLastCurrentDate = (Calendar) c.clone();

            String[] split_last_date_stored = last_date_stored.split("-");
            calendarLastCurrentDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(split_last_date_stored[0]));
            calendarLastCurrentDate.set(Calendar.MONTH, Integer.parseInt(split_last_date_stored[1]));
            calendarLastCurrentDate.set(Calendar.YEAR, Integer.parseInt(split_last_date_stored[2]));

            long currentTimeInMillis = c.getTimeInMillis();
            long lastDateTimeInMillis = calendarLastCurrentDate.getTimeInMillis();

            return currentTimeInMillis == lastDateTimeInMillis;

        }else {
            if (!TextUtils.isEmpty(UserPreferences.getUserSession().getRol())) {
                String current_date = c.get(Calendar.DAY_OF_MONTH) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.YEAR);
                UserPreferences.savePreference(UserPreferences.LAST_SESSION_DATE, current_date);
                return true;
            }else
                return false;
        }
    }

}
