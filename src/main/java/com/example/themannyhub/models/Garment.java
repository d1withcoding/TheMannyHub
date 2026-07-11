package com.example.themannyhub.models;

import java.time.LocalDateTime;

/**
 * Abstract parent class for all garment types.
 * Defines common behavior that all garments share.
 * Each garment type inherits from this and overrides validate() and getMeasurementSummary().
 */
public abstract class Garment {

    // Common fields for all garments
    protected int id;
    protected String garmentType; // TROUSERS, SHIRT, JACKET, SUIT
    protected LocalDateTime dateCreated;
    protected LocalDateTime dateLastModified;
    protected String notes;

    /**
     * Constructor for all garment types.
     * Initializes common fields.
     */
    public Garment(int id, String garmentType, String notes) {
        this.id = id;
        this.garmentType = garmentType;
        this.notes = notes;
        this.dateCreated = LocalDateTime.now();
        this.dateLastModified = LocalDateTime.now();
    }

    /**
     * Abstract method that must be overridden by each garment type.
     * Each garment type has different measurement validation rules.
     * Polymorphism happens here - the actual implementation depends on the concrete class.
     */
    public abstract void validate();

    /**
     * Abstract method that each garment type implements differently.
     * Returns a formatted string of all measurements specific to that garment.
     * Polymorphism - caller doesn't need to know which garment type it is.
     */
    public abstract String getMeasurementSummary();

    /**
     * Abstract method that returns which measurements are required for this garment type.
     * Useful for UI forms that need to know which fields to show.
     */
    public abstract String[] getRequiredMeasurements();

    // Common getters and setters for all garments

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGarmentType() {
        return garmentType;
    }

    public void setGarmentType(String garmentType) {
        this.garmentType = garmentType;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getDateLastModified() {
        return dateLastModified;
    }

    public void setDateLastModified(LocalDateTime dateLastModified) {
        this.dateLastModified = dateLastModified;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
        this.dateLastModified = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Garment{" +
                "id=" + id +
                ", garmentType='" + garmentType + '\'' +
                ", dateCreated=" + dateCreated +
                ", dateLastModified=" + dateLastModified +
                ", notes='" + notes + '\'' +
                '}';
    }
}