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
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DoctorActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private long selectedPatientId = -1;
    private String selectedPatientName = "";
    private RecyclerView rvItems;
    private TextView tvWelcome, tvPatientLabel;
    private TabLayout tabs;
    private int currentTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);
        db = new DatabaseHelper(this);

        tvWelcome = findViewById(R.id.tv_welcome);
        tvWelcome.setText(StaffSession.getInstance().name);
        tvPatientLabel = findViewById(R.id.tv_patient_label);

        tabs = findViewById(R.id.tabs);
        rvItems = findViewById(R.id.rv_items);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            public void onTabSelected(TabLayout.Tab tab) { currentTab = tab.getPosition(); loadList(); }
            public void onTabUnselected(TabLayout.Tab tab) {}
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        findViewById(R.id.btn_select_patient).setOnClickListener(v -> showPatientPicker());
        findViewById(R.id.btn_add_order).setOnClickListener(v -> {
            if (selectedPatientId == -1) { Toast.makeText(this, "Select a patient first", Toast.LENGTH_SHORT).show(); return; }
            showAddOrderDialog();
        });
        findViewById(R.id.btn_logout).setOnClickListener(v -> { StaffSession.getInstance().clear(); finish(); });
    }

    @Override
    protected void onDestroy() { super.onDestroy(); db.close(); }

    private void showPatientPicker() {
        Cursor c = db.getAllPatients();
        List<String> names = new ArrayList<>();
        List<Long> ids = new ArrayList<>();
        if (c != null) {
            while (c.moveToNext()) {
                ids.add(c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.COL_ID)));
                names.add(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_NAME))
                        + " (" + c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_PATIENT_ID)) + ")");
            }
            c.close();
        }
        if (names.isEmpty()) { Toast.makeText(this, "No patients registered", Toast.LENGTH_SHORT).show(); return; }
        new AlertDialog.Builder(this)
                .setTitle("Select Patient")
                .setItems(names.toArray(new String[0]), (d, which) -> {
                    selectedPatientId = ids.get(which);
                    selectedPatientName = names.get(which);
                    tvPatientLabel.setText("Patient: " + selectedPatientName);
                    loadList();
                }).show();
    }

    private void loadList() {
        if (selectedPatientId == -1) return;
        List<String[]> rows = new ArrayList<>();
        if (currentTab == 0) {
            Cursor c = db.getPrescriptionsForPatient(selectedPatientId);
            if (c != null) { while (c.moveToNext()) { rows.add(new String[]{
                c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_PRESC_MEDICINE)),
                c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_PRESC_DOSAGE)),
                c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_PRESC_STATUS)),
                c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_PRESC_DATE))
            }); } c.close(); }
        } else if (currentTab == 1) {
            Cursor c = db.getLabOrdersForPatient(selectedPatientId);
            if (c != null) { while (c.moveToNext()) { rows.add(new String[]{
                c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_LAB_TEST)),
                c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_LAB_NOTES)),
                c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_LAB_STATUS)),
                c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_LAB_DATE))
            }); } c.close(); }
        } else {
            Cursor c = db.getNurseTasksForPatient(selectedPatientId);
            if (c != null) { while (c.moveToNext()) { rows.add(new String[]{
                c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_TASK_SERVICE)),
                c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_TASK_NOTES)),
                c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_TASK_STATUS)),
                c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_TASK_DATE))
            }); } c.close(); }
        }
        rvItems.setAdapter(new SimpleOrderAdapter(rows));
    }

    private void showAddOrderDialog() {
        String today = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());
        if (currentTab == 0) {
            View v = LayoutInflater.from(this).inflate(R.layout.dialog_add_prescription, null);
            TextInputEditText etMed = v.findViewById(R.id.et_medicine);
            TextInputEditText etDosage = v.findViewById(R.id.et_dosage);
            TextInputEditText etNotes = v.findViewById(R.id.et_notes);
            new AlertDialog.Builder(this).setTitle("New Prescription").setView(v)
                    .setPositiveButton("Prescribe", (d, w) -> {
                        String med = etMed.getText().toString().trim();
                        if (med.isEmpty()) { Toast.makeText(this, "Enter medicine name", Toast.LENGTH_SHORT).show(); return; }
                        db.addPrescription(selectedPatientId, StaffSession.getInstance().id, med,
                                etDosage.getText().toString().trim(), etNotes.getText().toString().trim(), today);
                        Toast.makeText(this, "Prescription added", Toast.LENGTH_SHORT).show();
                        loadList();
                    }).setNegativeButton("Cancel", null).show();
        } else if (currentTab == 1) {
            View v = LayoutInflater.from(this).inflate(R.layout.dialog_add_lab_order, null);
            TextInputEditText etTest = v.findViewById(R.id.et_test);
            TextInputEditText etNotes = v.findViewById(R.id.et_notes);
            new AlertDialog.Builder(this).setTitle("Order Lab Test").setView(v)
                    .setPositiveButton("Order", (d, w) -> {
                        String test = etTest.getText().toString().trim();
                        if (test.isEmpty()) { Toast.makeText(this, "Enter test name", Toast.LENGTH_SHORT).show(); return; }
                        db.addLabOrder(selectedPatientId, StaffSession.getInstance().id, test,
                                etNotes.getText().toString().trim(), today);
                        Toast.makeText(this, "Lab order sent", Toast.LENGTH_SHORT).show();
                        loadList();
                    }).setNegativeButton("Cancel", null).show();
        } else {
            View v = LayoutInflater.from(this).inflate(R.layout.dialog_add_nurse_task, null);
            TextInputEditText etService = v.findViewById(R.id.et_service);
            TextInputEditText etNotes = v.findViewById(R.id.et_notes);
            new AlertDialog.Builder(this).setTitle("Assign Nurse Task").setView(v)
                    .setPositiveButton("Assign", (d, w) -> {
                        String service = etService.getText().toString().trim();
                        if (service.isEmpty()) { Toast.makeText(this, "Enter service", Toast.LENGTH_SHORT).show(); return; }
                        db.addNurseTask(selectedPatientId, StaffSession.getInstance().id, service,
                                etNotes.getText().toString().trim(), today);
                        Toast.makeText(this, "Task assigned to nurse", Toast.LENGTH_SHORT).show();
                        loadList();
                    }).setNegativeButton("Cancel", null).show();
        }
    }

    static class SimpleOrderAdapter extends RecyclerView.Adapter<SimpleOrderAdapter.VH> {
        private final List<String[]> list;
        SimpleOrderAdapter(List<String[]> list) { this.list = list; }
        @Override public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_row, parent, false);
            return new VH(v);
        }
        @Override public void onBindViewHolder(VH h, int pos) {
            String[] row = list.get(pos);
            h.tvTitle.setText(row[0]);
            h.tvSub.setText(row[1]);
            h.tvStatus.setText(row[2]);
            h.tvDate.setText(row[3]);
            h.tvStatus.setTextColor(row[2].equals("pending") ? 0xFFF9A825 : 0xFF2E7D32);
        }
        @Override public int getItemCount() { return list.size(); }
        static class VH extends RecyclerView.ViewHolder {
            TextView tvTitle, tvSub, tvStatus, tvDate;
            VH(View v) { super(v);
                tvTitle = v.findViewById(R.id.tv_title); tvSub = v.findViewById(R.id.tv_sub);
                tvStatus = v.findViewById(R.id.tv_status); tvDate = v.findViewById(R.id.tv_date);
            }
        }
    }
}
