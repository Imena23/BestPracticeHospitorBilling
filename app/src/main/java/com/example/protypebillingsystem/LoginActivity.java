package com.example.protypebillingsystem;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etName, etDob, etPin, etUsername, etPassword;
    private TextView tvError, tvSubtitle, tvDemoHint;
    private LinearLayout layoutPatient, layoutStaff;
    private DatabaseHelper dbHelper;
    private boolean isStaffMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Auto-login for patients
        SharedPreferences prefs = getSharedPreferences("MediPayPrefs", MODE_PRIVATE);
        if (prefs.getBoolean("isLoggedIn", false)) {
            long patientId = prefs.getLong("patientId", -1);
            if (patientId != -1) { loadSessionAndNavigate(patientId); return; }
        }

        setContentView(R.layout.activity_login);
        dbHelper = new DatabaseHelper(this);

        etName     = findViewById(R.id.et_name);
        etDob      = findViewById(R.id.et_dob);
        etPin      = findViewById(R.id.et_pin);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        tvError    = findViewById(R.id.tv_error);
        tvSubtitle = findViewById(R.id.tv_subtitle);
        tvDemoHint = findViewById(R.id.tv_demo_hint);
        layoutPatient = findViewById(R.id.layout_patient);
        layoutStaff   = findViewById(R.id.layout_staff);

        etDob.setOnClickListener(v -> showDatePicker());
        findViewById(R.id.btn_login).setOnClickListener(v -> attemptLogin());
        findViewById(R.id.tv_goto_register).setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });

        TabLayout tabRole = findViewById(R.id.tab_role);
        tabRole.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            public void onTabSelected(TabLayout.Tab tab) { switchMode(tab.getPosition() == 1); }
            public void onTabUnselected(TabLayout.Tab tab) {}
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void switchMode(boolean staff) {
        isStaffMode = staff;
        tvError.setVisibility(View.GONE);
        if (staff) {
            layoutPatient.setVisibility(View.GONE);
            layoutStaff.setVisibility(View.VISIBLE);
            tvSubtitle.setText("Enter your staff credentials");
            tvDemoHint.setText("Demo staff — dr.sarah / doctor123   |   admin / admin123");
        } else {
            layoutPatient.setVisibility(View.VISIBLE);
            layoutStaff.setVisibility(View.GONE);
            tvSubtitle.setText("Enter your details to access your billing information");
            tvDemoHint.setText("Demo: John Doe · PIN 1234   |   Jane Smith · PIN 5678");
        }
    }

    private void attemptLogin() {
        tvError.setVisibility(View.GONE);
        if (isStaffMode) attemptStaffLogin();
        else attemptPatientLogin();
    }

    private void attemptPatientLogin() {
        String name = etName.getText().toString().trim();
        String dob  = etDob.getText().toString().trim();
        String pin  = etPin.getText().toString().trim();

        if (name.isEmpty()) { showError("Please enter your full name"); return; }
        if (dob.isEmpty())  { showError("Please select your date of birth"); return; }
        if (pin.length() != 4) { showError("PIN must be exactly 4 digits"); return; }

        Cursor cursor = dbHelper.findPatient(name, dob, pin);
        if (cursor != null && cursor.moveToFirst()) {
            PatientSession session = PatientSession.getInstance();
            session.id            = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
            session.name          = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME));
            session.dob           = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DOB));
            session.patientId     = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PATIENT_ID));
            session.ward          = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_WARD));
            session.doctor        = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DOCTOR));
            session.admissionDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ADMISSION));
            session.bloodType     = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BLOOD));
            cursor.close();

            refreshPatientBillTotals(session);

            getSharedPreferences("MediPayPrefs", MODE_PRIVATE).edit()
                    .putBoolean("isLoggedIn", true).putLong("patientId", session.id).apply();

            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            if (cursor != null) cursor.close();
            showError("Incorrect name, date of birth, or PIN.");
        }
    }

    private void attemptStaffLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter username and password");
            return;
        }

        Cursor c = dbHelper.loginStaff(username, password);
        if (c != null && c.moveToFirst()) {
            long staffId  = c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.COL_STAFF_ID));
            String name   = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_STAFF_NAME));
            String role   = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_STAFF_ROLE));
            c.close();
            StaffSession.getInstance().set(staffId, name, role);
            navigateByRole(role);
        } else {
            if (c != null) c.close();
            showError("Invalid username or password");
        }
    }

    private void navigateByRole(String role) {
        Class<?> target;
        switch (role) {
            case "admin":      target = AdminActivity.class; break;
            case "doctor":     target = DoctorActivity.class; break;
            case "nurse":      target = NurseActivity.class; break;
            case "pharmacist": target = PharmacistActivity.class; break;
            case "lab":        target = LabActivity.class; break;
            default: showError("Unknown role: " + role); return;
        }
        startActivity(new Intent(this, target));
        finish();
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) ->
                etDob.setText(String.format("%02d/%02d/%04d", day, month + 1, year)),
                cal.get(Calendar.YEAR) - 30, cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showError(String msg) {
        tvError.setText(msg);
        tvError.setVisibility(View.VISIBLE);
    }

    private void refreshPatientBillTotals(PatientSession session) {
        Cursor bills = dbHelper.getBillsForPatient(session.id);
        double total = 0; String billId = "";
        if (bills != null) {
            while (bills.moveToNext()) {
                total += bills.getDouble(bills.getColumnIndexOrThrow(DatabaseHelper.COL_AMOUNT));
                billId = bills.getString(bills.getColumnIndexOrThrow(DatabaseHelper.COL_BILL_ID));
            }
            bills.close();
        }
        session.totalBill    = total;
        session.billId       = billId;
        session.paidAmount   = dbHelper.getTotalPaid(session.id);
        session.unpaidAmount = dbHelper.getTotalUnpaid(session.id);
    }

    private void loadSessionAndNavigate(long patientId) {
        DatabaseHelper db = new DatabaseHelper(this);
        Cursor cursor = db.getPatientById(patientId);
        if (cursor != null && cursor.moveToFirst()) {
            PatientSession session = PatientSession.getInstance();
            session.id            = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
            session.name          = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME));
            session.dob           = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DOB));
            session.patientId     = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PATIENT_ID));
            session.ward          = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_WARD));
            session.doctor        = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DOCTOR));
            session.admissionDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ADMISSION));
            session.bloodType     = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BLOOD));
            cursor.close();
            refreshPatientBillTotals(session);
            db.close();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            if (cursor != null) cursor.close();
            db.close();
        }
    }
}
