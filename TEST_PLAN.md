# SOFTWARE TEST PLAN
## Musanze Hospital — Mobile Patient Billing Portal
**Version:** 2.0.0
**Date:** May 2026
**Prepared by:** Group AA
**Repository:** https://github.com/Imena23/BestPracticeHospitorBilling

---

## 1. INTRODUCTION

### 1.1 Purpose
This Software Test Plan defines the testing strategy, scope, objectives, resources,
schedule, and approach for testing the Musanze Hospital Mobile Patient Billing Portal.
It ensures the application meets functional and non-functional requirements before
deployment, particularly in digitizing a hospital that currently operates entirely on
manual, paper-based billing processes.

### 1.2 Project Overview
The Musanze Hospital Billing System is an Android mobile application that digitizes
staff workflows and connects them directly to a patient-facing billing portal. It
supports six user roles: Patient, Doctor, Pharmacist, Lab Technician, Nurse, and Admin.
Every service rendered by staff automatically generates a charge on the patient's bill
in real time, replacing manual paper-based charge compilation at the billing office.

### 1.3 Scope of Testing
The following modules are in scope for testing:

| Module | Description |
|--------|-------------|
| Authentication | Patient login, staff login, registration, auto-login, role routing |
| Patient Portal | Home dashboard, bills, prescriptions, payments, profile |
| Doctor Dashboard | Patient selection, prescriptions, lab orders, nurse tasks |
| Pharmacist Dashboard | View prescriptions, dispense medication, add charge |
| Lab Dashboard | View lab orders, record results, add charge |
| Nurse Dashboard | View assigned tasks, complete task, add charge |
| Admin Panel | Create staff accounts, delete staff accounts, assign roles |
| Billing Engine | Auto-charge on service completion, real-time bill update |
| Payment Processing | MTN Mobile Money flow, Visa/Mastercard flow, QR code generation |
| Database | SQLite CRUD operations, data integrity, schema version management |

**Out of scope:**
- Real payment gateway integration (payments are simulated)
- Network/API testing (no external hospital system integration in this version)
- Performance/load testing under concurrent users

---

## 2. TEST OBJECTIVES

1. Verify that all user roles can log in and are routed to the correct dashboard
2. Verify that patient registration stores data correctly in the local database
3. Verify that doctors can write prescriptions, order lab tests, and assign nurse tasks
4. Verify that pharmacist, lab, and nurse actions automatically add charges to the patient bill
5. Verify that patients see updated bills in real time after staff add charges
6. Verify that payment processing marks bills as paid and updates all totals
7. Verify that the admin can create and delete staff accounts
8. Verify that the QR code is generated correctly with accurate bill data
9. Verify that input validation prevents invalid or empty form submissions
10. Verify that logout clears the session and returns to the login screen

---

## 3. TEST APPROACH

### 3.1 Testing Types

| Type | Description | Tool |
|------|-------------|------|
| Unit Testing | Test individual methods in isolation | JUnit 4 (Android) |
| Integration Testing | Test interaction between modules (e.g. staff action → bill update) | Manual + Android Instrumented Tests |
| System Testing | Test complete end-to-end user journeys | Manual on emulator/device |
| User Acceptance Testing (UAT) | Validate the system meets Musanze Hospital's real-world billing needs | Manual walkthrough |
| Regression Testing | Re-run tests after any code change to ensure nothing is broken | Manual |

### 3.2 Testing Levels
- **Black-box testing** — test inputs and outputs without knowledge of internal code
- **White-box testing** — test internal logic, database queries, and session management

---

## 4. TEST ENVIRONMENT

| Item | Details |
|------|---------|
| Operating System | Windows 11 |
| Development IDE | Android Studio |
| Device | Android Emulator (API 34) or Physical Android Device (API 24+) |
| Database | SQLite (local, version 6) |
| Language | Java 11 |
| Min SDK | Android 7.0 (API 24) |
| Target SDK | Android 14 (API 36) |
| Build Tool | Gradle 8.13 |

---

## 5. TEST SCHEDULE

| Phase | Activity | Duration |
|-------|----------|----------|
| Phase 1 | Unit test execution | 1 day |
| Phase 2 | Integration testing | 2 days |
| Phase 3 | System / end-to-end testing | 2 days |
| Phase 4 | User Acceptance Testing | 1 day |
| Phase 5 | Regression testing after bug fixes | 1 day |

---

## 6. TEST CASES

---

### MODULE 1 — AUTHENTICATION

#### TC-001: Patient Login with Valid Credentials
| Field | Details |
|-------|---------|
| **Test Case ID** | TC-001 |
| **Module** | Authentication |
| **Test Type** | System Test |
| **Precondition** | App installed. Demo patient exists (Kevine Imena, DOB: 15/03/1990, PIN: 1234) |
| **Test Steps** | 1. Open app — Login page appears. 2. Ensure Patient tab is selected. 3. Enter Name: "Kevine Imena". 4. Select DOB: 15/03/1990. 5. Enter PIN: 1234. 6. Tap Login. |
| **Expected Result** | Patient authenticated and navigated to Home dashboard showing name, outstanding balance in RWF, and recent activity. Bill is displayed digitally instead of requiring a visit to the cashier. |
| **Actual Result** | |
| **Status** | Pass / Fail |

---

#### TC-002: Patient Login with Invalid PIN
| Field | Details |
|-------|---------|
| **Test Case ID** | TC-002 |
| **Module** | Authentication |
| **Test Type** | System Test |
| **Precondition** | App is on Login page |
| **Test Steps** | 1. Enter Name: "Kevine Imena". 2. Select DOB: 15/03/1990. 3. Enter PIN: 9999. 4. Tap Login. |
| **Expected Result** | Error message: "Incorrect name, date of birth, or PIN." User remains on login screen |
| **Actual Result** | |
| **Status** | Pass / Fail |

---

#### TC-003: Staff Login — Doctor Role Routing
| Field | Details |
|-------|---------|
| **Test Case ID** | TC-003 |
| **Module** | Authentication |
| **Test Type** | System Test |
| **Precondition** | App is on Login page |
| **Test Steps** | 1. Tap "Staff / Admin" tab. 2. Enter Username: dr.sarah. 3. Enter Password: doctor123. 4. Tap Login. |
| **Expected Result** | Staff authenticated and navigated to Doctor Dashboard showing patient selector and 3 tabs (Prescriptions, Lab Orders, Nurse Tasks) |
| **Actual Result** | |
| **Status** | Pass / Fail |

---

#### TC-004: Staff Login — Admin Role Routing
| Field | Details |
|-------|---------|
| **Test Case ID** | TC-004 |
| **Module** | Authentication |
| **Test Type** | System Test |
| **Precondition** | App is on Login page |
| **Test Steps** | 1. Tap "Staff / Admin" tab. 2. Enter Username: admin. 3. Enter Password: admin123. 4. Tap Login. |
| **Expected Result** | Admin navigated to Admin Panel showing list of staff accounts and "Add Staff" button |
| **Actual Result** | |
| **Status** | Pass / Fail |

---

#### TC-005: Staff Login with Wrong Password
| Field | Details |
|-------|---------|
| **Test Case ID** | TC-005 |
| **Module** | Authentication |
| **Test Type** | Validation Test |
| **Precondition** | App is on Login page, Staff tab selected |
| **Test Steps** | 1. Enter Username: dr.sarah. 2. Enter Password: wrongpass. 3. Tap Login. |
| **Expected Result** | Error message: "Invalid username or password" |
| **Actual Result** | |
| **Status** | Pass / Fail |

---

#### TC-006: Patient Registration
| Field | Details |
|-------|---------|
| **Test Case ID** | TC-006 |
| **Module** | Authentication |
| **Test Type** | System Test |
| **Precondition** | App is on Login page |
| **Test Steps** | 1. Tap "New patient? Register". 2. Enter Name: "Test Patient". 3. Select DOB. 4. Enter PIN: 4321. 5. Confirm PIN: 4321. 6. Tap Register. |
| **Expected Result** | Patient account created and stored in local database. Navigated to Home dashboard with empty bill. No paper form required. |
| **Actual Result** | |
| **Status** | Pass / Fail |

---

#### TC-007: Registration with Mismatched PINs
| Field | Details |
|-------|---------|
| **Test Case ID** | TC-007 |
| **Module** | Authentication |
| **Test Type** | Validation Test |
| **Precondition** | App is on Register page |
| **Test Steps** | 1. Fill all fields. 2. Enter PIN: 1111. 3. Confirm PIN: 2222. 4. Tap Register. |
| **Expected Result** | Error message: "PINs do not match" |
| **Actual Result** | |
| **Status** | Pass / Fail |

---

#### TC-008: Auto-Login on App Restart
| Field | Details |
|-------|---------|
| **Test Case ID** | TC-008 |
| **Module** | Authentication |
| **Test Type** | System Test |
| **Precondition** | Patient is already logged in |
| **Test Steps** | 1. Close the app completely. 2. Reopen the app. |
| **Expected Result** | App skips login screen and goes directly to patient Home dashboard |
| **Actual Result** | |
| **Status** | Pass / Fail |

---

### MODULE 2 — PATIENT PORTAL

#### TC-009: View Itemised Bill
| Field | Details |
|-------|---------|
| **Test Case ID** | TC-009 |
| **Module** | Patient Portal — Bills |
| **Test Type** | System Test |
| **Precondition** | Logged in as Kevine Imena (PIN: 1234) |
| **Test Steps** | 1. Tap Bills tab. |
| **Expected Result** | Bills tab shows itemised list: Consultation Fee RWF 195,000 — Unpaid, Laboratory Tests RWF 390,000 — Unpaid, Medication RWF 260,000 — Unpaid, Ward Charges RWF 1,950,000 — Unpaid, X-Ray RWF 390,000 — Unpaid. Grand total displayed. Patient sees this digitally without visiting the cashier. |
| **Actual Result** | |
| **Status** | Pass / Fail |

---

#### TC-010: QR Code Generation
| Field | Details |
|-------|---------|
| **Test Case ID** | TC-010 |
| **Module** | Patient Portal — Bills |
| **Test Type** | System Test |
| **Precondition** | Logged in as patient with unpaid bills |
| **Test Steps** | 1. Go to Bills tab. 2. Tap "Show QR Code". |
| **Expected Result** | QR code dialog opens showing a scannable QR code, bill ID, and total amount due in RWF |
| **Actual Result** | |
| **Status** | Pass / Fail |

---

#### TC-011: View Prescriptions
| Field | Details |
|-------|---------|
| **Test Case ID** | TC-011 |
| **Module** | Patient Portal — Prescriptions |
| **Test Type** | Integration Test |
| **Precondition** | Doctor has written a prescription for this patient (TC-015 completed) |
| **Test Steps** | 1. Log in as patient. 2. Tap Prescriptions tab. |
| **Expected Result** | Prescription list shows medicine name, dosage, notes, date, and status (Pending/Dispensed). Patient sees prescription digitally without needing a paper copy. |
| **Actual Result** | |
| **Status** | Pass / Fail |

---

#### TC-012: MTN Mobile Money Payment
| Field | Details |
|-------|---------|
| **Test Case ID** | TC-012 |
| **Module** | Patient Portal — Payments |
| **Test Type** | System Test |
| **Precondition** | Logged in as patient with unpaid balance |
| **Test Steps** | 1. Go to Payments tab. 2. Tap "Mobile Money". 3. Enter phone number: 0781234567. 4. Tap "Send Payment Prompt". 5. Tap "Reply" on USSD screen. 6. Enter PIN: 12345. 7. Tap "Reply". |
| **Expected Result** | Payment success screen shown. Outstanding balance becomes RWF 0. Bills tab shows all items as Paid. Patient no longer needs to queue at cashier. |
| **Actual Result** | |
| **Status** | Pass / Fail |

---

#### TC-013: Card Payment
| Field | Details |
|-------|---------|
| **Test Case ID** | TC-013 |
| **Module** | Patient Portal — Payments |
| **Test Type** | System Test |
| **Precondition** | Logged in as patient with unpaid balance |
| **Test Steps** | 1. Go to Payments tab. 2. Tap "POS Card". 3. Enter card number: 4532123456789012. 4. Enter name: KEVINE IMENA. 5. Enter expiry: 12/28. 6. Enter CVV: 123. 7. Tap "Pay Now". |
| **Expected Result** | Payment success screen shown with masked card number. Balance updated to RWF 0 |
| **Actual Result** | |
| **Status** | Pass / Fail |

---

#### TC-014: Logout
| Field | Details |
|-------|---------|
| **Test Case ID** | TC-014 |
| **Module** | Patient Portal — Profile |
| **Test Type** | System Test |
| **Precondition** | Logged in as patient |
| **Test Steps** | 1. Go to Profile tab. 2. Tap "Logout". |
| **Expected Result** | Session cleared. App navigates back to Login screen. Auto-login no longer triggers on restart. |
| **Actual Result** | |
| **Status** | Pass / Fail |

---

### MODULE 3 — DOCTOR DASHBOARD

#### TC-015: Write Prescription
| Field | Details |
|-------|---------|
| **Test Case ID** | TC-015 |
| **Module** | Doctor Dashboard |
| **Test Type** | Integration Test |
| **Precondition** | Logged in as dr.sarah |
| **Test Steps** | 1. Tap "Select Patient" → choose Kevine Imena. 2. Ensure Prescriptions tab is active. 3. Tap "+ Add Order". 4. Enter Medicine: Amoxicillin. 5. Enter Dosage: 500mg twice daily. 6. Enter Notes: Take after meals. 7. Tap "Prescribe". |
| **Expected Result** | Prescription saved to database. Appears in doctor's list with status "pending". Pharmacist dashboard shows it immediately. No paper prescription needed. |
| **Actual Result** | |
| **Status** | Pass / Fail |

---

#### TC-016: Order Lab Test
| Field | Details |
|-------|---------|
| **Test Case ID** | TC-016 |
| **Module** | Doctor Dashboard |
| **Test Type** | Integration Test |
| **Precondition** | Logged in as dr.sarah, patient selected |
| **Test Steps** | 1. Tap Lab Orders tab. 2. Tap "+ Add Order". 3. Enter Test: Full Blood Count. 4. Enter Notes: Check for anaemia. 5. Tap "Order". |
| **Expected Result** | Lab order saved. Appears in lab technician dashboard as pending. No paper request form needed. |
| **Actual Result** | |
| **Status** | Pass / Fail |

---

#### TC-017: Assign Nurse Task
| Field | Details |
|-------|---------|
| **Test Case ID** | TC-017 |
| **Module** | Doctor Dashboard |
| **Test Type** | Integration Test |
| **Precondition** | Logged in as dr.sarah, patient selected |
| **Test Steps** | 1. Tap Nurse Tasks tab. 2. Tap "+ Add Order". 3. Enter Service: IV Drip Administration. 4. Enter Notes: 500ml saline. 5. Tap "Assign". |
| **Expected Result** | Task saved. Appears in nurse dashboard as pending. No paper ward instruction needed. |
| **Actual Result** | |
| **Status** | Pass / Fail |

---

### MODULE 4 — PHARMACIST DASHBOARD

#### TC-018: Dispense Medication and Auto-Charge Patient
| Field | Details |
|-------|---------|
| **Test Case ID** | TC-018 |
| **Module** | Pharmacist Dashboard |
| **Test Type** | Integration Test |
| **Precondition** | Logged in as pharm.paul. TC-015 completed. |
| **Test Steps** | 1. View pending prescriptions list. 2. Find Amoxicillin for Kevine Imena. 3. Tap "Dispense". 4. Enter charge: 15000. 5. Tap "Dispense & Charge". |
| **Expected Result** | Prescription status changes to "Dispensed". New bill entry "Medication: Amoxicillin — RWF 15,000" appears automatically on Kevine Imena's Bills tab. No manual billing office submission needed. |
| **Actual Result** | |
| **Status** | Pass / Fail |

---

### MODULE 5 — LAB DASHBOARD

#### TC-019: Record Lab Result and Auto-Charge Patient
| Field | Details |
|-------|---------|
| **Test Case ID** | TC-019 |
| **Module** | Lab Dashboard |
| **Test Type** | Integration Test |
| **Precondition** | Logged in as lab.alice. TC-016 completed. |
| **Test Steps** | 1. View pending lab orders. 2. Find Full Blood Count for Kevine Imena. 3. Tap "Record Result". 4. Enter Result: Haemoglobin 12.5 g/dL — Normal range. 5. Enter charge: 25000. 6. Tap "Complete & Charge". |
| **Expected Result** | Lab order status changes to "Completed". New bill entry "Lab Test: Full Blood Count — RWF 25,000" appears on patient's Bills tab. No paper result sheet or manual billing needed. |
| **Actual Result** | |
| **Status** | Pass / Fail |

---

### MODULE 6 — NURSE DASHBOARD

#### TC-020: Complete Nurse Task and Auto-Charge Patient
| Field | Details |
|-------|---------|
| **Test Case ID** | TC-020 |
| **Module** | Nurse Dashboard |
| **Test Type** | Integration Test |
| **Precondition** | Logged in as nurse.grace. TC-017 completed. |
| **Test Steps** | 1. View pending tasks. 2. Find IV Drip Administration for Kevine Imena. 3. Tap "Mark Done". 4. Enter charge: 10000. 5. Tap "Done & Charge". |
| **Expected Result** | Task status changes to "Done". New bill entry "Nursing: IV Drip Administration — RWF 10,000" appears on patient's Bills tab. No ward register entry needed. |
| **Actual Result** | |
| **Status** | Pass / Fail |

---

### MODULE 7 — ADMIN PANEL

#### TC-021: Create New Staff Account
| Field | Details |
|-------|---------|
| **Test Case ID** | TC-021 |
| **Module** | Admin Panel |
| **Test Type** | System Test |
| **Precondition** | Logged in as admin |
| **Test Steps** | 1. Tap "+ Add Staff". 2. Enter Name: Dr. Jean Pierre. 3. Select Role: doctor. 4. Enter Username: dr.jean. 5. Enter Password: jean123. 6. Tap "Create". |
| **Expected Result** | New staff account appears in the staff list with role "DOCTOR". Account can be used to log in immediately. |
| **Actual Result** | |
| **Status** | Pass / Fail |

---

#### TC-022: Delete Staff Account
| Field | Details |
|-------|---------|
| **Test Case ID** | TC-022 |
| **Module** | Admin Panel |
| **Test Type** | System Test |
| **Precondition** | Logged in as admin. TC-021 completed. |
| **Test Steps** | 1. Find dr.jean in staff list. 2. Tap "Delete". 3. Confirm deletion. |
| **Expected Result** | Staff account removed from list. Login attempt with dr.jean/jean123 returns "Invalid username or password". |
| **Actual Result** | |
| **Status** | Pass / Fail |

---

#### TC-023: Duplicate Username Prevention
| Field | Details |
|-------|---------|
| **Test Case ID** | TC-023 |
| **Module** | Admin Panel |
| **Test Type** | Validation Test |
| **Precondition** | Logged in as admin |
| **Test Steps** | 1. Tap "+ Add Staff". 2. Enter Username: dr.sarah (already exists). 3. Tap "Create". |
| **Expected Result** | Error message: "Username already exists" |
| **Actual Result** | |
| **Status** | Pass / Fail |

---

### MODULE 8 — REAL-TIME BILLING INTEGRATION

#### TC-024: Patient Bill Updates in Real Time After Staff Charge
| Field | Details |
|-------|---------|
| **Test Case ID** | TC-024 |
| **Module** | Billing Integration |
| **Test Type** | End-to-End Integration Test |
| **Precondition** | Patient Kevine Imena is logged in. Pharmacist is logged in separately. |
| **Test Steps** | 1. Note patient's current outstanding balance on Home tab. 2. Pharmacist dispenses medication and adds charge of RWF 20,000. 3. Patient navigates away and returns to Home tab. |
| **Expected Result** | Patient's outstanding balance increases by RWF 20,000. New bill item appears in Bills tab automatically. This replaces the manual process where billing office compiled charges at end of day. |
| **Actual Result** | |
| **Status** | Pass / Fail |

---

## 7. UNIT TEST CASES (Automated)

Located in:
`app/src/test/java/com/example/protypebillingsystem/ExampleUnitTest.java`

| Test ID | Method Tested | Input | Expected Output |
|---------|--------------|-------|-----------------|
| UT-001 | `PatientSession.getInitials()` | name = "Kevine Imena" | "KI" |
| UT-002 | `PatientSession.getInitials()` | name = "Keza" | "K" |
| UT-003 | `PatientSession.isFullyPaid()` | unpaidAmount = 0 | true |
| UT-004 | `PatientSession.isFullyPaid()` | unpaidAmount = 195000 | false |
| UT-005 | `PatientSession.getInstance()` | called twice | same instance (Singleton) |
| UT-006 | `StaffSession.getInstance()` | called twice | same instance (Singleton) |
| UT-007 | `StaffSession.set()` | id=1, name="Dr. Sarah Mensah", role="doctor" | session fields updated correctly |

---

## 8. BUG REPORTING TEMPLATE

| Field | Details |
|-------|---------|
| **Bug ID** | BUG-XXX |
| **Test Case** | TC-XXX |
| **Severity** | Critical / High / Medium / Low |
| **Description** | What went wrong |
| **Steps to Reproduce** | Exact steps that caused the bug |
| **Expected Result** | What should have happened |
| **Actual Result** | What actually happened |
| **Screenshot** | Attach if available |
| **Status** | Open / Fixed / Closed |

---

## 9. TEST COMPLETION CRITERIA

Testing is considered complete when:
- All 24 system/integration test cases have been executed
- All 7 unit tests pass
- No Critical or High severity bugs remain open
- Real-time billing update (TC-024) passes consistently
- Logout and session clearing work correctly on all tested devices
- All payment flows complete successfully

---

## 10. DEMO ACCOUNTS FOR TESTING

| Role | Username / Name | Password / PIN |
|------|----------------|----------------|
| Patient | Kevine Imena (DOB: 15/03/1990) | PIN: 1234 |
| Patient | Keza Ketia (DOB: 22/07/1985) | PIN: 5678 |
| Admin | admin | admin123 |
| Doctor | dr.sarah | doctor123 |
| Doctor | dr.kwame | doctor123 |
| Nurse | nurse.grace | nurse123 |
| Pharmacist | pharm.paul | pharm123 |
| Lab Tech | lab.alice | lab123 |
