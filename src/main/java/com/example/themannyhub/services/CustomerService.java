package com.example.themannyhub.services;

import com.example.themannyhub.data.CustomerDAO;
import com.example.themannyhub.models.*;
import com.example.themannyhub.utils.ValidationUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerService {
    // Initializing master list of all customers
    private  List<Customer> customers;

    //Reference to reuse Data Access Object
    private CustomerDAO dao;

    //ID tracking and assigning to customers
    private int nextCustomerId;

    // Constructor for users
    public  CustomerService(){
        //first init a new dao to load and save customer data
        this.dao = new CustomerDAO();

        //load  all existing customers
        this.customers = dao.loadCustomersFromFile();

        //calculate next customers id
        this.nextCustomerId = calculateNextId();

        //Debugging and error handling
        System.out.println("CustomerService initialized with "
                + this.customers.size() +
                " customers. Next ID: " +
                this.nextCustomerId
        );

    }

    private  int calculateNextId(){
        //check if list is empty
        if (customers.isEmpty()){
            return 1;
        }
        int maxId = 0;
        for (Customer c : customers){
            if (c.getId() > maxId){
                maxId =c.getId();
            }
        }
        return maxId + 1;
    }

    // Adding Customers (new customers)
    public Customer addCustomer(String name, String phone,
                                double waist, double inseam,
                                double hip, double thigh,
                                double frontRise, double backRise,
                                String fitPreferences, Status status)
            throws IllegalArgumentException, IOException{
        //validate all input by user
        String validationError = ValidationUtil.validateAllCustomerFields(name, phone, waist, inseam, hip, thigh, frontRise, backRise, fitPreferences, status);
         if (!validationError.isEmpty()){
             throw new IllegalArgumentException(validationError);
         }

         //Create Customer Object
        int newId = nextCustomerId;
         nextCustomerId++;

         Customer newCustomer = new Customer(newId, name, phone,
                 waist, inseam,
                 hip, thigh,
                 frontRise, backRise,
                 fitPreferences, status);
         customers.add(newCustomer);

         //Autosave file
        dao.saveCustomersToFile(customers);

        return newCustomer;
    }

    //update customer data
    public  void updateCustomer(Customer customer) throws  IllegalArgumentException,
            IOException{
        //Check if customer exists
        int index = findCustomerIndexById(customer.getId());

        if (index == -1){
            throw new IllegalArgumentException(
                    "Customer with ID " + customer.getId()+ "not found. Cannot update."
            );
        }

        //validate updated data
        String validationError = ValidationUtil.validateAllCustomerFields(customer.getName(), customer.getPhone(),
                customer.getWaist(), customer.getInseam(),
                customer.getHip(), customer.getThigh(),
                customer.getFrontRise(), customer.getBackRise(),
                customer.getFitPreferences(), customer.getStatus()
        );
        if (!validationError.isEmpty()) {
            throw  new IllegalArgumentException(validationError);

        }

        customers.set(index, customer);

        //Autosave to file
        dao.saveCustomersToFile(customers);
        System.out.println("Customer " + customer.getId() + " updated successfully.");

    }

    // Delete a customer
    public  void deleteCustomer(int customerId)
        throws  IllegalArgumentException, IOException{

        // find the customer
        int index = findCustomerIndexById(customerId);

        if (index == -1) {
            throw new IllegalArgumentException(
                    "Customer with ID " + customerId + " not found. Cannot delete nonexistent customer"
            );
        }
        customers.remove(index);
        dao.saveCustomersToFile(customers); //auto save to file and update the list
        System.out.println("Customer " + customerId + " deleted successfully");
    }

    // Read customer data
    public Customer getCustomerByID(int id){
        // find index
        int index = findCustomerIndexById(id);

        if (index != -1){
            return customers.get(index);
        }else{
            return null;
        }
    }

    //Get all customers in the system
    public  List<Customer> getAllCustomers(){
        return new ArrayList<>(customers);
    }

    // Search customers by name
    public List<Customer> searchCustomersByName(String query){
        // handle empty queries
        if (query == null || query.trim().isEmpty()){
            return getAllCustomers();
        }

        final  String lowerQuery = query.toLowerCase().trim();

        List<Customer> results = new ArrayList<>();
        for (Customer c : customers){
            if (c.getName().toLowerCase().contains(lowerQuery)){
                results.add(c);
            }
        }
        return results;
    }

    //Total number of customers
    public int getCustomerCount(){
        return customers.size();
    }

    // filter customer by status (active and inactive)
    public List<Customer> filterCustomerByStatus(Status status){
        List<Customer> results = new ArrayList<>();

        for (Customer c : customers){
            if (c.getStatus() == status){
                results.add(c);
            }
        }
        return  results;
    }


    private int findCustomerIndexById(int id){
        for(int i = 0; i < customers.size(); i++){
            if (customers.get(i).getId() == id){
                return i;
            }
        }
        return  -1;
    }
    // In CustomerService.java
    public List<Customer> searchCustomersByName(String query, List<Customer> sourceList) {
        String lowerQuery = query.toLowerCase();
        return sourceList.stream()
                .filter(c -> c.getName().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }
    public Customer getCustomerById(int id) {
        List<Customer> all = getAllCustomers();
        for (Customer c : all) {
            if (c.getId() == id) return c;
        }
        return null;
    }
}
