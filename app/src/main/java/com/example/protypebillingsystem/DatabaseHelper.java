package com.example.protypebillingsystem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "medipay.db";
    private static final int DB_VERSION = 6;

    // Staff table
    public static final String TABLE_STAFF = "staff";
    public static final String COL_STAFF_ID = "id";
    public static final String COL_STAFF_NAME = "name";
    public static final String COL_STAFF_ROLE = "role"; // admin, doctor, nurse, pharmacist, lab
    public static final String COL_STAFF_USERNAME = "username";
    public static final String COL_STAFF_PASSWORD = "password";

    // Prescriptions table
    public static final String TABLE_PRESCRIPTIONS = "prescriptions";
    public static final String COL_PRESC_ID = "id";
    public static final String COL_PRESC_PATIENT_REF = "patient_ref";
    public static final String COL_PRESC_DOCTOR_REF = "doctor_ref";
    public static final String COL_PRESC_MEDICINE = "medicine";
    public static final String COL_PRESC_DOSAGE = "dosage";
    public static final String COL_PRESC_NOTES = "notes";
    public static final String COL_PRESC_DATE = "date";
    public static final String COL_PRESC_STATUS = "status"; // pending, dispensed

    // Lab orders table
    public static final String TABLE_LAB_ORDERS = "lab_orders";
    public static final String COL_LAB_ID = "id";
    public static final String COL_LAB_PATIENT_REF = "patient_ref";
    public static final String COL_LAB_DOCTOR_REF = "doctor_ref";
    public static final String COL_LAB_TEST = "test_name";
    public static final String COL_LAB_NOTES = "notes";
    public static final String COL_LAB_DATE = "date";
    public static final String COL_LAB_STATUS = "status"; // pending, completed
    public static final String COL_LAB_RESULT = "result";

    // Nurse tasks table
    public static final String TABLE_NURSE_TASKS = "nurse_tasks";
    public static final String COL_TASK_ID = "id";
    public static final String COL_TASK_PATIENT_REF = "patient_ref";
    public static final String COL_TASK_DOCTOR_REF = "doctor_ref";
    public static final String COL_TASK_SERVICE = "service";
    public static final String COL_TASK_NOTES = "notes";
    public static final String COL_TASK_DATE = "date";
    public static final String COL_TASK_STATUS = "status"; // pending, done

    // Patients table
    public static final String TABLE_PATIENTS = "patients";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_DOB = "dob";
    public static final String COL_PATIENT_ID = "patient_id";
    public static final String COL_WARD = "ward";
    public static final String COL_DOCTOR = "doctor";
    public static final String COL_ADMISSION = "admission_date";
    public static final String COL_BLOOD = "blood_type";
    public static final String COL_PIN = "pin";

    // Bills table
    public static final String TABLE_BILLS = "bills";
    public static final String COL_BILL_ID = "bill_id";
    public static final String COL_PATIENT_REF = "patient_ref";
    public static final String COL_ITEM = "item";
    public static final String COL_AMOUNT = "amount";
    public static final String COL_DATE = "date";
    public static final String COL_STATUS = "status";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_STAFF + " (" +
                COL_STAFF_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_STAFF_NAME + " TEXT NOT NULL, " +
                COL_STAFF_ROLE + " TEXT NOT NULL, " +
                COL_STAFF_USERNAME + " TEXT NOT NULL UNIQUE, " +
                COL_STAFF_PASSWORD + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + TABLE_PRESCRIPTIONS + " (" +
                COL_PRESC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PRESC_PATIENT_REF + " INTEGER, " +
                COL_PRESC_DOCTOR_REF + " INTEGER, " +
                COL_PRESC_MEDICINE + " TEXT, " +
                COL_PRESC_DOSAGE + " TEXT, " +
                COL_PRESC_NOTES + " TEXT, " +
                COL_PRESC_DATE + " TEXT, " +
                COL_PRESC_STATUS + " TEXT DEFAULT 'pending')");

        db.execSQL("CREATE TABLE " + TABLE_LAB_ORDERS + " (" +
                COL_LAB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_LAB_PATIENT_REF + " INTEGER, " +
                COL_LAB_DOCTOR_REF + " INTEGER, " +
                COL_LAB_TEST + " TEXT, " +
                COL_LAB_NOTES + " TEXT, " +
                COL_LAB_DATE + " TEXT, " +
                COL_LAB_STATUS + " TEXT DEFAULT 'pending', " +
                COL_LAB_RESULT + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_NURSE_TASKS + " (" +
                COL_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TASK_PATIENT_REF + " INTEGER, " +
                COL_TASK_DOCTOR_REF + " INTEGER, " +
                COL_TASK_SERVICE + " TEXT, " +
                COL_TASK_NOTES + " TEXT, " +
                COL_TASK_DATE + " TEXT, " +
                COL_TASK_STATUS + " TEXT DEFAULT 'pending')");

        db.execSQL("CREATE TABLE " + TABLE_PATIENTS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_DOB + " TEXT NOT NULL, " +
                COL_PATIENT_ID + " TEXT, " +
                COL_WARD + " TEXT, " +
                COL_DOCTOR + " TEXT, " +
                COL_ADMISSION + " TEXT, " +
                COL_BLOOD + " TEXT, " +
                COL_PIN + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_BILLS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_BILL_ID + " TEXT, " +
                COL_PATIENT_REF + " INTEGER, " +
                COL_ITEM + " TEXT, " +
                COL_AMOUNT + " REAL, " +
                COL_DATE + " TEXT, " +
                COL_STATUS + " TEXT)");

        seedData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STAFF);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRESCRIPTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LAB_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NURSE_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PATIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BILLS);
        onCreate(db);
    }

    // Seed sample patients and bills for demo
    private void seedData(SQLiteDatabase db) {
        // Seed default admin
        insertStaff(db, "Admin User", "admin", "admin", "admin123");
        // Seed demo staff
        insertStaff(db, "Dr. Sarah Mensah", "doctor", "dr.sarah", "doctor123");
        insertStaff(db, "Dr. Kwame Asante", "doctor", "dr.kwame", "doctor123");
        insertStaff(db, "Nurse Grace", "nurse", "nurse.grace", "nurse123");
        insertStaff(db, "Pharmacist Paul", "pharmacist", "pharm.paul", "pharm123");
        insertStaff(db, "Lab Tech Alice", "lab", "lab.alice", "lab123");
        // Patient 1
        ContentValues p1 = new ContentValues();
        p1.put(COL_NAME, "Kevine Imena");
        p1.put(COL_DOB, "15/03/1990");
        p1.put(COL_PATIENT_ID, "PAT-2026-0042");
        p1.put(COL_WARD, "Ward A - Room 203");
        p1.put(COL_DOCTOR, "Dr. Sarah Mensah");
        p1.put(COL_ADMISSION, "March 28, 2026");
        p1.put(COL_BLOOD, "O+");
        p1.put(COL_PIN, "1234");
        long id1 = db.insert(TABLE_PATIENTS, null, p1);

        // Bills for patient 1
        insertBill(db, "BILL-2026-0042", id1, "Consultation Fee", 195000, "Mar 28, 2026", "unpaid");
        insertBill(db, "BILL-2026-0042", id1, "Laboratory Tests", 390000, "Mar 29, 2026", "unpaid");
        insertBill(db, "BILL-2026-0042", id1, "Medication", 260000, "Mar 29, 2026", "unpaid");
        insertBill(db, "BILL-2026-0042", id1, "Ward Charges (5 days)", 1950000, "Mar 28 - Apr 2", "unpaid");
        insertBill(db, "BILL-2026-0042", id1, "X-Ray", 390000, "Mar 30, 2026", "unpaid");

        // Patient 2
        ContentValues p2 = new ContentValues();
        p2.put(COL_NAME, "Keza Ketia");
        p2.put(COL_DOB, "22/07/1985");
        p2.put(COL_PATIENT_ID, "PAT-2026-0043");
        p2.put(COL_WARD, "Ward B - Room 105");
        p2.put(COL_DOCTOR, "Dr. Kwame Asante");
        p2.put(COL_ADMISSION, "April 1, 2026");
        p2.put(COL_BLOOD, "A+");
        p2.put(COL_PIN, "5678");
        long id2 = db.insert(TABLE_PATIENTS, null, p2);

        insertBill(db, "BILL-2026-0043", id2, "Consultation Fee", 195000, "Apr 1, 2026", "unpaid");
        insertBill(db, "BILL-2026-0043", id2, "Blood Test", 234000, "Apr 1, 2026", "paid");
        insertBill(db, "BILL-2026-0043", id2, "Ward Charges (2 days)", 780000, "Apr 1 - Apr 2", "unpaid");
    }

    private void insertStaff(SQLiteDatabase db, String name, String role, String username, String password) {
        ContentValues v = new ContentValues();
        v.put(COL_STAFF_NAME, name);
        v.put(COL_STAFF_ROLE, role);
        v.put(COL_STAFF_USERNAME, username);
        v.put(COL_STAFF_PASSWORD, password);
        db.insert(TABLE_STAFF, null, v);
    }

    private void insertBill(SQLiteDatabase db, String billId, long patientRef,
                            String item, double amount, String date, String status) {
        ContentValues v = new ContentValues();
        v.put(COL_BILL_ID, billId);
        v.put(COL_PATIENT_REF, patientRef);
        v.put(COL_ITEM, item);
        v.put(COL_AMOUNT, amount);
        v.put(COL_DATE, date);
        v.put(COL_STATUS, status);
        db.insert(TABLE_BILLS, null, v);
    }

    // Login: match name (case-insensitive), dob, and PIN
    public Cursor findPatient(String name, String dob, String pin) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_PATIENTS, null,
                "LOWER(" + COL_NAME + ") = LOWER(?) AND " + COL_DOB + " = ? AND " + COL_PIN + " = ?",
                new String[]{name.trim(), dob.trim(), pin.trim()},
                null, null, null);
    }

    // Get patient by ID
    public Cursor getPatientById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_PATIENTS, null,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);
    }

    // Register new patient
    public long registerPatient(String name, String dob, String patientId, String pin) {
        SQLiteDatabase db = getWritableDatabase();
        
        // Check if patient already exists
        Cursor existing = db.query(TABLE_PATIENTS, null,
                "LOWER(" + COL_NAME + ") = LOWER(?) AND " + COL_DOB + " = ?",
                new String[]{name.trim(), dob.trim()},
                null, null, null);
        
        if (existing != null && existing.moveToFirst()) {
            existing.close();
            return -1; // Already exists
        }
        if (existing != null) existing.close();

        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_DOB, dob);
        values.put(COL_PATIENT_ID, patientId);
        values.put(COL_PIN, pin);
        values.put(COL_WARD, "Not assigned");
        values.put(COL_DOCTOR, "Not assigned");
        values.put(COL_ADMISSION, "N/A");
        values.put(COL_BLOOD, "Unknown");

        return db.insert(TABLE_PATIENTS, null, values);
    }

    // Get all bills for a patient by their row id
    public Cursor getBillsForPatient(long patientId) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_BILLS, null,
                COL_PATIENT_REF + " = ?",
                new String[]{String.valueOf(patientId)},
                null, null, COL_DATE + " DESC");
    }

    // Update bill status to paid
    public boolean updateBillStatus(long patientId, String status) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_STATUS, status);
        int rows = db.update(TABLE_BILLS, values,
                COL_PATIENT_REF + " = ?",
                new String[]{String.valueOf(patientId)});
        return rows > 0;
    }

    // Get total paid amount for a patient
    public double getTotalPaid(long patientId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_BILLS,
                new String[]{COL_AMOUNT},
                COL_PATIENT_REF + " = ? AND " + COL_STATUS + " = ?",
                new String[]{String.valueOf(patientId), "paid"},
                null, null, null);
        double total = 0;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                total += cursor.getDouble(0);
            }
            cursor.close();
        }
        return total;
    }

    // Get total unpaid amount for a patient
    public double getTotalUnpaid(long patientId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_BILLS,
                new String[]{COL_AMOUNT},
                COL_PATIENT_REF + " = ? AND " + COL_STATUS + " = ?",
                new String[]{String.valueOf(patientId), "unpaid"},
                null, null, null);
        double total = 0;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                total += cursor.getDouble(0);
            }
            cursor.close();
        }
        return total;
    }

    // ── Staff methods ──────────────────────────────────────────────────────────

    public Cursor loginStaff(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_STAFF, null,
                COL_STAFF_USERNAME + " = ? AND " + COL_STAFF_PASSWORD + " = ?",
                new String[]{username.trim(), password.trim()},
                null, null, null);
    }

    public long createStaff(String name, String role, String username, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_STAFF_NAME, name);
        v.put(COL_STAFF_ROLE, role);
        v.put(COL_STAFF_USERNAME, username);
        v.put(COL_STAFF_PASSWORD, password);
        try { return db.insertOrThrow(TABLE_STAFF, null, v); }
        catch (Exception e) { return -1; }
    }

    public Cursor getAllStaff() {
        return getReadableDatabase().query(TABLE_STAFF, null, null, null, null, null, COL_STAFF_ROLE);
    }

    public boolean deleteStaff(long staffId) {
        return getWritableDatabase().delete(TABLE_STAFF, COL_STAFF_ID + "=?",
                new String[]{String.valueOf(staffId)}) > 0;
    }

    public Cursor getAllPatients() {
        return getReadableDatabase().query(TABLE_PATIENTS, null, null, null, null, null, COL_NAME);
    }

    // ── Prescription methods ───────────────────────────────────────────────────

    public long addPrescription(long patientRef, long doctorRef, String medicine, String dosage, String notes, String date) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_PRESC_PATIENT_REF, patientRef);
        v.put(COL_PRESC_DOCTOR_REF, doctorRef);
        v.put(COL_PRESC_MEDICINE, medicine);
        v.put(COL_PRESC_DOSAGE, dosage);
        v.put(COL_PRESC_NOTES, notes);
        v.put(COL_PRESC_DATE, date);
        v.put(COL_PRESC_STATUS, "pending");
        return db.insert(TABLE_PRESCRIPTIONS, null, v);
    }

    public Cursor getPrescriptionsForPatient(long patientRef) {
        return getReadableDatabase().query(TABLE_PRESCRIPTIONS, null,
                COL_PRESC_PATIENT_REF + "=?", new String[]{String.valueOf(patientRef)},
                null, null, COL_PRESC_DATE + " DESC");
    }

    public Cursor getPendingPrescriptions() {
        return getReadableDatabase().query(TABLE_PRESCRIPTIONS, null,
                COL_PRESC_STATUS + "='pending'", null, null, null, COL_PRESC_DATE + " DESC");
    }

    public boolean dispensePrescription(long prescId, long patientRef, String medicine, double amount, String date) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_PRESC_STATUS, "dispensed");
        db.update(TABLE_PRESCRIPTIONS, v, COL_PRESC_ID + "=?", new String[]{String.valueOf(prescId)});
        // Auto-add bill
        String billId = "BILL-PHARM-" + prescId;
        insertBill(db, billId, patientRef, "Medication: " + medicine, amount, date, "unpaid");
        return true;
    }

    // ── Lab order methods ──────────────────────────────────────────────────────

    public long addLabOrder(long patientRef, long doctorRef, String testName, String notes, String date) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_LAB_PATIENT_REF, patientRef);
        v.put(COL_LAB_DOCTOR_REF, doctorRef);
        v.put(COL_LAB_TEST, testName);
        v.put(COL_LAB_NOTES, notes);
        v.put(COL_LAB_DATE, date);
        v.put(COL_LAB_STATUS, "pending");
        return db.insert(TABLE_LAB_ORDERS, null, v);
    }

    public Cursor getPendingLabOrders() {
        return getReadableDatabase().query(TABLE_LAB_ORDERS, null,
                COL_LAB_STATUS + "='pending'", null, null, null, COL_LAB_DATE + " DESC");
    }

    public Cursor getLabOrdersForPatient(long patientRef) {
        return getReadableDatabase().query(TABLE_LAB_ORDERS, null,
                COL_LAB_PATIENT_REF + "=?", new String[]{String.valueOf(patientRef)},
                null, null, COL_LAB_DATE + " DESC");
    }

    public boolean completeLabOrder(long labId, long patientRef, String testName, String result, double amount, String date) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_LAB_STATUS, "completed");
        v.put(COL_LAB_RESULT, result);
        db.update(TABLE_LAB_ORDERS, v, COL_LAB_ID + "=?", new String[]{String.valueOf(labId)});
        insertBill(db, "BILL-LAB-" + labId, patientRef, "Lab Test: " + testName, amount, date, "unpaid");
        return true;
    }

    // ── Nurse task methods ─────────────────────────────────────────────────────

    public long addNurseTask(long patientRef, long doctorRef, String service, String notes, String date) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_TASK_PATIENT_REF, patientRef);
        v.put(COL_TASK_DOCTOR_REF, doctorRef);
        v.put(COL_TASK_SERVICE, service);
        v.put(COL_TASK_NOTES, notes);
        v.put(COL_TASK_DATE, date);
        v.put(COL_TASK_STATUS, "pending");
        return db.insert(TABLE_NURSE_TASKS, null, v);
    }

    public Cursor getPendingNurseTasks() {
        return getReadableDatabase().query(TABLE_NURSE_TASKS, null,
                COL_TASK_STATUS + "='pending'", null, null, null, COL_TASK_DATE + " DESC");
    }

    public Cursor getNurseTasksForPatient(long patientRef) {
        return getReadableDatabase().query(TABLE_NURSE_TASKS, null,
                COL_TASK_PATIENT_REF + "=?", new String[]{String.valueOf(patientRef)},
                null, null, COL_TASK_DATE + " DESC");
    }

    public boolean completeNurseTask(long taskId, long patientRef, String service, double amount, String date) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_TASK_STATUS, "done");
        db.update(TABLE_NURSE_TASKS, v, COL_TASK_ID + "=?", new String[]{String.valueOf(taskId)});
        insertBill(db, "BILL-NURSE-" + taskId, patientRef, "Nursing: " + service, amount, date, "unpaid");
        return true;
    }

    // ── Bill helper (public for direct charges) ────────────────────────────────

    public long addBillEntry(long patientRef, String item, double amount, String date) {
        SQLiteDatabase db = getWritableDatabase();
        String billId = "BILL-" + System.currentTimeMillis();
        insertBill(db, billId, patientRef, item, amount, date, "unpaid");
        return patientRef;
    }

    // ── Reset bills to unpaid (for demo/testing) ───────────────────────────────

    public boolean resetBillsToUnpaid(long patientId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_STATUS, "unpaid");
        int rows = db.update(TABLE_BILLS, values,
                COL_PATIENT_REF + " = ?",
                new String[]{String.valueOf(patientId)});
        return rows > 0;
    }
}
