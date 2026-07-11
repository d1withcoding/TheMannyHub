package com.example.themannyhub.services;


import com.example.themannyhub.models.*;
import java.util.ArrayList;
import java.util.List;

/**
 * GarmentService class demonstrates polymorphism in action.
 * This service works with the abstract Garment class and can handle
 * any subclass (TrouserMeasurements, ShirtMeasurements, JacketMeasurements, SuitMeasurements)
 * without needing to know which specific type it is.
 *
 * This is powerful because the service doesn't need a separate method for each garment type.
 * One method can handle all types through polymorphism.
 */
public class GarmentService {

    private List<Garment> garments; // Can hold any garment type - this is polymorphism

    /**
     * Constructor - initializes the garment list.
     * The list can hold TrouserMeasurements, ShirtMeasurements, etc.
     */
    public GarmentService() {
        this.garments = new ArrayList<>();
    }

    /**
     * Adds a garment to the service.
     * Works with ANY garment type thanks to polymorphism.
     * The parameter is the abstract Garment class, so it accepts all subclasses.
     */
    public void addGarment(Garment garment) {
        try {
            garment.validate(); // Polymorphism: calls the validate() method of whatever subclass this is
            garments.add(garment);
            System.out.println("Garment added: " + garment.getMeasurementSummary()); // Polymorphism: different output per type
        } catch (IllegalArgumentException e) {
            System.out.println("Error adding garment: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Updates an existing garment.
     * Works with any garment type.
     */
    public void updateGarment(int id, Garment updatedGarment) {
        try {
            updatedGarment.validate();
            for (int i = 0; i < garments.size(); i++) {
                if (garments.get(i).getId() == id) {
                    garments.set(i, updatedGarment);
                    System.out.println("Garment updated: " + updatedGarment.getMeasurementSummary());
                    return;
                }
            }
            throw new IllegalArgumentException("Garment with ID " + id + " not found");
        } catch (IllegalArgumentException e) {
            System.out.println("Error updating garment: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Deletes a garment by ID.
     * Works with any garment type.
     */
    public void deleteGarment(int id) {
        for (Garment g : garments) {
            if (g.getId() == id) {
                garments.remove(g);
                System.out.println("Garment deleted: " + g.getGarmentType());
                return;
            }
        }
        throw new IllegalArgumentException("Garment with ID " + id + " not found");
    }

    /**
     * Gets a garment by ID.
     * Returns the abstract Garment type - the caller doesn't need to know the specific subclass.
     * Polymorphism: the actual object could be any subclass.
     */
    public Garment getGarmentById(int id) {
        for (Garment g : garments) {
            if (g.getId() == id) {
                return g;
            }
        }
        throw new IllegalArgumentException("Garment with ID " + id + " not found");
    }

    /**
     * Gets all garments.
     * Returns a list of abstract Garment objects, but they can be any subclass.
     */
    public List<Garment> getAllGarments() {
        return new ArrayList<>(garments);
    }

    /**
     * Gets all garments of a specific type.
     * Demonstrates how polymorphism works: we iterate through a list of Garment objects
     * and use getGarmentType() to filter. Each object knows its own type.
     */
    public List<Garment> getGarmentsByType(String garmentType) {
        List<Garment> filtered = new ArrayList<>();
        for (Garment g : garments) {
            if (g.getGarmentType().equals(garmentType)) {
                filtered.add(g);
            }
        }
        return filtered;
    }

    /**
     * Prints a summary of all garments.
     * This demonstrates polymorphism beautifully.
     * We call getMeasurementSummary() on each garment without knowing its type.
     * Each subclass returns its own unique summary.
     */
    public void printAllGarmentsSummary() {
        System.out.println("\n=== Garment Summary ===");
        for (Garment g : garments) {
            // Polymorphism: getMeasurementSummary() is different for each garment type
            System.out.println(g.getMeasurementSummary());
        }
        System.out.println("======================\n");
    }

    /**
     * Demonstrates polymorphism with a method that accepts any Garment.
     * The method doesn't need to care what type it is.
     */
    public String getFormattedMeasurements(Garment garment) {
        return garment.getMeasurementSummary(); // Polymorphism: different output per type
    }

    /**
     * Shows which measurements are required for a garment.
     * Polymorphism: each garment type returns its own required fields.
     */
    public void printRequiredMeasurements(int garmentId) {
        Garment g = getGarmentById(garmentId);
        String[] required = g.getRequiredMeasurements(); // Polymorphism: different fields per type
        System.out.println("Required measurements for " + g.getGarmentType() + ": ");
        for (String measurement : required) {
            System.out.println("  - " + measurement);
        }
    }

    /**
     * Gets the count of all garments.
     */
    public int getGarmentCount() {
        return garments.size();
    }
}