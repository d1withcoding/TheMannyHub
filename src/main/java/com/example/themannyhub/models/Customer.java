package com.example.themannyhub.models;

public class Customer {
    // These are private fields for customer data declared as variables
    private  int id; // customer id
    private String name; // customer name
    private String phone; // customer phone number (String because we don't need actual numbers for calculation
    private double waist; //waist measurement
    private double inseam;
    private double hip;
    private double thigh;
    private double frontRise;
    private double backRise;
    private String fitPreferences;
    private Status status;


    // Customer Constructor
    public Customer(int id, String name,
                    String phone, double waist,
                    double inseam, double hip,
                    double thigh, double frontRise,
                    double backRise,
                    String fitPreferences, Status status){
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.waist = waist;
        this.inseam = inseam;
        this.hip = hip;
        this.thigh = thigh;
        this.frontRise = frontRise;
        this.backRise = backRise;
        this.fitPreferences = fitPreferences;
        this.status = status;
    }


    // methods to get Customer Data
    public  int getId(){
        return id;
    }

    public String getName(){
        return  name;
    }

    public String getPhone() {
        return phone;
    }

    public double getWaist() {
        return waist;
    }

    public double getInseam() {
        return inseam;
    }

    public double getHip() {
        return hip;
    }

    public double getThigh() {
        return thigh;
    }

    public double getFrontRise() {
        return frontRise;
    }

    public double getBackRise() {
        return backRise;
    }

    public String getFitPreferences(){
        return  fitPreferences;
    }

    public Status getStatus() {
        return status;
    }

    // Set method to set Customer parameters

    public void setId(int id) {this.id = id;}

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setWaist(double waist) {
        this.waist = waist;
    }

    public void setInseam(double inseam) {
        this.inseam = inseam;
    }

    public void setHip(double hip) {
        this.hip = hip;
    }

    public void setThigh(double thigh) {
        this.thigh = thigh;
    }

    public void setFrontRise(double frontRise) {
        this.frontRise = frontRise;
    }

    public void setBackRise(double backRise) {
        this.backRise = backRise;
    }

    public void setFitPreferences(String fitPreferences) {
        this.fitPreferences = fitPreferences;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    //Helper methods to assist get and set methods
    public String getDisplayName(){
        return  name + "(" + phone + ")";
    }


    // Implementing overrides for toString() for easy debugging

    @Override
    public String toString(){
        return "Customer{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", phone='" + phone + '\'' +
                    ", waist=" + waist +
                    ", inseam=" + inseam +
                    ", hip=" + hip +
                    ", thigh=" + thigh +
                    ", frontRise=" + frontRise +
                    ", backRise=" + backRise +
                    ", fitPreferences='" + fitPreferences + '\'' +
                    ", status=" + status +
                    '}';
    }
}
