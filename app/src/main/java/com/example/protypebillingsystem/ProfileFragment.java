package com.example.protypebillingsystem;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.Locale;

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

        loadDigitalReceipts(view, inflater);

        view.findViewById(R.id.btn_logout).setOnClickListener(v -> {
            // Clear session
            android.content.SharedPreferences prefs = requireContext()
                    .getSharedPreferences("MediPayPrefs", android.content.Context.MODE_PRIVATE);
            prefs.edit()
                    .putBoolean("isLoggedIn", false)
                    .remove("patientId")
                    .apply();

            PatientSession.getInstance().clear();
            startActivity(new Intent(requireContext(), LoginActivity.class));
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
                        "All bills reset to unpaid!",
                        android.widget.Toast.LENGTH_SHORT).show();
                
                // Refresh receipts UI
                loadDigitalReceipts(view, inflater);
            }
        });

        return view;
    }

    private void loadDigitalReceipts(View view, LayoutInflater inflater) {
        LinearLayout receiptContainer = view.findViewById(R.id.receipt_container);
        if (receiptContainer == null) return;

        receiptContainer.removeAllViews();
        
        PatientSession s = PatientSession.getInstance();
        DatabaseHelper db = new DatabaseHelper(requireContext());
        Cursor cursor = db.getBillsForPatient(s.id);

        boolean hasPaid = false;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_STATUS));
                if (!"paid".equalsIgnoreCase(status)) continue;
                hasPaid = true;

                String item = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ITEM));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DATE));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_AMOUNT));

                View row = inflater.inflate(R.layout.item_activity_row, receiptContainer, false);
                TextView tvTitle = row.findViewById(R.id.tv_activity_title);
                TextView tvDate = row.findViewById(R.id.tv_activity_date);
                TextView tvAmount = row.findViewById(R.id.tv_activity_amount);
                
                if (tvTitle != null) tvTitle.setText(item + " Receipt");
                if (tvDate != null) tvDate.setText("Paid on: " + date);
                if (tvAmount != null) {
                    tvAmount.setText(String.format(Locale.getDefault(), "RWF %,.0f", amount));
                    tvAmount.setTextColor(getResources().getColor(R.color.accent_green, null));
                }
                receiptContainer.addView(row);
            }
            cursor.close();
        }
        db.close();

        if (!hasPaid) {
            TextView tvEmpty = new TextView(requireContext());
            tvEmpty.setText("No receipts available yet.\nReceipts will appear here after payment.");
            tvEmpty.setGravity(android.view.Gravity.CENTER);
            tvEmpty.setPadding(0, 50, 0, 50);
            tvEmpty.setTextColor(getResources().getColor(R.color.md_outline, null));
            receiptContainer.addView(tvEmpty);
        }
    }

    private void setProfileRow(View row, String label, String value) {
        if (row == null) return;
        TextView tvLabel = row.findViewById(R.id.tv_profile_label);
        TextView tvValue = row.findViewById(R.id.tv_profile_value);
        if (tvLabel != null) tvLabel.setText(label);
        if (tvValue != null) tvValue.setText(value != null ? value : "—");
    }
}
