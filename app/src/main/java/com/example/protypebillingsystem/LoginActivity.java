package com.example.protypebillingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private TextView tvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        tvError = findViewById(R.id.tv_error);

        findViewById(R.id.btn_login).setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            tvError.setText("Please enter both email and password");
            tvError.setVisibility(View.VISIBLE);
            return;
        }

        // Mocking session data for demo purposes. 
        // In a real app, the server would provide the specific insurance for this user.
        PatientSession session = PatientSession.getInstance();
        session.id = 1;
        session.name = "John Doe";
        session.dob = "15/03/1990";
        session.patientId = "PAT-2026-0042";
        session.ward = "Ward A - Room 203";
        session.doctor = "Dr. Sarah Mensah";
        session.admissionDate = "March 28, 2026";
        session.bloodType = "O+";
        session.totalBill = 2450.00;
        session.billId = "BILL-2026-0042";
        
        // Hospital-assigned Insurance Detail (e.g. RSSB is standard for this user)
        session.insuranceProvider = "RSSB";
        session.insuranceCoverage = 1960.00; // 80% coverage
        session.netPayable = session.totalBill - session.insuranceCoverage;

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
