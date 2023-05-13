package ck.dev.students.bloodhub.model;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import ck.dev.students.bloodhub.utils.Config;

public class Appointment {

    public String userid, orgId;
    public long date, created_at;
    public Boolean transport, confirmed;

    public Appointment() { }

    public Appointment(String userid, String orgId, String date, String time, Boolean transport) {
        this.userid = userid;
        this.orgId = orgId;
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm aa");
        try {
            this.date = Objects.requireNonNull(df.parse(date + " " + time)).getTime();
        } catch (Exception e){
            Config.Log(Config.TAG_DATE, "Error in date : " + e.getMessage(), true);
        }
        this.transport = transport;
        this.created_at = Calendar.getInstance().getTime().getTime();
        this.confirmed = false;
    }

}
