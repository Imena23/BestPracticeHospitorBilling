package com.example.protypebillingsystem;

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

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        PatientSession s = PatientSession.getInstance();

        TextView tvName = view.findViewById(R.id.tv_patient_name);
        if (tvName != null) tvName.setText(s.name);

        TextView tvAvatar = view.findViewById(R.id.tv_avatar_initials);
        if (tvAvatar != null) tvAvatar.setText(s.getInitials());

        TextView tvLocation = view.findViewById(R.id.tv_location);
        if (tvLocation != null) tvLocation.setText(s.ward);

        // Refresh unpaid balance from DB
        DatabaseHelper db = new DatabaseHelper(requireContext());
        s.paidAmount = db.getTotalPaid(s.id);
        s.unpaidAmount = db.getTotalUnpaid(s.id);
        db.close();

        TextView tvTotal = view.findViewById(R.id.tv_bill_total);
        if (tvTotal != null) tvTotal.setText(String.format(Locale.getDefault(), "RWF %,.0f", s.unpaidAmount * 1300));

        setupActivityRows(view, inflater);

        view.findViewById(R.id.btn_view_bill).setOnClickListener(v -> navigateTo(1));
        view.findViewById(R.id.action_view_bills).setOnClickListener(v -> navigateTo(1));
        view.findViewById(R.id.action_payment_history).setOnClickListener(v -> navigateTo(2));

        return view;
    }

    private void setupActivityRows(View view, LayoutInflater inflater) {
        LinearLayout container = view.findViewById(R.id.recent_activity_container);
        if (container == null) return;
        container.removeAllViews();

        DatabaseHelper db = new DatabaseHelper(requireContext());
        Cursor cursor = db.getBillsForPatient(PatientSession.getInstance().id);
        boolean first = true;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                if (!first) {
                    View divider = new View(requireContext());
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, 1);
                    lp.setMargins(0, 10, 0, 10);
                    divider.setLayoutParams(lp);
                    divider.setBackgroundColor(getResources().getColor(R.color.divider, null));
                    container.addView(divider);
                }
                first = false;

                String item = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ITEM));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DATE));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_AMOUNT));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_STATUS));

                View row = inflater.inflate(R.layout.item_activity_row, container, false);
                TextView tvTitle = row.findViewById(R.id.tv_activity_title);
                TextView tvDate = row.findViewById(R.id.tv_activity_date);
                TextView tvAmount = row.findViewById(R.id.tv_activity_amount);

                if (tvTitle != null) tvTitle.setText(item);
                if (tvDate != null) tvDate.setText(date);
                if (tvAmount != null) {
                    tvAmount.setText(String.format(Locale.getDefault(), "RWF %,.0f", amount * 1300));
                    boolean isPaid = "paid".equalsIgnoreCase(status);
                    tvAmount.setTextColor(getResources().getColor(
                            isPaid ? R.color.accent_green : R.color.accent_red, null));
                }
                container.addView(row);
            }
            cursor.close();
        }
        db.close();

        if (first) {
            // No bills — show empty state
            TextView empty = new TextView(requireContext());
            empty.setText("No recent activity");
            empty.setTextColor(getResources().getColor(R.color.text_hint, null));
            empty.setTextSize(14);
            container.addView(empty);
        }
    }

    private void navigateTo(int index) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).navigateToTab(index);
        }
    }
}
