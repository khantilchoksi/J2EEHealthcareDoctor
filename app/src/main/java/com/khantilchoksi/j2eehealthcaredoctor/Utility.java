package com.khantilchoksi.j2eehealthcaredoctor;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Khantil on 22-03-2017.
 */

public class Utility {

    public static boolean isMobileValid(String mobileNo) {

        //return ( (mobileNo.contains("[1-9][0-9]+")) && (mobileNo.length() == 10));

        return (mobileNo.length() == 10 && (mobileNo.matches("[1-9][0-9]+")));
    }

    public static int getDoctorId(Context context){
        SharedPreferences settings = context.getSharedPreferences(context.getResources().getString(R.string.login_pref), 0);

        //Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
        return settings.getInt(context.getString(R.string.pref_doctor_Id), 0);
    }

    public static String getPatientFullName(Context context){
        SharedPreferences settings = context.getSharedPreferences(context.getResources().getString(R.string.login_pref), 0);

        //Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
        return settings.getString(context.getString(R.string.pref_full_name), null);
    }

    public static String getPatientMobileNumber(Context context){
        SharedPreferences settings = context.getSharedPreferences(context.getResources().getString(R.string.login_pref), 0);

        //Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
        return settings.getString(context.getString(R.string.pref_mobile_no), null);
    }

}
