package com.example.protypebillingsystem;

import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.Locale;

public class BillsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bills, container, false);
        loadBills(view, inflater);
        view.findViewById(R.id.btn_show_qr).setOnClickListener(v -> showQrDialog());
        return view;
    }

    private void loadBills(View view, LayoutInflater inflater) {
        PatientSession s = PatientSession.getInstance();
        DatabaseHelper db = new DatabaseHelper(requireContext());

        // Refresh paid/unpaid from DB
        s.paidAmount = db.getTotalPaid(s.id);
        s.unpaidAmount = db.getTotalUnpaid(s.id);

        TextView tvTotal = view.findViewById(R.id.tv_total_charges);
        if (tvTotal != null) tvTotal.setText(String.format(Locale.getDefault(), "RWF %,.0f", s.totalBill * 1300));

        TextView tvPaid = view.findViewById(R.id.tv_amount_paid);
        if (tvPaid != null) tvPaid.setText(String.format(Locale.getDefault(), "RWF %,.0f", s.paidAmount * 1300));

        LinearLayout billContainer = view.findViewById(R.id.bill_items_container);
        if (billContainer != null) {
            billContainer.removeAllViews();
            Cursor cursor = db.getBillsForPatient(s.id);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String item = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ITEM));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DATE));
                    double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_AMOUNT));
                    String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_STATUS));

                    View row = inflater.inflate(R.layout.item_bill_row, billContainer, false);
                    TextView tvName = row.findViewById(R.id.tv_bill_item_name);
                    TextView tvDate = row.findViewById(R.id.tv_bill_item_date);
                    TextView tvAmount = row.findViewById(R.id.tv_bill_item_amount);
                    TextView tvStatus = row.findViewById(R.id.tv_bill_status);
                    FrameLayout badge = row.findViewById(R.id.badge_status);

                    if (tvName != null) tvName.setText(item);
                    if (tvDate != null) tvDate.setText(date);
                    if (tvAmount != null) tvAmount.setText(String.format(Locale.getDefault(), "RWF %,.0f", amount * 1300));

                    boolean isPaid = "paid".equalsIgnoreCase(status);
                    if (tvStatus != null) {
                        tvStatus.setText(isPaid ? "Paid" : "Unpaid");
                        tvStatus.setTextColor(getResources().getColor(
                                isPaid ? R.color.accent_green : R.color.pending_yellow, null));
                    }
                    if (badge != null) {
                        badge.setBackgroundResource(isPaid
                                ? R.drawable.status_badge_paid
                                : R.drawable.status_badge_pending);
                    }

                    billContainer.addView(row);

                    View divider = new View(requireContext());
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, 1);
                    lp.setMargins(0, 20, 0, 20);
                    divider.setLayoutParams(lp);
                    divider.setBackgroundColor(getResources().getColor(R.color.divider, null));
                    billContainer.addView(divider);
                }
                cursor.close();
            }
            db.close();
        }

        TextView tvGrandTotal = view.findViewById(R.id.tv_bill_grand_total);
        if (tvGrandTotal != null) tvGrandTotal.setText(String.format(Locale.getDefault(), "RWF %,.0f", s.unpaidAmount * 1300));
    }

    private void showQrDialog() {
        if (getContext() == null) return;
        PatientSession s = PatientSession.getInstance();
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_qr_code);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        TextView tvBillId = dialog.findViewById(R.id.tv_qr_bill_id);
        TextView tvTotal = dialog.findViewById(R.id.tv_qr_total);
        if (tvBillId != null) tvBillId.setText("Bill ID: #" + s.billId);
        if (tvTotal != null) tvTotal.setText(String.format(Locale.getDefault(), "Total Due: $%.2f", s.unpaidAmount));
        dialog.findViewById(R.id.btn_close_qr).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
