package ck.dev.students.bloodhub.utils;

import android.util.Log;

public class Config {

    public static final String TAG_LOGIN    = "message_activity_login";
    public static final String TAG_REGISTER = "message_activity_register";
    public static final String TAG_FIREBASE = "message_firebase";
    public static final String TAG_DATE     = "message_date";
    public static final String TAG_APPOINTMENT = "message_appointment";



    public static final String KEY_USER              = "Users";
    public static final String KEY_TYPE_INDIVIDUAL   = "individual";
    public static final String KEY_TYPE_ORGANIZATION = "organization";
    public static final String KEY_ACCOUNT_TYPE      = "account_type";
    public static final String KEY_APPOINTMENTS      = "Appointments";
    public static final String KEY_REQUESTS          = "Requests";

    public static final String KEY_ACT_APP_BAR = "activity_app_bar";

    private static final boolean DEBUG = true;

    public static void Log(String TAG, String msg, boolean error) {
        if (!DEBUG) {
            return;
        }
        if (error) {
            Log.e(TAG, msg);
        } else {
            Log.d(TAG, msg);
        }
    }

}
