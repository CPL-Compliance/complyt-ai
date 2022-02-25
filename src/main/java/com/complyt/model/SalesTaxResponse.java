package com.complyt.model;

import java.util.ArrayList;

public class SalesTaxResponse {
    private float rCode;
    ArrayList<Object> results = new ArrayList<Object>();
    private String version;


    // Getter Methods

    public float getRCode() {
        return rCode;
    }

    public String getVersion() {
        return version;
    }

    // Setter Methods

    public void setRCode( float rCode ) {
        this.rCode = rCode;
    }

    public void setVersion( String version ) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "SalesTaxResponse{" +
                "rCode=" + rCode +
                ", results=" + results +
                ", version='" + version + '\'' +
                '}';
    }
}