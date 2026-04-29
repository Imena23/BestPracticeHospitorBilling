package com.example.protypebillingsystem;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.protypebillingsystem.fhir.FhirClient;
import com.example.protypebillingsystem.fhir.models.FhirPatient;
import com.google.android.material.snackbar.Snackbar;
import java.util.Arrays;
import java.util.Calendar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private EditText etName, etDob, etPatientId, etPin, etPinConfirm;
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

        setContentView(R.layout.activity_register);

        rootView = findViewById(android.R.id.content);
        dbHelper = new DatabaseHelper(this);
        etName = findViewById(R.id.et_register_name);
        etDob = findViewById(R.id.et_register_dob);
        etPatientId = findViewById(R.id.et_register_patient_id);
        etPin = findViewById(R.id.et_register_pin);
        etPinConfirm = findViewById(R.id.et_register_pin_confirm);
        tvError = findViewById(R.id.tv_register_error);

        etDob.setOnClickListener(v -> showDatePicker());
        findViewById(R.id.btn_register).setOnClickListener(v -> attemptRegister());
        findViewById(R.id.tv_goto_login).setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
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

    private void attemptRegister() {
        String name = etName.getText().toString().trim();
        String dob = etDob.getText().toString().trim();
        String patientId = etPatientId.getText().toString().trim();
        String pin = etPin.getText().toString().trim();
        String pinConfirm = etPinConfirm.getText().toString().trim();

        if (name.isEmpty()) { showError("Please enter your full name"); return; }
        if (dob.isEmpty()) { showError("Please select your date of birth"); return; }
        if (pin.isEmpty()) { showError("Please create a 4-digit PIN"); return; }
        if (pin.length() != 4) { showError("PIN must be exactly 4 digits"); return; }
        if (!pin.equals(pinConfirm)) { showError("PINs do not match"); return; }

        // Auto-generate patient ID if not provided
        if (patientId.isEmpty()) {
            patientId = "PAT-" + System.currentTimeMillis();
        }

        // Register user in database
        long newPatientId = dbHelper.registerPatient(name, dob, patientId, pin);
        if (newPatientId == -1) {
            showError("Registration failed. This name and DOB may already exist.");
            return;
        }

        // Create patient on FHIR server
        createFhirPatient(name, dob, patientId, newPatientId);
    }

    private void createFhirPatient(String name, String dob, String patientId, long localPatientId) {
        // Convert DD/MM/YYYY to YYYY-MM-DD for FHIR
        String[] parts = dob.split("/");
        String fhirDate = parts[2] + "-" + parts[1] + "-" + parts[0];

        FhirPatient patient = new FhirPatient();
        patient.setName(Arrays.asList(new FhirPatient.HumanName(name)));
        patient.setBirthDate(fhirDate);
        patient.setIdentifier(Arrays.asList(
                new FhirPatient.Identifier("http://medipay.rw/patients", patientId)
        ));

        // Show loading snackbar
        Snackbar loadingSnack = Snackbar.make(rootView, "Syncing with hospital system...", Snackbar.LENGTH_INDEFINITE);
        loadingSnack.show();

        FhirClient.getInstance()
                .getApiService()
                .createPatient(patient)
                .enqueue(new Callback<FhirPatient>() {
                    @Override
                    public void onResponse(Call<FhirPatient> call, Response<FhirPatient> response) {
                        loadingSnack.dismiss();
                        if (response.isSuccessful() && response.body() != null) {
                            String fhirId = response.body().getId();
                            Log.d(TAG, "Patient created on FHIR server with ID: " + fhirId);
                            
                            Snackbar.make(rootView, "✓ Synced with hospital system", Snackbar.LENGTH_SHORT).show();
                            
                            // Save login session and navigate
                            saveSessionAndNavigate(localPatientId);
                        } else {
                            Log.w(TAG, "FHIR patient creation failed: " + response.code());
                            // Continue anyway - local registration succeeded
                            Snackbar.make(rootView, "Registered locally (hospital sync pending)", Snackbar.LENGTH_SHORT).show();
                            saveSessionAndNavigate(localPatientId);
                        }
                    }

                    @Override
                    public void onFailure(Call<FhirPatient> call, Throwable t) {
                        loadingSnack.dismiss();
                        Log.e(TAG, "FHIR patient creation error", t);
                        // Continue anyway - local registration succeeded
                        Snackbar.make(rootView, "Registered locally (hospital sync pending)", Snackbar.LENGTH_SHORT).show();
                        saveSessionAndNavigate(localPatientId);
                    }
                });
    }

    private void saveSessionAndNavigate(long patientId) {
        // Save login session
        SharedPreferences prefs = getSharedPreferences("MediPayPrefs", MODE_PRIVATE);
        prefs.edit()
                .putBoolean("isLoggedIn", true)
                .putLong("patientId", patientId)
                .apply();

        // Load session and navigate
        loadSessionAndNavigate(patientId);
    }

    private void loadSessionAndNavigate(long patientId) {
        DatabaseHelper db = new DatabaseHelper(this);
        android.database.Cursor cursor = db.getPatientById(patientId);

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

            android.database.Cursor bills = db.getBillsForPatient(session.id);
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
            showError("Failed to load patient data");
        }
    }

    private void showError(String msg) {
        tvError.setText(msg);
        tvError.setVisibility(View.VISIBLE);
    }
}
