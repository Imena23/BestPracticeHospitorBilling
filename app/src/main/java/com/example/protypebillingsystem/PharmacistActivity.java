package com.example.protypebillingsystem;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PharmacistActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private PrescAdapter adapter;
    private final List<PrescItem> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacist);
        db = new DatabaseHelper(this);

        ((TextView) findViewById(R.id.tv_welcome)).setText(StaffSession.getInstance().name);

        RecyclerView rv = findViewById(R.id.rv_prescriptions);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PrescAdapter(list, this::showDispenseDialog);
        rv.setAdapter(adapter);

        findViewById(R.id.btn_logout).setOnClickListener(v -> { StaffSession.getInstance().clear(); finish(); });
        loadPrescriptions();
    }

    @Override protected void onResume() { super.onResume(); loadPrescriptions(); }
    @Override protected void onDestroy() { super.onDestroy(); db.close(); }

    private void loadPrescriptions() {
        list.clear();
        Cursor c = db.getPendingPrescriptions();
        if (c != null) {
            while (c.moveToNext()) {
                long patientRef = c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.COL_PRESC_PATIENT_REF));
                list.add(new PrescItem(
                        c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.COL_PRESC_ID)),
                        patientRef, getPatientName(patientRef),
                        c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_PRESC_MEDICINE)),
                        c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_PRESC_DOSAGE)),
                        c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_PRESC_DATE))
                ));
            }
            c.close();
        }
        adapter.notifyDataSetChanged();
        findViewById(R.id.tv_empty).setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private String getPatientName(long ref) {
        Cursor c = db.getPatientById(ref);
        String name = "Unknown";
        if (c != null && c.moveToFirst()) { name = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_NAME)); c.close(); }
        return name;
    }

    private void showDispenseDialog(PrescItem item) {
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_dispense, null);
        TextInputEditText etAmount = v.findViewById(R.id.et_amount);
        ((TextView) v.findViewById(R.id.tv_info)).setText(item.patientName + "\n" + item.medicine + " — " + item.dosage);
        new AlertDialog.Builder(this).setTitle("Dispense Medication").setView(v)
                .setPositiveButton("Dispense & Charge", (d, w) -> {
                    String amtStr = etAmount.getText().toString().trim();
                    if (amtStr.isEmpty()) { Toast.makeText(this, "Enter charge amount", Toast.LENGTH_SHORT).show(); return; }
                    String today = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());
                    db.dispensePrescription(item.id, item.patientRef, item.medicine, Double.parseDouble(amtStr), today);
                    Toast.makeText(this, "Dispensed & bill added for " + item.patientName, Toast.LENGTH_SHORT).show();
                    loadPrescriptions();
                }).setNegativeButton("Cancel", null).show();
    }

    static class PrescItem {
        long id, patientRef;
        String patientName, medicine, dosage, date;
        PrescItem(long id, long patientRef, String patientName, String medicine, String dosage, String date) {
            this.id = id; this.patientRef = patientRef; this.patientName = patientName;
            this.medicine = medicine; this.dosage = dosage; this.date = date;
        }
    }

    static class PrescAdapter extends RecyclerView.Adapter<PrescAdapter.VH> {
        interface OnDispense { void onDispense(PrescItem item); }
        private final List<PrescItem> list;
        private final OnDispense onDispense;
        PrescAdapter(List<PrescItem> list, OnDispense onDispense) { this.list = list; this.onDispense = onDispense; }
        @Override public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_presc_row, parent, false));
        }
        @Override public void onBindViewHolder(VH h, int pos) { h.bind(list.get(pos), onDispense); }
        @Override public int getItemCount() { return list.size(); }
        static class VH extends RecyclerView.ViewHolder {
            TextView tvPatient, tvMedicine, tvDosage, tvDate; MaterialButton btnAction;
            VH(View v) { super(v);
                tvPatient = v.findViewById(R.id.tv_patient); tvMedicine = v.findViewById(R.id.tv_medicine);
                tvDosage = v.findViewById(R.id.tv_dosage); tvDate = v.findViewById(R.id.tv_date);
                btnAction = v.findViewById(R.id.btn_action);
            }
            void bind(PrescItem item, OnDispense cb) {
                tvPatient.setText(item.patientName); tvMedicine.setText(item.medicine);
                tvDosage.setText(item.dosage); tvDate.setText(item.date);
                btnAction.setText("Dispense");
                btnAction.setOnClickListener(v -> cb.onDispense(item));
            }
        }
    }
}
