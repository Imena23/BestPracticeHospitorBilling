package com.example.protypebillingsystem;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.protypebillingsystem.fhir.FhirClient;
import com.example.protypebillingsystem.fhir.models.FhirBundle;
import com.google.android.material.snackbar.Snackbar;
import java.util.Calendar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText etName, etDob, etPin;
    private TextView tvError;
    private DatabaseHelper dbHelper;
    private View rootView;

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

        rootView = findViewById(android.R.id.content);
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
            // Verify with FHIR server in background
            verifyWithFhirServer(name, dob);
            
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

    private void verifyWithFhirServer(String name, String dob) {
        // Convert DD/MM/YYYY to YYYY-MM-DD for FHIR
        String[] parts = dob.split("/");
        String fhirDate = parts[2] + "-" + parts[1] + "-" + parts[0];

        FhirClient.getInstance()
                .getApiService()
                .searchPatients(name, fhirDate)
                .enqueue(new Callback<FhirBundle>() {
                    @Override
                    public void onResponse(Call<FhirBundle> call, Response<FhirBundle> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getTotal() > 0) {
                            Log.d(TAG, "Patient verified on FHIR server");
                            Snackbar.make(rootView, "✓ Verified with hospital system", Snackbar.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "Patient not found on FHIR server (local only)");
                        }
                    }

                    @Override
                    public void onFailure(Call<FhirBundle> call, Throwable t) {
                        Log.e(TAG, "FHIR verification error", t);
                    }
                });
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
