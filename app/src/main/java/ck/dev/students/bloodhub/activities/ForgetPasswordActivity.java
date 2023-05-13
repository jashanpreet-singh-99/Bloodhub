package ck.dev.students.bloodhub.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;

import ck.dev.students.bloodhub.R;

public class ForgetPasswordActivity extends Activity {

    private ProgressBar  progressBar;
    private EditText     emailTxtView;
    private Button       resetBtn;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_forget_password);
        firebaseAuth = FirebaseAuth.getInstance();
        initView();
    }

    private void initView() {
        emailTxtView = findViewById(R.id.email);
        progressBar  = findViewById(R.id.progress_bar);
        resetBtn     = findViewById( R.id.password_reset_button);

        showProgress(false);

        onClick();
    }

    private void onClick() {
        resetBtn.setOnClickListener((v) -> {
            String email = emailTxtView.getText().toString();
            if (email.isEmpty()) {
                emailTxtView.setError("Email is empty, please enter the email you used to register.");
                return;
            } else if (!isEmailValid(email)) {
                emailTxtView.setError("This email address is invalid.");
                return;
            }
            showProgress(true);
            firebaseAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener((task) -> {
                       showProgress(false);
                       if (task.isSuccessful()) {
                           Toast.makeText(getApplicationContext(), "Email sent : " + email, Toast.LENGTH_LONG).show();
                       }
                    });

        });
    }

    private boolean isEmailValid(String email) { return email.matches("(.+)(@)(.+)(\\.)(.+)"); }

    private void showProgress(boolean stat) {
        if (stat) {
            resetBtn.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            resetBtn.setVisibility(View.VISIBLE);
        }
    }
}
