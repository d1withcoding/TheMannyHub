package com.example.themannyhub.services;

import com.example.themannyhub.data.GarmentDAO;
import com.example.themannyhub.models.Garment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages Garment persistence and lookups. Holds a polymorphic List<Garment>
 * but every entry is now tied to a specific customer (via customerId).
 */
public class GarmentService {

    private final GarmentDAO dao;
    private List<Garment> garments;
    private int nextId;

    public GarmentService() {
        this.dao = new GarmentDAO();
        this.garments = dao.loadGarments();
        this.nextId = calculateNextId();
        System.out.println("GarmentService initialized with " + garments.size()
                + " garments. Next ID: " + nextId);
    }

    private int calculateNextId() {
        int max = 0;
        for (Garment g : garments) {
            if (g.getId() > max) max = g.getId();
        }
        return max + 1;
    }

    /** Adds a new garment, validates it, and persists the whole list. */
    public Garment addGarment(Garment garment) throws IOException {
        garment.validate();
        garment.setId(nextId++);
        garments.add(garment);
        dao.saveGarments(garments);
        return garment;
    }

    /** Replaces the garment with the same id. Validates first. */
    public void updateGarment(Garment updated) throws IOException {
        updated.validate();
        boolean found = false;
        for (int i = 0; i < garments.size(); i++) {
            if (garments.get(i).getId() == updated.getId()) {
                garments.set(i, updated);
                found = true;
                break;
            }
        }
        if (!found) {
            throw new IllegalArgumentException("Garment with ID " + updated.getId() + " not found");
        }
        dao.saveGarments(garments);
    }

    public void deleteGarment(int id) throws IOException {
        boolean removed = garments.removeIf(g -> g.getId() == id);
        if (!removed) {
            throw new IllegalArgumentException("Garment with ID " + id + " not found");
        }
        dao.saveGarments(garments);
    }

    public List<Garment> getAllGarments() {
        return new ArrayList<>(garments);
    }

    public List<Garment> getGarmentsForCustomer(int customerId) {
        List<Garment> out = new ArrayList<>();
        for (Garment g : garments) {
            if (g.getCustomerId() == customerId) {
                out.add(g);
            }
        }
        return out;
    }

    public List<Garment> getGarmentsByType(String garmentType) {
        List<Garment> out = new ArrayList<>();
        for (Garment g : garments) {
            if (g.getGarmentType().equalsIgnoreCase(garmentType)) {
                out.add(g);
            }
        }
        return out;
    }

    /**
     * Removes every garment belonging to a customer. Called when a customer
     * is deleted so we don't leave orphan rows in garments.json.
     */
    public void deleteGarmentsForCustomer(int customerId) throws IOException {
        boolean removed = garments.removeIf(g -> g.getCustomerId() == customerId);
        if (removed) {
            dao.saveGarments(garments);
        }
    }

    public int getGarmentCount() {
        return garments.size();
    }

    public String getFormattedMeasurements(Garment garment) {
        return garment.getMeasurementSummary();
    }
}
