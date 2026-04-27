package com.example.protypebillingsystem;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

public class CardPaymentDialog {

    public interface OnPaymentCompleteListener {
        void onPaymentSuccess();
    }

    private final Context context;
    private final double amountRwf;
    private final String billRef;
    private final OnPaymentCompleteListener listener;

    private Dialog currentDialog;

    public CardPaymentDialog(Context context, double amountRwf, String billRef,
                             OnPaymentCompleteListener listener) {
        this.context = context;
        this.amountRwf = amountRwf;
        this.billRef = billRef;
        this.listener = listener;
    }

    public void show() {
        showPaymentDialog();
    }

    // ── Step 1: Card input dialog ─────────────────────────────────────────────

    private void showPaymentDialog() {
        Dialog dialog = createDialog(R.layout.dialog_card_payment);
        currentDialog = dialog;

        TextView tvAmount = dialog.findViewById(R.id.tv_card_amount);
        EditText etCardNumber = dialog.findViewById(R.id.et_card_number);
        EditText etCardName = dialog.findViewById(R.id.et_card_name);
        EditText etExpiry = dialog.findViewById(R.id.et_card_expiry);
        EditText etCvv = dialog.findViewById(R.id.et_card_cvv);
        Button btnPay = dialog.findViewById(R.id.btn_card_pay);
        Button btnCancel = dialog.findViewById(R.id.btn_card_cancel);

        tvAmount.setText(formatRwf(amountRwf));

        // Auto-format card number with spaces
        etCardNumber.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;
                isFormatting = true;
                String text = s.toString().replaceAll("\\s", "");
                StringBuilder formatted = new StringBuilder();
                for (int i = 0; i < text.length(); i++) {
                    if (i > 0 && i % 4 == 0) formatted.append(" ");
                    formatted.append(text.charAt(i));
                }
                s.replace(0, s.length(), formatted.toString());
                isFormatting = false;
            }
        });

        // Auto-format expiry with slash
        etExpiry.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;
                isFormatting = true;
                String text = s.toString().replaceAll("/", "");
                if (text.length() >= 2) {
                    text = text.substring(0, 2) + "/" + text.substring(2);
                }
                s.replace(0, s.length(), text);
                isFormatting = false;
            }
        });

        btnPay.setOnClickListener(v -> {
            String cardNumber = etCardNumber.getText().toString().replaceAll("\\s", "");
            String cardName = etCardName.getText().toString().trim();
            String expiry = etExpiry.getText().toString().trim();
            String cvv = etCvv.getText().toString().trim();

            if (!validateCard(cardNumber, etCardNumber)) return;
            if (cardName.isEmpty()) {
                etCardName.setError("Enter cardholder name");
                etCardName.requestFocus();
                return;
            }
            if (!validateExpiry(expiry, etExpiry)) return;
            if (!validateCvv(cvv, etCvv)) return;

            dialog.dismiss();
            showProcessingDialog(cardNumber);
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        animateIn(dialog);
    }

    // ── Step 2: Processing dialog ─────────────────────────────────────────────

    private void showProcessingDialog(String cardNumber) {
        Dialog dialog = createDialog(R.layout.dialog_mtn_loading);
        currentDialog = dialog;

        TextView tvPhone = dialog.findViewById(R.id.tv_loading_phone);
        tvPhone.setText("Processing card payment...");

        dialog.setCancelable(false);
        dialog.show();
        animateIn(dialog);

        // Simulate 2s processing
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            dialog.dismiss();
            showResultDialog(true, getMaskedCard(cardNumber));
        }, 2000);
    }

    // ── Step 3: Result dialog ─────────────────────────────────────────────────

    private void showResultDialog(boolean success, String maskedCard) {
        Dialog dialog = createDialog(R.layout.dialog_mtn_result);
        currentDialog = dialog;

        FrameLayout iconContainer = dialog.findViewById(R.id.icon_container);
        TextView tvIcon = dialog.findViewById(R.id.tv_result_icon);
        TextView tvTitle = dialog.findViewById(R.id.tv_result_title);
        TextView tvMessage = dialog.findViewById(R.id.tv_result_message);
        TextView tvAmount = dialog.findViewById(R.id.tv_result_amount);
        TextView tvPhone = dialog.findViewById(R.id.tv_result_phone);
        TextView tvTxnId = dialog.findViewById(R.id.tv_result_txn_id);
        TextView tvDate = dialog.findViewById(R.id.tv_result_datetime);
        Button btnDone = dialog.findViewById(R.id.btn_result_done);

        if (success) {
            iconContainer.setBackgroundResource(R.drawable.mtn_success_circle);
            tvIcon.setText("✓");
            tvIcon.setTextColor(Color.parseColor("#2E7D32"));
            tvTitle.setText("Payment Successful");
            tvTitle.setTextColor(Color.parseColor("#2E7D32"));
            tvMessage.setText("Your card payment has been processed successfully.");
            tvAmount.setText(formatRwf(amountRwf));
            tvPhone.setText(maskedCard);
            tvTxnId.setText(generateTxnId());
            tvDate.setText(getCurrentDateTime());
            btnDone.setBackgroundResource(R.drawable.btn_primary);
            btnDone.setTextColor(Color.WHITE);
        } else {
            iconContainer.setBackgroundResource(R.drawable.status_badge_pending);
            tvIcon.setText("✕");
            tvIcon.setTextColor(Color.parseColor("#C62828"));
            tvTitle.setText("Payment Failed");
            tvTitle.setTextColor(Color.parseColor("#C62828"));
            tvMessage.setText("Card declined or insufficient funds.\nPlease try again.");
            tvAmount.setText(formatRwf(amountRwf));
            tvPhone.setText(maskedCard);
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

    // ── Validation helpers ────────────────────────────────────────────────────

    private boolean validateCard(String cardNumber, EditText field) {
        if (TextUtils.isEmpty(cardNumber)) {
            field.setError("Enter card number");
            field.requestFocus();
            return false;
        }
        if (cardNumber.length() < 13 || cardNumber.length() > 19) {
            field.setError("Invalid card number");
            field.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validateExpiry(String expiry, EditText field) {
        if (TextUtils.isEmpty(expiry)) {
            field.setError("Enter expiry date");
            field.requestFocus();
            return false;
        }
        if (!expiry.matches("\\d{2}/\\d{2}")) {
            field.setError("Format: MM/YY");
            field.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validateCvv(String cvv, EditText field) {
        if (TextUtils.isEmpty(cvv)) {
            field.setError("Enter CVV");
            field.requestFocus();
            return false;
        }
        if (cvv.length() < 3) {
            field.setError("Invalid CVV");
            field.requestFocus();
            return false;
        }
        return true;
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

    private String getMaskedCard(String cardNumber) {
        if (cardNumber.length() < 4) return "•••• " + cardNumber;
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        return "•••• •••• •••• " + last4;
    }

    private String formatRwf(double amount) {
        return String.format(Locale.getDefault(), "RWF %,.0f", amount);
    }

    private String generateTxnId() {
        Random rnd = new Random();
        long id = 1000000000L + (long)(rnd.nextDouble() * 9000000000L);
        return "CARD" + id;
    }

    private String getCurrentDateTime() {
        return new SimpleDateFormat("MMM dd, yyyy  hh:mm a", Locale.getDefault())
                .format(new Date());
    }
}
