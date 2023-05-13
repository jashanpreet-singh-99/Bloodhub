package ck.dev.students.bloodhub.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ck.dev.students.bloodhub.R;
import ck.dev.students.bloodhub.model.User;
import ck.dev.students.bloodhub.utils.Config;

public class ProfileView extends Activity {

    private ImageButton backBtn;
    private TextView nameView;
    private TextView uidView;
    private TextView emailView;
    private TextView contactView;
    private TextView accountTypeView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_profile_view);
        initView();
    }

    private void initView() {
        backBtn = findViewById(R.id.back_btn);
        nameView = findViewById(R.id.name_view);
        uidView  = findViewById(R.id.uid_view);
        emailView = findViewById(R.id.email_view);
        contactView = findViewById(R.id.contact_view);
        accountTypeView = findViewById(R.id.account_type);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uidView.setText(user.getUid());

            FirebaseDatabase.getInstance().getReference(Config.KEY_USER).child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User u = snapshot.getValue(User.class);
                    if (u != null) {
                        nameView.setText(u.username);
                        emailView.setText(u.email);
                        contactView.setText(u.number);
                        accountTypeView.setText(u.account_type);
                    } else {
                        Config.Log(Config.TAG_FIREBASE, "User null", true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ProfileView.this, "Server Error", Toast.LENGTH_SHORT).show();
                    Config.Log(Config.TAG_FIREBASE, "Unable to fetch user data : " + error.getMessage(), true);
                }
            });
        }
        onClick();
    }

    private void onClick() {
        backBtn.setOnClickListener((v) -> finish());
    }
}
