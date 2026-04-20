package com.example.protypebillingsystem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "medipay.db";
    private static final int DB_VERSION = 1;

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
        db.execSQL("CREATE TABLE " + TABLE_PATIENTS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_DOB + " TEXT NOT NULL, " +
                COL_PATIENT_ID + " TEXT, " +
                COL_WARD + " TEXT, " +
                COL_DOCTOR + " TEXT, " +
                COL_ADMISSION + " TEXT, " +
                COL_BLOOD + " TEXT)");

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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PATIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BILLS);
        onCreate(db);
    }

    // Seed sample patients and bills for demo
    private void seedData(SQLiteDatabase db) {
        // Patient 1
        ContentValues p1 = new ContentValues();
        p1.put(COL_NAME, "John Doe");
        p1.put(COL_DOB, "15/03/1990");
        p1.put(COL_PATIENT_ID, "PAT-2026-0042");
        p1.put(COL_WARD, "Ward A - Room 203");
        p1.put(COL_DOCTOR, "Dr. Sarah Mensah");
        p1.put(COL_ADMISSION, "March 28, 2026");
        p1.put(COL_BLOOD, "O+");
        long id1 = db.insert(TABLE_PATIENTS, null, p1);

        // Bills for patient 1
        insertBill(db, "BILL-2026-0042", id1, "Consultation Fee", 150.00, "Mar 28, 2026", "unpaid");
        insertBill(db, "BILL-2026-0042", id1, "Laboratory Tests", 300.00, "Mar 29, 2026", "unpaid");
        insertBill(db, "BILL-2026-0042", id1, "Medication", 200.00, "Mar 29, 2026", "unpaid");
        insertBill(db, "BILL-2026-0042", id1, "Ward Charges (5 days)", 1500.00, "Mar 28 - Apr 2", "unpaid");
        insertBill(db, "BILL-2026-0042", id1, "X-Ray", 300.00, "Mar 30, 2026", "unpaid");

        // Patient 2
        ContentValues p2 = new ContentValues();
        p2.put(COL_NAME, "Jane Smith");
        p2.put(COL_DOB, "22/07/1985");
        p2.put(COL_PATIENT_ID, "PAT-2026-0043");
        p2.put(COL_WARD, "Ward B - Room 105");
        p2.put(COL_DOCTOR, "Dr. Kwame Asante");
        p2.put(COL_ADMISSION, "April 1, 2026");
        p2.put(COL_BLOOD, "A+");
        long id2 = db.insert(TABLE_PATIENTS, null, p2);

        insertBill(db, "BILL-2026-0043", id2, "Consultation Fee", 150.00, "Apr 1, 2026", "unpaid");
        insertBill(db, "BILL-2026-0043", id2, "Blood Test", 180.00, "Apr 1, 2026", "unpaid");
        insertBill(db, "BILL-2026-0043", id2, "Ward Charges (2 days)", 600.00, "Apr 1 - Apr 2", "unpaid");
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

    // Login: match name (case-insensitive) and dob
    public Cursor findPatient(String name, String dob) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_PATIENTS, null,
                "LOWER(" + COL_NAME + ") = LOWER(?) AND " + COL_DOB + " = ?",
                new String[]{name.trim(), dob.trim()},
                null, null, null);
    }

    // Get all bills for a patient by their row id
    public Cursor getBillsForPatient(long patientId) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_BILLS, null,
                COL_PATIENT_REF + " = ?",
                new String[]{String.valueOf(patientId)},
                null, null, null);
    }
}
