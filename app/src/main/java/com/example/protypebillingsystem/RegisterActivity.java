package com.example.protypebillingsystem;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etDob, etPatientId, etPin, etPinConfirm;
    private TextView tvError;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        dbHelper       = new DatabaseHelper(this);
        etName         = findViewById(R.id.et_register_name);
        etDob          = findViewById(R.id.et_register_dob);
        etPatientId    = findViewById(R.id.et_register_patient_id);
        etPin          = findViewById(R.id.et_register_pin);
        etPinConfirm   = findViewById(R.id.et_register_pin_confirm);
        tvError        = findViewById(R.id.tv_register_error);

        etDob.setOnClickListener(v -> showDatePicker());
        findViewById(R.id.btn_register).setOnClickListener(v -> attemptRegister());
        findViewById(R.id.tv_goto_login).setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) ->
                etDob.setText(String.format("%02d/%02d/%04d", day, month + 1, year)),
                cal.get(Calendar.YEAR) - 30, cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void attemptRegister() {
        String name       = etName.getText().toString().trim();
        String dob        = etDob.getText().toString().trim();
        String patientId  = etPatientId.getText().toString().trim();
        String pin        = etPin.getText().toString().trim();
        String pinConfirm = etPinConfirm.getText().toString().trim();

        if (name.isEmpty())          { showError("Please enter your full name"); return; }
        if (dob.isEmpty())           { showError("Please select your date of birth"); return; }
        if (pin.length() != 4)       { showError("PIN must be exactly 4 digits"); return; }
        if (!pin.equals(pinConfirm)) { showError("PINs do not match"); return; }

        if (patientId.isEmpty()) patientId = "PAT-" + System.currentTimeMillis();

        long newId = dbHelper.registerPatient(name, dob, patientId, pin);
        if (newId == -1) { showError("This name and date of birth already exist."); return; }

        getSharedPreferences("MediPayPrefs", MODE_PRIVATE).edit()
                .putBoolean("isLoggedIn", true).putLong("patientId", newId).apply();
        loadSessionAndNavigate(newId);
    }

    private void loadSessionAndNavigate(long patientId) {
        DatabaseHelper db = new DatabaseHelper(this);
        Cursor cursor = db.getPatientById(patientId);
        if (cursor != null && cursor.moveToFirst()) {
            PatientSession s  = PatientSession.getInstance();
            s.id              = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
            s.name            = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME));
            s.dob             = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DOB));
            s.patientId       = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PATIENT_ID));
            s.ward            = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_WARD));
            s.doctor          = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DOCTOR));
            s.admissionDate   = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ADMISSION));
            s.bloodType       = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BLOOD));
            cursor.close();
            s.paidAmount   = db.getTotalPaid(s.id);
            s.unpaidAmount = db.getTotalUnpaid(s.id);
            s.totalBill    = s.paidAmount + s.unpaidAmount;
            db.close();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            if (cursor != null) cursor.close();
            db.close();
            showError("Failed to load patient data");
        }
    }

    private void showError(String msg) {
        tvError.setText(msg);
        tvError.setVisibility(View.VISIBLE);
    }
}
