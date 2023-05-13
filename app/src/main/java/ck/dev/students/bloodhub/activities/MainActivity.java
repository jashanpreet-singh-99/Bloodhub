package ck.dev.students.bloodhub.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import ck.dev.students.bloodhub.R;
import ck.dev.students.bloodhub.utils.Config;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout profileBtn;
    private RelativeLayout requestBloodBtn;
    private RelativeLayout bookAppointmentBtn;
    private RelativeLayout viewAppointmentBtn;
    private Button         logoutBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        initView();
    }

    private void initView() {
        profileBtn         = findViewById(R.id.profile_btn);
        requestBloodBtn    = findViewById(R.id.blood_request_btn);
        bookAppointmentBtn = findViewById(R.id.appointment_btn);
        viewAppointmentBtn = findViewById(R.id.view_appointments_btn);
        logoutBtn          = findViewById(R.id.logout_btn);

        onClick();
    }

    private void onClick() {
        profileBtn.setOnClickListener((v) -> startActivity(new Intent(MainActivity.this, ProfileView.class)));

        requestBloodBtn.setOnClickListener((v) -> {
            Intent intent = new Intent(MainActivity.this, AddEmergencyRequestActivity.class);
            intent.putExtra(Config.KEY_ACT_APP_BAR, true);
            startActivity(intent);
        });

        bookAppointmentBtn.setOnClickListener((v) -> startActivity(new Intent(MainActivity.this, AddAppointmentActivity.class)));

        viewAppointmentBtn.setOnClickListener((v) -> startActivity(new Intent(MainActivity.this, ViewAppointmentsActivity.class)));

        logoutBtn.setOnClickListener((v) -> {
            Config.Log(Config.TAG_LOGIN, "Trying to Logout.", false);
            FirebaseAuth.getInstance().signOut();
            Config.Log(Config.TAG_LOGIN, "Logged Out.", false);
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });
    }
}