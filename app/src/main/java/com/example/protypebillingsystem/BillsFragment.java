package com.example.protypebillingsystem;

import android.app.Dialog;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.chip.Chip;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import java.util.Locale;

public class BillsFragment extends Fragment {

    private View rootView;
    private LayoutInflater rootInflater;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bills, container, false);
        rootInflater = inflater;
        loadBills(rootView, inflater);
        rootView.findViewById(R.id.btn_show_qr).setOnClickListener(v -> showQrDialog());
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (rootView != null) loadBills(rootView, rootInflater);
    }

    private void loadBills(View view, LayoutInflater inflater) {
        PatientSession s = PatientSession.getInstance();
        DatabaseHelper db = new DatabaseHelper(requireContext());

        // Refresh all totals from DB so staff-added charges appear immediately
        s.paidAmount   = db.getTotalPaid(s.id);
        s.unpaidAmount = db.getTotalUnpaid(s.id);
        s.totalBill    = s.paidAmount + s.unpaidAmount;

        View btnShowQr = view.findViewById(R.id.btn_show_qr);
        if (btnShowQr != null) btnShowQr.setVisibility(s.unpaidAmount > 0 ? View.VISIBLE : View.GONE);

        TextView tvTotal = view.findViewById(R.id.tv_total_charges);
        if (tvTotal != null) tvTotal.setText(String.format(Locale.getDefault(), "RWF %,.0f", s.totalBill));

        TextView tvPaid = view.findViewById(R.id.tv_amount_paid);
        if (tvPaid != null) tvPaid.setText(String.format(Locale.getDefault(), "RWF %,.0f", s.paidAmount));

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
                    Chip chipStatus = row.findViewById(R.id.tv_bill_status);

                    if (tvName != null) tvName.setText(item);
                    if (tvDate != null) tvDate.setText(date);
                    if (tvAmount != null) tvAmount.setText(String.format(Locale.getDefault(), "RWF %,.0f", amount));

                    boolean isPaid = "paid".equalsIgnoreCase(status);
                    if (chipStatus != null) {
                        chipStatus.setText(isPaid ? "Paid" : "Unpaid");
                        chipStatus.setTextColor(getResources().getColor(
                                isPaid ? R.color.accent_green : R.color.pending_yellow, null));
                        chipStatus.setChipBackgroundColorResource(
                                isPaid ? R.color.accent_green_light : R.color.pending_yellow_light);
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
        }
        db.close();

        TextView tvGrandTotal = view.findViewById(R.id.tv_bill_grand_total);
        if (tvGrandTotal != null) tvGrandTotal.setText(String.format(Locale.getDefault(), "RWF %,.0f", s.unpaidAmount));
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

        ImageView ivQrCode = dialog.findViewById(R.id.iv_qr_code);
        TextView tvBillId = dialog.findViewById(R.id.tv_qr_bill_id);
        TextView tvTotal = dialog.findViewById(R.id.tv_qr_total);

        // Fallback for Bill ID if missing (common in newly registered users)
        String billId = (s.billId != null && !s.billId.isEmpty()) ? s.billId : "BILL-" + System.currentTimeMillis();
        String qrData = "BILL_ID:" + billId + "|PATIENT:" + s.name + "|AMOUNT:" + s.unpaidAmount;
        
        try {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = multiFormatWriter.encode(qrData, BarcodeFormat.QR_CODE, 500, 500);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            if (ivQrCode != null) ivQrCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        if (tvBillId != null) tvBillId.setText("Bill ID: #" + billId);
        if (tvTotal != null) tvTotal.setText(String.format(Locale.getDefault(), "Total Due: RWF %,.0f", s.unpaidAmount));

        dialog.findViewById(R.id.btn_close_qr).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
