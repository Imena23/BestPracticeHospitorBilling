package com.example.protypebillingsystem.fhir.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MedicationRequest {
    @SerializedName("resourceType")
    private String resourceType;
    
    @SerializedName("id")
    private String id;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("medicationCodeableConcept")
    private CodeableConcept medicationCodeableConcept;
    
    @SerializedName("medicationReference")
    private Reference medicationReference;
    
    @SerializedName("authoredOn")
    private String authoredOn;
    
    @SerializedName("dosageInstruction")
    private List<Dosage> dosageInstruction;

    public String getId() { return id; }

    public String getMedicationName() {
        if (medicationCodeableConcept != null && medicationCodeableConcept.getCoding() != null && !medicationCodeableConcept.getCoding().isEmpty()) {
            String display = medicationCodeableConcept.getCoding().get(0).getDisplay();
            if (display != null && !display.isEmpty()) return display;
        }
        if (medicationReference != null && medicationReference.getDisplay() != null) {
            return medicationReference.getDisplay();
        }
        return "Unknown Medication";
    }

    public String getAuthoredOn() {
        return authoredOn != null ? authoredOn : "N/A";
    }

    public String getDosageText() {
        if (dosageInstruction != null && !dosageInstruction.isEmpty()) {
            return dosageInstruction.get(0).getText();
        }
        return "As directed";
    }

    public static class CodeableConcept {
        @SerializedName("coding")
        private List<Coding> coding;
        public List<Coding> getCoding() { return coding; }
    }

    public static class Coding {
        @SerializedName("display")
        private String display;
        public String getDisplay() { return display; }
    }

    public static class Reference {
        @SerializedName("display")
        private String display;
        public String getDisplay() { return display; }
    }

    public static class Dosage {
        @SerializedName("text")
        private String text;
        public String getText() { return text; }
    }
}
