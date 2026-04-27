package com.example.protypebillingsystem.fhir;

import com.example.protypebillingsystem.fhir.models.FhirPatient;
import com.example.protypebillingsystem.fhir.models.FhirClaim;
import com.example.protypebillingsystem.fhir.models.FhirBundle;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * FHIR R4 API Service Interface
 * Based on HL7 FHIR specification
 */
public interface FhirApiService {
    
    // Patient endpoints
    @GET("Patient/{id}")
    Call<FhirPatient> getPatient(@Path("id") String patientId);
    
    @GET("Patient")
    Call<FhirBundle> searchPatients(
            @Query("name") String name,
            @Query("birthdate") String birthdate
    );
    
    @POST("Patient")
    Call<FhirPatient> createPatient(@Body FhirPatient patient);
    
    // Claim (Bill) endpoints
    @GET("Claim/{id}")
    Call<FhirClaim> getClaim(@Path("id") String claimId);
    
    @GET("Claim")
    Call<FhirBundle> searchClaims(@Query("patient") String patientId);
    
    @POST("Claim")
    Call<FhirClaim> createClaim(@Body FhirClaim claim);
}
