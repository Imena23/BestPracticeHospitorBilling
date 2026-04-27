package com.example.protypebillingsystem;

// Singleton to hold the logged-in patient's data across fragments
public class PatientSession {

    private static PatientSession instance;

    public long id;
    public String name;
    public String dob;
    public String patientId;
    public String ward;
    public String doctor;
    public String admissionDate;
    public String bloodType;
    public double totalBill;
    public double paidAmount;
    public double unpaidAmount;
    public String billId;

    private PatientSession() {}

    public static PatientSession getInstance() {
        if (instance == null) instance = new PatientSession();
        return instance;
    }

    public void clear() {
        instance = null;
    }

    public String getInitials() {
        if (name == null || name.isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        if (parts.length >= 2) return String.valueOf(parts[0].charAt(0)) + parts[1].charAt(0);
        return String.valueOf(parts[0].charAt(0));
    }

    public boolean isFullyPaid() {
        return unpaidAmount <= 0;
    }
}
