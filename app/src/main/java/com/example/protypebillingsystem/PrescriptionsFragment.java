package com.example.protypebillingsystem;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.protypebillingsystem.adapters.PrescriptionAdapter;
import com.example.protypebillingsystem.fhir.FhirClient;
import com.example.protypebillingsystem.fhir.models.FhirBundle;
import com.example.protypebillingsystem.fhir.models.MedicationRequest;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrescriptionsFragment extends Fragment {

    private static final String TAG = "PrescriptionsFragment";
    private RecyclerView recyclerView;
    private PrescriptionAdapter adapter;
    private ProgressBar progressBar;
    private View layoutEmpty;
    private TextView tvEmptyMessage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prescriptions, container, false);
        
        recyclerView = view.findViewById(R.id.rv_prescriptions);
        progressBar = view.findViewById(R.id.pb_loading);
        layoutEmpty = view.findViewById(R.id.layout_empty);
        tvEmptyMessage = view.findViewById(R.id.tv_empty_message);
        view.findViewById(R.id.btn_retry).setOnClickListener(v -> fetchPrescriptions());

        setupRecyclerView();
        fetchPrescriptions();
        
        return view;
    }

    private void setupRecyclerView() {
        adapter = new PrescriptionAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void fetchPrescriptions() {
        PatientSession session = PatientSession.getInstance();
        String searchId = session.patientId;

        showLoading();

        FhirClient.getInstance()
                .getApiService()
                .getPrescriptions(searchId)
                .enqueue(new Callback<FhirBundle>() {
                    @Override
                    public void onResponse(@NonNull Call<FhirBundle> call, @NonNull Response<FhirBundle> response) {
                        if (!isAdded()) return;
                        
                        if (response.isSuccessful() && response.body() != null) {
                            processResponse(response.body());
                        } else {
                            showError("Failed to fetch prescriptions (Error " + response.code() + ")");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<FhirBundle> call, @NonNull Throwable t) {
                        if (!isAdded()) return;
                        showError("Network error: Please check your connection");
                        Log.e(TAG, "API Failure", t);
                    }
                });
    }

    private void processResponse(FhirBundle bundle) {
        List<FhirBundle.Entry> entries = bundle.getEntry();
        
        if (entries == null || entries.isEmpty()) {
            showEmpty("No prescriptions found for your account.");
            return;
        }

        List<MedicationRequest> medications = new ArrayList<>();
        Gson gson = new Gson();

        for (FhirBundle.Entry entry : entries) {
            if (entry.getResource() != null) {
                try {
                    // Convert JsonObject to our type-safe MedicationRequest model
                    MedicationRequest med = gson.fromJson(entry.getResource(), MedicationRequest.class);
                    if (med != null) {
                        medications.add(med);
                    }
                } catch (Exception e) {
                    Log.w(TAG, "Error parsing medication resource", e);
                }
            }
        }

        if (medications.isEmpty()) {
            showEmpty("Could not parse prescription data.");
        } else {
            showContent(medications);
        }
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
    }

    private void showContent(List<MedicationRequest> medications) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);
        adapter.submitList(medications);
    }

    private void showEmpty(String message) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);
        tvEmptyMessage.setText(message);
    }

    private void showError(String message) {
        showEmpty(message);
    }
}
