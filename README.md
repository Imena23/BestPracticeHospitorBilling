# Musanze Hospital — Mobile Patient Billing Portal

A modern Android application for Musanze Hospital patient billing management with integrated mobile money and card payment systems.

## 📱 Features

### 🔐 User Authentication
- **Patient Registration**: New patients register with name, date of birth, patient ID, and secure 4-digit PIN
- **Staff Login**: Role-based login for Doctor, Nurse, Pharmacist, Lab Technician, and Admin
- **Persistent Login**: Auto-login on app restart using SharedPreferences
- **Secure Authentication**: PIN-based login for patients, username/password for staff

### 👥 Staff Roles
- **Admin**: Create and manage staff accounts
- **Doctor**: Write prescriptions, order lab tests, assign nurse tasks
- **Pharmacist**: View and dispense prescriptions, add medication charges
- **Lab Technician**: View lab orders, record results, add test charges
- **Nurse**: View assigned tasks, complete services, add nursing charges

### 💳 Payment Systems

#### MTN Mobile Money
- Realistic USSD-style payment flow
- Transaction selection prompt
- PIN entry screen with authentic MTN styling
- Real-time payment processing simulation
- Transaction ID generation

#### Visa/Mastercard Payment
- Professional card payment interface
- Auto-formatting for card numbers (spaces every 4 digits)
- Auto-formatting for expiry dates (MM/YY)
- CVV validation
- Masked card display on success

### 📊 Dashboard & Billing
- **Home Dashboard**: Outstanding balance, ward location, recent activity
- **Bills Management**: Itemised bill breakdown with paid/unpaid status badges
- **Real-time Billing**: Staff charges automatically appear on patient bill
- **Payment History**: Track all completed payments
- **QR Code Generation**: Generate QR codes for cashier payments
- **Profile Management**: View patient information and medical details
- **Prescriptions**: View doctor-prescribed medications and dispensing status

### 💰 Currency Support
- All amounts displayed in **RWF (Rwandan Franc)**
- Formatted currency display with thousand separators

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24 or higher
- Gradle 8.0+
- Java 11

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/Imena23/BestPracticeHospitorBilling.git
   cd BestPracticeHospitorBilling
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory
   - Wait for Gradle sync to complete

3. **Run the app**
   - Connect an Android device or start an emulator
   - Click the "Run" button or press `Shift + F10`

### Docker Build
```bash
docker build -t musanze-billing-app .
```
The APK will be built at `/app/app/build/outputs/apk/debug/app-debug.apk`

## 👤 Demo Accounts

### Patient Accounts
| Name | Date of Birth | PIN |
|------|--------------|-----|
| Kevine Imena | 15/03/1990 | 1234 |
| Keza Ketia | 22/07/1985 | 5678 |

### Staff Accounts
| Role | Username | Password |
|------|----------|----------|
| Admin | admin | admin123 |
| Doctor | dr.sarah | doctor123 |
| Doctor | dr.kwame | doctor123 |
| Nurse | nurse.grace | nurse123 |
| Pharmacist | pharm.paul | pharm123 |
| Lab Tech | lab.alice | lab123 |

## 🗄️ Database Schema

### Tables
- **patients** — id, name, dob, patient_id, ward, doctor, admission_date, blood_type, pin
- **staff** — id, name, role, username, password
- **bills** — id, bill_id, patient_ref, item, amount, date, status
- **prescriptions** — id, patient_ref, doctor_ref, medicine, dosage, notes, date, status
- **lab_orders** — id, patient_ref, doctor_ref, test_name, notes, date, status, result
- **nurse_tasks** — id, patient_ref, doctor_ref, service, notes, date, status

## 🔧 Technical Stack

- **Language**: Java
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36
- **Database**: SQLite (local storage)
- **Architecture**: Fragment-based with Bottom Navigation
- **Storage**: SharedPreferences for session management
- **UI Framework**: Material Design Components (MDC) 1.13.0
- **QR Code**: ZXing 4.3.0
- **Build**: Gradle 8.13 / Docker

## 🐛 Known Limitations
- Payment processing is simulated (no real payment gateway)
- No backend API — uses local SQLite database
- No email/SMS notifications
- No receipt PDF generation

## 🚧 Future Enhancements
- [ ] Integrate real payment gateways (Flutterwave, Paystack)
- [ ] Connect to Musanze Hospital's live billing system API
- [ ] Receipt PDF generation and email delivery
- [ ] Push notifications for payment reminders
- [ ] Support Kinyarwanda and French languages
- [ ] Biometric authentication
- [ ] Payment plans for large bills

## 📄 License
MIT License

## 👥 Contributors
- **Group AA** — Development

## 📞 Support
https://github.com/Imena23/BestPracticeHospitorBilling/issues

---
**Version**: 2.0.0
**Hospital**: Musanze Hospital, Rwanda
**Last Updated**: May 2026
**Status**: Active Development
