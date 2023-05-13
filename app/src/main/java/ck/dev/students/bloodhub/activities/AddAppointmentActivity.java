package ck.dev.students.bloodhub.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import ck.dev.students.bloodhub.R;
import ck.dev.students.bloodhub.model.Appointment;
import ck.dev.students.bloodhub.model.User;
import ck.dev.students.bloodhub.utils.Config;

public class AddAppointmentActivity extends Activity {

    private FirebaseUser firebaseUser;

    private Spinner orgSpinner;
    private EditText dateTxtView;
    private EditText timeTxtView;

    private RadioGroup transportGroup;

    private Button submitRequestBtn;
    private ImageButton backBtn;

    private ArrayList<String> hospitals;
    private ArrayList<String> keys;

    private String time = "";
    private String date = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_appointment);
        initView();
    }

    private void initView() {
        orgSpinner       = findViewById(R.id.org_spinner);
        dateTxtView      = findViewById(R.id.date);
        timeTxtView      = findViewById(R.id.time);
        transportGroup   = findViewById(R.id.transport);
        submitRequestBtn = findViewById(R.id.request_btn);
        backBtn          = findViewById(R.id.back_btn);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        onClick();
    }

    private void onClick() {
        hospitals = new ArrayList<>();
        keys      = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child(Config.KEY_USER)
                .orderByChild(Config.KEY_ACCOUNT_TYPE).equalTo(Config.KEY_TYPE_ORGANIZATION)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot child: snapshot.getChildren()) {
                            User user = child.getValue(User.class);
                            hospitals.add(Objects.requireNonNull(user).username);
                            keys.add(child.getKey());
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                getApplicationContext(),
                                R.layout.item_spinner_view,
                                hospitals);
                        adapter.setDropDownViewResource(R.layout.item_spinner_view);
                        Config.Log(Config.TAG_APPOINTMENT, "Organization added.", false);
                        orgSpinner.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Config.Log(Config.TAG_FIREBASE, "Error Hospital List : " + error.getMessage(), true);
                    }
                });

        dateTxtView.setOnClickListener((v) -> {
            Dialog dialog = new Dialog(AddAppointmentActivity.this);
            dialog.setTitle("Set Date");
            dialog.setContentView(R.layout.dialog_date);
            dialog.show();

            final Button setDateBtn     = dialog.findViewById(R.id.set_date_btn);
            final DatePicker datePicker = dialog.findViewById(R.id.date_picker);
            datePicker.setMinDate(System.currentTimeMillis() - 1000);
            setDateBtn.setOnClickListener((view) -> {
                int day   = datePicker.getDayOfMonth();
                int month = datePicker.getMonth();
                int year  = datePicker.getYear();
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                Date pDate = calendar.getTime();
                date = DateFormat.getDateInstance().format(pDate);
                dateTxtView.setText(date);
                dialog.cancel();
            });
        });

        timeTxtView.setOnClickListener((v) -> {
            Dialog dialog = new Dialog(AddAppointmentActivity.this);
            dialog.setTitle("Set Time");
            dialog.setContentView(R.layout.dialog_time);
            dialog.show();
            final Button setTime = dialog.findViewById(R.id.set_time_btn);
            final TimePicker timePicker = dialog.findViewById(R.id.time_picker);
            setTime.setOnClickListener(view -> {
                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();

                if (hour >= 12){
                    time = "PM";
                } else {
                    time = "AM";
                }
                if (hour > 12){
                    hour = hour - 12;
                }
                if (minute > 10){
                    time = minute + " " + time;
                } else {
                    time = "0" + minute + " " + time;
                }
                if (hour > 10){
                    time = hour + ":" + time;
                } else {
                    time = "0" + hour + ":" + time;
                }
                timeTxtView.setText(time);
                dialog.cancel();
            });
        });

        backBtn.setOnClickListener((v) -> {
            finish();
        });

        submitRequestBtn.setOnClickListener((v) -> {
            try {
                String dateTxt = dateTxtView.getText().toString();
                if (TextUtils.isEmpty(dateTxt)) {
                    dateTxtView.setError("Date is missing.");
                    return;
                }

                String timeTxt = timeTxtView.getText().toString();
                if (TextUtils.isEmpty(timeTxt)) {
                    timeTxtView.setError("Time is missing.");
                    return;
                }

                RadioButton radioButton = findViewById(transportGroup.getCheckedRadioButtonId());
                String transportTxt = radioButton.getText().toString();
                boolean transport = transportTxt.equals("Yes");
                Appointment appointment  = new Appointment(
                        firebaseUser.getUid(),
                        keys.get(orgSpinner.getSelectedItemPosition()),
                        dateTxt,
                        timeTxt,
                        transport
                );
                FirebaseDatabase.getInstance().getReference().child(Config.KEY_APPOINTMENTS).push().setValue(appointment);
                Dialog dialog = new Dialog(AddAppointmentActivity.this);
                dialog.setTitle("Submit Request");
                dialog.setContentView(R.layout.dialog_appointment_submit);
                dialog.show();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                final Button requestBtn = dialog.findViewById(R.id.ok_btn);
                requestBtn.setOnClickListener((vi) -> {
                    startActivity(new Intent(AddAppointmentActivity.this, MainActivity.class));
                });
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Server Error.", Toast.LENGTH_SHORT).show();
                Config.Log(Config.TAG_FIREBASE, "Appointment Error : " + e.getMessage(), true);
            }
        });
    }
}
