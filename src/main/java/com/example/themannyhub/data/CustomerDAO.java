package com.example.themannyhub.data;
import com.example.themannyhub.models.Customer;
import com.example.themannyhub.models.Status;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import  java.nio.file.Path;
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
            }

        }
    }


}
