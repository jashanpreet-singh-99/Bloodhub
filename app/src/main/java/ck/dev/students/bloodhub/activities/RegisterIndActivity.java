package ck.dev.students.bloodhub.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import ck.dev.students.bloodhub.R;
import ck.dev.students.bloodhub.model.User;
import ck.dev.students.bloodhub.utils.Config;

public class RegisterIndActivity extends Activity {


    private FirebaseAuth firebaseAuth;

    private EditText nameTxtView;
    private EditText emailTxtView;
    private EditText passwordTxtView;
    private EditText contactTxtView;

    private Spinner bloodGroupSpinner;

    private Button registerBtn;

    private ProgressBar progressBar;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register_ind);
        initView();
    }

    private void initView() {
        nameTxtView       = findViewById(R.id.name);
        emailTxtView      = findViewById(R.id.email);
        passwordTxtView   = findViewById(R.id.password);
        contactTxtView    = findViewById(R.id.contact);

        bloodGroupSpinner = findViewById(R.id.blood_spinner);
        registerBtn       = findViewById(R.id.register_button);

        progressBar       = findViewById(R.id.progress_bar);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        showProgressbar(false);

        onClick();
    }

    private void onClick() {
        registerBtn.setOnClickListener((v) -> registerNewUser());

        List<String> bGroups = Arrays.asList(getResources().getStringArray(R.array.blood_groups));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_spinner_view, bGroups) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                v.setPadding(8, 8, 8, 16);
                return v;
            }

            @Override
            public int getCount() {
                return super.getCount();
            }
        };
        adapter.setDropDownViewResource(R.layout.item_spinner_view);
        bloodGroupSpinner.setAdapter(adapter);
        bloodGroupSpinner.setSelection(0);
    }

    private void sendVerificationEmail() {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        Objects.requireNonNull(user).sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user1 = firebaseAuth.getCurrentUser();
                        writeNewUser(user1.getUid(), user1.getEmail());
                        startActivity(new Intent(RegisterIndActivity.this, LoginActivity.class));
                        finish();
                    }
                });
    }

    private void writeNewUser(String userId, String email){
        User user = new User(
                nameTxtView.getText().toString(),
                email,
                contactTxtView.getText().toString(),
                bloodGroupSpinner.getSelectedItem().toString(),
                Config.KEY_TYPE_INDIVIDUAL);
        databaseReference.child(Config.KEY_USER).child(userId).setValue(user);
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

    private void registerNewUser() {
        String email      = emailTxtView.getText().toString();
        String password   = passwordTxtView.getText().toString();
        String contact    = contactTxtView.getText().toString();
        String bGroup     = bloodGroupSpinner.getSelectedItem().toString();

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
                        Toast.makeText(RegisterIndActivity.this,
                                "Registration Complete.",
                                Toast.LENGTH_SHORT).show();
                        int id = Arrays.asList(getResources().getStringArray(R.array.blood_groups)).indexOf(bGroup);
                        FirebaseMessaging.getInstance().subscribeToTopic("Request_"+ id);
                        FirebaseMessaging.getInstance().subscribeToTopic("URGENT");
                        sendVerificationEmail();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(RegisterIndActivity.this,
                                    "User with this email already exists.",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(RegisterIndActivity.this,
                                    "Error while creating user.",
                                    Toast.LENGTH_LONG).show();
                        }
                        Config.Log(Config.TAG_REGISTER, "Error : " + Objects.requireNonNull(task.getException()).getMessage(), true);
                    }
                });
    }
}
