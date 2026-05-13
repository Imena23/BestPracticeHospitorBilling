# My Test Plan for Protype Billing System

I've put together this guide to help test the app features and make sure everything is working correctly.

## 1. What I'm Testing
*   **User Account**: Registering new patients and logging in with a PIN.
*   **FHIR Integration**: Syncing patient data and fetching prescriptions from the hospital's FHIR server.
*   **Database**: Making sure patient info and bills are saved correctly in SQLite.
*   **Billing & QR**: Checking if the app calculates totals and generates a scannable QR code for the cashier.
*   **Session**: Making sure the app remembers who is logged in and shows the right initials.

## 2. Running the Automated Tests

### Unit Tests (Logic)
I wrote some tests in `ExampleUnitTest.java` to check the session logic:
1.  Go to `app/src/test/java/com/example/protypebillingsystem/`.
2.  Right-click `ExampleUnitTest` and choose **Run**.
3.  It checks things like if "Kevine" correctly turns into "K".

### Instrumented Tests (Database/UI)
These need an emulator or physical phone:
1.  Go to `app/src/androidTest/java/com/example/protypebillingsystem/`.
2.  Right-click `ExampleInstrumentedTest` and choose **Run**.
3.  This checks if the app context and database are set up right.

## 3. Manual Testing Steps (How I test it on my phone)

1.  **Registration & FHIR Sync**: 
    *   Open the app, go to Register.
    *   Fill in name, DOB, and a 4-digit PIN.
    *   Click Register. Verify that it says "Synced with hospital system" (this tests the FHIR Patient creation).
2.  **Login**:
    *   Log out or restart the app.
    *   Enter the same Name, DOB, and PIN.
    *   Verify that it verifies your data with the FHIR server in the background.
3.  **Prescriptions (FHIR)**:
    *   Navigate to the **Prescriptions** tab.
    *   Verify that it fetches and displays prescriptions from the HAPI FHIR server.
4.  **Viewing Bills & QR**:
    *   Go to the **Bills** tab to see the list of charges.
    *   Click **Show QR Code**. Verify that a real QR code is generated (not a placeholder).
5.  **Payments**:
    *   Click on **Pay Now**.
    *   Choose a method (Momo or Card).
    *   After "paying", check if the "Unpaid" amount goes down to 0 and a receipt appears on your **Profile** page.
