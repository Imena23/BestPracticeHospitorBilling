package com.example.protypebillingsystem.fhir.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * FHIR R4 Bundle Resource
 * Used for search results
 */
public class FhirBundle {
    
    @SerializedName("resourceType")
    private String resourceType = "Bundle";
    
    @SerializedName("type")
    private String type;
    
    @SerializedName("total")
    private int total;
    
    @SerializedName("entry")
    private List<Entry> entry;
    
    public static class Entry {
        @SerializedName("resource")
        private Object resource;
        
        public Object getResource() { return resource; }
    }
    
    public int getTotal() { return total; }
    public List<Entry> getEntry() { return entry; }
}
