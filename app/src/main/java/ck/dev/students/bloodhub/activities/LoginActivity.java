package ck.dev.students.bloodhub.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import java.util.Objects;

import ck.dev.students.bloodhub.R;
import ck.dev.students.bloodhub.model.User;
import ck.dev.students.bloodhub.utils.Config;

public class LoginActivity extends Activity {

    private EditText emailTxtView;
    private EditText passwordTxtView;
    private Button loginBtn;
    private TextView forgetTxtView;
    private TextView individualTxtView;
    private TextView organizationTxtView;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login_activity);
        initView();
    }

    private void initView() {
        emailTxtView        = findViewById(R.id.email);
        passwordTxtView     = findViewById(R.id.password);
        loginBtn            = findViewById(R.id.email_sign_in_button);
        forgetTxtView       = findViewById(R.id.forgot_password_button);
        individualTxtView   = findViewById(R.id.email_register_individual_button);
        organizationTxtView = findViewById(R.id.email_register_organization_button);
        progressBar         = findViewById(R.id.progress_bar);

        firebaseAuth = FirebaseAuth.getInstance();

        showProgress(false);
        onClick();
    }

    private void onClick() {
        loginBtn.setOnClickListener((v) -> attemptLogin());

        forgetTxtView.setOnClickListener((v) -> startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class)));

        individualTxtView.setOnClickListener((v) -> startActivity(new Intent(LoginActivity.this, RegisterIndActivity.class)));

        organizationTxtView.setOnClickListener((v) -> startActivity(new Intent(LoginActivity.this, RegisterOrgActivity.class)));
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
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        } else if(account_type.equals(Config.KEY_TYPE_ORGANIZATION)) {
                            startActivity(new Intent(LoginActivity.this, MainActivityOrg.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Config.Log(Config.TAG_LOGIN, "Error in DB : " + databaseError.getMessage(), true);
                    }
                });
    }

    private void attemptLogin() {
        emailTxtView.setError(null);
        passwordTxtView.setError(null);

        String email = emailTxtView.getText().toString();
        String password = passwordTxtView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordTxtView.setError("This password is too short.");
            focusView = passwordTxtView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            emailTxtView.setError("This field is required.");
            focusView = emailTxtView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            emailTxtView.setError("This email address is invalid.");
            focusView = emailTxtView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            final Context context = getApplicationContext();

            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, (task) -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (Objects.requireNonNull(user).isEmailVerified()) {
                                setView(user.getUid());
                            } else {
                                Toast.makeText(context,
                                        "Please verify the Email address first.",
                                        Toast.LENGTH_LONG).
                                        show();
                                showProgress(false);
                                firebaseAuth.signOut();
                            }
                        } else {
                            showProgress(false);
                            Toast.makeText(context,
                                    "Authentication failed.",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
        }

    }

    private void showProgress(boolean stat) {
        if (stat) {
            loginBtn.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            loginBtn.setVisibility(View.VISIBLE);
        }
    }

    private boolean isEmailValid(String email) { return email.matches("(.+)(@)(.+)(\\.)(.+)"); }

    private boolean isPasswordValid(String password) { return password.length() >= 6; }
}
