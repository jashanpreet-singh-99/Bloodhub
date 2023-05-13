package ck.dev.students.bloodhub.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Calendar;

import ck.dev.students.bloodhub.utils.Config;

@IgnoreExtraProperties
public class User {

    public String username, email, account_type, blood_group, number, address;
    public long created_at;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, String number, String blood_group, String accType) {
        this.username = username;
        this.email = email;
        this.account_type = accType;
        this.number = number;
        this.blood_group = blood_group;
        this.created_at = Calendar.getInstance().getTime().getTime();
    }

    public User(String username, String email, String number, String address) {
        this.username = username;
        this.email = email;
        this.account_type = Config.KEY_TYPE_ORGANIZATION;
        this.number = number;
        this.address = address;
        this.created_at = Calendar.getInstance().getTime().getTime();
    }
}
