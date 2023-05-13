package ck.dev.students.bloodhub.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import ck.dev.students.bloodhub.R;
import ck.dev.students.bloodhub.adapter.AppointmentsAdapter;
import ck.dev.students.bloodhub.adapter.RequestsAdapter;
import ck.dev.students.bloodhub.model.Appointment;
import ck.dev.students.bloodhub.model.BloodRequest;
import ck.dev.students.bloodhub.model.User;
import ck.dev.students.bloodhub.utils.Config;


public class MainActivityOrg extends AppCompatActivity {

    private Button profileBtn;
    private Button logoutBtn;
    private Button requestBtn;
    private Button appointmentBtn;

    private ProgressBar progressBar;

    private RecyclerView requestsView;
    private RecyclerView appointmentsView;

    private final ArrayList<Appointment> appointmentsData = new ArrayList<>();
    private final ArrayList<BloodRequest> requestsData = new ArrayList<>();
    private final ArrayList<User> userList = new ArrayList<>();
    private final ArrayList<String> userKeyList = new ArrayList<>();

    private AppointmentsAdapter appointmentsAdapter;
    private RequestsAdapter     requestsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main_org);
        initView();
    }

    private void initView() {
        requestBtn     = findViewById(R.id.request_btn);
        appointmentBtn = findViewById(R.id.appointment_btn);

        profileBtn     = findViewById(R.id.profile_btn);
        logoutBtn      = findViewById(R.id.logout_btn);

        requestsView     = findViewById(R.id.requests_view);
        appointmentsView = findViewById(R.id.appointments_view);

        progressBar      = findViewById(R.id.progress_bar);

        appointmentsAdapter = new AppointmentsAdapter(appointmentsData, userList, userKeyList);
        appointmentsView.setLayoutManager(new LinearLayoutManager(MainActivityOrg.this));
        appointmentsView.setAdapter(appointmentsAdapter);

        requestsAdapter = new RequestsAdapter(requestsData);
        requestsView.setLayoutManager(new LinearLayoutManager(MainActivityOrg.this));
        requestsView.setAdapter(requestsAdapter);

        getFirebaseData();
        onClick();
    }

    private void onClick() {

        profileBtn.setOnClickListener((v) -> startActivity(new Intent(MainActivityOrg.this, ProfileView.class)));

        logoutBtn.setOnClickListener((v) -> {
            Config.Log(Config.TAG_LOGIN, "Trying to Logout.", false);
            FirebaseAuth.getInstance().signOut();
            Config.Log(Config.TAG_LOGIN, "Logged Out.", false);
            startActivity(new Intent(MainActivityOrg.this, LoginActivity.class));
        });

        requestBtn.setOnClickListener((v) -> {
            visualBtnSwitch(true);
        });

        appointmentBtn.setOnClickListener((v) -> {
            visualBtnSwitch(false);
        });
    }

    private void visualBtnSwitch(Boolean request) {
        if (request) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.fab1, null)));
                appointmentBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.fab2, null)));
            }
            requestsView.setVisibility(View.VISIBLE);
            appointmentsView.setVisibility(View.GONE);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                appointmentBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.fab1, null)));
                requestBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.fab2, null)));
            }
            requestsView.setVisibility(View.GONE);
            appointmentsView.setVisibility(View.VISIBLE);
        }
    }

    private void getFirebaseData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child(Config.KEY_USER).addListenerForSingleValueEvent(new ValueEventListener() {
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
                Toast.makeText(MainActivityOrg.this, "Server Error.", Toast.LENGTH_SHORT).show();
                Config.Log(Config.TAG_FIREBASE, "Error while fetching the Appointments data : " + error.getMessage(), true);
            }
        });

        databaseReference.child(Config.KEY_APPOINTMENTS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (progressBar.getVisibility() == View.GONE)
                    progressBar.setVisibility(View.VISIBLE);
                appointmentsData.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Appointment appointment = dataSnapshot.getValue(Appointment.class);
                    appointmentsData.add(appointment);
                    Config.Log(Config.TAG_FIREBASE, "Appointment : " + Objects.requireNonNull(appointment).userid, false);
                }
                appointmentsAdapter.notifyDataSetChanged();
                if (progressBar.getVisibility() == View.VISIBLE)
                    progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivityOrg.this, "Server Error.", Toast.LENGTH_SHORT).show();
                Config.Log(Config.TAG_FIREBASE, "Error while Fetching appointment : " + error.getMessage(), true);
            }
        });

        databaseReference.child(Config.KEY_REQUESTS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (progressBar.getVisibility() == View.GONE)
                    progressBar.setVisibility(View.VISIBLE);
                requestsData.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    BloodRequest request = dataSnapshot.getValue(BloodRequest.class);
                    requestsData.add(request);
                    Config.Log(Config.TAG_FIREBASE, "Requests : " + Objects.requireNonNull(request).name, false);
                }
                requestsAdapter.notifyDataSetChanged();
                if (progressBar.getVisibility() == View.VISIBLE)
                    progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivityOrg.this, "Server Error.", Toast.LENGTH_SHORT).show();
                Config.Log(Config.TAG_FIREBASE, "Error while Fetching requests : " + error.getMessage(), true);
            }
        });
    }
}