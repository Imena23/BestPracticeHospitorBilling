package com.example.protypebillingsystem.fhir.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * FHIR R4 Patient Resource
 * Simplified model for billing system
 */
public class FhirPatient {
    
    @SerializedName("resourceType")
    private String resourceType = "Patient";
    
    @SerializedName("id")
    private String id;
    
    @SerializedName("identifier")
    private List<Identifier> identifier;
    
    @SerializedName("name")
    private List<HumanName> name;
    
    @SerializedName("birthDate")
    private String birthDate;
    
    @SerializedName("gender")
    private String gender;
    
    @SerializedName("telecom")
    private List<ContactPoint> telecom;
    
    // Nested classes
    public static class Identifier {
        @SerializedName("system")
        private String system;
        
        @SerializedName("value")
        private String value;
        
        public Identifier(String system, String value) {
            this.system = system;
            this.value = value;
        }
        
        public String getValue() { return value; }
    }
    
    public static class HumanName {
        @SerializedName("use")
        private String use;
        
        @SerializedName("family")
        private String family;
        
        @SerializedName("given")
        private List<String> given;
        
        @SerializedName("text")
        private String text;
        
        public HumanName(String text) {
            this.use = "official";
            this.text = text;
        }
        
        public String getText() { return text; }
    }
    
    public static class ContactPoint {
        @SerializedName("system")
        private String system;
        
        @SerializedName("value")
        private String value;
        
        public ContactPoint(String system, String value) {
            this.system = system;
            this.value = value;
        }
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public List<Identifier> getIdentifier() { return identifier; }
    public void setIdentifier(List<Identifier> identifier) { this.identifier = identifier; }
    
    public List<HumanName> getName() { return name; }
    public void setName(List<HumanName> name) { this.name = name; }
    
    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public List<ContactPoint> getTelecom() { return telecom; }
    public void setTelecom(List<ContactPoint> telecom) { this.telecom = telecom; }
}
