package com.example.themannyhub.data;

import com.example.themannyhub.models.Garment;
import com.example.themannyhub.models.JacketMeasurements;
import com.example.themannyhub.models.ShirtMeasurements;
import com.example.themannyhub.models.SuitMeasurements;
import com.example.themannyhub.models.TrouserMeasurements;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Persists Garment objects to garments.json.
 * Each garment is stored as a flat object with a "type" discriminator
 * (TROUSERS, SHIRT, JACKET, SUIT) and a generic fields map.
 */
public class GarmentDAO {

    private static final String FILE_PATH = "garments.json";

    public List<Garment> loadGarments() {
        List<Garment> garments = new ArrayList<>();
        try {
            Path filePath = Paths.get(FILE_PATH);
            if (!Files.exists(filePath)) {
                System.out.println("No existing garment data found. Starting with an empty list");
                return garments;
            }
            String content = Files.readString(filePath);
            if (content.trim().isEmpty()) {
                return garments;
            }

            JSONObject root = new JSONObject(content);
            JSONArray arr = root.getJSONArray("garments");
            for (int i = 0; i < arr.length(); i++) {
                Garment g = parseGarment(arr.getJSONObject(i));
                if (g != null) {
                    garments.add(g);
                }
            }
            System.out.println("Successfully loaded " + garments.size() + " garments from file.");
        } catch (IOException e) {
            System.err.println("Error reading garment file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error parsing garment data: " + e.getMessage());
        }
        return garments;
    }

    public void saveGarments(List<Garment> garments) throws IOException {
        JSONObject root = new JSONObject();
        JSONArray arr = new JSONArray();
        for (Garment g : garments) {
            arr.put(convertGarment(g));
        }
        root.put("garments", arr);
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            writer.write(root.toString(4));
            writer.flush();
        }
        System.out.println("Successfully saved " + garments.size() + " garments to file");
    }

    // ----- serialization helpers -----

    private JSONObject convertGarment(Garment g) {
        JSONObject o = new JSONObject();
        o.put("id", g.getId());
        o.put("customerId", g.getCustomerId());
        o.put("type", g.getGarmentType());
        o.put("notes", g.getNotes() == null ? "" : g.getNotes());

        // Store the garment-specific measurements in a nested object.
        JSONObject m = new JSONObject();
        if (g instanceof TrouserMeasurements) {
            TrouserMeasurements t = (TrouserMeasurements) g;
            m.put("waist", t.getWaist());
            m.put("inseam", t.getInseam());
            m.put("hip", t.getHip());
            m.put("thigh", t.getThigh());
            m.put("frontRise", t.getFrontRise());
            m.put("backRise", t.getBackRise());
            m.put("fitPreferences", t.getFitPreferences());
        } else if (g instanceof ShirtMeasurements) {
            ShirtMeasurements s = (ShirtMeasurements) g;
            m.put("chestWidth", s.getChestWidth());
            m.put("shoulderWidth", s.getShoulderWidth());
            m.put("sleeveLength", s.getSleeveLength());
            m.put("neckSize", s.getNeckSize());
            m.put("shirtLength", s.getShirtLength());
            m.put("sleeveStyle", s.getSleeveStyle());
            m.put("collarStyle", s.getCollarStyle());
        } else if (g instanceof JacketMeasurements) {
            JacketMeasurements j = (JacketMeasurements) g;
            m.put("chestWidth", j.getChestWidth());
            m.put("shoulderWidth", j.getShoulderWidth());
            m.put("jacketLength", j.getJacketLength());
            m.put("sleeveLength", j.getSleeveLength());
            m.put("waistWidth", j.getWaistWidth());
            m.put("jacketFit", j.getJacketFit());
            m.put("lapelStyle", j.getLapelStyle());
        } else if (g instanceof SuitMeasurements) {
            SuitMeasurements s = (SuitMeasurements) g;
            m.put("jacketChestWidth", s.getJacketChestWidth());
            m.put("jacketShoulderWidth", s.getJacketShoulderWidth());
            m.put("jacketLength", s.getJacketLength());
            m.put("jacketSleeveLength", s.getJacketSleeveLength());
            m.put("trouserWaist", s.getTrouserWaist());
            m.put("trouserInseam", s.getTrouserInseam());
            m.put("trouserHip", s.getTrouserHip());
            m.put("trouserThigh", s.getTrouserThigh());
            m.put("trouserFrontRise", s.getTrouserFrontRise());
            m.put("trouserBackRise", s.getTrouserBackRise());
            m.put("jacketFit", s.getJacketFit());
            m.put("lapelStyle", s.getLapelStyle());
            m.put("trouserFitPreferences", s.getTrouserFitPreferences());
        }
        o.put("measurements", m);
        return o;
    }

    private Garment parseGarment(JSONObject o) {
        try {
            int id = o.optInt("id", 0);
            int customerId = o.optInt("customerId", 0);
            String type = o.optString("type", "TROUSERS");
            String notes = o.optString("notes", "");
            JSONObject m = o.optJSONObject("measurements");
            if (m == null) m = new JSONObject();

            switch (type.toUpperCase()) {
                case "SHIRT":
                    return new ShirtMeasurements(
                            id, customerId,
                            m.optDouble("chestWidth", 0),
                            m.optDouble("shoulderWidth", 0),
                            m.optDouble("sleeveLength", 0),
                            m.optDouble("neckSize", 0),
                            m.optDouble("shirtLength", 0),
                            m.optString("sleeveStyle", "LONG"),
                            m.optString("collarStyle", "SPREAD"),
                            notes);
                case "JACKET":
                    return new JacketMeasurements(
                            id, customerId,
                            m.optDouble("chestWidth", 0),
                            m.optDouble("shoulderWidth", 0),
                            m.optDouble("jacketLength", 0),
                            m.optDouble("sleeveLength", 0),
                            m.optDouble("waistWidth", 0),
                            m.optString("jacketFit", "REGULAR"),
                            m.optString("lapelStyle", "NOTCH"),
                            notes);
                case "SUIT":
                    return new SuitMeasurements(
                            id, customerId,
                            m.optDouble("jacketChestWidth", 0),
                            m.optDouble("jacketShoulderWidth", 0),
                            m.optDouble("jacketLength", 0),
                            m.optDouble("jacketSleeveLength", 0),
                            m.optDouble("trouserWaist", 0),
                            m.optDouble("trouserInseam", 0),
                            m.optDouble("trouserHip", 0),
                            m.optDouble("trouserThigh", 0),
                            m.optDouble("trouserFrontRise", 0),
                            m.optDouble("trouserBackRise", 0),
                            m.optString("jacketFit", "REGULAR"),
                            m.optString("lapelStyle", "NOTCH"),
                            m.optString("trouserFitPreferences", ""),
                            notes);
                case "TROUSERS":
                default:
                    return new TrouserMeasurements(
                            id, customerId,
                            m.optDouble("waist", 0),
                            m.optDouble("inseam", 0),
                            m.optDouble("hip", 0),
                            m.optDouble("thigh", 0),
                            m.optDouble("frontRise", 0),
                            m.optDouble("backRise", 0),
                            m.optString("fitPreferences", ""),
                            notes);
            }
        } catch (Exception e) {
            System.err.println("Error parsing garment from JSON: " + e.getMessage());
            return null;
        }
    }
}
