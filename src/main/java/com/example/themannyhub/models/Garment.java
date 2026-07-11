package com.example.themannyhub.models;

import java.time.LocalDateTime;

/**
 * Abstract parent class for all garment types.
 * Owns the link to a Customer so garments can be persisted per-customer.
 */
public abstract class Garment {

    protected int id;
    protected int customerId;          // FK to Customer.id
    protected String garmentType;     // TROUSERS, SHIRT, JACKET, SUIT
    protected LocalDateTime dateCreated;
    protected LocalDateTime dateLastModified;
    protected String notes;

    public Garment(int id, int customerId, String garmentType, String notes) {
        this.id = id;
        this.customerId = customerId;
        this.garmentType = garmentType;
        this.notes = notes;
        this.dateCreated = LocalDateTime.now();
        this.dateLastModified = LocalDateTime.now();
    }

    public abstract void validate();
    public abstract String getMeasurementSummary();
    public abstract String[] getRequiredMeasurements();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getGarmentType() { return garmentType; }
    public void setGarmentType(String garmentType) { this.garmentType = garmentType; }

    public LocalDateTime getDateCreated() { return dateCreated; }
    public void setDateCreated(LocalDateTime dateCreated) { this.dateCreated = dateCreated; }

    public LocalDateTime getDateLastModified() { return dateLastModified; }
    public void setDateLastModified(LocalDateTime dateLastModified) { this.dateLastModified = dateLastModified; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) {
        this.notes = notes;
        this.dateLastModified = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Garment{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", garmentType='" + garmentType + '\'' +
                ", dateCreated=" + dateCreated +
                ", dateLastModified=" + dateLastModified +
                ", notes='" + notes + '\'' +
                '}';
    }
}
