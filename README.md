# MediPay - Patient Billing Portal

A modern Android application for hospital patient billing management with integrated mobile money and card payment systems.

## 📱 Features

### 🔐 User Authentication
- **Registration System**: New patients can register with name, date of birth, patient ID, and secure 4-digit PIN
- **Persistent Login**: Auto-login on app restart using SharedPreferences
- **Secure Authentication**: PIN-based login with validation
- **FHIR Synchronization**: Patient data synced to hospital FHIR server on registration
- **Material Design Inputs**: Outlined text fields with icons and password toggle

### 💳 Payment Systems

#### MTN Mobile Money
- Realistic USSD-style payment flow
- Transaction selection prompt
- PIN entry screen with authentic MTN styling
- Real-time payment processing simulation
- Transaction ID generation
- Success/failure notifications

#### Visa/Mastercard Payment
- Professional card payment interface
- Auto-formatting for card numbers (spaces every 4 digits)
- Auto-formatting for expiry dates (MM/YY)
- CVV validation
- Secure payment processing with SSL badge
- Masked card display on success

### 📊 Dashboard & Billing
- **Home Dashboard**: Overview of outstanding balance, ward location, and recent activity
- **Bills Management**: Itemized bill breakdown with paid/unpaid status badges
- **Payment History**: Track all completed payments
- **QR Code Generation**: Generate QR codes for cashier payments
- **Profile Management**: View patient information and medical details
- **FHIR Integration**: Bills can be exported as FHIR Claim resources
- **Real-time Sync**: Snackbar notifications for hospital system synchronization

### 💰 Currency Support
- All amounts displayed in **RWF (Rwandan Franc)**
- Conversion rate: 1 USD = 1,300 RWF
- Formatted currency display with thousand separators

### 🧪 Testing Features
- **Demo Accounts**: Pre-seeded test accounts for quick testing
- **Reset Bills**: Reset demo bills to unpaid status for repeated testing
- **Sample Data**: Pre-populated bills and patient records

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24 or higher
- Gradle 8.0+
- Java 11

### Installation

1. **Clone the repository**
   ```bash
   git clone https://gitlab.com/mucyojoel505/billing-app.git
   cd billing-app
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory
   - Wait for Gradle sync to complete

3. **Run the app**
   - Connect an Android device or start an emulator
   - Click the "Run" button or press `Shift + F10`

## 👤 Demo Accounts

### Test Account 1
- **Name**: John Doe
- **Date of Birth**: 15/03/1990
- **PIN**: 1234
- **Outstanding Balance**: RWF 3,185,000 (5 unpaid bills)

### Test Account 2
- **Name**: Jane Smith
- **Date of Birth**: 22/07/1985
- **PIN**: 5678
- **Outstanding Balance**: RWF 1,209,000 (3 unpaid bills)

## 🧭 App Navigation

### 1. Registration/Login
- First launch shows registration screen
- Existing users can tap "Already have an account? Login"
- Enter credentials to access the app

### 2. Home Tab
- View outstanding balance in RWF
- See current ward location
- Quick access to bills and payment history
- Recent activity feed

### 3. Bills Tab
- View total charges and amount paid
- Itemized bill breakdown with status badges
- Generate QR code for payment
- See grand total of unpaid bills

### 4. Payments Tab
- Outstanding balance display
- Payment method selection:
  - 📱 MTN Mobile Money
  - 💳 POS Card (Visa/Mastercard)
  - 💵 Cash at Cashier
- Payment history with transaction details

### 5. Profile Tab
- Patient information display
- Ward, doctor, admission date, blood type
- Digital receipt section
- Logout button
- Reset demo bills (for testing)

## 💳 Testing Payments

### MTN Mobile Money Flow
1. Tap "Mobile Money" on Payments tab
2. Enter MTN phone number (e.g., `078XXXXXXX` or `78XXXXXXX`)
3. Tap "Send Payment Prompt"
4. Wait for USSD prompt (2 seconds)
5. Select transaction by tapping "Reply"
6. Enter any 4-5 digit PIN
7. Tap "Reply" to confirm
8. View success screen with transaction details

**Valid MTN Prefixes**: 078, 079, 083, 073

### Card Payment Flow
1. Tap "POS Card" on Payments tab
2. Enter card details:
   - **Card Number**: Any 13-19 digit number (e.g., `4532123456789012`)
   - **Cardholder Name**: Any name in CAPS (e.g., `JOHN DOE`)
   - **Expiry Date**: Any future date (e.g., `12/28`)
   - **CVV**: Any 3-4 digits (e.g., `123`)
3. Tap "Pay Now"
4. Wait for processing (2 seconds)
5. View success screen with masked card number

## 🗄️ Database Schema

### Patients Table
- `id` (Primary Key)
- `name` (TEXT)
- `dob` (TEXT)
- `patient_id` (TEXT)
- `ward` (TEXT)
- `doctor` (TEXT)
- `admission_date` (TEXT)
- `blood_type` (TEXT)
- `pin` (TEXT)

### Bills Table
- `id` (Primary Key)
- `bill_id` (TEXT)
- `patient_ref` (INTEGER, Foreign Key)
- `item` (TEXT)
- `amount` (REAL)
- `date` (TEXT)
- `status` (TEXT: 'paid' or 'unpaid')

## 🎨 Design System

### Colors
- **Primary Blue**: `#1565C0`
- **MTN Yellow**: `#FFCC00`
- **Success Green**: `#2E7D32`
- **Pending Yellow**: `#F9A825`
- **Error Red**: `#C62828`
- **Background**: `#F5F7FA`

### Typography
- **Headers**: Bold, 20-26sp
- **Body**: Regular, 14-16sp
- **Captions**: Regular, 11-13sp

## 📦 Project Structure

```
app/src/main/
├── java/com/example/protypebillingsystem/
│   ├── MainActivity.java
│   ├── RegisterActivity.java
│   ├── LoginActivity.java
│   ├── HomeFragment.java
│   ├── BillsFragment.java
│   ├── PaymentsFragment.java
│   ├── ProfileFragment.java
│   ├── DatabaseHelper.java
│   ├── PatientSession.java
│   ├── MtnMomoDialog.java
│   ├── CardPaymentDialog.java
│   └── fhir/
│       ├── FhirClient.java
│       ├── FhirApiService.java
│       └── models/
│           ├── FhirPatient.java
│           ├── FhirClaim.java
│           └── FhirBundle.java
├── res/
│   ├── layout/
│   │   ├── activity_main.xml
│   │   ├── activity_register.xml
│   │   ├── activity_login.xml
│   │   ├── fragment_*.xml
│   │   ├── dialog_mtn_*.xml
│   │   └── dialog_card_payment.xml
│   ├── drawable/
│   │   ├── mtn_*.xml
│   │   ├── ussd_*.xml
│   │   └── btn_*.xml
│   └── values/
│       ├── colors.xml
│       ├── strings.xml
│       └── themes.xml
└── AndroidManifest.xml
```

## 🔧 Technical Stack

- **Language**: Java
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36
- **Database**: SQLite (local storage)
- **Architecture**: Fragment-based with Bottom Navigation
- **Storage**: SharedPreferences for session management
- **UI Framework**: Material Design Components (MDC) 1.13.0
  - TextInputLayout with outlined style and icons
  - MaterialButton with elevation and ripple effects
  - BottomNavigationView for tab navigation
  - Snackbar for user feedback
  - Material themes and color system
  - Password toggle for secure inputs
- **Healthcare Interoperability**: HL7 FHIR R4
  - FHIR Patient resource for demographics
  - FHIR Claim resource for billing data
  - HAPI FHIR public test server integration
  - Retrofit 2.9.0 for REST API calls
  - Gson for JSON serialization
  - Real-time synchronization with hospital systems

## 🐛 Known Limitations

- Payment processing is simulated (no real payment gateway integration)
- No backend API integration
- No email/SMS notifications
- No receipt PDF generation
- No multi-language support (English only)
- No biometric authentication

## 🚧 Future Enhancements

- [ ] Integrate real payment gateways (Flutterwave, Paystack)
- [ ] Add backend API for real-time data sync
- [ ] Implement receipt PDF generation and email delivery
- [ ] Add push notifications for payment reminders
- [ ] Support multiple languages (Kinyarwanda, French)
- [ ] Add biometric authentication (fingerprint/face)
- [ ] Implement payment plans for large bills
- [ ] Add insurance claim integration
- [ ] Dark mode support
- [ ] Offline mode with sync
- [ ] FHIR Observation resource for lab results
- [ ] FHIR Medication resource for prescriptions
- [ ] FHIR Appointment resource for scheduling
- [ ] Real-time FHIR subscriptions
- [ ] OAuth2 authentication for production FHIR server

## 🏥 FHIR Integration

This app integrates with **HL7 FHIR R4** (Fast Healthcare Interoperability Resources) standard for healthcare data exchange.

### FHIR Server
- **Server**: HAPI FHIR Public Test Server
- **Base URL**: `https://hapi.fhir.org/baseR4/`
- **Documentation**: https://hapi.fhir.org/

### FHIR Resources Used

#### Patient Resource
Represents patient demographic and administrative data:
- Patient ID (identifier)
- Full name
- Date of birth
- Gender
- Contact information

#### Claim Resource
Represents bills and invoices for healthcare services:
- Bill ID
- Patient reference
- Service items
- Amounts in RWF
- Bill status (active/paid)

### FHIR Features
- ✅ **Registration Sync**: New patients automatically created on FHIR server
- ✅ **Login Verification**: Patient data verified with FHIR server on login
- ✅ **Background Sync**: Non-blocking API calls with Snackbar feedback
- ✅ **Graceful Fallback**: App works offline if FHIR server unavailable
- ✅ **Interoperability**: Data can be shared with other healthcare systems

### Viewing FHIR Data
Visit the HAPI FHIR server to view synced patient data:
https://hapi.fhir.org/resource?serverId=home_r4&pretty=true&resource=Patient

For detailed FHIR documentation, see **[FHIR_INTEGRATION.md](FHIR_INTEGRATION.md)**

## 🎨 Material Design Components

This app showcases modern Material Design Components:

### Input Fields
- **TextInputLayout**: Outlined style with floating labels
- **Icons**: Start icons for visual context (calendar, lock, etc.)
- **Password Toggle**: Eye icon to show/hide PIN
- **Helper Text**: Contextual hints below fields
- **Error States**: Red error messages with validation

### Buttons
- **MaterialButton**: Elevated primary buttons with ripple effects
- **Outlined Buttons**: Secondary actions with stroke borders
- **Corner Radius**: Rounded corners (8dp) for modern look
- **State Animations**: Smooth press and release animations

### Feedback
- **Snackbar**: Bottom notifications for sync status
- **Material Colors**: Primary blue theme with accent colors
- **Ripple Effects**: Touch feedback on all interactive elements

### Navigation
- **BottomNavigationView**: Tab-based navigation with icons
- **Material Transitions**: Smooth fragment transitions

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Contributors

- **Group AA** - Initial work and feature development

## 📞 Support

For issues, questions, or contributions, please open an issue on GitLab:
https://gitlab.com/mucyojoel505/billing-app/-/issues

## 🙏 Acknowledgments

- MTN Rwanda for payment flow inspiration
- Material Design guidelines for UI/UX patterns
- Android community for best practices

---

**Version**: 1.0.0  
**Last Updated**: April 2026  
**Status**: Active Development
