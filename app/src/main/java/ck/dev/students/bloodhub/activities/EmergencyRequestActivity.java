package ck.dev.students.bloodhub.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

import ck.dev.students.bloodhub.R;

public class EmergencyRequestActivity extends Activity {

    private Button makeRequestBtn;
    private Button manageRequestBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_emergency_request);

        initView();
    }

    private void initView() {

        makeRequestBtn   = findViewById(R.id.make_request_btn);
        manageRequestBtn = findViewById(R.id.manage_btn);

        onClick();
    }

    private void onClick() {
        makeRequestBtn.setOnClickListener((v) -> {
            startActivity(new Intent(EmergencyRequestActivity.this, AddEmergencyRequestActivity.class));
        });

        manageRequestBtn.setOnClickListener((v) -> {
            Toast.makeText(getApplicationContext(), "Please, login to manage requests.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(EmergencyRequestActivity.this, SplashScreen.class));
        });
    }
}
