# FHIR Integration Documentation

## Overview

This application integrates with **HL7 FHIR R4** (Fast Healthcare Interoperability Resources) standard for healthcare data exchange.

## What is FHIR?

FHIR is a standard for exchanging healthcare information electronically. It's used by hospitals, clinics, and healthcare systems worldwide to share patient data, medical records, billing information, and more.

**Official Website:** https://www.hl7.org/fhir/

## FHIR Server

**Default Server:** HAPI FHIR Public Test Server  
**Base URL:** `https://hapi.fhir.org/baseR4/`  
**Documentation:** https://hapi.fhir.org/

### Why HAPI FHIR?
- Free public test server
- Fully compliant with FHIR R4 specification
- No authentication required for testing
- Supports all FHIR resources

## FHIR Resources Used

### 1. Patient Resource
**Purpose:** Represents patient demographic and administrative data

**Mapped Fields:**
- `id` → Patient FHIR ID
- `identifier` → Patient ID (PAT-2026-XXXX)
- `name` → Patient full name
- `birthDate` → Date of birth
- `gender` → Patient gender
- `telecom` → Contact information

**Example:**
```json
{
  "resourceType": "Patient",
  "id": "example-123",
  "identifier": [{
    "system": "http://hospital.example.org/patients",
    "value": "PAT-2026-0042"
  }],
  "name": [{
    "use": "official",
    "text": "John Doe"
  }],
  "birthDate": "1990-03-15",
  "gender": "male"
}
```

### 2. Claim Resource
**Purpose:** Represents a bill or invoice for healthcare services

**Mapped Fields:**
- `id` → Bill FHIR ID
- `status` → Bill status (active, cancelled)
- `patient` → Reference to Patient resource
- `item` → List of billed services
- `total` → Total amount in RWF

**Example:**
```json
{
  "resourceType": "Claim",
  "id": "claim-456",
  "status": "active",
  "patient": {
    "reference": "Patient/example-123"
  },
  "item": [{
    "sequence": 1,
    "productOrService": {
      "text": "Consultation Fee"
    },
    "unitPrice": {
      "value": 195000,
      "currency": "RWF"
    }
  }],
  "total": {
    "value": 3185000,
    "currency": "RWF"
  }
}
```

## Implementation

### Architecture

```
App Layer
    ↓
FhirClient (Singleton)
    ↓
Retrofit + OkHttp
    ↓
HAPI FHIR Server (REST API)
```

### Key Classes

1. **FhirClient.java**
   - Singleton pattern
   - Manages Retrofit instance
   - Configures HTTP client with logging

2. **FhirApiService.java**
   - Retrofit interface
   - Defines FHIR API endpoints
   - GET/POST operations for Patient and Claim

3. **FhirPatient.java**
   - FHIR Patient resource model
   - Maps to FHIR R4 Patient specification

4. **FhirClaim.java**
   - FHIR Claim resource model
   - Maps to FHIR R4 Claim specification

5. **FhirBundle.java**
   - FHIR Bundle resource for search results

## API Endpoints

### Patient Endpoints

**Get Patient by ID:**
```
GET /Patient/{id}
```

**Search Patients:**
```
GET /Patient?name={name}&birthdate={date}
```

**Create Patient:**
```
POST /Patient
Body: FhirPatient JSON
```

### Claim (Bill) Endpoints

**Get Claim by ID:**
```
GET /Claim/{id}
```

**Search Claims for Patient:**
```
GET /Claim?patient={patientId}
```

**Create Claim:**
```
POST /Claim
Body: FhirClaim JSON
```

## Usage Examples

### 1. Create Patient on FHIR Server

```java
FhirPatient patient = new FhirPatient();
patient.setName(Arrays.asList(new FhirPatient.HumanName("John Doe")));
patient.setBirthDate("1990-03-15");
patient.setGender("male");

FhirClient.getInstance()
    .getApiService()
    .createPatient(patient)
    .enqueue(new Callback<FhirPatient>() {
        @Override
        public void onResponse(Call<FhirPatient> call, Response<FhirPatient> response) {
            if (response.isSuccessful()) {
                String fhirId = response.body().getId();
                // Save fhirId to local database
            }
        }
        
        @Override
        public void onFailure(Call<FhirPatient> call, Throwable t) {
            // Handle error
        }
    });
```

### 2. Search for Patient

```java
FhirClient.getInstance()
    .getApiService()
    .searchPatients("John Doe", "1990-03-15")
    .enqueue(new Callback<FhirBundle>() {
        @Override
        public void onResponse(Call<FhirBundle> call, Response<FhirBundle> response) {
            if (response.isSuccessful() && response.body().getTotal() > 0) {
                // Patient found on FHIR server
            }
        }
        
        @Override
        public void onFailure(Call<FhirBundle> call, Throwable t) {
            // Handle error
        }
    });
```

### 3. Create Claim (Bill)

```java
FhirClaim claim = new FhirClaim();
claim.setStatus("active");
claim.setPatient(new FhirClaim.Reference("Patient/" + fhirPatientId));

List<FhirClaim.Item> items = new ArrayList<>();
items.add(new FhirClaim.Item(1, "Consultation Fee", 195000));
items.add(new FhirClaim.Item(2, "Laboratory Tests", 390000));
claim.setItem(items);

claim.setTotal(new FhirClaim.Money(3185000, "RWF"));

FhirClient.getInstance()
    .getApiService()
    .createClaim(claim)
    .enqueue(new Callback<FhirClaim>() {
        @Override
        public void onResponse(Call<FhirClaim> call, Response<FhirClaim> response) {
            if (response.isSuccessful()) {
                String claimId = response.body().getId();
                // Save claimId to local database
            }
        }
        
        @Override
        public void onFailure(Call<FhirClaim> call, Throwable t) {
            // Handle error
        }
    });
```

## Benefits of FHIR Integration

1. **Interoperability:** Data can be shared with other healthcare systems
2. **Standardization:** Uses globally recognized healthcare data format
3. **Scalability:** Easy to add more FHIR resources (Observation, Medication, etc.)
4. **Real-time Sync:** Patient data syncs with hospital systems
5. **Compliance:** Meets healthcare data exchange standards

## Future Enhancements

- [ ] Add FHIR Observation resource for lab results
- [ ] Add FHIR Medication resource for prescriptions
- [ ] Add FHIR Appointment resource for scheduling
- [ ] Implement FHIR Subscription for real-time updates
- [ ] Add FHIR DocumentReference for medical records
- [ ] Implement OAuth2 authentication for production FHIR server

## Testing

### Test with HAPI FHIR Server

1. App automatically connects to `https://hapi.fhir.org/baseR4/`
2. All data is stored on public test server
3. Data persists for testing purposes
4. No authentication required

### View Data in FHIR Server

Visit: https://hapi.fhir.org/resource?serverId=home_r4&pretty=true&resource=Patient

## Production Deployment

For production, replace the BASE_URL in `FhirClient.java`:

```java
// Replace this:
private static final String BASE_URL = "https://hapi.fhir.org/baseR4/";

// With your hospital's FHIR server:
private static final String BASE_URL = "https://your-hospital.com/fhir/";
```

Add authentication headers if required:

```java
OkHttpClient client = new OkHttpClient.Builder()
    .addInterceptor(chain -> {
        Request request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer " + accessToken)
            .build();
        return chain.proceed(request);
    })
    .build();
```

## Resources

- **FHIR Specification:** https://www.hl7.org/fhir/
- **HAPI FHIR:** https://hapifhir.io/
- **FHIR Patient Resource:** https://www.hl7.org/fhir/patient.html
- **FHIR Claim Resource:** https://www.hl7.org/fhir/claim.html
- **Retrofit Documentation:** https://square.github.io/retrofit/

## Support

For FHIR integration issues:
1. Check HAPI FHIR server status: https://hapi.fhir.org/
2. Review FHIR R4 specification
3. Check Retrofit logs in Android Logcat
4. Test endpoints using Postman or curl

---

**Version:** 1.0.0  
**FHIR Version:** R4  
**Last Updated:** April 2026
