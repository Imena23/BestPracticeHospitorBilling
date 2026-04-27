package com.example.protypebillingsystem;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.chip.Chip;
import java.util.Locale;

public class PaymentsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payments, container, false);
        refreshUI(view, inflater);

        view.findViewById(R.id.pay_mobile_money).setOnClickListener(v ->
                showMtnMomoDialog(view, inflater));

        view.findViewById(R.id.pay_card).setOnClickListener(v ->
                showCardPaymentDialog(view, inflater));

        view.findViewById(R.id.pay_cash).setOnClickListener(v ->
                Toast.makeText(getContext(),
                        "Please proceed to the cashier with your QR code",
                        Toast.LENGTH_LONG).show());

        return view;
    }

    private void refreshUI(View view, LayoutInflater inflater) {
        PatientSession s = PatientSession.getInstance();
        DatabaseHelper db = new DatabaseHelper(requireContext());
        s.paidAmount = db.getTotalPaid(s.id);
        s.unpaidAmount = db.getTotalUnpaid(s.id);
        db.close();

        // Outstanding balance — show in RWF
        TextView tvBalance = view.findViewById(R.id.tv_outstanding_balance);
        if (tvBalance != null) tvBalance.setText(
                String.format(Locale.getDefault(), "RWF %,.0f", s.unpaidAmount * 1300));

        // Status badge (now a Chip)
        Chip chipStatus = view.findViewById(R.id.badge_payment_status);
        if (chipStatus != null) {
            boolean allPaid = s.unpaidAmount <= 0;
            chipStatus.setText(allPaid ? "Paid" : "Pending");
            chipStatus.setTextColor(getResources().getColor(
                    allPaid ? R.color.accent_green : R.color.pending_yellow, null));
            chipStatus.setChipBackgroundColorResource(
                    allPaid ? R.color.accent_green_light : R.color.pending_yellow_light);
        }

        // Payment history list
        loadPaymentHistory(view, inflater);
    }

    private void showMtnMomoDialog(View view, LayoutInflater inflater) {
        PatientSession s = PatientSession.getInstance();
        if (s.unpaidAmount <= 0) {
            Toast.makeText(getContext(), "No outstanding balance to pay", Toast.LENGTH_SHORT).show();
            return;
        }
        // Convert USD → RWF (1 USD ≈ 1,300 RWF for demo)
        double amountRwf = s.unpaidAmount * 1300;
        new MtnMomoDialog(requireContext(), amountRwf, s.billId, () -> {
            // Mark bills paid in DB after successful MoMo payment
            processPayment("MTN Mobile Money", view, inflater);
        }).show();
    }

    private void showCardPaymentDialog(View view, LayoutInflater inflater) {
        PatientSession s = PatientSession.getInstance();
        if (s.unpaidAmount <= 0) {
            Toast.makeText(getContext(), "No outstanding balance to pay", Toast.LENGTH_SHORT).show();
            return;
        }
        double amountRwf = s.unpaidAmount * 1300;
        new CardPaymentDialog(requireContext(), amountRwf, s.billId, () -> {
            processPayment("Visa/Mastercard", view, inflater);
        }).show();
    }

    private void confirmPayment(String method, View view, LayoutInflater inflater) {
        PatientSession s = PatientSession.getInstance();
        if (s.unpaidAmount <= 0) {
            Toast.makeText(getContext(), "No outstanding balance to pay", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirm Payment")
                .setMessage(String.format(Locale.getDefault(),
                        "Pay $%.2f via %s?", s.unpaidAmount, method))
                .setPositiveButton("Pay Now", (dialog, which) -> processPayment(method, view, inflater))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void processPayment(String method, View view, LayoutInflater inflater) {
        PatientSession s = PatientSession.getInstance();
        DatabaseHelper db = new DatabaseHelper(requireContext());
        boolean success = db.updateBillStatus(s.id, "paid");
        db.close();

        if (success) {
            s.paidAmount = s.totalBill;
            s.unpaidAmount = 0;
            Toast.makeText(getContext(),
                    "Payment successful via " + method + "!", Toast.LENGTH_LONG).show();
            refreshUI(view, inflater);
        } else {
            Toast.makeText(getContext(), "Payment failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadPaymentHistory(View view, LayoutInflater inflater) {
        LinearLayout historyContainer = view.findViewById(R.id.payment_history_container);
        if (historyContainer == null) return;
        historyContainer.removeAllViews();

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

                View row = inflater.inflate(R.layout.item_activity_row, historyContainer, false);
                TextView tvTitle = row.findViewById(R.id.tv_activity_title);
                TextView tvDate = row.findViewById(R.id.tv_activity_date);
                TextView tvAmount = row.findViewById(R.id.tv_activity_amount);
                if (tvTitle != null) tvTitle.setText(item);
                if (tvDate != null) tvDate.setText(date);
                if (tvAmount != null) {
                    tvAmount.setText(String.format(Locale.getDefault(), "RWF %,.0f", amount * 1300));
                    tvAmount.setTextColor(getResources().getColor(R.color.accent_green, null));
                }
                historyContainer.addView(row);

                View divider = new View(requireContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 1);
                lp.setMargins(0, 10, 0, 10);
                divider.setLayoutParams(lp);
                divider.setBackgroundColor(getResources().getColor(R.color.divider, null));
                historyContainer.addView(divider);
            }
            cursor.close();
        }
        db.close();

        if (!hasPaid) {
            TextView empty = new TextView(requireContext());
            empty.setText("No payments recorded yet");
            empty.setTextColor(getResources().getColor(R.color.text_hint, null));
            empty.setTextSize(14);
            empty.setGravity(android.view.Gravity.CENTER);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 40, 0, 40);
            empty.setLayoutParams(lp);
            historyContainer.addView(empty);
        }
    }
}
