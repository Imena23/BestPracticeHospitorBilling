package com.example.protypebillingsystem;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private StaffListAdapter adapter;
    private List<StaffItem> staffList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        db = new DatabaseHelper(this);

        RecyclerView rv = findViewById(R.id.rv_staff);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StaffListAdapter(staffList, this::confirmDelete);
        rv.setAdapter(adapter);

        findViewById(R.id.btn_add_staff).setOnClickListener(v -> showAddStaffDialog());
        findViewById(R.id.btn_logout).setOnClickListener(v -> { StaffSession.getInstance().clear(); finish(); });

        TextView tvWelcome = findViewById(R.id.tv_welcome);
        tvWelcome.setText("Admin: " + StaffSession.getInstance().name);

        loadStaff();
    }

    private void loadStaff() {
        staffList.clear();
        Cursor c = db.getAllStaff();
        if (c != null) {
            while (c.moveToNext()) {
                staffList.add(new StaffItem(
                        c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.COL_STAFF_ID)),
                        c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_STAFF_NAME)),
                        c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_STAFF_ROLE)),
                        c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_STAFF_USERNAME))
                ));
            }
            c.close();
        }
        adapter.notifyDataSetChanged();
    }

    private void showAddStaffDialog() {
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_add_staff, null);
        TextInputEditText etName = v.findViewById(R.id.et_name);
        TextInputEditText etUsername = v.findViewById(R.id.et_username);
        TextInputEditText etPassword = v.findViewById(R.id.et_password);
        Spinner spRole = v.findViewById(R.id.sp_role);
        spRole.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"doctor", "nurse", "pharmacist", "lab", "admin"}));

        new AlertDialog.Builder(this)
                .setTitle("Create Staff Account")
                .setView(v)
                .setPositiveButton("Create", (d, w) -> {
                    String name = etName.getText().toString().trim();
                    String username = etUsername.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();
                    String role = spRole.getSelectedItem().toString();
                    if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
                        Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    long id = db.createStaff(name, role, username, password);
                    if (id == -1) Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
                    else { Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show(); loadStaff(); }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmDelete(StaffItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Delete " + item.name + " (" + item.role + ")?")
                .setPositiveButton("Delete", (d, w) -> {
                    db.deleteStaff(item.id);
                    Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                    loadStaff();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ── Inner data class ──────────────────────────────────────────────────────
    static class StaffItem {
        long id; String name, role, username;
        StaffItem(long id, String name, String role, String username) {
            this.id = id; this.name = name; this.role = role; this.username = username;
        }
    }

    // ── Inner adapter ─────────────────────────────────────────────────────────
    static class StaffListAdapter extends RecyclerView.Adapter<StaffListAdapter.VH> {
        interface OnDelete { void onDelete(StaffItem item); }
        private final List<StaffItem> list;
        private final OnDelete onDelete;
        StaffListAdapter(List<StaffItem> list, OnDelete onDelete) { this.list = list; this.onDelete = onDelete; }

        @Override public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staff_row, parent, false);
            return new VH(v);
        }
        @Override public void onBindViewHolder(VH h, int pos) { h.bind(list.get(pos), onDelete); }
        @Override public int getItemCount() { return list.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvName, tvRole, tvUsername; MaterialButton btnDelete;
            VH(View v) {
                super(v);
                tvName = v.findViewById(R.id.tv_name);
                tvRole = v.findViewById(R.id.tv_role);
                tvUsername = v.findViewById(R.id.tv_username);
                btnDelete = v.findViewById(R.id.btn_delete);
            }
            void bind(StaffItem item, OnDelete onDelete) {
                tvName.setText(item.name);
                tvRole.setText(item.role.toUpperCase());
                tvUsername.setText("@" + item.username);
                btnDelete.setOnClickListener(v -> onDelete.onDelete(item));
            }
        }
    }
}
