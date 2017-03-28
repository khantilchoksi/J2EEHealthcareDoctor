package com.khantilchoksi.arztdoctor;

/**
 * Created by khantilchoksi on 28/03/17.
 */

public class Clinic {
    public String getClinicId() {
        return clinicId;
    }

    private String clinicId;
    private String clinicName;
    private String clinicAddress;

    public String getClinicName() {
        return clinicName;
    }

    public String getClinicAddress() {
        return clinicAddress;
    }

    public int getClinicPincode() {
        return clinicPincode;
    }

    public float getClinicLatitude() {
        return clinicLatitude;
    }

    public float getClinicLongitude() {
        return clinicLongitude;
    }

    public Clinic(String clinicId,String clinicName, String clinicAddress, int clinicPincode, float clinicLatitude, float clinicLongitude) {
        this.clinicId = clinicId;
        this.clinicName = clinicName;
        this.clinicAddress = clinicAddress;
        this.clinicPincode = clinicPincode;
        this.clinicLatitude = clinicLatitude;
        this.clinicLongitude = clinicLongitude;
    }

    private int clinicPincode;
    private float clinicLatitude;
    private float clinicLongitude;

}
