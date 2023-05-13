package ck.dev.students.bloodhub.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import ck.dev.students.bloodhub.R;
import ck.dev.students.bloodhub.model.User;
import ck.dev.students.bloodhub.utils.Config;

public class RegisterOrgActivity extends Activity {

    private EditText instituteTxtView;
    private EditText emailTxtView;
    private EditText passwordTxtView;
    private EditText contactTxtView;
    private EditText addressTxtView;

    private CheckBox termCheckBox;

    private Button      registerBtn;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register_org);

        initView();
    }

    private void initView() {
        instituteTxtView = findViewById(R.id.name);
        emailTxtView     = findViewById(R.id.email);
        passwordTxtView  = findViewById(R.id.password);
        contactTxtView   = findViewById(R.id.contact);
        addressTxtView   = findViewById(R.id.add);

        termCheckBox     = findViewById(R.id.agree_terms);
        registerBtn      = findViewById(R.id.register_button);

        progressBar      = findViewById(R.id.progress_bar);

        firebaseAuth      = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        showProgressbar(false);
        onClick();
    }

    private void onClick() {
        termCheckBox.setOnCheckedChangeListener((compoundButton, b) -> registerBtn.setEnabled(b));

        registerBtn.setOnClickListener((v) -> {
            if (!v.isEnabled()) {
                Toast.makeText(getApplicationContext(),
                        "Please, Accept the terms and conditions.",
                        Toast.LENGTH_LONG)
                        .show();
                return;
            }
            registerNewOrg();
        });
    }

    private void registerNewOrg() {
        String email    = emailTxtView.getText().toString();
        String password = passwordTxtView.getText().toString();
        String contact  = contactTxtView.getText().toString();

        if (TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordTxtView.setError("This password is too short.");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            emailTxtView.setError("This field is required.");
            return;
        } else if (!isEmailValid(email)) {
            emailTxtView.setError("This email address is invalid.");
            return;
        }

        if (TextUtils.isEmpty(contact)) {
            contactTxtView.setError("This field is required.");
            return;
        } else if (!isContactValid(contact)) {
            contactTxtView.setError("This contact is invalid.");
            return;
        }

        showProgressbar(true);
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, (task) -> {
                   showProgressbar(false);
                   if (task.isSuccessful()) {
                       sendVerificationEmail();
                       startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                   } else {
                       if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                           Toast.makeText(RegisterOrgActivity.this,
                                   "User with this email already exists.",
                                   Toast.LENGTH_LONG).show();
                       } else {
                           Toast.makeText(RegisterOrgActivity.this,
                                   "Error while creating user.",
                                   Toast.LENGTH_LONG).show();
                       }
                       Config.Log(Config.TAG_REGISTER, "Error : " + Objects.requireNonNull(task.getException()).getMessage(), true);
                   }
                });
    }

    private void sendVerificationEmail() {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        Objects.requireNonNull(user).sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user1 = firebaseAuth.getCurrentUser();
                        writeNewUser(user1.getUid(), user1.getEmail());
                        startActivity(new Intent(RegisterOrgActivity.this, LoginActivity.class));
                        finish();
                    }
                });
    }

    private void writeNewUser(String uid, String email) {
        User user = new User(
                instituteTxtView.getText().toString(),
                email,
                contactTxtView.getText().toString(),
                addressTxtView.getText().toString()
        );
        databaseReference.child(Config.KEY_USER).child(uid).setValue(user);
    }


    private boolean isEmailValid(String email) { return email.matches("(.+)(@)(.+)(\\.)(.+)"); }

    private boolean isPasswordValid(String password) { return password.length() >= 6; }

    private boolean isContactValid(String contact){return ( contact.length() == 10); }

        private void showProgressbar(boolean stat) {
        if (stat)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);
    }
}
