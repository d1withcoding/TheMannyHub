package com.example.themannyhub.models;

/**
 * SuitMeasurements class - inherits from Garment.
 * Represents all measurements needed for custom tailored suits.
 * A suit combines both jacket and trouser measurements.
 * Overrides validate() and getMeasurementSummary() with suit-specific logic.
 * Demonstrates inheritance - reuses common fields and methods from Garment.
 * Demonstrates polymorphism - validates and summarizes differently from other garment types.
 */
public class SuitMeasurements extends Garment {

    // Jacket part of the suit
    private double jacketChestWidth;
    private double jacketShoulderWidth;
    private double jacketLength;
    private double jacketSleeveLength;

    // Trouser part of the suit
    private double trouserWaist;
    private double trouserInseam;
    private double trouserHip;
    private double trouserThigh;
    private double trouserFrontRise;
    private double trouserBackRise;

    // Suit-specific options
    private String jacketFit; // SLIM, REGULAR, RELAXED
    private String lapelStyle; // NOTCH, PEAK, SHAWL
    private String trouserFitPreferences;

    /**
     * Constructor for suit measurements.
     * Calls parent constructor and initializes suit-specific fields.
     * Note: A suit has both jacket and trouser measurements combined.
     */
    public SuitMeasurements(int id,
                            double jacketChestWidth, double jacketShoulderWidth,
                            double jacketLength, double jacketSleeveLength,
                            double trouserWaist, double trouserInseam, double trouserHip,
                            double trouserThigh, double trouserFrontRise, double trouserBackRise,
                            String jacketFit, String lapelStyle, String trouserFitPreferences,
                            String notes) {
        super(id, "SUIT", notes);
        this.jacketChestWidth = jacketChestWidth;
        this.jacketShoulderWidth = jacketShoulderWidth;
        this.jacketLength = jacketLength;
        this.jacketSleeveLength = jacketSleeveLength;
        this.trouserWaist = trouserWaist;
        this.trouserInseam = trouserInseam;
        this.trouserHip = trouserHip;
        this.trouserThigh = trouserThigh;
        this.trouserFrontRise = trouserFrontRise;
        this.trouserBackRise = trouserBackRise;
        this.jacketFit = jacketFit;
        this.lapelStyle = lapelStyle;
        this.trouserFitPreferences = trouserFitPreferences;
    }

    /**
     * Overrides validate() with suit-specific validation rules.
     * A suit must validate both jacket and trouser measurements separately.
     * Polymorphism - same method name, different logic for suits.
     */
    @Override
    public void validate() {
        // Validate jacket measurements
        if (jacketChestWidth < 34 || jacketChestWidth > 60) {
            throw new IllegalArgumentException("Jacket chest width must be between 34 and 60 inches");
        }
        if (jacketShoulderWidth < 16 || jacketShoulderWidth > 24) {
            throw new IllegalArgumentException("Jacket shoulder width must be between 16 and 24 inches");
        }
        if (jacketLength < 26 || jacketLength > 36) {
            throw new IllegalArgumentException("Jacket length must be between 26 and 36 inches");
        }
        if (jacketSleeveLength < 25 || jacketSleeveLength > 40) {
            throw new IllegalArgumentException("Jacket sleeve length must be between 25 and 40 inches");
        }

        // Validate trouser measurements
        if (trouserWaist < 24 || trouserWaist > 50) {
            throw new IllegalArgumentException("Trouser waist must be between 24 and 50 inches");
        }
        if (trouserInseam < 26 || trouserInseam > 50) {
            throw new IllegalArgumentException("Trouser inseam must be between 26 and 50 inches");
        }
        if (trouserHip < 30 || trouserHip > 70) {
            throw new IllegalArgumentException("Trouser hip must be between 30 and 70 inches");
        }
        if (trouserThigh < 18 || trouserThigh > 70) {
            throw new IllegalArgumentException("Trouser thigh must be between 18 and 70 inches");
        }
        if (trouserFrontRise < 6 || trouserFrontRise > 20) {
            throw new IllegalArgumentException("Trouser front rise must be between 6 and 20 inches");
        }
        if (trouserBackRise < 6 || trouserBackRise > 20) {
            throw new IllegalArgumentException("Trouser back rise must be between 6 and 20 inches");
        }
    }

    /**
     * Overrides getMeasurementSummary() to format suit measurements.
     * A suit displays both jacket and trouser measurements together.
     * Polymorphism - same method, but returns combined jacket and trouser summary.
     */
    @Override
    public String getMeasurementSummary() {
        return String.format("Suit - Jacket: Chest %.1f\", Shoulder %.1f\", Length %.1f\", Sleeve %.1f\" | Trousers: Waist %.1f\", Inseam %.1f\" (%s, %s)",
                jacketChestWidth, jacketShoulderWidth, jacketLength, jacketSleeveLength,
                trouserWaist, trouserInseam, jacketFit, lapelStyle);
    }

    /**
     * Overrides getRequiredMeasurements() to list all suit-specific fields.
     * A suit has more measurements than any other garment type.
     */
    @Override
    public String[] getRequiredMeasurements() {
        return new String[] {"jacketChestWidth", "jacketShoulderWidth", "jacketLength", "jacketSleeveLength",
                "trouserWaist", "trouserInseam", "trouserHip", "trouserThigh",
                "trouserFrontRise", "trouserBackRise", "jacketFit", "lapelStyle"};
    }

    // Jacket measurement getters and setters

    public double getJacketChestWidth() {
        return jacketChestWidth;
    }

    public void setJacketChestWidth(double jacketChestWidth) {
        this.jacketChestWidth = jacketChestWidth;
        this.dateLastModified = java.time.LocalDateTime.now();
    }

    public double getJacketShoulderWidth() {
        return jacketShoulderWidth;
    }

    public void setJacketShoulderWidth(double jacketShoulderWidth) {
        this.jacketShoulderWidth = jacketShoulderWidth;
        this.dateLastModified = java.time.LocalDateTime.now();
    }

    public double getJacketLength() {
        return jacketLength;
    }

    public void setJacketLength(double jacketLength) {
        this.jacketLength = jacketLength;
        this.dateLastModified = java.time.LocalDateTime.now();
    }

    public double getJacketSleeveLength() {
        return jacketSleeveLength;
    }

    public void setJacketSleeveLength(double jacketSleeveLength) {
        this.jacketSleeveLength = jacketSleeveLength;
        this.dateLastModified = java.time.LocalDateTime.now();
    }

    // Trouser measurement getters and setters

    public double getTrouserWaist() {
        return trouserWaist;
    }

    public void setTrouserWaist(double trouserWaist) {
        this.trouserWaist = trouserWaist;
        this.dateLastModified = java.time.LocalDateTime.now();
    }

    public double getTrouserInseam() {
        return trouserInseam;
    }

    public void setTrouserInseam(double trouserInseam) {
        this.trouserInseam = trouserInseam;
        this.dateLastModified = java.time.LocalDateTime.now();
    }

    public double getTrouserHip() {
        return trouserHip;
    }

    public void setTrouserHip(double trouserHip) {
        this.trouserHip = trouserHip;
        this.dateLastModified = java.time.LocalDateTime.now();
    }

    public double getTrouserThigh() {
        return trouserThigh;
    }

    public void setTrouserThigh(double trouserThigh) {
        this.trouserThigh = trouserThigh;
        this.dateLastModified = java.time.LocalDateTime.now();
    }

    public double getTrouserFrontRise() {
        return trouserFrontRise;
    }

    public void setTrouserFrontRise(double trouserFrontRise) {
        this.trouserFrontRise = trouserFrontRise;
        this.dateLastModified = java.time.LocalDateTime.now();
    }

    public double getTrouserBackRise() {
        return trouserBackRise;
    }

    public void setTrouserBackRise(double trouserBackRise) {
        this.trouserBackRise = trouserBackRise;
        this.dateLastModified = java.time.LocalDateTime.now();
    }

    // Style options getters and setters

    public String getJacketFit() {
        return jacketFit;
    }

    public void setJacketFit(String jacketFit) {
        this.jacketFit = jacketFit;
        this.dateLastModified = java.time.LocalDateTime.now();
    }

    public String getLapelStyle() {
        return lapelStyle;
    }

    public void setLapelStyle(String lapelStyle) {
        this.lapelStyle = lapelStyle;
        this.dateLastModified = java.time.LocalDateTime.now();
    }

    public String getTrouserFitPreferences() {
        return trouserFitPreferences;
    }

    public void setTrouserFitPreferences(String trouserFitPreferences) {
        this.trouserFitPreferences = trouserFitPreferences;
        this.dateLastModified = java.time.LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "SuitMeasurements{" +
                "id=" + id +
                ", jacketChestWidth=" + jacketChestWidth +
                ", jacketShoulderWidth=" + jacketShoulderWidth +
                ", jacketLength=" + jacketLength +
                ", jacketSleeveLength=" + jacketSleeveLength +
                ", trouserWaist=" + trouserWaist +
                ", trouserInseam=" + trouserInseam +
                ", trouserHip=" + trouserHip +
                ", trouserThigh=" + trouserThigh +
                ", trouserFrontRise=" + trouserFrontRise +
                ", trouserBackRise=" + trouserBackRise +
                ", jacketFit='" + jacketFit + '\'' +
                ", lapelStyle='" + lapelStyle + '\'' +
                ", dateCreated=" + dateCreated +
                ", dateLastModified=" + dateLastModified +
                '}';
    }
}