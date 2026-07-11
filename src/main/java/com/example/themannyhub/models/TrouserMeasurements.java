package com.example.themannyhub.models;

/**
 * TrouserMeasurements class - inherits from Garment.
 * Represents all measurements needed for custom tailored trousers.
 * Overrides validate() and getMeasurementSummary() to implement trouser-specific behavior.
 */
public class TrouserMeasurements extends Garment {

    // Trouser-specific measurements
    private double waist;
    private double inseam;
    private double hip;
    private double thigh;
    private double frontRise;
    private double backRise;
    private String fitPreferences;

    /**
     * Constructor for trouser measurements.
     * Calls the parent constructor and initializes trouser-specific fields.
     */
    public TrouserMeasurements(int id, double waist, double inseam, double hip,
                               double thigh, double frontRise, double backRise,
                               String fitPreferences, String notes) {
        super(id, "TROUSERS", notes);
        this.waist = waist;
        this.inseam = inseam;
        this.hip = hip;
        this.thigh = thigh;
        this.frontRise = frontRise;
        this.backRise = backRise;
        this.fitPreferences = fitPreferences;
    }

    /**
     * Overrides the abstract validate() method from Garment.
     * Implements trouser-specific validation rules.
     * This is polymorphism - the same method name does different things depending on the garment type.
     */
    @Override
    public void validate() {
        if (waist < 24 || waist > 50) {
            throw new IllegalArgumentException("Waist must be between 24 and 50 inches");
        }
        if (inseam < 26 || inseam > 50) {
            throw new IllegalArgumentException("Inseam must be between 26 and 50 inches");
        }
        if (hip < 30 || hip > 70) {
            throw new IllegalArgumentException("Hip must be between 30 and 70 inches");
        }
        if (thigh < 18 || thigh > 70) {
            throw new IllegalArgumentException("Thigh must be between 18 and 70 inches");
        }
        if (frontRise < 6 || frontRise > 20) {
            throw new IllegalArgumentException("Front rise must be between 6 and 20 inches");
        }
        if (backRise < 6 || backRise > 20) {
            throw new IllegalArgumentException("Back rise must be between 6 and 20 inches");
        }
    }

    /**
     * Overrides getMeasurementSummary() to return a formatted string of all trouser measurements.
     * Polymorphism - each garment type returns its own format.
     */
    @Override
    public String getMeasurementSummary() {
        return String.format("Trousers - Waist: %.1f\", Inseam: %.1f\", Hip: %.1f\", Thigh: %.1f\", Front Rise: %.1f\", Back Rise: %.1f\"",
                waist, inseam, hip, thigh, frontRise, backRise);
    }

    /**
     * Overrides getRequiredMeasurements() to list which fields are needed for trousers.
     * Useful for UI that needs to know which form fields to display.
     */
    @Override
    public String[] getRequiredMeasurements() {
        return new String[] {"waist", "inseam", "hip", "thigh", "frontRise", "backRise"};
    }

    // Getters and setters for trouser-specific measurements

    public double getWaist() {
        return waist;
    }

    public void setWaist(double waist) {
        this.waist = waist;
        this.dateLastModified = java.time.LocalDateTime.now();
    }

    public double getInseam() {
        return inseam;
    }

    public void setInseam(double inseam) {
        this.inseam = inseam;
        this.dateLastModified = java.time.LocalDateTime.now();
    }

    public double getHip() {
        return hip;
    }

    public void setHip(double hip) {
        this.hip = hip;
        this.dateLastModified = java.time.LocalDateTime.now();
    }

    public double getThigh() {
        return thigh;
    }

    public void setThigh(double thigh) {
        this.thigh = thigh;
        this.dateLastModified = java.time.LocalDateTime.now();
    }

    public double getFrontRise() {
        return frontRise;
    }

    public void setFrontRise(double frontRise) {
        this.frontRise = frontRise;
        this.dateLastModified = java.time.LocalDateTime.now();
    }

    public double getBackRise() {
        return backRise;
    }

    public void setBackRise(double backRise) {
        this.backRise = backRise;
        this.dateLastModified = java.time.LocalDateTime.now();
    }

    public String getFitPreferences() {
        return fitPreferences;
    }

    public void setFitPreferences(String fitPreferences) {
        this.fitPreferences = fitPreferences;
        this.dateLastModified = java.time.LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "TrouserMeasurements{" +
                "id=" + id +
                ", waist=" + waist +
                ", inseam=" + inseam +
                ", hip=" + hip +
                ", thigh=" + thigh +
                ", frontRise=" + frontRise +
                ", backRise=" + backRise +
                ", fitPreferences='" + fitPreferences + '\'' +
                ", dateCreated=" + dateCreated +
                ", dateLastModified=" + dateLastModified +
                '}';
    }
}