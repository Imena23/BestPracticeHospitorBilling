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

public class NurseActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private NurseAdapter adapter;
    private final List<TaskItem> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nurse);
        db = new DatabaseHelper(this);

        ((TextView) findViewById(R.id.tv_welcome)).setText(StaffSession.getInstance().name);

        RecyclerView rv = findViewById(R.id.rv_tasks);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NurseAdapter(list, this::showCompleteDialog);
        rv.setAdapter(adapter);

        findViewById(R.id.btn_logout).setOnClickListener(v -> { StaffSession.getInstance().clear(); finish(); });
        loadTasks();
    }

    @Override protected void onResume() { super.onResume(); loadTasks(); }
    @Override protected void onDestroy() { super.onDestroy(); db.close(); }

    private void loadTasks() {
        list.clear();
        Cursor c = db.getPendingNurseTasks();
        if (c != null) {
            while (c.moveToNext()) {
                long patientRef = c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.COL_TASK_PATIENT_REF));
                list.add(new TaskItem(
                        c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.COL_TASK_ID)),
                        patientRef, getPatientName(patientRef),
                        c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_TASK_SERVICE)),
                        c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_TASK_NOTES)),
                        c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_TASK_DATE))
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

    private void showCompleteDialog(TaskItem item) {
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_complete_task, null);
        TextInputEditText etAmount = v.findViewById(R.id.et_amount);
        ((TextView) v.findViewById(R.id.tv_info)).setText(item.patientName + "\nService: " + item.service);
        new AlertDialog.Builder(this).setTitle("Complete Task").setView(v)
                .setPositiveButton("Done & Charge", (d, w) -> {
                    String amtStr = etAmount.getText().toString().trim();
                    if (amtStr.isEmpty()) { Toast.makeText(this, "Enter charge amount", Toast.LENGTH_SHORT).show(); return; }
                    String today = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());
                    db.completeNurseTask(item.id, item.patientRef, item.service, Double.parseDouble(amtStr), today);
                    Toast.makeText(this, "Task done & bill added for " + item.patientName, Toast.LENGTH_SHORT).show();
                    loadTasks();
                }).setNegativeButton("Cancel", null).show();
    }

    static class TaskItem {
        long id, patientRef;
        String patientName, service, notes, date;
        TaskItem(long id, long patientRef, String patientName, String service, String notes, String date) {
            this.id = id; this.patientRef = patientRef; this.patientName = patientName;
            this.service = service; this.notes = notes; this.date = date;
        }
    }

    static class NurseAdapter extends RecyclerView.Adapter<NurseAdapter.VH> {
        interface OnComplete { void onComplete(TaskItem item); }
        private final List<TaskItem> list;
        private final OnComplete onComplete;
        NurseAdapter(List<TaskItem> list, OnComplete onComplete) { this.list = list; this.onComplete = onComplete; }
        @Override public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task_row, parent, false));
        }
        @Override public void onBindViewHolder(VH h, int pos) { h.bind(list.get(pos), onComplete); }
        @Override public int getItemCount() { return list.size(); }
        static class VH extends RecyclerView.ViewHolder {
            TextView tvPatient, tvService, tvNotes, tvDate; MaterialButton btnAction;
            VH(View v) { super(v);
                tvPatient = v.findViewById(R.id.tv_patient); tvService = v.findViewById(R.id.tv_service);
                tvNotes = v.findViewById(R.id.tv_notes); tvDate = v.findViewById(R.id.tv_date);
                btnAction = v.findViewById(R.id.btn_action);
            }
            void bind(TaskItem item, OnComplete cb) {
                tvPatient.setText(item.patientName); tvService.setText(item.service);
                tvNotes.setText(item.notes); tvDate.setText(item.date);
                btnAction.setText("Mark Done");
                btnAction.setOnClickListener(v -> cb.onComplete(item));
            }
        }
    }
}
