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

public class LabActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private LabAdapter adapter;
    private final List<LabItem> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab);
        db = new DatabaseHelper(this);

        ((TextView) findViewById(R.id.tv_welcome)).setText(StaffSession.getInstance().name);

        RecyclerView rv = findViewById(R.id.rv_lab_orders);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LabAdapter(list, this::showCompleteDialog);
        rv.setAdapter(adapter);

        findViewById(R.id.btn_logout).setOnClickListener(v -> { StaffSession.getInstance().clear(); finish(); });
        loadOrders();
    }

    @Override protected void onResume() { super.onResume(); loadOrders(); }
    @Override protected void onDestroy() { super.onDestroy(); db.close(); }

    private void loadOrders() {
        list.clear();
        Cursor c = db.getPendingLabOrders();
        if (c != null) {
            while (c.moveToNext()) {
                long patientRef = c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.COL_LAB_PATIENT_REF));
                list.add(new LabItem(
                        c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.COL_LAB_ID)),
                        patientRef, getPatientName(patientRef),
                        c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_LAB_TEST)),
                        c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_LAB_NOTES)),
                        c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_LAB_DATE))
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

    private void showCompleteDialog(LabItem item) {
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_complete_lab, null);
        TextInputEditText etResult = v.findViewById(R.id.et_result);
        TextInputEditText etAmount = v.findViewById(R.id.et_amount);
        ((TextView) v.findViewById(R.id.tv_info)).setText(item.patientName + "\nTest: " + item.testName);
        new AlertDialog.Builder(this).setTitle("Record Lab Result").setView(v)
                .setPositiveButton("Complete & Charge", (d, w) -> {
                    String result = etResult.getText().toString().trim();
                    String amtStr = etAmount.getText().toString().trim();
                    if (result.isEmpty() || amtStr.isEmpty()) { Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show(); return; }
                    String today = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());
                    db.completeLabOrder(item.id, item.patientRef, item.testName, result, Double.parseDouble(amtStr), today);
                    Toast.makeText(this, "Result recorded & bill added", Toast.LENGTH_SHORT).show();
                    loadOrders();
                }).setNegativeButton("Cancel", null).show();
    }

    static class LabItem {
        long id, patientRef;
        String patientName, testName, notes, date;
        LabItem(long id, long patientRef, String patientName, String testName, String notes, String date) {
            this.id = id; this.patientRef = patientRef; this.patientName = patientName;
            this.testName = testName; this.notes = notes; this.date = date;
        }
    }

    static class LabAdapter extends RecyclerView.Adapter<LabAdapter.VH> {
        interface OnComplete { void onComplete(LabItem item); }
        private final List<LabItem> list;
        private final OnComplete onComplete;
        LabAdapter(List<LabItem> list, OnComplete onComplete) { this.list = list; this.onComplete = onComplete; }
        @Override public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lab_row, parent, false));
        }
        @Override public void onBindViewHolder(VH h, int pos) { h.bind(list.get(pos), onComplete); }
        @Override public int getItemCount() { return list.size(); }
        static class VH extends RecyclerView.ViewHolder {
            TextView tvPatient, tvTest, tvNotes, tvDate; MaterialButton btnAction;
            VH(View v) { super(v);
                tvPatient = v.findViewById(R.id.tv_patient); tvTest = v.findViewById(R.id.tv_test);
                tvNotes = v.findViewById(R.id.tv_notes); tvDate = v.findViewById(R.id.tv_date);
                btnAction = v.findViewById(R.id.btn_action);
            }
            void bind(LabItem item, OnComplete cb) {
                tvPatient.setText(item.patientName); tvTest.setText(item.testName);
                tvNotes.setText(item.notes); tvDate.setText(item.date);
                btnAction.setText("Record Result");
                btnAction.setOnClickListener(v -> cb.onComplete(item));
            }
        }
    }
}
