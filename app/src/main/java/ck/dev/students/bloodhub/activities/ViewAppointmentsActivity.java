package ck.dev.students.bloodhub.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import ck.dev.students.bloodhub.R;
import ck.dev.students.bloodhub.adapter.AppointmentsAdapter;
import ck.dev.students.bloodhub.model.Appointment;
import ck.dev.students.bloodhub.model.User;
import ck.dev.students.bloodhub.utils.Config;

public class ViewAppointmentsActivity  extends Activity {

    private ImageButton backBtn;
    private RecyclerView appointmentView;
    private ProgressBar  progressBar;

    private AppointmentsAdapter appointmentsAdapter;

    private final ArrayList<Appointment> appointmentsDataList = new ArrayList<>();
    private final ArrayList<User> userList = new ArrayList<>();
    private final ArrayList<String> userKeyList = new ArrayList<>();;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_view_appointments);

        initView();
    }

    private void initView() {
        backBtn = findViewById(R.id.back_btn);
        progressBar = findViewById(R.id.progress_bar);
        appointmentView = findViewById(R.id.appointments_view);

        fetchFirebaseData();

        appointmentsAdapter = new AppointmentsAdapter(appointmentsDataList, userList, userKeyList);
        appointmentView.setLayoutManager(new LinearLayoutManager(ViewAppointmentsActivity.this));
        appointmentView.setAdapter(appointmentsAdapter);

        onClick();
    }

    private void onClick() {
        backBtn.setOnClickListener((v) -> {
            finish();
        });
    }

    private void fetchFirebaseData() {
        DatabaseReference databaseReferenceU = FirebaseDatabase.getInstance().getReference(Config.KEY_USER);
        DatabaseReference databaseReferenceA = FirebaseDatabase.getInstance().getReference(Config.KEY_APPOINTMENTS);

        databaseReferenceU.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (progressBar.getVisibility() == View.GONE)
                    progressBar.setVisibility(View.VISIBLE);
                userList.clear();
                userKeyList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    userList.add(user);
                    userKeyList.add(dataSnapshot.getKey());
                    Config.Log(Config.TAG_FIREBASE, "Appointment : " + Objects.requireNonNull(user).username, false);
                }
                appointmentsAdapter.notifyDataSetChanged();
                if (progressBar.getVisibility() == View.VISIBLE)
                    progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewAppointmentsActivity.this, "Server Error.", Toast.LENGTH_SHORT).show();
                Config.Log(Config.TAG_FIREBASE, "Error while fetching the Appointments data : " + error.getMessage(), true);
            }
        });

        databaseReferenceA.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (progressBar.getVisibility() == View.GONE)
                    progressBar.setVisibility(View.VISIBLE);
                appointmentsDataList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Appointment appointment = dataSnapshot.getValue(Appointment.class);
                    appointmentsDataList.add(appointment);
                    Config.Log(Config.TAG_FIREBASE, "Appointment : " + Objects.requireNonNull(appointment).userid, false);
                }
                appointmentsAdapter.notifyDataSetChanged();
                if (progressBar.getVisibility() == View.VISIBLE)
                    progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewAppointmentsActivity.this, "Server Error.", Toast.LENGTH_SHORT).show();
                Config.Log(Config.TAG_FIREBASE, "Error while fetching the Appointments data : " + error.getMessage(), true);
            }
        });
    }
}
