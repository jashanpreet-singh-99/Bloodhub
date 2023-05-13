package ck.dev.students.bloodhub.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import ck.dev.students.bloodhub.R;
import ck.dev.students.bloodhub.model.User;
import ck.dev.students.bloodhub.utils.Config;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends Activity {

    private int backButtonCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_splashscreen);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser != null){
            showProgress(true);
            setView(currentUser.getUid());
        } else {
            setContentView(R.layout.layout_first_screen);
            Button button = findViewById(R.id.request_btn);
            button.setOnClickListener(view -> {
                Intent intent = new Intent(SplashScreen.this,
                        EmergencyRequestActivity.class);
                startActivity(intent);
            });
            Button button1 = findViewById(R.id.login_btn);
            button1.setOnClickListener(view -> {
                Intent intent = new Intent(SplashScreen.this,LoginActivity.class);
                startActivity(intent);
            });
        }
    }

    private void setView(String userId) {
        FirebaseDatabase.getInstance().getReference().child(Config.KEY_USER).child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        String account_type = Objects.requireNonNull(user).account_type;
                        showProgress(false);
                        if(account_type.equals(Config.KEY_TYPE_INDIVIDUAL)) {
                            startActivity(new Intent(SplashScreen.this, MainActivity.class));
                        } else if(account_type.equals(Config.KEY_TYPE_ORGANIZATION)) {
                            startActivity(new Intent(SplashScreen.this, MainActivityOrg.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    void showProgress(boolean stat) {
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        if (stat) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (backButtonCount <= 0) {
            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        } else {
            super.onBackPressed();
        }
    }
}
