package com.example.protypebillingsystem;

import android.app.Dialog;
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

public class BillsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bills, container, false);
        PatientSession s = PatientSession.getInstance();

        // Summary totals (Top cards)
        TextView tvTotal = view.findViewById(R.id.tv_total_charges);
        TextView tvNetTop = view.findViewById(R.id.tv_net_payable_top);
        
        if (tvTotal != null) tvTotal.setText(String.format(Locale.getDefault(), "$%.2f", s.totalBill));
        if (tvNetTop != null) tvNetTop.setText(String.format(Locale.getDefault(), "$%.2f", s.netPayable));

        // Load bill rows - Using mock items for prototype transparency
        LinearLayout billContainer = view.findViewById(R.id.bill_items_container);
        if (billContainer != null) {
            billContainer.removeAllViews();
            
            // Adding a few mock items directly
            addMockBillRow(inflater, billContainer, "Consultation Fee", "Mar 28, 2026", 150.00);
            addMockBillRow(inflater, billContainer, "Laboratory Tests", "Mar 29, 2026", 300.00);
            addMockBillRow(inflater, billContainer, "Medication", "Mar 29, 2026", 200.00);
            addMockBillRow(inflater, billContainer, "Ward Charges (5 days)", "Mar 28 - Apr 2", 1500.00);
            addMockBillRow(inflater, billContainer, "X-Ray", "Mar 30, 2026", 300.00);
        }

        // Insurance Transparency Section
        TextView tvSubtotal = view.findViewById(R.id.tv_subtotal);
        TextView tvInsuranceLabel = view.findViewById(R.id.tv_insurance_label);
        TextView tvInsuranceAmount = view.findViewById(R.id.tv_insurance_amount);
        TextView tvBillTotal = view.findViewById(R.id.tv_bill_grand_total);

        if (tvSubtotal != null) tvSubtotal.setText(String.format(Locale.getDefault(), "$%.2f", s.totalBill));
        if (tvInsuranceLabel != null) tvInsuranceLabel.setText(s.insuranceProvider + " Cover");
        if (tvInsuranceAmount != null) tvInsuranceAmount.setText(String.format(Locale.getDefault(), "-$%.2f", s.insuranceCoverage));
        if (tvBillTotal != null) tvBillTotal.setText(String.format(Locale.getDefault(), "$%.2f", s.netPayable));

        view.findViewById(R.id.btn_show_qr).setOnClickListener(v -> showQrDialog());

        return view;
    }

    private void addMockBillRow(LayoutInflater inflater, LinearLayout container, String item, String date, double amount) {
        View row = inflater.inflate(R.layout.item_bill_row, container, false);
        TextView tvName = row.findViewById(R.id.tv_bill_item_name);
        TextView tvDate = row.findViewById(R.id.tv_bill_item_date);
        TextView tvAmount = row.findViewById(R.id.tv_bill_item_amount);
        
        if (tvName != null) tvName.setText(item);
        if (tvDate != null) tvDate.setText(date);
        if (tvAmount != null) tvAmount.setText(String.format(Locale.getDefault(), "$%.2f", amount));
        
        container.addView(row);

        // Divider
        View divider = new View(requireContext());
        divider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2));
        divider.setBackgroundColor(getResources().getColor(R.color.divider, null));
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) divider.getLayoutParams();
        lp.setMargins(0, 16, 0, 16);
        divider.setLayoutParams(lp);
        container.addView(divider);
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
        if (tvTotal != null) tvTotal.setText(String.format(Locale.getDefault(), "Payable: $%.2f", s.netPayable));
        dialog.findViewById(R.id.btn_close_qr).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
