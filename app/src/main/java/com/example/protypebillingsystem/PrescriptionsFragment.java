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
import com.google.android.material.chip.Chip;

public class PrescriptionsFragment extends Fragment {

    private View rootView;
    private LayoutInflater rootInflater;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_prescriptions, container, false);
        rootInflater = inflater;
        loadPrescriptions(rootView, inflater);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (rootView != null) loadPrescriptions(rootView, rootInflater);
    }

    private void loadPrescriptions(View view, LayoutInflater inflater) {
        PatientSession s = PatientSession.getInstance();
        DatabaseHelper db = new DatabaseHelper(requireContext());

        LinearLayout container = view.findViewById(R.id.prescriptions_container);
        TextView tvEmpty = view.findViewById(R.id.tv_empty_message);
        View layoutEmpty = view.findViewById(R.id.layout_empty);

        if (container == null) return;
        container.removeAllViews();

        Cursor cursor = db.getPrescriptionsForPatient(s.id);
        int count = 0;

        if (cursor != null) {
            while (cursor.moveToNext()) {
                count++;
                String medicine = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRESC_MEDICINE));
                String dosage   = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRESC_DOSAGE));
                String notes    = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRESC_NOTES));
                String date     = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRESC_DATE));
                String status   = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRESC_STATUS));

                View row = inflater.inflate(R.layout.item_prescription_local, container, false);

                setText(row, R.id.tv_medicine, medicine);
                setText(row, R.id.tv_dosage, dosage);
                setText(row, R.id.tv_notes, (notes != null && !notes.isEmpty()) ? notes : "No additional notes");
                setText(row, R.id.tv_date, "Prescribed: " + date);

                Chip chip = row.findViewById(R.id.chip_status);
                if (chip != null) {
                    boolean dispensed = "dispensed".equalsIgnoreCase(status);
                    chip.setText(dispensed ? "Dispensed" : "Pending");
                    chip.setTextColor(getResources().getColor(
                            dispensed ? R.color.accent_green : R.color.pending_yellow, null));
                    chip.setChipBackgroundColorResource(
                            dispensed ? R.color.accent_green_light : R.color.pending_yellow_light);
                }

                container.addView(row);

                // divider
                View divider = new View(requireContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 1);
                lp.setMargins(0, 12, 0, 12);
                divider.setLayoutParams(lp);
                divider.setBackgroundColor(getResources().getColor(R.color.divider, null));
                container.addView(divider);
            }
            cursor.close();
        }
        db.close();

        if (layoutEmpty != null) layoutEmpty.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
        if (tvEmpty != null && count == 0) tvEmpty.setText("No prescriptions from your doctor yet.");
        container.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
    }

    private void setText(View parent, int id, String text) {
        TextView tv = parent.findViewById(id);
        if (tv != null) tv.setText(text);
    }
}
