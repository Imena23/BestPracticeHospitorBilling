package com.example.protypebillingsystem;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class MtnMomoDialog {

    public interface OnPaymentCompleteListener {
        void onPaymentSuccess();
    }

    private final Context context;
    private final double amountRwf;
    private final String billRef;
    private final OnPaymentCompleteListener listener;

    private Dialog currentDialog;

    public MtnMomoDialog(Context context, double amountRwf, String billRef,
                         OnPaymentCompleteListener listener) {
        this.context = context;
        this.amountRwf = amountRwf;
        this.billRef = billRef;
        this.listener = listener;
    }

    public void show() {
        showPaymentDialog();
    }

    // ── Step 1: Payment input dialog ──────────────────────────────────────────

    private void showPaymentDialog() {
        Dialog dialog = createDialog(R.layout.dialog_mtn_momo);
        currentDialog = dialog;

        TextView tvAmount = dialog.findViewById(R.id.tv_momo_amount);
        TextView tvRef    = dialog.findViewById(R.id.tv_momo_bill_ref);
        EditText etPhone  = dialog.findViewById(R.id.et_momo_phone);
        Button   btnSend  = dialog.findViewById(R.id.btn_momo_send);
        Button   btnCancel = dialog.findViewById(R.id.btn_momo_cancel);

        tvAmount.setText(formatRwf(amountRwf));
        tvRef.setText("Ref: " + billRef);

        btnSend.setOnClickListener(v -> {
            String phone = etPhone.getText().toString().trim();
            if (!isValidMtnNumber(phone)) {
                etPhone.setError("Enter a valid MTN number (e.g. 078XXXXXXX)");
                etPhone.requestFocus();
                return;
            }
            dialog.dismiss();
            showLoadingDialog("+250 " + phone);
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        animateIn(dialog);
    }

    // ── Step 2: Loading dialog ────────────────────────────────────────────────

    private void showLoadingDialog(String formattedPhone) {
        Dialog dialog = createDialog(R.layout.dialog_mtn_loading);
        currentDialog = dialog;

        TextView tvPhone = dialog.findViewById(R.id.tv_loading_phone);
        tvPhone.setText(formattedPhone);

        dialog.setCancelable(false);
        dialog.show();
        animateIn(dialog);

        // Simulate 2s network delay then show transaction selection
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            dialog.dismiss();
            showTransactionSelectDialog(formattedPhone);
        }, 2000);
    }

    // ── Step 3: Transaction selection dialog ──────────────────────────────────

    private void showTransactionSelectDialog(String formattedPhone) {
        Dialog dialog = createDialog(R.layout.dialog_mtn_select);
        currentDialog = dialog;

        TextView tvTxnId = dialog.findViewById(R.id.tv_select_txn_id);
        TextView tvAmount = dialog.findViewById(R.id.tv_select_amount);
        Button btnDismiss = dialog.findViewById(R.id.btn_select_dismiss);
        Button btnReply = dialog.findViewById(R.id.btn_select_reply);

        // Generate transaction ID
        Random rnd = new Random();
        long txnId = 1000000000L + (long)(rnd.nextDouble() * 9000000000L);
        tvTxnId.setText(String.valueOf(txnId));
        tvAmount.setText(String.format(Locale.getDefault(), "%,.0f RWF", amountRwf));

        btnDismiss.setOnClickListener(v -> {
            dialog.dismiss();
            showResultDialog(false, formattedPhone);
        });

        btnReply.setOnClickListener(v -> {
            dialog.dismiss();
            showPinDialog(formattedPhone);
        });

        dialog.show();
        animateIn(dialog);
    }

    // ── Step 4: PIN prompt dialog ─────────────────────────────────────────────

    private void showPinDialog(String formattedPhone) {
        Dialog dialog = createDialog(R.layout.dialog_mtn_pin);
        currentDialog = dialog;

        EditText etPin = dialog.findViewById(R.id.et_momo_pin);
        Button btnCancel = dialog.findViewById(R.id.btn_pin_cancel);
        Button btnReply = dialog.findViewById(R.id.btn_pin_reply);

        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
            showResultDialog(false, formattedPhone);
        });

        btnReply.setOnClickListener(v -> {
            String pin = etPin.getText().toString().trim();
            if (pin.isEmpty() || pin.length() < 4) {
                etPin.setError("Enter your MoMo PIN");
                etPin.requestFocus();
                return;
            }
            dialog.dismiss();
            showProcessingDialog(formattedPhone);
        });

        dialog.show();
        animateIn(dialog);
    }

    // ── Step 5: Processing dialog ─────────────────────────────────────────────

    private void showProcessingDialog(String formattedPhone) {
        Dialog dialog = createDialog(R.layout.dialog_mtn_loading);
        currentDialog = dialog;

        TextView tvPhone = dialog.findViewById(R.id.tv_loading_phone);
        tvPhone.setText("Processing payment...");

        dialog.setCancelable(false);
        dialog.show();
        animateIn(dialog);

        // Simulate 1.5s processing then show success
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            dialog.dismiss();
            showResultDialog(true, formattedPhone);
        }, 1500);
    }

    // ── Step 6: Result dialog ─────────────────────────────────────────────────

    private void showResultDialog(boolean success, String phone) {
        Dialog dialog = createDialog(R.layout.dialog_mtn_result);
        currentDialog = dialog;

        FrameLayout iconContainer = dialog.findViewById(R.id.icon_container);
        TextView tvIcon    = dialog.findViewById(R.id.tv_result_icon);
        TextView tvTitle   = dialog.findViewById(R.id.tv_result_title);
        TextView tvMessage = dialog.findViewById(R.id.tv_result_message);
        TextView tvAmount  = dialog.findViewById(R.id.tv_result_amount);
        TextView tvPhone   = dialog.findViewById(R.id.tv_result_phone);
        TextView tvTxnId   = dialog.findViewById(R.id.tv_result_txn_id);
        TextView tvDate    = dialog.findViewById(R.id.tv_result_datetime);
        Button   btnDone   = dialog.findViewById(R.id.btn_result_done);

        if (success) {
            iconContainer.setBackgroundResource(R.drawable.mtn_success_circle);
            tvIcon.setText("✓");
            tvIcon.setTextColor(Color.parseColor("#2E7D32"));
            tvTitle.setText("Payment Successful");
            tvTitle.setTextColor(Color.parseColor("#2E7D32"));
            tvMessage.setText("Your payment has been processed successfully.");
            tvAmount.setText(formatRwf(amountRwf));
            tvPhone.setText(phone);
            tvTxnId.setText(generateTxnId());
            tvDate.setText(getCurrentDateTime());
            btnDone.setBackgroundResource(R.drawable.mtn_btn_yellow);
            btnDone.setTextColor(Color.BLACK);
        } else {
            iconContainer.setBackgroundResource(R.drawable.status_badge_pending);
            tvIcon.setText("✕");
            tvIcon.setTextColor(Color.parseColor("#C62828"));
            tvTitle.setText("Payment Failed");
            tvTitle.setTextColor(Color.parseColor("#C62828"));
            tvMessage.setText("Request cancelled or insufficient balance.\nPlease try again.");
            tvAmount.setText(formatRwf(amountRwf));
            tvPhone.setText(phone);
            tvTxnId.setText("—");
            tvDate.setText(getCurrentDateTime());
            btnDone.setBackgroundResource(R.drawable.btn_primary);
            btnDone.setTextColor(Color.WHITE);
        }

        btnDone.setOnClickListener(v -> {
            dialog.dismiss();
            if (success && listener != null) listener.onPaymentSuccess();
        });

        dialog.show();
        animateIn(dialog);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Dialog createDialog(int layoutRes) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(context).inflate(layoutRes, null);
        dialog.setContentView(view);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            // Dim background
            android.view.WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.dimAmount = 0.6f;
            dialog.getWindow().setAttributes(lp);
            dialog.getWindow().addFlags(
                    android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    private void animateIn(Dialog dialog) {
        if (dialog.getWindow() != null) {
            View decorView = dialog.getWindow().getDecorView();
            decorView.setAlpha(0f);
            decorView.setTranslationY(60f);
            decorView.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(280)
                    .setInterpolator(new android.view.animation.DecelerateInterpolator())
                    .start();
        }
    }

    private boolean isValidMtnNumber(String phone) {
        // MTN Rwanda prefixes: 078, 079, 083, 073
        if (TextUtils.isEmpty(phone)) return false;
        String cleaned = phone.replaceAll("\\s", "");
        // Accept with or without leading 0
        return cleaned.matches("0?(78|79|83|73)\\d{7}");
    }

    private String formatRwf(double amount) {
        return String.format(Locale.getDefault(), "RWF %,.0f", amount);
    }

    private String generateTxnId() {
        Random rnd = new Random();
        long id = 1000000000L + (long)(rnd.nextDouble() * 9000000000L);
        return "MTN" + id;
    }

    private String getCurrentDateTime() {
        return new SimpleDateFormat("MMM dd, yyyy  hh:mm a", Locale.getDefault())
                .format(new Date());
    }
}
