package com.example.protypebillingsystem;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class LoginActivity extends AppCompatActivity {

    private EditText etName, etDob, etPin;
    private TextView tvError;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is already logged in
        SharedPreferences prefs = getSharedPreferences("MediPayPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            long patientId = prefs.getLong("patientId", -1);
            if (patientId != -1) {
                loadSessionAndNavigate(patientId);
                return;
            }
        }

        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);
        etName = findViewById(R.id.et_name);
        etDob = findViewById(R.id.et_dob);
        etPin = findViewById(R.id.et_pin);
        tvError = findViewById(R.id.tv_error);

        etDob.setOnClickListener(v -> showDatePicker());
        findViewById(R.id.btn_login).setOnClickListener(v -> attemptLogin());
        findViewById(R.id.tv_goto_register).setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            String dob = String.format("%02d/%02d/%04d", day, month + 1, year);
            etDob.setText(dob);
        }, cal.get(Calendar.YEAR) - 30,
           cal.get(Calendar.MONTH),
           cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void attemptLogin() {
        String name = etName.getText().toString().trim();
        String dob = etDob.getText().toString().trim();
        String pin = etPin.getText().toString().trim();

        if (name.isEmpty()) { showError("Please enter your full name"); return; }
        if (dob.isEmpty()) { showError("Please select your date of birth"); return; }
        if (pin.isEmpty()) { showError("Please enter your 4-digit PIN"); return; }
        if (pin.length() != 4) { showError("PIN must be exactly 4 digits"); return; }

        Cursor cursor = dbHelper.findPatient(name, dob, pin);

        if (cursor != null && cursor.moveToFirst()) {
            PatientSession session = PatientSession.getInstance();
            session.id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
            session.name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME));
            session.dob = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DOB));
            session.patientId = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PATIENT_ID));
            session.ward = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_WARD));
            session.doctor = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DOCTOR));
            session.admissionDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ADMISSION));
            session.bloodType = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BLOOD));
            cursor.close();

            Cursor bills = dbHelper.getBillsForPatient(session.id);
            double total = 0;
            String billId = "";
            if (bills != null) {
                while (bills.moveToNext()) {
                    total += bills.getDouble(bills.getColumnIndexOrThrow(DatabaseHelper.COL_AMOUNT));
                    billId = bills.getString(bills.getColumnIndexOrThrow(DatabaseHelper.COL_BILL_ID));
                }
                bills.close();
            }
            session.totalBill = total;
            session.billId = billId;
            session.paidAmount = dbHelper.getTotalPaid(session.id);
            session.unpaidAmount = dbHelper.getTotalUnpaid(session.id);

            // Save login session
            SharedPreferences prefs = getSharedPreferences("MediPayPrefs", MODE_PRIVATE);
            prefs.edit()
                    .putBoolean("isLoggedIn", true)
                    .putLong("patientId", session.id)
                    .apply();

            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            if (cursor != null) cursor.close();
            showError("Incorrect name, date of birth, or PIN.\nPlease check your details or contact reception.");
        }
    }

    private void showError(String msg) {
        tvError.setText(msg);
        tvError.setVisibility(View.VISIBLE);
    }

    private void loadSessionAndNavigate(long patientId) {
        DatabaseHelper db = new DatabaseHelper(this);
        Cursor cursor = db.getPatientById(patientId);

        if (cursor != null && cursor.moveToFirst()) {
            PatientSession session = PatientSession.getInstance();
            session.id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
            session.name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME));
            session.dob = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DOB));
            session.patientId = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PATIENT_ID));
            session.ward = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_WARD));
            session.doctor = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DOCTOR));
            session.admissionDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ADMISSION));
            session.bloodType = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BLOOD));
            cursor.close();

            Cursor bills = db.getBillsForPatient(session.id);
            double total = 0;
            String billId = "";
            if (bills != null) {
                while (bills.moveToNext()) {
                    total += bills.getDouble(bills.getColumnIndexOrThrow(DatabaseHelper.COL_AMOUNT));
                    billId = bills.getString(bills.getColumnIndexOrThrow(DatabaseHelper.COL_BILL_ID));
                }
                bills.close();
            }
            session.totalBill = total;
            session.billId = billId;
            session.paidAmount = db.getTotalPaid(session.id);
            session.unpaidAmount = db.getTotalUnpaid(session.id);
            db.close();

            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            if (cursor != null) cursor.close();
            db.close();
        }
    }
}
