package com.example.protypebillingsystem.fhir;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

/**
 * FHIR Client for connecting to HAPI FHIR server
 * Default: Public HAPI FHIR test server
 */
public class FhirClient {
    
    // Public HAPI FHIR test server (you can replace with your own)
    private static final String BASE_URL = "https://hapi.fhir.org/baseR4/";
    
    private static FhirClient instance;
    private final FhirApiService apiService;
    
    private FhirClient() {
        // Logging interceptor for debugging
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        // OkHttp client with timeout
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        
        // Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        apiService = retrofit.create(FhirApiService.class);
    }
    
    public static synchronized FhirClient getInstance() {
        if (instance == null) {
            instance = new FhirClient();
        }
        return instance;
    }
    
    public FhirApiService getApiService() {
        return apiService;
    }
}
