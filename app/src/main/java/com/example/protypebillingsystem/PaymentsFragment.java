package com.example.protypebillingsystem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PaymentsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payments, container, false);

        view.findViewById(R.id.pay_mobile_money).setOnClickListener(v ->
            Toast.makeText(getContext(), "Mobile Money payment coming soon", Toast.LENGTH_SHORT).show());

        view.findViewById(R.id.pay_card).setOnClickListener(v ->
            Toast.makeText(getContext(), "POS Card payment coming soon", Toast.LENGTH_SHORT).show());

        view.findViewById(R.id.pay_cash).setOnClickListener(v ->
            Toast.makeText(getContext(), "Please proceed to the cashier with your QR code", Toast.LENGTH_LONG).show());

        return view;
    }
}
