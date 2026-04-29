package com.example.protypebillingsystem;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.protypebillingsystem.fhir.FhirClient;
import com.example.protypebillingsystem.fhir.models.FhirBundle;
import com.google.gson.JsonObject;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrescriptionsFragment extends Fragment {

    private static final String TAG = "PrescriptionsFragment";
    private LinearLayout container;
    private ProgressBar progressBar;
    private TextView tvEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prescriptions, container, false);
        
        this.container = view.findViewById(R.id.prescription_container);
        this.progressBar = view.findViewById(R.id.pb_loading);
        this.tvEmpty = view.findViewById(R.id.tv_empty_prescriptions);

        fetchPrescriptions(inflater);
        
        return view;
    }

    private void fetchPrescriptions(LayoutInflater inflater) {
        PatientSession session = PatientSession.getInstance();
        // Use the patient's local ID or patientId string for FHIR search
        // For HAPI FHIR public server, we search by the patientId we generated
        String searchId = session.patientId;

        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        container.removeAllViews();

        FhirClient.getInstance()
                .getApiService()
                .getPrescriptions(searchId)
                .enqueue(new Callback<FhirBundle>() {
                    @Override
                    public void onResponse(Call<FhirBundle> call, Response<FhirBundle> response) {
                        if (!isAdded()) return;
                        progressBar.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null) {
                            displayPrescriptions(response.body(), inflater);
                        } else {
                            handleError("Failed to fetch prescriptions (Error " + response.code() + ")");
                        }
                    }

                    @Override
                    public void onFailure(Call<FhirBundle> call, Throwable t) {
                        if (!isAdded()) return;
                        progressBar.setVisibility(View.GONE);
                        handleError("Network error: Please check your connection");
                        Log.e(TAG, "API Failure", t);
                    }
                });
    }

    private void displayPrescriptions(FhirBundle bundle, LayoutInflater inflater) {
        List<FhirBundle.Entry> entries = bundle.getEntry();
        
        if (entries == null || entries.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            return;
        }

        for (FhirBundle.Entry entry : entries) {
            JsonObject resource = entry.getResource();
            if (resource == null) continue;

            // Extract basic medication info from FHIR MedicationRequest resource
            String medicationName = "Unknown Medication";
            String dosage = "As directed";
            String date = "N/A";

            try {
                if (resource.has("medicationCodeableConcept")) {
                    medicationName = resource.getAsJsonObject("medicationCodeableConcept")
                            .getAsJsonArray("coding").get(0).getAsJsonObject()
                            .get("display").getAsString();
                } else if (resource.has("medicationReference")) {
                    medicationName = resource.getAsJsonObject("medicationReference")
                            .get("display").getAsString();
                }

                if (resource.has("authoredOn")) {
                    date = resource.get("authoredOn").getAsString();
                }

                if (resource.has("dosageInstruction")) {
                    dosage = resource.getAsJsonArray("dosageInstruction").get(0)
                            .getAsJsonObject().get("text").getAsString();
                }
            } catch (Exception e) {
                Log.w(TAG, "Error parsing resource", e);
            }

            View row = inflater.inflate(R.layout.item_activity_row, container, false);
            TextView tvTitle = row.findViewById(R.id.tv_activity_title);
            TextView tvDate = row.findViewById(R.id.tv_activity_date);
            TextView tvDosage = row.findViewById(R.id.tv_activity_amount);

            if (tvTitle != null) tvTitle.setText(medicationName);
            if (tvDate != null) tvDate.setText("Prescribed: " + date);
            if (tvDosage != null) {
                tvDosage.setText(dosage);
                tvDosage.setTextColor(getResources().getColor(R.color.md_primary, null));
                tvDosage.setTextSize(12);
            }

            container.addView(row);

            // Add divider
            View divider = new View(requireContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 1);
            lp.setMargins(0, 10, 0, 10);
            divider.setLayoutParams(lp);
            divider.setBackgroundColor(getResources().getColor(R.color.divider, null));
            container.addView(divider);
        }
    }

    private void handleError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        tvEmpty.setText(message);
        tvEmpty.setVisibility(View.VISIBLE);
    }
}
