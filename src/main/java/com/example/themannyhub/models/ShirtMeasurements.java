package com.example.themannyhub.models;


/**
 * ShirtMeasurements class - inherits from Garment.
 * Represents all measurements needed for custom tailored shirts.
 * Overrides validate() and getMeasurementSummary() with shirt-specific logic.
 * Demonstrates inheritance - reuses common fields and methods from Garment.
 * Demonstrates polymorphism - each garment type validates and summarizes differently.
 */
public class ShirtMeasurements extends Garment {

    // Shirt-specific measurements
    private double chestWidth;
    private double shoulderWidth;
    private double sleeveLength;
    private double neckSize;
    private double shirtLength;
    private String sleeveStyle; // SHORT, LONG, THREE_QUARTER
    private String collarStyle; // SPREAD, POINT, BUTTON_DOWN

    /**
     * Constructor for shirt measurements.
     * Calls parent constructor and initializes shirt-specific fields.
     */
    public ShirtMeasurements(int id, double chestWidth, double shoulderWidth,
                             double sleeveLength, double neckSize, double shirtLength,
                             String sleeveStyle, String collarStyle, String notes) {
        super(id, "SHIRT", notes);
        this.chestWidth = chestWidth;
        this.shoulderWidth = shoulderWidth;
        this.sleeveLength = sleeveLength;
        this.neckSize = neckSize;
        this.shirtLength = shirtLength;
        this.sleeveStyle = sleeveStyle;
        this.collarStyle = collarStyle;
    }

    /**
     * Overrides validate() with shirt-specific validation rules.
     * Different garment types have different measurement ranges.
     * This is polymorphism - the method name is the same, but behavior differs.
     */
    @Override
    public void validate() {
        if (chestWidth < 32 || chestWidth > 60) {
            throw new IllegalArgumentException("Chest width must be between 32 and 60 inches");
        }
        if (shoulderWidth < 14 || shoulderWidth > 22) {
            throw new IllegalArgumentException("Shoulder width must be between 14 and 22 inches");
        }
        if (sleeveLength < 24 || sleeveLength > 38) {
            throw new IllegalArgumentException("Sleeve length must be between 24 and 38 inches");
        }
        if (neckSize < 13 || neckSize > 20) {
            throw new IllegalArgumentException("Neck size must be between 13 and 20 inches");
        }
        if (shirtLength < 28 || shirtLength > 35) {
            throw new IllegalArgumentException("Shirt length must be between 28 and 35 inches");
        }
    }

    /**
     * Overrides getMeasurementSummary() to format shirt measurements.
     * Each garment type has its own specific measurements to display.
     * Polymorphism - same method name, different output based on garment type.
     */
    @Override
    public String getMeasurementSummary() {
        return String.format("Shirt - Chest: %.1f\", Shoulder: %.1f\", Sleeve: %.1f\", Neck: %.1f\", Length: %.1f\" (%s, %s)",
                chestWidth, shoulderWidth, sleeveLength, neckSize, shirtLength, sleeveStyle, collarStyle);
    }

    /**
     * Overrides getRequiredMeasurements() to list shirt-specific fields.
     */
    @Override
    public String[] getRequiredMeasurements() {
        return new String[] {"chestWidth", "shoulderWidth", "sleeveLength", "neckSize", "shirtLength", "sleeveStyle", "collarStyle"};
    }

    // Getters and setters for shirt-specific measurements

    public double getChestWidth() {
        return chestWidth;
    }

    public void setChestWidth(double chestWidth) {
        this.chestWidth = chestWidth;
        this.dateLastModified = java.time.LocalDateTime.now();
    }

    public double getShoulderWidth() {
        return shoulderWidth;
    }

    public void setShoulderWidth(double shoulderWidth) {
        this.shoulderWidth = shoulderWidth;
        this.dateLastModified = java.time.LocalDateTime.now();
    }

    public double getSleeveLength() {
        return sleeveLength;
    }

    public void setSleeveLength(double sleeveLength) {
        this.sleeveLength = sleeveLength;
        this.dateLastModified = java.time.LocalDateTime.now();
    }

    public double getNeckSize() {
        return neckSize;
    }

    public void setNeckSize(double neckSize) {
        this.neckSize = neckSize;
        this.dateLastModified = java.time.LocalDateTime.now();
    }

    public double getShirtLength() {
        return shirtLength;
    }

    public void setShirtLength(double shirtLength) {
        this.shirtLength = shirtLength;
        this.dateLastModified = java.time.LocalDateTime.now();
    }

    public String getSleeveStyle() {
        return sleeveStyle;
    }

    public void setSleeveStyle(String sleeveStyle) {
        this.sleeveStyle = sleeveStyle;
        this.dateLastModified = java.time.LocalDateTime.now();
    }

    public String getCollarStyle() {
        return collarStyle;
    }

    public void setCollarStyle(String collarStyle) {
        this.collarStyle = collarStyle;
        this.dateLastModified = java.time.LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "ShirtMeasurements{" +
                "id=" + id +
                ", chestWidth=" + chestWidth +
                ", shoulderWidth=" + shoulderWidth +
                ", sleeveLength=" + sleeveLength +
                ", neckSize=" + neckSize +
                ", shirtLength=" + shirtLength +
                ", sleeveStyle='" + sleeveStyle + '\'' +
                ", collarStyle='" + collarStyle + '\'' +
                ", dateCreated=" + dateCreated +
                ", dateLastModified=" + dateLastModified +
                '}';
    }
}