package com.example.themannyhub.data;
import com.example.themannyhub.models.Customer;
import com.example.themannyhub.models.Status;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    private static final String FILE_PATH = "customers.json";
    //load customers from json file to an array

    public List<Customer> loadCustomersFromFile(){
        List<Customer> customers = new ArrayList<>();

        try{
            Path filePath = Paths.get(FILE_PATH);

            if (!Files.exists(filePath)){
                System.out.println("No existing customer data found. Starting with an empty list");
                //Return empty list for customers
                return customers;
            }

            String content = Files.readString(filePath);
            if (content.trim().isEmpty()){
                System.out.println("Customer list is empty. Starting with empty list");
                return customers;
            }

            JSONObject jsonObject = new JSONObject(content);
            JSONArray jsonArray = jsonObject.getJSONArray("customers");


            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject customerJson = jsonArray.getJSONObject(i);
                Customer customer = parseCustomerFromJson(customerJson);
                if (customer != null){
                    customers.add(customer);
                }
            }

            System.out.println("Successfully loaded " + customers.size() + " customers from this file.");
        } catch (IOException e){
            System.err.println("Error reading customer file: " + e.getMessage());
        } catch (Exception e){
            System.out.println("Error parsing customer data: " + e.getMessage());
        }
        return  customers;
    }

    public void saveCustomersToFile(List<Customer> customers) throws IOException{
        // Object to hold customer information (Folder)
        JSONObject rootObject = new JSONObject();

        //Array to hold all customers
        JSONArray customersArray = new JSONArray();

        for (Customer customer : customers){
            JSONObject customerJson = convertCustomerToJson(customer);
            customersArray.put(customerJson);
        }

        rootObject.put("customers", customersArray);

        // Open fileWriter and close with try
        try(FileWriter  writer = new FileWriter(FILE_PATH)){
            writer.write(rootObject.toString(4));
            writer.flush();//Ensures all data is written
        }

        System.out.println("Successfully saved " + customers.size() + " customers to file");
    }

    //Helper methods to convert Customer Data to JSON and JSON to customer data
    private Customer parseCustomerFromJson(JSONObject json){
        try{
            int id = json.optInt("id", 0);
            String name = json.optString("name", "");
            String phone = json.optString("phone", "");

            // Use opt method to keep system from crashing if field is missing
            double waist = json.optDouble("waist", 0.0);
            double inseam = json.optDouble("inseam", 0.0);
            double hip = json.optDouble("hip", 0.0);
            double thigh = json.optDouble("thigh", 0.0);
            double frontRise = json.optDouble("frontRise", 0.0);
            double backRise = json.optDouble("backRise", 0.0);

            String fitPreferences = json.optString("fitPreferences", "");
            int orderCount = json.optInt("orderCount", 0);

            String statusString = json.optString("status", "ACTIVE");
            Status status;
            try{
                status = Status.valueOf(statusString.toUpperCase());
            } catch (IllegalArgumentException e){
                System.err.println("Invalid status " + statusString + " for customer " + id + ". Defaulting to ACTIVE");
                status = Status.ACTIVE;
            }

            Customer customer = new Customer(id, name, phone, waist, inseam, thigh,hip, frontRise, backRise, fitPreferences, status);
            return customer;
        } catch (Exception e){
            System.err.println("Error parsing customer from JSON: " + e.getMessage());
            return null;
        }


    }

    private JSONObject convertCustomerToJson(Customer customer){
        JSONObject json = new JSONObject();
        json.put("id", customer.getId());
        json.put("name", customer.getName());
        json.put("phone", customer.getPhone());
        json.put("waist", customer.getWaist());
        json.put("inseam", customer.getInseam());
        json.put("hip", customer.getHip());
        json.put("thigh", customer.getThigh());
        json.put("frontRise", customer.getFrontRise());
        json.put("backRise", customer.getBackRise());
        json.put("fitPreferences", customer.getFitPreferences());
        json.put("orderCount", customer.getOrderCount());

        json.put("status", customer.getStatus().name());
        return json;
    }

}
