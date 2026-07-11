package com.example.themannyhub.models;

/**
 * JacketMeasurements class - inherits from Garment.
 */
public class JacketMeasurements extends Garment {

    private double chestWidth;
    private double shoulderWidth;
    private double jacketLength;
    private double sleeveLength;
    private double waistWidth;
    private String jacketFit;    // SLIM, REGULAR, RELAXED
    private String lapelStyle;   // NOTCH, PEAK, SHAWL

    public JacketMeasurements(int id, int customerId,
                              double chestWidth, double shoulderWidth,
                              double jacketLength, double sleeveLength, double waistWidth,
                              String jacketFit, String lapelStyle, String notes) {
        super(id, customerId, "JACKET", notes);
        this.chestWidth = chestWidth;
        this.shoulderWidth = shoulderWidth;
        this.jacketLength = jacketLength;
        this.sleeveLength = sleeveLength;
        this.waistWidth = waistWidth;
        this.jacketFit = jacketFit;
        this.lapelStyle = lapelStyle;
    }

    @Override
    public void validate() {
        if (chestWidth < 34 || chestWidth > 60) {
            throw new IllegalArgumentException("Chest width must be between 34 and 60 inches");
        }
        if (shoulderWidth < 16 || shoulderWidth > 24) {
            throw new IllegalArgumentException("Shoulder width must be between 16 and 24 inches");
        }
        if (jacketLength < 26 || jacketLength > 36) {
            throw new IllegalArgumentException("Jacket length must be between 26 and 36 inches");
        }
        if (sleeveLength < 25 || sleeveLength > 40) {
            throw new IllegalArgumentException("Sleeve length must be between 25 and 40 inches");
        }
        if (waistWidth < 30 || waistWidth > 58) {
            throw new IllegalArgumentException("Waist width must be between 30 and 58 inches");
        }
    }

    @Override
    public String getMeasurementSummary() {
        return String.format("Jacket - Chest: %.1f\", Shoulder: %.1f\", Length: %.1f\", Sleeve: %.1f\", Waist: %.1f\" (%s, %s)",
                chestWidth, shoulderWidth, jacketLength, sleeveLength, waistWidth, jacketFit, lapelStyle);
    }

    @Override
    public String[] getRequiredMeasurements() {
        return new String[] {"chestWidth", "shoulderWidth", "jacketLength", "sleeveLength", "waistWidth", "jacketFit", "lapelStyle"};
    }

    public double getChestWidth() { return chestWidth; }
    public void setChestWidth(double chestWidth) {
        this.chestWidth = chestWidth;
        this.dateLastModified = java.time.LocalDateTime.now();
    }

    public double getShoulderWidth() { return shoulderWidth; }
    public void setShoulderWidth(double shoulderWidth) {
        this.shoulderWidth = shoulderWidth;
        this.dateLastModified = java.time.LocalDateTime.now();
    }

    public double getJacketLength() { return jacketLength; }
    public void setJacketLength(double jacketLength) {
        this.jacketLength = jacketLength;
        this.dateLastModified = java.time.LocalDateTime.now();
    }

    public double getSleeveLength() { return sleeveLength; }
    public void setSleeveLength(double sleeveLength) {
        this.sleeveLength = sleeveLength;
        this.dateLastModified = java.time.LocalDateTime.now();
    }

    public double getWaistWidth() { return waistWidth; }
    public void setWaistWidth(double waistWidth) {
        this.waistWidth = waistWidth;
        this.dateLastModified = java.time.LocalDateTime.now();
    }

    public String getJacketFit() { return jacketFit; }
    public void setJacketFit(String jacketFit) {
        this.jacketFit = jacketFit;
        this.dateLastModified = java.time.LocalDateTime.now();
    }

    public String getLapelStyle() { return lapelStyle; }
    public void setLapelStyle(String lapelStyle) {
        this.lapelStyle = lapelStyle;
        this.dateLastModified = java.time.LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "JacketMeasurements{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", chestWidth=" + chestWidth +
                ", shoulderWidth=" + shoulderWidth +
                ", jacketLength=" + jacketLength +
                ", sleeveLength=" + sleeveLength +
                ", waistWidth=" + waistWidth +
                ", jacketFit='" + jacketFit + '\'' +
                ", lapelStyle='" + lapelStyle + '\'' +
                ", dateCreated=" + dateCreated +
                ", dateLastModified=" + dateLastModified +
                '}';
    }
}
