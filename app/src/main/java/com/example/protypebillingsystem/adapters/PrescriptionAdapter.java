package com.example.protypebillingsystem.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.protypebillingsystem.R;
import com.example.protypebillingsystem.fhir.models.MedicationRequest;

public class PrescriptionAdapter extends ListAdapter<MedicationRequest, PrescriptionAdapter.ViewHolder> {

    public PrescriptionAdapter() {
        super(new DiffUtil.ItemCallback<MedicationRequest>() {
            @Override
            public boolean areItemsTheSame(@NonNull MedicationRequest oldItem, @NonNull MedicationRequest newItem) {
                return oldItem == newItem; // For simplicity, since MedicationRequest doesn't have an ID field in this model yet
            }

            @Override
            public boolean areContentsTheSame(@NonNull MedicationRequest oldItem, @NonNull MedicationRequest newItem) {
                return oldItem.getMedicationName().equals(newItem.getMedicationName()) &&
                        oldItem.getAuthoredOn().equals(newItem.getAuthoredOn());
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prescription, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvDate;
        private final TextView tvDosage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_medication_name);
            tvDate = itemView.findViewById(R.id.tv_authored_on);
            tvDosage = itemView.findViewById(R.id.tv_dosage);
        }

        public void bind(MedicationRequest medication) {
            tvName.setText(medication.getMedicationName());
            tvDate.setText("Prescribed: " + medication.getAuthoredOn());
            tvDosage.setText(medication.getDosageText());
        }
    }
}
