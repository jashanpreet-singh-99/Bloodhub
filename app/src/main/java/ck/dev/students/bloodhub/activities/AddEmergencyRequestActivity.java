package ck.dev.students.bloodhub.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import ck.dev.students.bloodhub.R;
import ck.dev.students.bloodhub.model.Appointment;
import ck.dev.students.bloodhub.model.BloodRequest;
import ck.dev.students.bloodhub.utils.Config;

public class AddEmergencyRequestActivity extends Activity {

    private EditText nameTxtView;
    private Spinner bloodGroupSpinner;
    private Spinner qtySpinner;
    private EditText mobileTxtView;
    private EditText locationTxtView;
    private Spinner diagnoseSpinner;
    private RadioGroup transportGroup;
    private Button submitBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_emergency_request);
        initView();
    }

    private void initView() {
        nameTxtView       = findViewById(R.id.name);
        bloodGroupSpinner = findViewById(R.id.blood_spinner);
        qtySpinner        = findViewById(R.id.quantity_spinner);
        mobileTxtView     = findViewById(R.id.contact);
        locationTxtView   = findViewById(R.id.location);
        diagnoseSpinner   = findViewById(R.id.diagnosis_spinner);
        transportGroup    = findViewById(R.id.transport);
        submitBtn         = findViewById(R.id.request_btn);

        Bundle extras = getIntent().getExtras();
        boolean appBarVisible;
        if (extras != null) {
            appBarVisible = extras.getBoolean(Config.KEY_ACT_APP_BAR);
            if (appBarVisible) {
                RelativeLayout appBarLayout = findViewById(R.id.app_bar);
                ImageButton    backBtn      = findViewById(R.id.back_btn);
                appBarLayout.setVisibility(View.VISIBLE);
                backBtn.setOnClickListener((v) -> finish());
            }
        }

        String[] bloodGroups = getResources().getStringArray(R.array.blood_groups);
        ArrayAdapter<String> adapterBg = new ArrayAdapter<>(this, R.layout.item_spinner_view, bloodGroups);
        bloodGroupSpinner.setAdapter(adapterBg);

        String[] bloodQty = getResources().getStringArray(R.array.bag_quantity);
        ArrayAdapter<String> adapterQty = new ArrayAdapter<>(this, R.layout.item_spinner_view, bloodQty);
        qtySpinner.setAdapter(adapterQty);

        String[] diagnosis = getResources().getStringArray(R.array.diagnosis);
        ArrayAdapter<String> adapterDiagnose = new ArrayAdapter<>(this, R.layout.item_spinner_view, diagnosis);
        diagnoseSpinner.setAdapter(adapterDiagnose);

        onClick();
    }

    private void onClick() {
        submitBtn.setOnClickListener((v) -> {
            try {
                String name     = nameTxtView.getText().toString();
                String contact  = mobileTxtView.getText().toString();
                String location = locationTxtView.getText().toString();

                if (TextUtils.isEmpty(name)) {
                    nameTxtView.setError("Name is required.");
                    return;
                }

                if (TextUtils.isEmpty(contact) || contact.length() != 10) {
                    mobileTxtView.setError("Invalid Mobile Number.");
                    return;
                }

                if (TextUtils.isEmpty(location)) {
                    locationTxtView.setError("Location required.");
                    return;
                }

                String bloodGroup = bloodGroupSpinner.getSelectedItem().toString();
                String bloodQty   = qtySpinner.getSelectedItem().toString();
                String diagnose   = diagnoseSpinner.getSelectedItem().toString();

                RadioButton radioButton = findViewById(transportGroup.getCheckedRadioButtonId());
                String transportTxt = radioButton.getText().toString();
                boolean transport = transportTxt.equals("Yes");

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String Uid = (user != null)? user.getUid(): null;
                BloodRequest bloodRequest = new BloodRequest(
                        Uid,
                        name,
                        bloodGroup,
                        bloodQty,
                        contact,
                        location,
                        diagnose,
                        new Date().getTime(),
                        transport
                );
                FirebaseDatabase.getInstance().getReference().child(Config.KEY_REQUESTS).push().setValue(bloodRequest);

                Dialog dialog = new Dialog(AddEmergencyRequestActivity.this);
                dialog.setTitle("Submit Request");
                dialog.setContentView(R.layout.dialog_blood_request_submit);
                dialog.show();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                final Button requestBtn = dialog.findViewById(R.id.ok_btn);
                requestBtn.setOnClickListener((vi) -> {
                    finish();
                });
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Server Error.", Toast.LENGTH_SHORT).show();
                Config.Log(Config.TAG_FIREBASE, "Blood request Error : " + e.getMessage(), true);
            }
        });
    }
}
