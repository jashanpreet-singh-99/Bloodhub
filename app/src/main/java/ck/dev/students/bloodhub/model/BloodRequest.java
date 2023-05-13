package ck.dev.students.bloodhub.model;

import java.util.Calendar;

public class BloodRequest {

    public String userid, name, blood_group, quantity, number, location, diagnosis;
    public Boolean completed, transport;
    public long date, created_at;

    public BloodRequest() {}

    public BloodRequest(String userid, String name, String blood_group, String quantity, String number, String location, String diagnosis, long date, Boolean transport) {
        this.userid = userid;
        this.name = name;
        this.blood_group = blood_group;
        this.quantity = quantity;
        this.number = number;
        this.location = location;
        this.diagnosis = diagnosis;
        this.date = date;
        this.transport = transport;
        this.completed = false;
        this.created_at = Calendar.getInstance().getTime().getTime();
    }

}
