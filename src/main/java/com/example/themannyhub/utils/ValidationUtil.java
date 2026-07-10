package com.example.themannyhub.utils;

import com.example.themannyhub.models.Status;

public class ValidationUtil {
    //Validation criteria for various measurements

    //WAIST
    private static final double MIN_WAIST = 24.0;
    private static final double MAX_WAIST = 50.0;

    //INSEAM
    private static final double MIN_INSEAM = 26.0;
    private static final double MAX_INSEAM = 50.0;

    //HIP
    private static final double MIN_HIP = 30.0;
    private static final double MAX_HIP = 70.0;

    //THIGH
    private static final double MIN_THIGH = 18.0;
    private static final double MAX_THIGH = 70.0;

    //RISE FOR BOTH FRON AND BACK
    private static final double MIN_RISE = 6.0;
    private static final double MAX_RISE = 20.0;

    //PHONE NUMBER why use int because we want to know the length of the phone number
    private static final int PHONE_LENGTH = 10;

    //FIT PREFERENCE
    private static final int MAX_FIT_PREFERENCES = 200;


    //VALIDATION METHODS

    //NAME
    public static String validateName(String name){
        if (name == null){
            return "Name cannot be empty";
        }

        if (name.trim().length() < 2){
            return "Name must be at least 2 characters";
        }

        return null;
    }


    //Phone number validation
    public static String validatePhone(String phone){
        if (phone == null){
            return "phone cannot be empty";
        }

        if (!phone.matches("\\d{10}")){
            return "Phone must be exactly 10 digits";
        }
        return null;
    }

    //Waist
    public static String validateWaist (double waist){
        if (waist < MIN_WAIST || waist > MAX_WAIST){
            return "waist must be between " + MIN_WAIST + " - " + MAX_WAIST + "inches";
        }

        return null;
    }

    //validateInseam
    public static String validateInseam(double inseam){
        if (inseam < MIN_INSEAM || inseam > MAX_INSEAM){
            return "inseam must be between " + MIN_INSEAM + " - " + MAX_INSEAM + " inches";
        }
        return  null;
    }

    //validateHip
    public static String validateHip(double hip){
        if (hip < MIN_HIP || hip > MAX_HIP){
            return "Hip must be between " + MIN_HIP + " - " + MAX_HIP + " inches";
        }
        return null;
    }


    //validateTHigh
    public static String validateThigh(double thigh){
        if (thigh < MIN_THIGH || thigh > MAX_THIGH){
            return "Thigh must be between " + MIN_THIGH + " - " + MAX_THIGH + " inches";
        }
        return  null;
    }

    //validateFrontRise

    public static String  validateFrontRise(double frontRise){
        if (frontRise < MIN_RISE || frontRise > MAX_RISE){
            return "Front-rise must be between " + MIN_RISE + " - " + MAX_RISE + " inches";
        }
        return null;
    }

    //validateBackRise
    public static String validateBackRise(double backRise){
        if (backRise < MIN_RISE || backRise > MAX_RISE){
            return "Back-rise must be between " + MIN_RISE + " - " + MAX_RISE + " inches";
        }
        return null;
    }

    //validateFitPreferences 2 scopes
    public static String validateFitPreferences(String fitPreferences){
        if (fitPreferences == null || fitPreferences.trim().isEmpty()){
            return null;
        }

        //check fitPreferences length

        if(fitPreferences.length() > MAX_FIT_PREFERENCES){
            return "fit preferences cannot exceed " + MAX_FIT_PREFERENCES + " characters";
        }

        return null;
    }


    //validateStatus
    public static String validateStatus(Status status){
        if (status==null){
            return "Status must be selected";
        }

        return null;
    }


    // Final validation for all customer fields
    public static String validateAllCustomerFields(String name, String phone,
                                                   double waist, double inseam,
                                                   double hip, double thigh,
                                                   double frontRise, double backRise,
                                                   String fitPreferences, Status status){
        String nameError = validateName(name);
        if (nameError != null){
            return nameError;
        }
        String phoneError = validatePhone(phone);
        if (phoneError != null){
            return phoneError;
        }

        String waistError = validateWaist(waist);
        if (waistError != null){
            return waistError;
        }

        String inseamError = validateInseam(inseam);
            if (inseamError != null){
                return inseamError;
            }


        String hipError = validateHip(hip);
        if (hipError != null){
            return  hipError;
        }

        String thighError = validateThigh(thigh);
        if (thighError != null) {
            return thighError;
        }

        String frontRiseError = validateFrontRise(frontRise);
        if (frontRiseError != null) {
            return frontRiseError;
        }

        String backRiseError = validateBackRise(backRise);
        if (backRiseError != null) {
            return backRiseError;
        }

        String fitPreferencesError = validateFitPreferences(fitPreferences);
        if (fitPreferencesError != null) {
            return fitPreferencesError;
        }

        String statusError = validateStatus(status);
        if (statusError != null) {
            return statusError;
        }

        return null;
    }






}
