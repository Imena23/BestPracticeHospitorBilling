package com.example.protypebillingsystem;

public class StaffSession {
    private static StaffSession instance;
    public long id;
    public String name;
    public String role;

    private StaffSession() {}

    public static StaffSession getInstance() {
        if (instance == null) instance = new StaffSession();
        return instance;
    }

    public void set(long id, String name, String role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    public void clear() { instance = null; }
}
