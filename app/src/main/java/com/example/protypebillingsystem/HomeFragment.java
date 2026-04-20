package com.example.protypebillingsystem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.Locale;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        PatientSession s = PatientSession.getInstance();

        // Header
        TextView tvName = view.findViewById(R.id.tv_patient_name);
        if (tvName != null) tvName.setText(s.name);

        // Avatar initials
        TextView tvAvatar = view.findViewById(R.id.tv_avatar_initials);
        if (tvAvatar != null) tvAvatar.setText(s.getInitials());

        // Location
        TextView tvLocation = view.findViewById(R.id.tv_location);
        if (tvLocation != null) tvLocation.setText(s.ward);

        // Bill total
        TextView tvTotal = view.findViewById(R.id.tv_bill_total);
        if (tvTotal != null) tvTotal.setText(String.format(Locale.getDefault(), "$%.2f", s.totalBill));

        // Activity rows
        setupActivityRows(view);

        view.findViewById(R.id.btn_view_bill).setOnClickListener(v -> navigateTo(1));
        view.findViewById(R.id.action_view_bills).setOnClickListener(v -> navigateTo(1));
        view.findViewById(R.id.action_payment_history).setOnClickListener(v -> navigateTo(2));

        return view;
    }

    private void setupActivityRows(View view) {
        DatabaseHelper db = new DatabaseHelper(requireContext());
        android.database.Cursor cursor = db.getBillsForPatient(PatientSession.getInstance().id);
        int[] rowIds = {R.id.row1, R.id.row2};
        int i = 0;
        if (cursor != null) {
            while (cursor.moveToNext() && i < rowIds.length) {
                View row = view.findViewById(rowIds[i]);
                if (row != null) {
                    String item = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ITEM));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DATE));
                    double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_AMOUNT));
                    TextView tvTitle = row.findViewById(R.id.tv_activity_title);
                    TextView tvDate = row.findViewById(R.id.tv_activity_date);
                    TextView tvAmount = row.findViewById(R.id.tv_activity_amount);
                    if (tvTitle != null) tvTitle.setText(item);
                    if (tvDate != null) tvDate.setText(date);
                    if (tvAmount != null) tvAmount.setText(String.format(Locale.getDefault(), "$%.2f", amount));
                }
                i++;
            }
            cursor.close();
        }
        db.close();
    }

    private void navigateTo(int index) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).navigateToTab(index);
        }
    }
}
