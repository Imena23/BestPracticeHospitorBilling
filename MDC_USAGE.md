# Material Design Components (MDC) Usage

This document shows where Material Design Components are implemented in the MediPay app.

## Overview

The app uses **Material Design Components (MDC) 1.13.0** library throughout the UI for a modern, consistent user experience.

**Dependency**: `com.google.android.material:material:1.13.0`

## MDC Components Used

### 1. TextInputLayout (Outlined Style)

**Location**: All input forms throughout the app

**Files**:
- `activity_login.xml`
- `activity_register.xml`
- `dialog_card_payment.xml`

**Features**:
- Outlined box style with floating labels
- Start icons for visual context (calendar, lock, person icons)
- Password toggle (eye icon) for PIN fields
- Helper text below fields
- Error state handling
- Custom stroke colors matching app theme

**Example Usage**:
```xml
<com.google.android.material.textfield.TextInputLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:hint="Full Name"
    style="@style/Widget.MaterialComponents.TextInputLayout.OutlinedBox"
    app:boxStrokeColor="@color/primary_blue"
    app:hintTextColor="@color/primary_blue"
    app:startIconDrawable="@android:drawable/ic_menu_info_details"
    app:startIconTint="@color/primary_blue">

    <com.google.android.material.textfield.TextInputEditText
        android:id="@+id/et_name"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="textPersonName" />

</com.google.android.material.textfield.TextInputLayout>
```

**Where to See in UI**:
1. **Registration Screen**: All 5 input fields (Name, DOB, Patient ID, PIN, Confirm PIN)
2. **Login Screen**: All 3 input fields (Name, DOB, PIN)
3. **Card Payment Dialog**: All 4 card input fields (Card Number, Name, Expiry, CVV)

### 2. MaterialButton

**Location**: All buttons throughout the app

**Files**:
- `activity_login.xml`
- `activity_register.xml`
- `dialog_card_payment.xml`
- `dialog_mtn_momo.xml`

**Features**:
- Elevated style with shadow
- Ripple effect on press
- Rounded corners (8dp)
- Outlined variant for secondary actions
- Smooth state animations

**Example Usage**:
```xml
<!-- Primary Button -->
<com.google.android.material.button.MaterialButton
    android:id="@+id/btn_login"
    android:layout_width="match_parent"
    android:layout_height="56dp"
    android:text="Access My Bills"
    app:cornerRadius="8dp"
    app:elevation="2dp" />

<!-- Outlined Button -->
<com.google.android.material.button.MaterialButton
    android:id="@+id/btn_cancel"
    android:layout_width="match_parent"
    android:layout_height="52dp"
    android:text="Cancel"
    style="@style/Widget.MaterialComponents.Button.OutlinedButton"
    app:cornerRadius="8dp" />
```

**Where to See in UI**:
1. **Registration Screen**: "Create Account" button
2. **Login Screen**: "Access My Bills" button
3. **Card Payment Dialog**: "Pay Now" (elevated) and "Cancel" (outlined) buttons
4. **MTN MoMo Dialog**: All action buttons

### 3. BottomNavigationView

**Location**: Main app navigation

**Files**:
- `activity_main.xml`
- `bottom_nav_menu.xml`

**Features**:
- Tab-based navigation with icons
- Active/inactive state colors
- Ripple effect on tab selection
- Smooth transitions between fragments
- Material elevation

**Example Usage**:
```xml
<com.google.android.material.bottomnavigation.BottomNavigationView
    android:id="@+id/bottom_navigation"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="@drawable/nav_background"
    app:itemIconTint="@color/nav_selector"
    app:itemTextColor="@color/nav_selector"
    app:menu="@menu/bottom_nav_menu" />
```

**Where to See in UI**:
1. **Main Screen**: Bottom navigation bar with 4 tabs (Home, Bills, Payments, Profile)
2. **Tab Icons**: Home, Receipt, Wallet, Person icons
3. **Active State**: Blue highlight on selected tab

### 4. Snackbar

**Location**: User feedback throughout the app

**Files**:
- `RegisterActivity.java`
- `LoginActivity.java`

**Features**:
- Bottom notification bar
- Auto-dismiss or indefinite duration
- Action buttons (optional)
- Material elevation and shadow
- Smooth slide-in/out animations

**Example Usage**:
```java
// Loading snackbar
Snackbar loadingSnack = Snackbar.make(rootView, "Syncing with hospital system...", Snackbar.LENGTH_INDEFINITE);
loadingSnack.show();

// Success snackbar
Snackbar.make(rootView, "✓ Synced with hospital system", Snackbar.LENGTH_SHORT).show();

// Info snackbar
Snackbar.make(rootView, "Registered locally (hospital sync pending)", Snackbar.LENGTH_SHORT).show();
```

**Where to See in UI**:
1. **Registration**: Shows "Syncing with hospital system..." then "✓ Synced with hospital system"
2. **Login**: Shows "✓ Verified with hospital system" when FHIR verification succeeds
3. **Fallback**: Shows "Registered locally (hospital sync pending)" if FHIR server unavailable

### 5. Material Themes

**Location**: App-wide theming

**Files**:
- `themes.xml`
- `colors.xml`

**Features**:
- Material color system
- Primary, secondary, accent colors
- Surface and background colors
- Typography scale
- Shape theming (rounded corners)

**Theme Configuration**:
```xml
<style name="Theme.ProtypeBillingSystem" parent="Theme.MaterialComponents.DayLight.NoActionBar">
    <item name="colorPrimary">@color/primary_blue</item>
    <item name="colorPrimaryVariant">@color/primary_blue_dark</item>
    <item name="colorOnPrimary">@color/white</item>
    <item name="colorSecondary">@color/accent_green</item>
    <item name="colorOnSecondary">@color/white</item>
</style>
```

**Where to See in UI**:
1. **Entire App**: Consistent color scheme with primary blue (#1565C0)
2. **Buttons**: Material elevation and shadows
3. **Cards**: Rounded corners and elevation
4. **Ripples**: Touch feedback on all interactive elements

### 6. Material Ripple Effects

**Location**: All interactive elements

**Features**:
- Touch feedback on buttons
- Ripple animation on press
- Bounded and unbounded ripples
- Color-matched to theme

**Where to See in UI**:
1. **All Buttons**: Press any button to see ripple effect
2. **Bottom Navigation**: Tap tabs to see ripple
3. **List Items**: Tap bill items to see ripple
4. **Cards**: Tap payment cards to see ripple

## Visual Comparison

### Before MDC (Old Style)
- Plain EditText with custom backgrounds
- Basic Button with drawable backgrounds
- No animations or transitions
- Inconsistent spacing and sizing
- Manual state management

### After MDC (Current Style)
- ✅ TextInputLayout with floating labels and icons
- ✅ MaterialButton with elevation and ripples
- ✅ Smooth animations and transitions
- ✅ Consistent Material spacing (8dp grid)
- ✅ Automatic state management

## Benefits of MDC

1. **Consistency**: All components follow Material Design guidelines
2. **Accessibility**: Built-in accessibility features (screen readers, touch targets)
3. **Animations**: Smooth, professional animations out of the box
4. **Theming**: Easy to customize colors and styles app-wide
5. **Maintenance**: Less custom code, more standard components
6. **Modern Look**: Up-to-date with latest Android design trends

## Testing MDC Features

### Test TextInputLayout
1. Open Registration or Login screen
2. Tap any input field
3. Observe:
   - Label floats up smoothly
   - Outline changes color to blue
   - Start icon appears
   - Cursor blinks inside field

### Test MaterialButton
1. Tap any button in the app
2. Observe:
   - Ripple effect spreads from touch point
   - Button slightly elevates on press
   - Smooth color transition
   - Shadow appears/disappears

### Test Password Toggle
1. Open Registration or Login screen
2. Enter PIN in password field
3. Tap eye icon on right
4. Observe:
   - PIN becomes visible/hidden
   - Icon changes between eye and eye-off
   - Smooth transition

### Test Snackbar
1. Register a new account
2. Observe bottom notification:
   - "Syncing with hospital system..." appears
   - Slides up from bottom
   - Changes to "✓ Synced with hospital system"
   - Auto-dismisses after 2 seconds

### Test Bottom Navigation
1. Open main app screen
2. Tap different tabs
3. Observe:
   - Ripple effect on tap
   - Icon and text color change
   - Smooth fragment transition
   - Active tab highlighted in blue

## MDC Documentation

- **Official Docs**: https://material.io/develop/android
- **Component Catalog**: https://material.io/components
- **Design Guidelines**: https://material.io/design

## Screenshots Reference

### Registration Screen (MDC TextInputLayout)
- 5 outlined text fields with icons
- Floating labels
- Helper text under Patient ID field
- Password toggle on PIN fields
- Material button at bottom

### Login Screen (MDC TextInputLayout)
- 3 outlined text fields with icons
- Calendar icon for DOB
- Lock icon for PIN
- Password toggle
- Material button

### Card Payment Dialog (MDC Components)
- 4 outlined text fields for card details
- Card icon on card number field
- Password toggle on CVV field
- 2 material buttons (elevated + outlined)

### Bottom Navigation (MDC BottomNavigationView)
- 4 tabs with icons
- Blue highlight on active tab
- Ripple effect on tap
- Material elevation

---

**Version**: 1.0.0  
**Last Updated**: April 2026  
**MDC Library**: 1.13.0
