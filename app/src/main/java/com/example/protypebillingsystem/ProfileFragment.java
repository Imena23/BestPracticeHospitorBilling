package com.example.protypebillingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        PatientSession s = PatientSession.getInstance();

        // Avatar initials
        TextView tvAvatar = view.findViewById(R.id.tv_profile_avatar);
        if (tvAvatar != null) tvAvatar.setText(s.getInitials());

        // Name and patient ID
        TextView tvName = view.findViewById(R.id.tv_profile_name);
        if (tvName != null) tvName.setText(s.name);

        TextView tvPatientId = view.findViewById(R.id.tv_profile_patient_id);
        if (tvPatientId != null) tvPatientId.setText("Patient ID: #" + s.patientId);

        setProfileRow(view.findViewById(R.id.row_ward),      "Ward",           s.ward);
        setProfileRow(view.findViewById(R.id.row_doctor),    "Doctor",         s.doctor);
        setProfileRow(view.findViewById(R.id.row_admission), "Admission Date", s.admissionDate);
        setProfileRow(view.findViewById(R.id.row_blood),     "Blood Type",     s.bloodType);

        view.findViewById(R.id.btn_logout).setOnClickListener(v -> {
            // Clear session
            android.content.SharedPreferences prefs = requireContext()
                    .getSharedPreferences("MediPayPrefs", android.content.Context.MODE_PRIVATE);
            prefs.edit()
                    .putBoolean("isLoggedIn", false)
                    .remove("patientId")
                    .apply();

            PatientSession.getInstance().clear();
            startActivity(new Intent(requireContext(), RegisterActivity.class));
            requireActivity().finish();
        });

        view.findViewById(R.id.btn_reset_bills).setOnClickListener(v -> {
            DatabaseHelper db = new DatabaseHelper(requireContext());
            boolean success = db.resetBillsToUnpaid(s.id);
            db.close();

            if (success) {
                // Refresh session amounts
                DatabaseHelper dbRefresh = new DatabaseHelper(requireContext());
                s.paidAmount = dbRefresh.getTotalPaid(s.id);
                s.unpaidAmount = dbRefresh.getTotalUnpaid(s.id);
                dbRefresh.close();

                android.widget.Toast.makeText(requireContext(),
                        "All bills reset to unpaid! Go to Payments to test again.",
                        android.widget.Toast.LENGTH_LONG).show();
            } else {
                android.widget.Toast.makeText(requireContext(),
                        "No bills to reset",
                        android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void setProfileRow(View row, String label, String value) {
        if (row == null) return;
        TextView tvLabel = row.findViewById(R.id.tv_profile_label);
        TextView tvValue = row.findViewById(R.id.tv_profile_value);
        if (tvLabel != null) tvLabel.setText(label);
        if (tvValue != null) tvValue.setText(value != null ? value : "—");
    }
}
