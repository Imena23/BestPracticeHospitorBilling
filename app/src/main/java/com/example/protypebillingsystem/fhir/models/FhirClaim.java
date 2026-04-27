package com.example.protypebillingsystem.fhir.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * FHIR R4 Claim Resource (represents a bill)
 * Simplified model for billing system
 */
public class FhirClaim {
    
    @SerializedName("resourceType")
    private String resourceType = "Claim";
    
    @SerializedName("id")
    private String id;
    
    @SerializedName("status")
    private String status; // active, cancelled, draft, entered-in-error
    
    @SerializedName("type")
    private CodeableConcept type;
    
    @SerializedName("patient")
    private Reference patient;
    
    @SerializedName("created")
    private String created;
    
    @SerializedName("provider")
    private Reference provider;
    
    @SerializedName("priority")
    private CodeableConcept priority;
    
    @SerializedName("item")
    private List<Item> item;
    
    @SerializedName("total")
    private Money total;
    
    // Nested classes
    public static class Reference {
        @SerializedName("reference")
        private String reference;
        
        @SerializedName("display")
        private String display;
        
        public Reference(String reference) {
            this.reference = reference;
        }
        
        public String getReference() { return reference; }
    }
    
    public static class CodeableConcept {
        @SerializedName("coding")
        private List<Coding> coding;
        
        @SerializedName("text")
        private String text;
        
        public CodeableConcept(String text) {
            this.text = text;
        }
    }
    
    public static class Coding {
        @SerializedName("system")
        private String system;
        
        @SerializedName("code")
        private String code;
        
        @SerializedName("display")
        private String display;
    }
    
    public static class Item {
        @SerializedName("sequence")
        private int sequence;
        
        @SerializedName("productOrService")
        private CodeableConcept productOrService;
        
        @SerializedName("unitPrice")
        private Money unitPrice;
        
        public Item(int sequence, String service, double amount) {
            this.sequence = sequence;
            this.productOrService = new CodeableConcept(service);
            this.unitPrice = new Money(amount, "RWF");
        }
    }
    
    public static class Money {
        @SerializedName("value")
        private double value;
        
        @SerializedName("currency")
        private String currency;
        
        public Money(double value, String currency) {
            this.value = value;
            this.currency = currency;
        }
        
        public double getValue() { return value; }
        public String getCurrency() { return currency; }
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Reference getPatient() { return patient; }
    public void setPatient(Reference patient) { this.patient = patient; }
    
    public List<Item> getItem() { return item; }
    public void setItem(List<Item> item) { this.item = item; }
    
    public Money getTotal() { return total; }
    public void setTotal(Money total) { this.total = total; }
}
